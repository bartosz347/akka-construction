package eu.bwbw.bridge.actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.commands.AcceptAchieveGoalOffer
import eu.bwbw.bridge.domain.commands.AchieveGoalRequest
import eu.bwbw.bridge.domain.commands.TestCommand
import eu.bwbw.bridge.domain.commands.WorkerCommand
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Worker private constructor(
    context: ActorContext<WorkerCommand>
) : AbstractBehaviorKT<WorkerCommand>(context) {

    override fun onMessage(msg: WorkerCommand): Behavior<WorkerCommand> {
        return when (msg) {
            is TestCommand -> onTestCommand()
            is AchieveGoalRequest -> TODO()
            is AcceptAchieveGoalOffer -> TODO()
        }
    }
    
    private fun onTestCommand(): Behavior<WorkerCommand> {
        context.log.info("Worker received test message")
        return this
    }

    companion object {
        fun create(): Behavior<WorkerCommand> = Behaviors.setup(::Worker)
    }
}

