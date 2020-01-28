package eu.bwbw.bridge.domain.errors

open class ConfigLoaderError(reason: String): Throwable("Failed to load config file, reason: $reason")

class MissingAbilityDefinitionError(abilityName: String):
    ConfigLoaderError("missing definition of $abilityName ability")