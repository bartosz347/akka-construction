package eu.bwbw.bridge.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import eu.bwbw.bridge.domain.Config
import eu.bwbw.bridge.domain.commands.*
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send


class Coordinator private constructor(
    private val config: Config,
    context: ActorContext<CoordinatorCommand>
) : AbstractBehaviorKT<CoordinatorCommand>(context) {

    private lateinit var workers: List<ActorRef<WorkerCommand>>

    override fun onMessage(msg: CoordinatorCommand): Behavior<CoordinatorCommand> {
        return when (msg) {
            is StartConstructing -> onStartConstructing()
            is AchieveGoalOffer -> onAchieveGoalOffer(msg)
        }
    }

    private fun onStartConstructing(): Behavior<CoordinatorCommand> {
        workers = config.workers.map { context.spawn(Worker.create(it.abilities), it.name) }
        workers.forEach {
            it send AchieveGoalRequest(
                config.initialState.toList(),
                config.goalState.toList(),
                context.self
            )
        }
        return this
    }

    private fun onAchieveGoalOffer(msg: AchieveGoalOffer): Behavior<CoordinatorCommand> {
        context.log.info("Worker ${msg.from.path().name()} offers: ${msg.goal}")
        return this
    }

    companion object {
        fun create(config: Config): Behavior<CoordinatorCommand> = Behaviors.setup { context -> Coordinator(config, context) }
    }
}

