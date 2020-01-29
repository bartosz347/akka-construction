package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.domain.commands.Begin
import eu.bwbw.bridge.domain.commands.SupervisorCommand
import java.io.IOException

fun main() {
    val supervisor = ActorSystem.create<SupervisorCommand>(Supervisor.create(), "supervisor")
    supervisor.tell(Begin)

    try {
        println(">>> Press ENTER to exit <<<")
        System.`in`.read()
    } catch (ignored: IOException) {
    } finally {
        supervisor.terminate()
    }
}
