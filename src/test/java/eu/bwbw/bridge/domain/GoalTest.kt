package eu.bwbw.bridge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


internal class GoalTest {
    @Test
    fun equal_sameNameAndInstanceAreEqual() {
        val goal1 = Goal("abc", "instance")
        val goal2 = Goal("abc", "instance")
        assertThat(goal1).isEqualTo(goal2)
    }

    @Test
    fun equal_sameNameOneIsAnyAreEqual() {
        val goal1 = Goal("abc", "instance")
        val goal2 = Goal("abc", "ANY")
        assertThat(goal1).isEqualTo(goal2)
        assertThat(goal1).hasSameHashCodeAs(goal2)
    }

    @Test
    fun equal_sameNameDifferentInstancesAreNotEqual() {
        val goal1 = Goal("abc", "instance_1")
        val goal2 = Goal("abc", "instance_2")
        assertThat(goal1).isNotEqualTo(goal2)
        // hashcode is equal but it is proper behaviour
    }

    @Test
    fun equal_differentNameOneIsAnyAreNotEqual() {
        val goal1 = Goal("abc", "instance")
        val goal2 = Goal("def", "ANY")
        assertThat(goal1).isNotEqualTo(goal2)
        assertThat(goal1.hashCode()).isNotEqualTo(goal2.hashCode())
    }

    @Test
    fun equal_differentNameDifferentInstanceAreNotEqual() {
        val goal1 = Goal("abc", "instance")
        val goal2 = Goal("def", "instance2")
        assertThat(goal1).isNotEqualTo(goal2)
        assertThat(goal1.hashCode()).isNotEqualTo(goal2.hashCode())
    }

}