package eu.bwbw.bridge.actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.algorithms.GeneralProblemSolver
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.commands.*
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Worker private constructor(
    private val abilities: Set<Operation>,
    context: ActorContext<WorkerCommand>
) : AbstractBehaviorKT<WorkerCommand>(context) {
    private val name: String
        get() = context.self.path().name()

    private val offers: HashMap<Goal, List<Operation>> = HashMap()

    override fun onMessage(msg: WorkerCommand): Behavior<WorkerCommand> {
        return when (msg) {
            is TestCommand -> onTestCommand()
            is AchieveGoalRequest -> onAchieveGoalRequest(msg)
            is AcceptAchieveGoalOffer -> TODO()
        }
    }

    private fun onAchieveGoalRequest(msg: AchieveGoalRequest): Behavior<WorkerCommand> {
        val gps = GeneralProblemSolver()
        msg.goalState.forEach {
            val gpsResult = gps.run(msg.initialState, listOf(it), abilities)
            if(gpsResult.finalStates.isNotEmpty()) {
                offers[it] = gpsResult.appliedOperators
                msg.from send AchieveGoalOffer(context.self, it)
            }
        }

        return this
    }

    private fun onTestCommand(): Behavior<WorkerCommand> {
        context.log.info("Worker $name received test message")
        return this
    }

    companion object {
        fun create(abilities: Set<Operation>): Behavior<WorkerCommand> = Behaviors.setup { context -> Worker(abilities, context) }
    }
}

