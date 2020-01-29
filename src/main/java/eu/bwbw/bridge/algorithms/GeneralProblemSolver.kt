package eu.bwbw.bridge.algorithms

import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation

class GeneralProblemSolver {

    fun run(initialStates: List<Goal>, targetStates: List<Goal>, operators: Set<Operation>): List<Operation> {
        val finalStates = achieveAll(initialStates, operators, targetStates, emptyList()) ?: return emptyList()
        return operators.filter { it.applied }
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
        operator.applied = true;

        return result.filter { !deleteList.contains(it) } + addList
    }


}