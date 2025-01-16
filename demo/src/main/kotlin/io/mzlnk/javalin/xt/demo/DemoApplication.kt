package io.mzlnk.javalin.xt.demo

import io.javalin.Javalin
import io.mzlnk.javalin.xt.context
import io.mzlnk.javalin.xt.enableApplicationProperties
import io.mzlnk.javalin.xt.enableIoC
import io.mzlnk.javalin.xt.properties
import kotlin.time.measureTimedValue

fun main(args: Array<String>) {
    val (app, elapsedTime) = measureTimedValue {
        Javalin.create { config ->
            config.enableApplicationProperties { propertiesConfig ->
                propertiesConfig.resolveEnvironmentVariables = true
                propertiesConfig.profile = "dev"
            }
            config.enableIoC()
            config.jetty.defaultHost
        }
    }

    println("Total elapsed time: $elapsedTime.")

    println("property10: ${app.properties.getOrNull("property10")?.asString ?: "<null>"}")
    println("ComponentA: ${app.context.findInstance(ComponentA::class.java) ?: "<null>"}")

    app.start()

}
