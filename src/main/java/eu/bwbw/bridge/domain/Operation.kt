package eu.bwbw.bridge.domain

data class Operation(
        val name: String,
        val preconditions: Set<Goal>,
        val adds: Set<Goal>,
        val deletes: Set<Goal>,
        var applicationOrder: Int = -1
) {
}