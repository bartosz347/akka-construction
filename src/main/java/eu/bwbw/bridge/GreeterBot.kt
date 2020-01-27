package eu.bwbw.bridge

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.Greeter.Greeted

class GreeterBot private constructor(context: ActorContext<Greeted>, private val max: Int) : AbstractBehavior<Greeted>(context) {
    private var greetingCounter = 0
    override fun createReceive(): Receive<Greeted> {
        return newReceiveBuilder().onMessage(Greeted::class.java) { message: Greeted -> onGreeted(message) }.build()
    }

    private fun onGreeted(message: Greeted): Behavior<Greeted?> {
        greetingCounter++
        context.log.info("Greeting {} for {}", greetingCounter, message.whom)
        return if (greetingCounter == max) {
            Behaviors.stopped()
        } else {
            message.from.tell(Greet(message.whom, context.self))
            this
        }
    }

    companion object {
        fun create(max: Int): Behavior<Greeted> {
            return Behaviors.setup { context: ActorContext<Greeted> -> GreeterBot(context, max) }
        }
    }

}