package eu.bwbw.bridge.actors.coordinator

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.TimerScheduler
import eu.bwbw.bridge.actors.Worker
import eu.bwbw.bridge.domain.Goal
import eu.bwbw.bridge.utils.AbstractBehaviorKT
import eu.bwbw.bridge.utils.send
import java.time.Duration


class OffersCollector private constructor(
    private val planner: ActorRef<Planner.Command>,
    private val timeout: Duration,
    private val numWorkers: Int,
    private val timers: TimerScheduler<Command>
) {

    fun idle(): Behavior<Command> {
        return Behaviors.receive(Command::class.java)
            .onMessage(Command::class.java) { onIdleCommand() }
            .build()
    }

    private fun onIdleCommand(): Behavior<Command> {
        timers.startSingleTimer(TIMER_KEY, Command.StartOrTimeout, timeout)
        return Behaviors.setup<Command> { context -> Active(context) }
    }

    inner class Active constructor(context: ActorContext<Command>) : AbstractBehaviorKT<Command>(context) {

        private val offers = mutableSetOf<Command.AchieveGoalOffer>()
        private val finishedWorkers = mutableSetOf<ActorRef<Worker.Command>>()

        override fun onMessage(msg: Command): Behavior<Command> {
            return when (msg) {
                is Command.AchieveGoalOffer -> onAchieveGoalOffer(msg)
                is Command.FinishedOffering -> onFinishedOffering(msg)
                is Command.StartOrTimeout -> onTimeout()
            }
        }

        private fun onAchieveGoalOffer(offer: Command.AchieveGoalOffer): Behavior<Command> {
            offers.add(offer)
            return this
        }

        private fun onFinishedOffering(msg: Command.FinishedOffering): Behavior<Command> {
            finishedWorkers.add(msg.from)
            if (finishedWorkers.size == numWorkers) {
                timers.cancel(TIMER_KEY)
                planner send Planner.Command.OffersCollected(offers)
                return Behaviors.stopped()
            }
            return this
        }

        private fun onTimeout(): Behavior<Command> {
            planner send Planner.Command.OffersCollected(offers)
            return Behaviors.stopped()
        }
    }

    companion object {
        private val TIMER_KEY = Any()

        fun create(
            planner: ActorRef<Planner.Command>,
            timeout: Duration,
            numWorkers: Int
        ): Behavior<Command> = Behaviors.withTimers { timers -> OffersCollector(planner, timeout, numWorkers, timers).idle() }
    }

    sealed class Command {
        object StartOrTimeout : Command()

        data class AchieveGoalOffer(
            val worker: ActorRef<Worker.Command>,
            val achievedGoal: Goal,
            val finalState: Set<Goal>,
            val cost: Int = 0 // TODO change
        ) : Command()

        data class FinishedOffering(val from: ActorRef<Worker.Command>) : Command()
    }
}

