package eu.bwbw.bridge.utils

import akka.actor.typed.ActorRef

infix fun <T> ActorRef<T>.send(cmd:T) = this.tell(cmd)