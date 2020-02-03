package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.config.ConfigLoader
import eu.bwbw.bridge.utils.send
import java.io.File

fun main() {
    val configFile = File("config/config.json")
    val config = ConfigLoader().load(configFile)
    val supervisor = ActorSystem.create(Supervisor.create(config), "supervisor")
    supervisor send Supervisor.Command.Begin
}
