package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.commands.CoordinatorCommand
import eu.bwbw.bridge.domain.commands.StartConstructing
import eu.bwbw.bridge.domain.commands.TestCommand
import eu.bwbw.bridge.domain.commands.WorkerCommand
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Coordinator private constructor(
    context: ActorContext<CoordinatorCommand>
) : AbstractBehaviorKT<CoordinatorCommand>(context) {

    private var workers: MutableList<ActorRef<WorkerCommand>> = ArrayList()

    override fun onMessage(msg: CoordinatorCommand): Behavior<CoordinatorCommand> {
        when (msg) {
            is StartConstructing -> {
                workers.addAll(
                    listOf(
                        context.spawn(Worker.create(), "worker1"),
                        context.spawn(Worker.create(), "worker2")
                    )
                )

                workers.forEach {
                    it.tell(TestCommand(""))
                }
            }
        }
        return this
    }

    companion object {
        fun create(): Behavior<CoordinatorCommand> = Behaviors.setup(::Coordinator)
    }

}

