package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import java.io.IOException

fun main() {
    val greeterMain = ActorSystem.create<Command>(GreeterMain.create(), "helloakka")

    greeterMain.tell(SaySomething("testing the use of sealed class"))
    greeterMain.tell(SayHello("Charles"))

    try {
        println(">>> Press ENTER to exit <<<")
        System.`in`.read()
    } catch (ignored: IOException) {
    } finally {
        greeterMain.terminate()
    }
}
