package player.hiring

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("player.hiring")
                .mainClass(Application.javaClass)
                .start()
    }
}