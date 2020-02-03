package eu.bwbw.bridge.actors.coordinator

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Goal.Companion.ANY
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.testutils.withinNext
import eu.bwbw.bridge.utils.send
import org.junit.Before
import org.junit.Test
import java.time.Duration

class CoordinatorTest {
    private lateinit var testKit: TestKitJunitResource

    private val buildAnchorageOperation = Operation(
        name = "build-anchorage",
        preconditions = setOf(Goal("concrete", ANY)),
        adds = setOf(Goal("anchorage", ANY)),
        deletes = setOf(Goal("concrete", ANY))
    )

    private val config = Config(
        initialState = setOf(Goal("concrete", "1"), Goal("concrete", "2")),
        goalState = setOf(Goal("anchorage", "left"), Goal("anchorage", "right")),
        workers = setOf(
            ConstructionWorker("Bob", setOf(buildAnchorageOperation)) { Thread.sleep(300) },
            ConstructionWorker("John", setOf(buildAnchorageOperation)) { Thread.sleep(200) }
        ),
        offersCollectionTimeout = Duration.ofMillis(50)
    )

    @Before
    fun setUp() {
        testKit = TestKitJunitResource()
    }

    @Test
    fun `handles planning and dispatching of concurrent work correctly`() {
        val supervisor = testKit.createTestProbe<Supervisor.Command>()

        val coordinator = testKit.spawn(Coordinator.create(supervisor.ref, config))
        coordinator send Coordinator.Command.StartConstructing

        supervisor.expectNoMessage(withinNext(300))
        supervisor.expectMessage(
            withinNext(100),
            "Workers should build simultaneously",
            Supervisor.Command.ConstructionFinished
        )
    }
}