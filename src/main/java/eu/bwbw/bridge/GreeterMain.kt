package eu.bwbw.bridge

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.Greeter.Greeted
import eu.bwbw.bridge.GreeterMain.SayHello

class GreeterMain private constructor(context: ActorContext<SayHello>) : AbstractBehavior<SayHello>(context) {
    data class SayHello(val name: String)

    private val greeter: ActorRef<Greet> = context.spawn(Greeter.create(), "greeter")

    override fun createReceive(): Receive<SayHello> {
        return newReceiveBuilder().onMessage(SayHello::class.java) { command: SayHello -> onSayHello(command) }.build()
    }

    private fun onSayHello(command: SayHello): Behavior<SayHello?> {
        val replyTo = context.spawn<Greeted>(GreeterBot.create(3), command.name)
        greeter.tell(Greet(command.name, replyTo))
        return this
    }

    companion object {
        fun create(): Behavior<SayHello> {
            return Behaviors.setup { context: ActorContext<SayHello> -> GreeterMain(context) }
        }
    }

}