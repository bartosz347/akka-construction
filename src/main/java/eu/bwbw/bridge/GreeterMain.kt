package eu.bwbw.bridge

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.Greeter.Greeted
import eu.bwbw.bridge.utils.AbstractBehaviorKT

sealed class Command
data class SayHello(val name: String) : Command()
data class SaySomething(val something: String) : Command()


class GreeterMain private constructor(context: ActorContext<Command>) : AbstractBehaviorKT<Command>(context) {


    private val greeter: ActorRef<Greet> = context.spawn(Greeter.create(), "greeter")

    override fun onMessage(msg: Command): Behavior<Command> {
        return when (msg) {
            is SayHello -> onSayHello(msg)
            is SaySomething -> onSaySomething(msg)
        }
    }

    private fun onSaySomething(command: SaySomething): Behavior<Command> {
        context.log.info("Something said by GreeterMain: {}", command.something)
        return this
    }

    private fun onSayHello(command: SayHello): Behavior<Command> {
        val replyTo = context.spawn<Greeted>(GreeterBot.create(3), command.name)
        greeter.tell(Greet(command.name, replyTo))
        return this
    }

    companion object {
        fun create(): Behavior<Command> = Behaviors.setup { context: ActorContext<Command> -> GreeterMain(context) }
    }

}