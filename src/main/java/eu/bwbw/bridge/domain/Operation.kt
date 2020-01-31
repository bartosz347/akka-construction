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
) : IOperation {

    override fun toString(): String {
        val finalState = preconditions.toMutableSet()
        finalState.addAll(adds)
        finalState.removeAll(deletes)
        return "${setOfGoalsToString(preconditions)} -> ${setOfGoalsToString(finalState)}"
    }

    private fun setOfGoalsToString(goals: Set<Goal>): String {
        if (goals.isEmpty()) {
            return "[]"
        }

        val result = goals.joinToString { it.toString() }
        if (goals.size > 1) {
            return "[$result]"
        } else {
            return result
        }
    }
}