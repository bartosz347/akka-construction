package eu.bwbw.bridge.actors.coordinator

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.Worker
import eu.bwbw.bridge.actors.coordinator.Coordinator.Command.*
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Coordinator private constructor(
    private val config: Config,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Coordinator.Command>(context) {

    private var totalCost = 0
    private lateinit var workers: List<ActorRef<Worker.Command>>
    private var currentState: Set<Goal> = config.initialState.toSet()

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is StartConstructing -> onStartConstructing()
            is IterationPlanned -> onIterationPlanned(msg)
            is PlanningFailed -> onPlanningFailed()
            is WorkCompleted -> onWorkCompleted(msg)
        }
    }

    private fun onStartConstructing(): Behavior<Command> {
        workers = config.workers.map { context.spawn(Worker.create(context.self, it.abilities), it.name) }
        val planner = context.spawn(Planner.create(context.self, workers, currentState, config.goalState), "planner")
        planner send Planner.Command.StartPlanning
        return this
    }

    private fun onIterationPlanned(msg: IterationPlanned): Behavior<Command> {
        val (worker, goal, finalState, cost) = msg.plan
        worker send Worker.Command.StartWorking(goal, currentState)
        currentState = finalState
        totalCost += cost
        // TODO Plan next iteration
        // TODO save info about work in progress
        return this
    }

    private fun onPlanningFailed(): Behavior<Command> {
        // TODO handle failed planning when waiting for certain preconditions
        // TODO handle failed planning when nothing in progress -> fail the whole construction
        TODO("not implemented")
    }

    private fun onWorkCompleted(msg: WorkCompleted): Behavior<Command> {
        TODO()
    }

    companion object {
        fun create(config: Config): Behavior<Command> = Behaviors.setup { context -> Coordinator(config, context) }
    }

    sealed class Command {
        object StartConstructing : Command()
        data class IterationPlanned(val plan: OffersCollector.Command.AchieveGoalOffer) : Command()
        object PlanningFailed : Command()
        data class WorkCompleted(val worker: ActorRef<Worker.Command>): Command()
    }
}

