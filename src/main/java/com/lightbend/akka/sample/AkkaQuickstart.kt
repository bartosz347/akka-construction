package com.lightbend.akka.sample

import akka.actor.typed.ActorSystem
import com.lightbend.akka.sample.GreeterMain.SayHello
import java.io.IOException

object AkkaQuickstart {
    @JvmStatic
    fun main(args: Array<String>) { //#actor-system
        val greeterMain = ActorSystem.create<SayHello>(GreeterMain.Companion.create(), "helloakka")
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