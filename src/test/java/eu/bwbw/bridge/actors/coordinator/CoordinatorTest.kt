package eu.bwbw.bridge.actors.coordinator

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Goal.Companion.ANY
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.utils.send
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.Duration

class CoordinatorTest {
    private lateinit var testKit: TestKitJunitResource

    private val buildAnchorageOperation = Operation(
        "build-anchorage",
        setOf(Goal("concrete", ANY)),
        setOf(Goal("anchorage", ANY)),
        setOf(Goal("concrete", ANY))
    )

    private val config = Config(
        setOf(Goal("concrete", "1"), Goal("concrete", "2")),
        setOf(Goal("anchorage", "left"), Goal("anchorage", "right")),
        setOf(
            ConstructionWorker("Bob", setOf(buildAnchorageOperation)),
            ConstructionWorker("John", setOf(buildAnchorageOperation))
        ),
        Duration.ofMinutes(60)
    )

    @Before
    fun setUp() {
        testKit = TestKitJunitResource()
    }

    @Test
    fun `handles work planning and dispatching correctly`() {
        val testProbe = testKit.createTestProbe<Planner.Command>()

        val coordinator = testKit.spawn(Coordinator.create(config))
        coordinator send Coordinator.Command.StartConstructing


        testProbe.expectTerminated(coordinator, Duration.ofHours(5))

//        val timeout = Duration.ofSeconds(1)
//        val planner = testKit.createTestProbe<Planner.Command>()
//
//        val collector = testKit.spawn(OffersCollector.create(planner.ref, timeout, 3), "collector")
//        collector send OffersCollector.Command.StartOrTimeout
//
//        planner.expectMessage(Duration.ofSeconds(2), Planner.Command.OffersCollected(setOf()))
//        planner.expectTerminated(collector)
    }
}