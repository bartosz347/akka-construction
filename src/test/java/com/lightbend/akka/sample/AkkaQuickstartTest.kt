package com.lightbend.akka.sample

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import com.lightbend.akka.sample.Greeter.Greet
import com.lightbend.akka.sample.Greeter.Greeted
import org.junit.ClassRule
import org.junit.Test

//#definition
class AkkaQuickstartTest {
    //#definition
//#test
    @Test
    fun testGreeterActorSendingOfGreeting() {
        val testProbe = testKit.createTestProbe<Greeted>()
        val underTest = testKit.spawn(Greeter.create(), "greeter")
        underTest.tell(Greet("Charles", testProbe.ref))
        testProbe.expectMessage(Greeted("Charles", underTest))
    } //#test

    companion object {
        @ClassRule
        val testKit = TestKitJunitResource()
    }
}