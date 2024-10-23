package io.mzlnk.javalin.di

import io.javalin.config.JavalinConfig
import io.mzlnk.javalin.di.spi.JavalinRunnerProvider
import java.util.*

object JavalinRunner {

    fun run(configCustomizer: (JavalinConfig) -> Unit = {}) {
        val serviceLoader = ServiceLoader.load(JavalinRunnerProvider::class.java)

        val providers = serviceLoader.toList()
        if (providers.isEmpty()) {
            throw IllegalStateException("No JavalinRunnerProvider implementation found")
        }

        if (providers.size > 1) {
            throw IllegalStateException("Multiple JavalinRunnerProvider implementations found")
        }

        providers.first().run(configCustomizer)
    }

}