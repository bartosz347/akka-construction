package eu.bwbw.bridge.algorithms

import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation

class GeneralProblemSolver {
    data class gpsResult(
        val finalStates: List<Goal>,
        val appliedOperators: List<Operation>
    )

    fun run(initialStates: List<Goal>, targetStates: List<Goal>, operators: Set<Operation>): gpsResult {
        val finalStates = achieveAll(initialStates, operators, targetStates, emptyList()) ?: emptyList()
        return gpsResult(
            finalStates = finalStates,
            appliedOperators =
            if (finalStates.isNotEmpty()) operators.sortedBy { operation -> operation.applicationOrder }.filter { it.applicationOrder >= 0 }
            else emptyList()
        )
    }

    private fun achieveAll(statesIn: List<Goal>, operators: Set<Operation>, goals: List<Goal>, goalStack: List<Goal>): List<Goal>? {
        var states = statesIn

        for (goal in goals) {
            val achievedStates = achieve(states, operators, goal, goalStack)
            if (achievedStates == null) {
                return null
            } else {
                states = achievedStates
            }
        }

        for (goal in goals) {
            if (!states.contains(goal)) {
                return null
            }
        }

        return states;
    }

    private fun achieve(states: List<Goal>, operators: Set<Operation>, goal: Goal, goalStack: List<Goal>): List<Goal>? {
        if (states.contains(goal)) {
            return states
        }

        if (goalStack.contains(goal)) {
            return null
        }

        for (operator in operators) {
            if (!operator.adds.contains(goal)) {
                continue
            }

            val result = applyOperator(operator, states, operators, goal, goalStack)
            result?.let {
                return it
            }

        }
        return null
    }

    private fun applyOperator(operator: Operation, states: List<Goal>, operators: Set<Operation>, goal: Goal, goalStack: List<Goal>): List<Goal>? {
        val result = achieveAll(states, operators, operator.preconditions.toList(), goalStack + listOf(goal))
            ?: return null

        val addList = operator.adds
        val deleteList = operator.deletes
        operator.applicationOrder = (operators.map { o: Operation -> o.applicationOrder }.max() ?: -1) + 1

        return result.filter { !deleteList.contains(it) } + addList
    }


}