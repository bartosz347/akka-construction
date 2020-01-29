package eu.bwbw.bridge.algorithms

import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation

import org.assertj.core.api.Assertions.*
import org.junit.Test

internal class GeneralProblemSolverTest {

    @Test
    internal fun TestGeneralProblemSolver() {


        val initialStates = listOf(
                Goal("hand empty", ""),
                Goal("arm_down", "")
        )
        val goals = listOf(
                Goal("satisfied", ""),
                Goal("baseball in air", "")
        )

        val generalProblemSolver = GeneralProblemSolver()
        val operators = setOf(
                other, throw_baseball, raise_arm, grab_baseball, drink_beer
        )

        val gpsResults = generalProblemSolver.run(initialStates, goals, operators)

        assertThat(gpsResults).containsOnly(
                drink_beer, grab_baseball, raise_arm, throw_baseball
        )
    }

    @Test
    fun testFail() {
        val initialStates = listOf(
                Goal("hand empty", ""),
                Goal("arm_down", "")
        )
        val operators = setOf(
                other, throw_baseball, raise_arm, grab_baseball, drink_beer
        )
        val generalProblemSolver = GeneralProblemSolver()

        val goals = listOf(
                Goal("satisfied", ""),
                Goal("baseball in air", ""),
                Goal("awesome", "")
        )

        val gpsResults = generalProblemSolver.run(initialStates, goals, operators)
        assertThat(gpsResults).isEmpty()

    }


    companion object {
        val raise_arm = Operation(
                "raise arm",
                preconditions = setOf(Goal("arm_down", "")),
                adds = mutableSetOf(
                        Goal("arm up", ""),
                        Goal("raising arm", "")
                ),
                deletes = setOf(Goal("arm_down", ""))
        )
        val grab_baseball = Operation(
                "grab baseball",
                preconditions = setOf(
                        Goal("hand empty", ""),
                        Goal("arm_down", "")),
                adds = mutableSetOf(
                        Goal("have baseball", ""),
                        Goal("grabbing baseball", "")
                ),
                deletes = setOf(Goal("hand empty", ""))
        )
        val other = Operation(
                "other operator",
                preconditions = setOf(
                        Goal("hand empty", ""),
                        Goal("arm_down", "")),
                adds = mutableSetOf(
                        Goal("drinking beer", ""),
                        Goal("grabbing baseball", "")
                ),
                deletes = setOf(Goal("hand empty", ""))
        )
        val drink_beer = Operation(
                "drink beer",
                preconditions = setOf(
                        Goal("arm_down", ""),
                        Goal("hand empty", "")
                ),
                adds = mutableSetOf(
                        Goal("satisfied", ""),
                        Goal("drinking beer", "")
                ),
                deletes = setOf()
        )
        val throw_baseball = Operation(
                "throw",
                preconditions = setOf(
                        Goal("have baseball", ""),
                        Goal("arm up", "")
                ),
                adds = mutableSetOf(Goal("arm_down", ""),
                        Goal("baseball in air", ""),
                        Goal("throwing baseball", "")
                ),
                deletes = setOf(
                        Goal("have baseball", ""),
                        Goal("arm up", "")
                )
        )
    }
}