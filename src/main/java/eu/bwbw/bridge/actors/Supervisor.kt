package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Supervisor private constructor(
    private val config: Config,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Supervisor.Command>(context) {
    private lateinit var coordinator: ActorRef<Coordinator.Command>

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.Begin -> onBegin()
        }
    }

    private fun onBegin(): Behavior<Command> {
        coordinator = context.spawn(Coordinator.create(config), "coordinator")
        coordinator send Coordinator.Command.StartConstructing
        return this
    }

    companion object {
        fun create(config: Config): Behavior<Command> = Behaviors.setup { Supervisor(config, it) }
    }

    sealed class Command {
        object Begin : Command()
    }
}

