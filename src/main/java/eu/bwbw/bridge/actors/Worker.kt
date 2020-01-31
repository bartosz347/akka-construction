package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.coordinator.OffersCollector
import eu.bwbw.bridge.actors.coordinator.Planner
import eu.bwbw.bridge.algorithms.GeneralProblemSolver
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Worker private constructor(
    private val abilities: Set<Operation>,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Worker.Command>(context) {

    private val gps = GeneralProblemSolver()
    private val name: String
        get() = context.self.path().name()

    private val offers: HashMap<Pair<Goal, Set<Goal>>, List<Operation>> = HashMap()

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.AchieveGoalRequest -> onAchieveGoalRequest(msg)
            is Command.AcceptAchieveGoalOffer -> TODO()
        }
    }

    private fun onAchieveGoalRequest(msg: Command.AchieveGoalRequest): Behavior<Command> {
        val planner = msg.from
        msg.goalState.forEach {
            val gpsResult = gps.run(msg.initialState.toList(), listOf(it), abilities)
            if (gpsResult.finalStates.isNotEmpty()) {
                offers[Pair(it, msg.initialState)] = gpsResult.appliedOperators
                planner send OffersCollector.Command.AchieveGoalOffer(context.self, it, gpsResult.finalStates)
            }
        }
        planner send OffersCollector.Command.FinishedOffering(context.self)
        return this
    }

    companion object {
        fun create(abilities: Set<Operation>): Behavior<Command> = Behaviors.setup { context -> Worker(abilities, context) }
    }

    sealed class Command {

        data class AchieveGoalRequest(
            val initialState: Set<Goal>,
            val goalState: Set<Goal>,
            val from: ActorRef<OffersCollector.Command>
        ) : Command()

        data class AcceptAchieveGoalOffer(val goal: Goal, val initialState: List<Goal>) : Command()
    }
}

