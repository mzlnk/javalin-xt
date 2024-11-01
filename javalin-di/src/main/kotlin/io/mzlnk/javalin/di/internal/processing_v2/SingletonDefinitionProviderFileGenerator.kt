package io.mzlnk.javalin.di.internal.processing_v2

internal object SingletonDefinitionProviderFileGenerator {

    fun generate(module: Clazz): GeneratedFile {
        val content =
            // language=kotlin
            """
            |package ${module.type.packageName}
            |
            |import io.mzlnk.javalin.di.internal.context.SingletonDefinitionProvider
            |import io.mzlnk.javalin.di.internal.context.SingletonDefinition
            |
            |class ${module.type.name}SingletonDefinitionProvider : SingletonDefinitionProvider {
            |
            |    override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |            type = ComponentA::class.java,
            |            dependencies = emptyList(),
            |            instanceProvider = { ComponentA() }
            |        ),
            |        SingletonDefinition(
            |            type = ComponentB::class.java,
            |            dependencies = emptyList(),
            |            instanceProvider = { ComponentB() }
            |        )
            |    )
            |
            |}
        """.trimMargin()

        return GeneratedFile(
            name = "${module.type.name}SingletonDefinitionProvider",
            extension = "kt",
            packageName = module.type.packageName,
            content = content
        )
    }

}