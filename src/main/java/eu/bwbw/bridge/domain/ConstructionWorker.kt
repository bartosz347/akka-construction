package eu.bwbw.bridge.domain

data class ConstructionWorker(
        val name: String,
        val abilities: Set<Operation>
)