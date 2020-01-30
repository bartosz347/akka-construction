package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Coordinator private constructor(
    private val config: Config,
    context: ActorContext<Command>
) : AbstractBehaviorKT<Coordinator.Command>(context) {

    private lateinit var workers: List<ActorRef<Worker.Command>>
    private val currentState: MutableSet<Goal> = config.initialState.toMutableSet()

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is Command.StartConstructing -> onStartConstructing()
        }
    }

    private fun onStartConstructing(): Behavior<Command> {
        // TODO spawn planner and send start planning command
        return this
    }

    companion object {
        fun create(config: Config): Behavior<Command> = Behaviors.setup { context -> Coordinator(config, context) }
    }

    sealed class Command {
        object StartConstructing : Command()
    }
}

