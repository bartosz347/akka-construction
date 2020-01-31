package eu.bwbw.bridge.domain

import akka.actor.typed.ActorRef
import eu.bwbw.bridge.actors.Worker

data class Work(
    val worker: ActorRef<Worker.Command>,
    val beforeState: Set<Goal>,
    val afterState: Set<Goal>
)