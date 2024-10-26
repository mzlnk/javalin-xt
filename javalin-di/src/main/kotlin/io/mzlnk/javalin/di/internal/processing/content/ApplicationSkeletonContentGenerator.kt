package io.mzlnk.javalin.di.internal.processing.content

import io.mzlnk.javalin.di.internal.processing.graph.DependencyGraph

internal object ApplicationSkeletonContentGenerator {

    fun generate(packageName: String, dependencyGraph: DependencyGraph): String {
        // language=kotlin
        return """
            |package $packageName
            |
            |import io.javalin.Javalin
            |import io.javalin.config.JavalinConfig
            |import io.mzlnk.javalin.di.spi.JavalinRunnerProvider
            |
            |class JavalinRunnerProviderImpl : JavalinRunnerProvider {
            |    override fun run(configCustomizer: (JavalinConfig) -> Unit) {
            |        val app = Javalin.create { config ->
            |            configCustomizer(config)
            |        }.start()
            |    }
            |
            |}
            """.trimMargin()
    }

}