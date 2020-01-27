package eu.bwbw.bridge

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import eu.bwbw.bridge.Greeter.Greet
import java.util.*

class Greeter private constructor(context: ActorContext<Greet>) : AbstractBehavior<Greet>(context) {
    class Greet(val whom: String?, val replyTo: ActorRef<Greeted>)

    class Greeted(val whom: String?, val from: ActorRef<Greet?>) {
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val greeted = o as Greeted
            return whom == greeted.whom &&
                    from == greeted.from
        }

        override fun hashCode(): Int {
            return Objects.hash(whom, from)
        }

        override fun toString(): String {
            return "Greeted{" +
                    "whom='" + whom + '\'' +
                    ", from=" + from +
                    '}'
        }

    }

    override fun createReceive(): Receive<Greet> {
        return newReceiveBuilder().onMessage(Greet::class.java) { command: Greet -> onGreet(command) }.build()
    }

    private fun onGreet(command: Greet): Behavior<Greet?> {
        context.log.info("Hello {}!", command.whom)
        command.replyTo.tell(Greeted(command.whom, context.self))
        return this
    }

    companion object {
        fun create(): Behavior<Greet> {
            return Behaviors.setup { context: ActorContext<Greet> -> Greeter(context) }
        }
    }
}