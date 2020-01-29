package eu.bwbw.bridge.domain.commands

import com.sun.xml.internal.bind.v2.TODO

sealed class CoordinatorCommand

object StartConstructing : CoordinatorCommand()

sealed class WorkerResponse : CoordinatorCommand()

data class AchieveGoalOffer(
    val todo: TODO
) : WorkerResponse()
