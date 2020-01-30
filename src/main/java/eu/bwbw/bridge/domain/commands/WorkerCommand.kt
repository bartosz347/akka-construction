package eu.bwbw.bridge.domain.commands

import com.sun.xml.internal.bind.v2.TODO
import eu.bwbw.bridge.domain.Goal

sealed class WorkerCommand

object TestCommand : WorkerCommand()

data class AchieveGoalRequest(
    val initialState: List<Goal>,
    val goalState: List<Goal>
) : WorkerCommand()

data class AcceptAchieveGoalOffer(
    val todo: TODO
) : WorkerCommand()