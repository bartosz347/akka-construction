package eu.bwbw.bridge

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import eu.bwbw.bridge.Greeter.Greet
import eu.bwbw.bridge.Greeter.Greeted
import org.junit.ClassRule
import org.junit.Test

class AkkaQuickstartTest {
    @Test
    fun testGreeterActorSendingOfGreeting() {
        val testProbe = testKit.createTestProbe<Greeted>()
        val underTest = testKit.spawn(Greeter.create(), "greeter")
        underTest.tell(Greet("Charles", testProbe.ref))
        testProbe.expectMessage(Greeted("Charles", underTest))
    }

    companion object {
        @ClassRule
        val testKit = TestKitJunitResource()
    }
}