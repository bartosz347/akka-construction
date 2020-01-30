package eu.bwbw.bridge.algorithms

import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class GeneralProblemSolverTest {

    @Test
    internal fun testGeneralProblemSolver() {
        val initialStates = listOf(
            Goal("hand empty"),
            Goal("arm_down")
        )
        val goals = listOf(
            Goal("satisfied"),
            Goal("baseball in air")
        )

        val generalProblemSolver = GeneralProblemSolver()
        val operators = setOf(
            other, raise_arm, grab_baseball, drink_beer, throw_baseball
        )

        val gpsResults = generalProblemSolver.run(initialStates, goals, operators)

        assertThat(gpsResults.appliedOperators).containsExactly(
            drink_beer, grab_baseball, raise_arm, throw_baseball
        )
        assertThat(gpsResults.finalStates).containsExactlyInAnyOrder(
            Goal("satisfied"),
            Goal("drinking beer"),
            Goal("grabbing baseball"),
            Goal("raising arm"),
            Goal("arm_down"),
            Goal("baseball in air"),
            Goal("throwing baseball")
        )
    }

    @Test
    fun testFail() {
        val initialStates = listOf(
            Goal("hand empty"),
            Goal("arm_down")
        )
        val operators = setOf(
            other, throw_baseball, raise_arm, grab_baseball, drink_beer
        )
        val generalProblemSolver = GeneralProblemSolver()

        val goals = listOf(
            Goal("satisfied"),
            Goal("baseball in air"),
            Goal("awesome")
        )

        val gpsResults = generalProblemSolver.run(initialStates, goals, operators)
        assertThat(gpsResults.appliedOperators).isEmpty()
    }


    companion object {
        val raise_arm = Operation(
            "raise arm",
            preconditions = setOf(Goal("arm_down")),
            adds = setOf(
                Goal("arm up"),
                Goal("raising arm")
            ),
            deletes = setOf(Goal("arm_down"))
        )
        val grab_baseball = Operation(
            "grab baseball",
            preconditions = setOf(
                Goal("hand empty"),
                Goal("arm_down")),
            adds = setOf(
                Goal("have baseball"),
                Goal("grabbing baseball")
            ),
            deletes = setOf(Goal("hand empty"))
        )
        val other = Operation(
            "other operator",
            preconditions = setOf(
                Goal("hand empty"),
                Goal("arm_down")),
            adds = setOf(
                Goal("drinking beer"),
                Goal("grabbing baseball")
            ),
            deletes = setOf(Goal("hand empty"))
        )
        val drink_beer = Operation(
            "drink beer",
            preconditions = setOf(
                Goal("arm_down"),
                Goal("hand empty")
            ),
            adds = setOf(
                Goal("satisfied"),
                Goal("drinking beer")
            ),
            deletes = setOf()
        )
        val throw_baseball = Operation(
            "throw",
            preconditions = setOf(
                Goal("have baseball"),
                Goal("arm up")
            ),
            adds = setOf(
                Goal("arm_down"),
                Goal("baseball in air"),
                Goal("throwing baseball")
            ),
            deletes = setOf(
                Goal("have baseball"),
                Goal("arm up")
            )
        )
    }
}