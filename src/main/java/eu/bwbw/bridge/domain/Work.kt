package eu.bwbw.bridge.domain

import akka.actor.typed.ActorRef
import eu.bwbw.bridge.actors.Worker

data class Work(
    val worker: ActorRef<Worker.Command>,
    val achievedGoal: Goal,
    val consumedResources: Set<Goal>
)