package eu.bwbw.bridge.domain.commands

import akka.actor.typed.ActorRef
import eu.bwbw.bridge.domain.Goal

sealed class CoordinatorCommand

object StartConstructing : CoordinatorCommand()

sealed class WorkerResponse : CoordinatorCommand()

data class AchieveGoalOffer(
    val from: ActorRef<WorkerCommand>,
    val goal: Goal
) : WorkerResponse()
