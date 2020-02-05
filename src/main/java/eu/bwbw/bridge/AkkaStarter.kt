package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.actors.Supervisor
import eu.bwbw.bridge.config.ConfigLoader
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.WorkerFunction
import eu.bwbw.bridge.utils.send
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val config = loadConfig(args)
    val configWithFunctions = setWorkerFunctions(config) { Thread.sleep(1000) }

    val supervisor = ActorSystem.create(Supervisor.create(configWithFunctions), "supervisor")
    supervisor send Supervisor.Command.Begin
}

private fun loadConfig(args: Array<String>): Config {
    if (args.size != 1) {
        printUsage()
        exitProcess(1)
    }

    return try {
        val configFile = File(args[0])
        ConfigLoader().load(configFile)
    } catch (e: Exception) {
        println(e.toString())
        printUsage()
        exitProcess(1)
    }
}

private fun printUsage() {
    println("""
        Usage:
        java -jar bridge-construction-1.0-all.jar <path-to-config-file>
    """.trimIndent())
}

private fun setWorkerFunctions(config: Config, doWork: WorkerFunction): Config {
    val workersWithFunction = config.workers.map { it.copy(doWork = doWork) }.toSet()
    return config.copy(workers = workersWithFunction)
}
