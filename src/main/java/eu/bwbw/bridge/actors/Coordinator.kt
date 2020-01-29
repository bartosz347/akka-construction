package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.commands.*
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Coordinator private constructor(
    val config: Config,
    context: ActorContext<CoordinatorCommand>
) : AbstractBehaviorKT<CoordinatorCommand>(context) {

    private var workers: MutableList<ActorRef<WorkerCommand>> = ArrayList()

    override fun onMessage(msg: CoordinatorCommand): Behavior<CoordinatorCommand> {
        return when (msg) {
            is StartConstructing -> onStartConstructing()
            is AchieveGoalOffer -> TODO()
        }
    }

    private fun onStartConstructing(): Behavior<CoordinatorCommand> {
        workers.addAll(
            listOf(
                context.spawn(Worker.create(), "worker1"),
                context.spawn(Worker.create(), "worker2")
            )
        )
        workers.forEach {
            it.tell(TestCommand(""))
        }
        return this
    }

    companion object {
        fun create(config: Config): Behavior<CoordinatorCommand> = Behaviors.setup { context -> Coordinator(config, context) }
    }

}

