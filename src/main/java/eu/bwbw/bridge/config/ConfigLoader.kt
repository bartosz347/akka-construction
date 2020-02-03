package eu.bwbw.bridge.config

import com.google.gson.Gson
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.errors.MissingAbilityDefinitionError
import java.io.File
import java.time.Duration

class ConfigLoader {
    private data class ConstructionWorkerSchema(
            val name: String,
            val abilities: List<String>
    )

    private data class ConfigSchema(
            val initialState: List<Goal>,
            val goalState: List<Goal>,
            val workers: List<ConstructionWorkerSchema>,
            val abilities: List<Operation>,
            val offersCollectionTimeout: Long
    )

    fun load(configFile: File): Config {
        return load(configFile.readText())
    }

    fun load(configJson: String): Config {
        val (initialState, goalState, workers, abilities, timeout) = Gson().fromJson<ConfigSchema>(configJson, ConfigSchema::class.java)

        val constructionWorkers = workers.map { toConstructionWorker(it, abilities) }

        return Config(
            initialState.toSet(),
            goalState.toSet(),
            constructionWorkers.toSet(),
            Duration.ofMillis(timeout)
        )
    }

    private fun toConstructionWorker(worker: ConstructionWorkerSchema, abilities: List<Operation>): ConstructionWorker {
        val abilityList = worker.abilities.map { getAbilityForName(it, abilities) }
        return ConstructionWorker(worker.name, abilityList.toSet())
    }

    private fun getAbilityForName(abilityName: String, abilities: List<Operation>): Operation {
        val ability = abilities.find { it.name == abilityName }
        return ability ?: throw MissingAbilityDefinitionError(abilityName)
    }
}