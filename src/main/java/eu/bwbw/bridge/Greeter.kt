package eu.bwbw.bridge

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.utils.AbstractBehaviorKT

class Greeter private constructor(context: ActorContext<Greet>) : AbstractBehaviorKT<Greet>(context) {
    data class Greet(val whom: String?, val replyTo: ActorRef<Greeted>)
    data class Greeted(val whom: String?, val from: ActorRef<Greet?>)

    override fun onMessage(msg: Greet): Behavior<Greet> {
        onGreet(msg)
        return this
    }

    private fun onGreet(command: Greet): Behavior<Greet?> {
        context.log.info("Hello {}!", command.whom)
        command.replyTo.tell(Greeted(command.whom, context.self))
        return this
    }

    companion object {
        fun create(): Behavior<Greet> = Behaviors.setup { context: ActorContext<Greet> -> Greeter(context) }
    }
}