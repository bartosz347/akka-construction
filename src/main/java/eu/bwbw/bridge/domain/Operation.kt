package eu.bwbw.bridge.domain

interface IOperation {
    val name: String
    val preconditions: Set<Goal>
    val adds: Set<Goal>
    val deletes: Set<Goal>
}

data class Operation(
    override val name: String,
    override val preconditions: Set<Goal>,
    override val adds: Set<Goal>,
    override val deletes: Set<Goal>
) : IOperation