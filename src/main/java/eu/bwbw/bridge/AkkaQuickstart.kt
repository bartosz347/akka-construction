package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.GreeterMain.SayHello
import java.io.IOException

fun main() {
    val greeterMain = ActorSystem.create<SayHello>(GreeterMain.create(), "helloakka")

    greeterMain.tell(SayHello("Charles"))

    try {
        println(">>> Press ENTER to exit <<<")
        System.`in`.read()
    } catch (ignored: IOException) {
    } finally {
        greeterMain.terminate()
    }
}
