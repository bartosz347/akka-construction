package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.coordinator.Coordinator
import eu.bwbw.bridge.actors.coordinator.OffersCollector
import eu.bwbw.bridge.algorithms.GeneralProblemSolver
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.WorkerFunction
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Worker private constructor(
    private val coordinator: ActorRef<Coordinator.Command>,
    private val abilities: Set<Operation>,
    private val doWork: WorkerFunction,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Worker.Command>(context) {

    private val gps = GeneralProblemSolver()
    private val name: String
        get() = context.self.path().name()

    private val offers: HashMap<Goal, List<Operation>> = HashMap()

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.AchieveGoalRequest -> onAchieveGoalRequest(msg)
            is Command.StartWorking -> onStartWorking(msg)
        }
    }

    private fun onAchieveGoalRequest(msg: Command.AchieveGoalRequest): Behavior<Command> {
        val planner = msg.from
        msg.goalState.forEach { goal ->
            val gpsResult = gps.run(msg.initialState.toList(), listOf(goal), abilities)
            if (gpsResult.finalStates.isNotEmpty()) {
                offers[goal] = gpsResult.appliedOperators

                val goalInResult = gpsResult.finalStates.find { it.name == goal.name }
                    ?: throw Error("this should never happen")
                val finalState = gpsResult.finalStates.minus(goalInResult).plus(goal).toSet()

                val consumed = msg.initialState.minus(finalState)
                planner send OffersCollector.Command.AchieveGoalOffer(context.self, goal, consumed, gpsResult.appliedOperators.size)
            }
        }
        planner send OffersCollector.Command.FinishedOffering(context.self)
        return this
    }

    private fun onStartWorking(msg: Command.StartWorking): Behavior<Command> {
        val operations = offers[msg.goal] ?: throw Error("this should never happen")
        context.log.info("$name starts working on goal: ${msg.goal}")
        operations.forEach {
            context.log.info("$name: Doing $it")
            doWork()
            context.log.info("$name finished working")
        }
        coordinator send Coordinator.Command.WorkCompleted(context.self)
        return this
    }

    companion object {
        fun create(
            coordinator: ActorRef<Coordinator.Command>,
            abilities: Set<Operation>,
            doWork: WorkerFunction
        ): Behavior<Command> = Behaviors.supervise<Command>(
            Behaviors.setup { context -> Worker(coordinator, abilities, doWork, context) }
        ).onFailure(SupervisorStrategy.restart())
    }

    sealed class Command {

        data class AchieveGoalRequest(
            val initialState: Set<Goal>,
            val goalState: Set<Goal>,
            val from: ActorRef<OffersCollector.Command>
        ) : Command()

        data class StartWorking(val goal: Goal, val initialState: Set<Goal>) : Command()
    }
}

