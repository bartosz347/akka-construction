package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.actors.coordinator.Coordinator
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
            is Command.CunstructionFinished -> onConstructionFinished()
        }
    }

    private fun onBegin(): Behavior<Command> {
        coordinator = context.spawn(Coordinator.create(context.self, config), "coordinator")
        coordinator send Coordinator.Command.StartConstructing
        return this
    }

    private fun onConstructionFinished(): Behavior<Command> {
        context.log.info("onConstructionFinished")
        return Behaviors.stopped()
    }

    companion object {
        fun create(config: Config): Behavior<Command> = Behaviors.setup { Supervisor(config, it) }
    }

    sealed class Command {
        object Begin : Command()
        object CunstructionFinished : Command()
    }
}

