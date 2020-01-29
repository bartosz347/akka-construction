package eu.bwbw.bridge.domain.commands

import com.sun.xml.internal.bind.v2.TODO
import eu.bwbw.bridge.domain.Goal

sealed class CoordinatorCommand
sealed class WorkerCommand : CoordinatorCommand()

object StartConstructing : CoordinatorCommand()
object ConstructingStarted : CoordinatorCommand()

data class TestCommand(
    val info: String
) : WorkerCommand()

data class AchieveGoalRequest(
    val initialState: List<Goal>,
    val goalState: List<Goal>
) : WorkerCommand()

data class AchieveGoalOffer(
    val todo: TODO
) : WorkerCommand()

data class AcceptAchieveGoalOffer(
    val todo: TODO
) : WorkerCommand()



