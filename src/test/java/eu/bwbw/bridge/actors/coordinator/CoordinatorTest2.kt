package eu.bwbw.bridge.actors.coordinator

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Goal.Companion.ANY
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.utils.send
import org.junit.Before
import org.junit.Test
import java.time.Duration

class CoordinatorTest2 {
    private lateinit var testKit: TestKitJunitResource

    private val buildAnchorageOperation = Operation(
        "build-anchorage",
        setOf(Goal("concrete", ANY)),
        setOf(Goal("anchorage", ANY)),
        setOf(Goal("concrete", ANY))
    )

    private val buildDeckOperation = Operation(
        "build-deck",
        setOf(Goal("concrete", ANY), Goal("anchorage", "left"), Goal("anchorage", "right")),
        setOf(Goal("deck", "one")),
        setOf(Goal("concrete", ANY))
    )

    private val config = Config(
        setOf(Goal("concrete", "1"), Goal("concrete", "2"), Goal("concrete", "3")),
        setOf(Goal("anchorage", "left"), Goal("anchorage", "right"), Goal("deck", "one")),
        setOf(
            ConstructionWorker("Bob", setOf(buildAnchorageOperation)),
            ConstructionWorker("John", setOf(buildAnchorageOperation)),
            ConstructionWorker("David", setOf(buildDeckOperation))
        ),
        Duration.ofSeconds(5)
    )

    @Before
    fun setUp() {
        testKit = TestKitJunitResource()
    }

    @Test
    fun `handles planning of sequential work`() {
        val supervisor = testKit.createTestProbe<Supervisor.Command>()

        val coordinator = testKit.spawn(Coordinator.create(supervisor.ref, config))
        coordinator send Coordinator.Command.StartConstructing

        supervisor.expectMessage(Duration.ofMinutes(1), Supervisor.Command.ConstructionFinished)
    }
}