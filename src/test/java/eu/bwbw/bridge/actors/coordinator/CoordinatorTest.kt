package eu.bwbw.bridge.actors.coordinator

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.actors.Worker
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
    private val concrete: (String) -> Goal = { Goal("concrete", it) }
    private val anchorage: (String) -> Goal = { Goal("anchorage", it) }
    private val deck = Goal("deck", "one")

    private val buildAnchorageOperation = Operation(
        name = "build-anchorage",
        preconditions = setOf(concrete(ANY)),
        adds = setOf(anchorage(ANY)),
        deletes = setOf(concrete(ANY))
    )

    private val buildDeckOperation = Operation(
        name = "build-deck",
        preconditions = setOf(concrete(ANY), anchorage("left"), anchorage("right")),
        adds = setOf(deck),
        deletes = setOf(concrete(ANY))
    )

    private lateinit var testKit: TestKitJunitResource

    @Before
    fun setUp() {
        testKit = TestKitJunitResource()
    }

    @Test
    fun `handles planning and dispatching of concurrent work correctly`() {
        val config = Config(
            initialState = setOf(concrete("1"), concrete("2")),
            goalState = setOf(anchorage("left"), anchorage("right")),
            workers = setOf(
                ConstructionWorker("Bob", setOf(buildAnchorageOperation)) { Thread.sleep(300) },
                ConstructionWorker("John", setOf(buildAnchorageOperation)) { Thread.sleep(200) }
            ),
            offersCollectionTimeout = Duration.ofMillis(50)
        )

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

    @Test
    fun `handles planning of sequential work`() {
        val config = Config(
            initialState = setOf(concrete("1"), concrete("2"), concrete("3")),
            goalState = setOf(anchorage("left"), anchorage("right"), deck),
            workers = setOf(
                ConstructionWorker("Bob", setOf(buildAnchorageOperation)) { Thread.sleep(200) },
                ConstructionWorker("John", setOf(buildAnchorageOperation)) { Thread.sleep(200) },
                ConstructionWorker("David", setOf(buildDeckOperation)) { Thread.sleep(200) }
            ),
            offersCollectionTimeout = Duration.ofMillis(50)
        )

        val supervisor = testKit.createTestProbe<Supervisor.Command>()
        val coordinator = testKit.spawn(Coordinator.create(supervisor.ref, config))
        coordinator send Coordinator.Command.StartConstructing

        // Deck should be build after anchorages
        supervisor.expectNoMessage(withinNext(400))

        // Anchorages should be built within ~250ms, plus ~200ms for deck, the whole construction should finish in under 550ms
        supervisor.expectMessage(withinNext(150), Supervisor.Command.ConstructionFinished)
    }

    @Test
    fun `handles worker termination when exception in doWork occurs`() {
        val goalState = Goal("anchorage", "ANY")
        val initialState = setOf(concrete("1"))

        val coordinator = testKit.createTestProbe<Coordinator.Command>()
        val collector = testKit.createTestProbe<OffersCollector.Command>()
        val worker = testKit.spawn(
            Worker.create(
                coordinator.ref(),
                setOf(buildAnchorageOperation)
            ) {
                throw Error("worker crash")
            }
        )
        worker send Worker.Command.AchieveGoalRequest(initialState, setOf(goalState), collector.ref)

        worker send Worker.Command.StartWorking(
            goalState,
            initialState
        )

        coordinator.expectTerminated(worker, withinNext(500))
    }
}