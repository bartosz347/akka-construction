package eu.bwbw.bridge.config

import com.google.gson.Gson
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.errors.MissingAbilityDefinitionError
import java.io.File

class ConfigLoader {
    private data class ConstructionWorkerSchema(
            val name: String,
            val abilities: List<String>
    )

    private data class ConfigSchema(
            val initialState: List<Goal>,
            val goalState: List<Goal>,
            val workers: List<ConstructionWorkerSchema>,
            val abilities: List<Operation>
    )

    fun load(configFile: File): Config {
        return load(configFile.readText())
    }

    fun load(configJson: String): Config {
        val parsedConfig = Gson().fromJson<ConfigSchema>(configJson, ConfigSchema::class.java)

        val workers = parsedConfig.workers.map { worker ->
            val abilityList = worker.abilities.map { abilityName ->
                val ability = parsedConfig.abilities.find { it.name == abilityName }
                ability ?: throw MissingAbilityDefinitionError(abilityName)
            }
            ConstructionWorker(worker.name, abilityList.toSet())
        }.toSet()

        return Config(
            parsedConfig.initialState.toSet(),
            parsedConfig.goalState.toSet(),
            workers
        )
    }
}