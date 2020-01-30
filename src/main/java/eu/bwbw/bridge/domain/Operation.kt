package eu.bwbw.bridge.domain

data class Operation(
    override val name: String,
    override val preconditions: Set<Goal>,
    override val adds: Set<Goal>,
    override val deletes: Set<Goal>
) : IOperation