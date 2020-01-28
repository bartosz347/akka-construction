package eu.bwbw.bridge.domain

data class ConstructionWorker(
        val name: String,
        val capabilities: Set<Operation>
)