package eu.bwbw.bridge.domain

interface IOperation {
    val name: String
    val preconditions: Set<Goal>
    val adds: Set<Goal>
    val deletes: Set<Goal>
}