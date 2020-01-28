package eu.bwbw.bridge.domain

data class Operation(
        val name: String,
        val preconditions: List<Goal>,
        val adds: List<Goal>,
        val deletes: List<Goal>
)