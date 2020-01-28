package eu.bwbw.bridge.domain

data class Configuration(
        val initialState: Set<Goal>,
        val goalState: Set<Goal>,
        val workers: Set<ConstructionWorker>
)