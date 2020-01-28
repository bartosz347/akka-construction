package eu.bwbw.bridge.config

import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.ConstructionWorker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.domain.Operation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

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
            ]
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
            workers = setOf(bob)
        )
    }


    private lateinit var configLoader: ConfigLoader

    @Before
    fun setUp() {
        configLoader = ConfigLoader()
    }

    @Test
    fun load() {
        assertThat(configLoader.load(configJson)).isEqualTo(expectedConfig)
    }
}