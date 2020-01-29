package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.japi.function.Function2
import eu.bwbw.bridge.domain.commands.Begin
import eu.bwbw.bridge.domain.commands.CoordinatorCommand
import eu.bwbw.bridge.domain.commands.StartConstructing
import eu.bwbw.bridge.domain.commands.SupervisorCommand
import eu.bwbw.bridge.utils.AbstractBehaviorKT


class Supervisor private constructor(
    context: ActorContext<SupervisorCommand>
) : AbstractBehaviorKT<SupervisorCommand>(context) {

    private var coordinator: ActorRef<CoordinatorCommand>? = null

    override fun onMessage(msg: SupervisorCommand): Behavior<SupervisorCommand> {
        val k = Function2 { arg1: Int, arg2: Int -> }
        when (msg) {
            is Begin -> {
                coordinator = context.spawn(Coordinator.create(), "coordinator")
                coordinator?.tell(StartConstructing())

                // TODO check if message has been delivered
/*                context.ask(
                    ConstructingStarted::class.java,
                    coordinator,
                    Duration.ofSeconds(4),
                      { it: ActorRef<CoordinatorCommand> -> ConstructingStarted() },
                      {arg1, arg2 -> ConstructingStarted()}
                )*/
            }
        }
        return this
    }

    companion object {
        fun create(): Behavior<SupervisorCommand> = Behaviors.setup { context: ActorContext<SupervisorCommand> ->
            Supervisor(context)
        }
    }
}

