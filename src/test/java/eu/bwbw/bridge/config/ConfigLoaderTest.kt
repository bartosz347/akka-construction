package eu.bwbw.bridge.config

import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import eu.bwbw.bridge.domain.errors.MissingAbilityDefinitionError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Before
import org.junit.Test
import java.time.Duration

class ConfigLoaderTest {
    private val configJson = """
        {
            "initialState": [{"name": "concrete"}],
            "goalState": [
                {"name": "deck"},
                {"name": "anchorage", "instance": "left"}
            ],
            "abilities": [
                {
                    "name": "build-anchorage",
                    "preconditions": [{"name": "concrete"}],
                    "adds": [{"name": "anchorage", "instance": "ANY"}],
                    "deletes": [{"name": "concrete"}]
                }
            ],
            "workers": [
                {
                    "name": "Bob",
                    "abilities": ["build-anchorage"]
                }
            ],
            "offersCollectionTimeout": "5000"   
        }   
    """

    private val incorrectConfigJson = """
        {
            "initialState": [{"name": "concrete"}],
            "goalState": [
                {"name": "deck"},
                {"name": "anchorage", "instance": "left"}
            ],
            "abilities": [
                {
                    "name": "build-anchorage",
                    "preconditions": [{"name": "concrete"}],
                    "adds": [{"name": "anchorage", "instance": "ANY"}],
                    "deletes": [{"name": "concrete"}]
                }
            ],
            "workers": [
                {
                    "name": "Bob",
                    "abilities": ["build-deck"]
                }
            ],
            "offersCollectionTimeout": "5000"
        }   
    """

    private val expectedConfig: Config

    init {
        val concrete = Goal("concrete")
        val deck = Goal("deck")
        val anchorageLeft = Goal("anchorage", "left")

        val anchorageAny = Goal("anchorage", "ANY")
        val buildAnchorage = Operation(
            name = "build-anchorage",
            preconditions = setOf(concrete),
            adds = setOf(anchorageAny),
            deletes = setOf(concrete)
        )

        val bob = ConstructionWorker(
            name = "Bob",
            abilities = setOf(buildAnchorage)
        )

        expectedConfig = Config(
            initialState = setOf(concrete),
            goalState = setOf(deck, anchorageLeft),
            workers = setOf(bob),
            offersCollectionTimeout = Duration.ofMillis(5000)
        )
    }


    private lateinit var configLoader: ConfigLoader

    @Before
    fun setUp() {
        configLoader = ConfigLoader()
    }

    @Test
    fun load_loadsConfigurationCorrectly() {
        assertThat(configLoader.load(configJson)).isEqualTo(expectedConfig)
    }

    @Test
    fun load_throwsForMissingAbilityDefinition() {
        assertThatExceptionOfType(MissingAbilityDefinitionError::class.java)
            .isThrownBy { configLoader.load(incorrectConfigJson) }
    }
}