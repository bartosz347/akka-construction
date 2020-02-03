package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.config.ConfigLoader
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.WorkerFunction
import eu.bwbw.bridge.utils.send
import java.io.File

fun main() {
    val configFile = File("config/config.json")
    val config = ConfigLoader().load(configFile)
    val configWithFunctions = setWorkerFunctions(config) { Thread.sleep(1000) }

    val supervisor = ActorSystem.create(Supervisor.create(configWithFunctions), "supervisor")
    supervisor send Supervisor.Command.Begin
}

fun setWorkerFunctions(config: Config, doWork: WorkerFunction): Config {
    val workersWithFunction = config.workers.map { it.copy(doWork = doWork) }.toSet()
    return config.copy(workers = workersWithFunction)
}
