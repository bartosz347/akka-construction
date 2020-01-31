package eu.bwbw.bridge.actors.coordinator

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.actors.Worker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.send
import org.junit.Before
import org.junit.Test
import java.time.Duration

class OffersCollectorTest {
    private lateinit var testKit: TestKitJunitResource

    @Before
    fun setUp() {
        testKit = TestKitJunitResource()
    }

    @Test
    fun `sends OffersCollected after timeout`() {
        val timeout = Duration.ofSeconds(1)
        val planner = testKit.createTestProbe<Planner.Command>()

        val collector = testKit.spawn(OffersCollector.create(planner.ref, timeout, 3), "collector")
        collector send OffersCollector.Command.StartOrTimeout

        planner.expectMessage(Duration.ofSeconds(2), Planner.Command.OffersCollected(setOf()))
        planner.expectTerminated(collector)
    }

    @Test
    fun `sends OffersCollected after collecting offers from all workers`() {
        val timeout = Duration.ofSeconds(10)

        val planner = testKit.createTestProbe<Planner.Command>()
        val worker = testKit.createTestProbe<Worker.Command>()

        val collector = testKit.spawn(OffersCollector.create(planner.ref, timeout, 1), "collector")
        collector send OffersCollector.Command.StartOrTimeout

        val achieveGoalOffer = OffersCollector.Command.AchieveGoalOffer(worker.ref, Goal("goal"), setOf())
        collector send achieveGoalOffer
        collector send OffersCollector.Command.FinishedOffering(worker.ref)

        planner.expectMessage(
            Planner.Command.OffersCollected(
                setOf(achieveGoalOffer)
            )
        )
        planner.expectTerminated(collector)
    }
}