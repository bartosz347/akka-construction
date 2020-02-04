package eu.bwbw.bridge.domain

import java.time.Duration

typealias WorkerFunction = () -> Unit

data class Config(
    val initialState: Set<Goal>,
    val goalState: Set<Goal>,
    val workers: Set<ConstructionWorker>,
    val offersCollectionTimeout: Duration
)

data class ConstructionWorker(
    val name: String,
    val abilities: Set<Operation>,
    val doWork: WorkerFunction = {}
)