package eu.bwbw.bridge.actors.coordinator

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.actors.Worker
import eu.bwbw.bridge.actors.coordinator.Coordinator.Command.*
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Work
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send

class Coordinator private constructor(
    private val supervisor: ActorRef<Supervisor.Command>,
    private val config: Config,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Coordinator.Command>(context) {

    private var currentIteration = 0
    private var totalCost = 0
    private lateinit var workers: List<ActorRef<Worker.Command>>

    private var currentState: Set<Goal> = config.initialState.toSet()
    private var remainingGoals: Set<Goal> = config.goalState.toSet()

    private val workInProgress = mutableSetOf<Work>()

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is StartConstructing -> onStartConstructing()
            is IterationPlanned -> onIterationPlanned(msg)
            is PlanningFailed -> onPlanningFailed(msg)
            is WorkCompleted -> onWorkCompleted(msg)
        }
    }

    private fun onStartConstructing(): Behavior<Command> {
        workers = config.workers.map {
            context.spawn(
                Worker.create(context.self, it.abilities, it.doWork),
                it.name
            )
        }
        nextPlanningIteration()
        return this
    }

    private fun onIterationPlanned(msg: IterationPlanned): Behavior<Command> {
        context.log.info("Coordinator.onIterationPlanned: currentState=$currentState, remainingGoals=$remainingGoals, workInProgress.size=${workInProgress.size}, currentIteration=$currentIteration, msg.iteration=${msg.iteration}")
        if (msg.iteration != currentIteration) {
            return this
        }

        val (worker, goal, consumedResources, cost) = msg.plan

        workInProgress.add(Work(
            worker,
            goal,
            consumedResources
        ))
        currentState = currentState.minus(consumedResources)
        remainingGoals = remainingGoals.minus(goal)
        totalCost += cost

        worker send Worker.Command.StartWorking(goal, currentState)
        nextPlanningIteration()
        return this
    }

    private fun onPlanningFailed(msg: PlanningFailed): Behavior<Command> {
        context.log.info("Coordinator.onPlanningFailed: workInProgress.size=${workInProgress.size}, currentIteration=$currentIteration, msg.iteration=${msg.iteration}")
        if (msg.iteration == currentIteration && workInProgress.isEmpty()) {
            supervisor send Supervisor.Command.ConstructionFailed
        }
        return this
    }

    private fun onWorkCompleted(msg: WorkCompleted): Behavior<Command> {
        val completedWork = workInProgress.find { it.worker == msg.worker }
        if (completedWork == null) {
            context.log.warn("Received onWorkCompleted from an actor that should not be working")
            return this
        }
        workInProgress.remove(completedWork)
        currentState = currentState.plus(completedWork.achievedGoal)
        context.log.info("Coordinator.onWorkCompleted: currentState=$currentState, remainingGoals=$remainingGoals")
        if (currentState.containsAll(config.goalState)) {
            supervisor send Supervisor.Command.ConstructionFinished
        } else {
            nextPlanningIteration()
        }
        return this
    }

    private fun nextPlanningIteration() {
        if (remainingGoals.isEmpty()) {
            return
        }
        val iteration = ++currentIteration
        val planner = context.spawn(
            Planner.create(context.self, workers, iteration, currentState, remainingGoals, config.offersCollectionTimeout),
            "planner-$iteration"
        )
        planner send Planner.Command.StartPlanning
    }

    companion object {
        fun create(
            supervisor: ActorRef<Supervisor.Command>,
            config: Config
        ): Behavior<Command> = Behaviors.setup { context -> Coordinator(supervisor, config, context) }
    }

    sealed class Command {
        object StartConstructing : Command()

        data class IterationPlanned(
            val iteration: Int,
            val plan: OffersCollector.Command.AchieveGoalOffer
        ) : Command()

        data class PlanningFailed(val iteration: Int) : Command()

        data class WorkCompleted(val worker: ActorRef<Worker.Command>) : Command()
    }
}

