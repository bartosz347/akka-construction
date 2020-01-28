package eu.bwbw.bridge.domain

data class Operation(
        val name: String,
        val preconditions: Set<Goal>,
        val adds: MutableSet<Goal>,
        val deletes: Set<Goal>,
        var applied: Boolean = false
) {
}