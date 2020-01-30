package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send

class Planner private constructor(
    private val workers: List<ActorRef<Worker.Command>>,
    private val currentState: Set<Goal>,
    private val goalState: Set<Goal>,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Planner.Command>(context) {


    override fun onMessage(msg: Command): Behavior<Command> {
        return when(msg) {
            is Command.StartPlanning -> onStartPlanning()
            is Command.AchieveGoalOffer -> onAchieveGoalOffer()
            is Command.FinishedOffering -> TODO()
        }
    }

    private fun onAchieveGoalOffer(): Behavior<Command> {
        TODO()
    }


    private fun onStartPlanning(): Behavior<Command> {
        workers.forEach {
            it send Worker.Command.AchieveGoalRequest(
                currentState,
                goalState,
                context.self
            )
        }
        // TODO set timeout for planning results
        return this
    }

    companion object {
        fun create(
            workers: List<ActorRef<Worker.Command>>,
            currentState: Set<Goal>,
            goalState: Set<Goal>
        ): Behavior<Command> = Behaviors.setup { Planner(workers, currentState, goalState, it) }
    }

    sealed class Command {

        object StartPlanning : Command()

        data class AchieveGoalOffer(
            val from: ActorRef<Worker.Command>,
            val achievedGoal: Goal,
            val finalState: Set<Goal>
        ) : Command()

        data class FinishedOffering(val from: ActorRef<Worker.Command>): Command()
    }
}