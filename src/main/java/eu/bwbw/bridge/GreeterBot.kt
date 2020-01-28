package eu.bwbw.bridge

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.Greeter.Greeted
import eu.bwbw.bridge.utils.AbstractBehaviorKT

class GreeterBot private constructor(context: ActorContext<Greeted>, private val max: Int) : AbstractBehaviorKT<Greeted>(context) {
    private var greetingCounter = 0

    override fun onMessage(msg: Greeted): Behavior<Greeted> {
        onGreeted(msg)
        return this
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
        fun create(max: Int): Behavior<Greeted> =
            Behaviors.setup { context: ActorContext<Greeted> -> GreeterBot(context, max) }
    }

}