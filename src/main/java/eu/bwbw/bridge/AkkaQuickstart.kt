package eu.bwbw.bridge

import akka.actor.typed.ActorSystem
import eu.bwbw.bridge.GreeterMain.SayHello
import java.io.IOException

object AkkaQuickstart {
    @JvmStatic
    fun main(args: Array<String>) { //#actor-system
        val greeterMain = ActorSystem.create<SayHello>(GreeterMain.create(), "helloakka")
        //#actor-system
//#main-send-messages
        greeterMain.tell(SayHello("Charles"))
        //#main-send-messages
        try {
            println(">>> Press ENTER to exit <<<")
            System.`in`.read()
        } catch (ignored: IOException) {
        } finally {
            greeterMain.terminate()
        }
    }
}