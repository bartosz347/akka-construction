package eu.bwbw.bridge.domain.commands

import akka.actor.typed.ActorRef
import eu.bwbw.bridge.domain.Goal

sealed class WorkerCommand

object TestCommand : WorkerCommand()

data class AchieveGoalRequest(
    val initialState: List<Goal>,
    val goalState: List<Goal>,
    val from: ActorRef<CoordinatorCommand>
) : WorkerCommand()

data class AcceptAchieveGoalOffer(
    val goal: Goal,
    val initialState: List<Goal>
) : WorkerCommand()