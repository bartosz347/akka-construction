package eu.bwbw.bridge.actors.coordinator

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.Worker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send
import java.time.Duration

class Planner private constructor(
    private val coordinator: ActorRef<Coordinator.Command>,
    private val workers: List<ActorRef<Worker.Command>>,
    private val currentState: Set<Goal>,
    private val goalState: Set<Goal>,
    private val offersCollectionTimeout: Duration,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Planner.Command>(context) {
    private var isPlanning = false

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.StartPlanning -> onStartPlanning()
            is Command.OffersCollected -> onOffersCollected(msg)
            is Command.FinishPlanning -> Behaviors.stopped()
        }
    }

    private fun onStartPlanning(): Behavior<Command> {
        if (isPlanning) {
            return this
        }
        isPlanning = true
        val offersCollector = context.spawn(
            OffersCollector.create(context.self, offersCollectionTimeout, workers.size),
            "offers-collector"
        )
        offersCollector send OffersCollector.Command.StartOrTimeout
        workers.forEach {
            it send Worker.Command.AchieveGoalRequest(
                currentState,
                goalState,
                offersCollector
            )
        }
        return this
    }

    private fun onOffersCollected(msg: Command.OffersCollected): Behavior<Command> {
        val bestOffer = msg.offers.minBy { it.cost }
        if (bestOffer == null) {
            coordinator send Coordinator.Command.PlanningFailed
            return this
        }
        coordinator send Coordinator.Command.IterationPlanned(context.self, bestOffer)
        return this
    }

    companion object {
        fun create(
            coordinator: ActorRef<Coordinator.Command>,
            workers: List<ActorRef<Worker.Command>>,
            currentState: Set<Goal>,
            goalState: Set<Goal>,
            offersCollectionTimeout: Duration
        ): Behavior<Command> = Behaviors.setup {
            Planner(coordinator, workers, currentState, goalState, offersCollectionTimeout, it)
        }
    }

    sealed class Command {
        object StartPlanning : Command()
        data class OffersCollected(val offers: Set<OffersCollector.Command.AchieveGoalOffer>) : Command()
        object FinishPlanning : Command()
    }
}