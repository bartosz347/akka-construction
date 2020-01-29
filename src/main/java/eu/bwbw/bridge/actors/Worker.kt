package eu.bwbw.bridge.actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.commands.AcceptAchieveGoalOffer
import eu.bwbw.bridge.domain.commands.AchieveGoalRequest
import eu.bwbw.bridge.domain.commands.TestCommand
import eu.bwbw.bridge.domain.commands.WorkerCommand
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Worker private constructor(
    private val abilities: Set<Operation>,
    context: ActorContext<WorkerCommand>
) : AbstractBehaviorKT<WorkerCommand>(context) {
    private val name: String
        get() = context.self.path().name()

    override fun onMessage(msg: WorkerCommand): Behavior<WorkerCommand> {
        return when (msg) {
            is TestCommand -> onTestCommand()
            is AchieveGoalRequest -> TODO()
            is AcceptAchieveGoalOffer -> TODO()
        }
    }

    private fun onTestCommand(): Behavior<WorkerCommand> {
        context.log.info("Worker $name received test message")
        return this
    }

    companion object {
        fun create(abilities: Set<Operation>): Behavior<WorkerCommand> = Behaviors.setup { context -> Worker(abilities, context) }
    }
}

