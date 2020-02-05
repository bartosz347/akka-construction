package eu.bwbw.bridge.utils

import akka.actor.typed.Behavior
import akka.actor.typed.Signal
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Receive

abstract class AbstractBehaviorKT<T>(context: ActorContext<T>) : AbstractBehavior<T>(context) {
    abstract fun onMessage(msg: T): Behavior<T>

    open fun onSignal(sig: Signal?): Behavior<T> {
        return this
    }

    override fun createReceive(): Receive<T> = object : Receive<T>() {
        override fun receiveMessage(msg: T): Behavior<T> = onMessage(msg)
        override fun receiveSignal(sig: Signal?): Behavior<T> = onSignal(sig)
    }
}