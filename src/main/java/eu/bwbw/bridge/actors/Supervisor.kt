package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.commands.CoordinatorCommand
import eu.bwbw.bridge.domain.commands.StartConstructing
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Supervisor private constructor(
    context: ActorContext<Command>
) : AbstractBehaviorKT<Supervisor.Command>(context) {
    sealed class Command {
        object Begin : Command()
    }

    private lateinit var coordinator: ActorRef<CoordinatorCommand>

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.Begin -> onBegin()
        }
    }

    private fun onBegin(): Behavior<Command> {
        coordinator = context.spawn(Coordinator.create(), "coordinator")
        coordinator send StartConstructing
        return this
    }

    companion object {
        fun create(): Behavior<Command> = Behaviors.setup(::Supervisor)
    }
}

