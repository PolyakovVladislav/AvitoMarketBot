package utils

import java.text.SimpleDateFormat
import java.util.*

class Logger(
    private val classname: String,
    private val logLevel: Level = Level.Debug
) {

    operator fun invoke(message: String, tag: String = "", logLevel: Level = Level.Debug) {
        if (logLevel >= this.logLevel) {
            var internalTag = tag
            if (tag != "") {
                internalTag += " "
            }
            val date = SimpleDateFormat("HH:mm:ss.SSS").format(
                Date(
                    System.currentTimeMillis()
                )
            )
            println("$classname [$date]: $tag$message")
        }
    }

    enum class Level(level: Int) {
        Debug(0),
        Staging(1),
        Release(2)
    }
}