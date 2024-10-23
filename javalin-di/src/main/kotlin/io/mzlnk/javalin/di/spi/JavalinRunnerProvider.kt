package io.mzlnk.javalin.di.spi

import io.javalin.config.JavalinConfig

interface JavalinRunnerProvider {

    fun run(configCustomizer: (JavalinConfig) -> Unit = {})

}