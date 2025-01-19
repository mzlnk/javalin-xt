package io.mzlnk.javalin.xt.routing.internal.processing

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject.GeneratedFile
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Endpoint

// TODO: refactor it
internal object KotlinEndpointAdapterFileGenerator : EndpointAdapterFileGenerator {

    override fun generate(endpoint: Endpoint): GeneratedFile {
        val file = FileSpec.builder(
            packageName = endpoint.type.packageName,
            fileName = "${endpoint.type.name}Adapter"
        )
            .addType(
                TypeSpec.classBuilder("${endpoint.type.name}Adapter")
                    .addSuperinterface(EndpointAdapter::class)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(
                                name = "endpoint",
                                type = ClassName(endpoint.type.packageName, endpoint.type.name)
                            )
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            name = "endpoint",
                            type = ClassName(endpoint.type.packageName, endpoint.type.name),
                            modifiers = listOf(KModifier.PRIVATE)
                        )
                            .initializer("endpoint")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder(name = "apply")
                            .addParameter(name = "javalin", type = ClassName("io.javalin", "Javalin"))
                            .addModifiers(KModifier.OVERRIDE)
                            .addCode(
                                CodeBlock.builder()
                                    .apply {
                                        endpoint.handlers.forEachIndexed { idx, handler ->
                                            add(
                                                "javalin.%L(%S) { ctx -> ",
                                                handler.method.toJavalinMethod(),
                                                handler.path
                                            )
                                            add("endpoint.%L(", handler.methodName)
                                            handler.parameters.forEachIndexed { idx, parameter ->
                                                when (parameter) {
                                                    is Endpoint.Handler.Parameter.Context ->
                                                        add("ctx")

                                                    is Endpoint.Handler.Parameter.PathVariable ->
                                                        add("ctx.pathParam(%S)", parameter.name)

                                                    is Endpoint.Handler.Parameter.Header -> {
                                                        add("ctx.header(%S)", parameter.name)
                                                        if (parameter.required) add("!!")
                                                    }

                                                    is Endpoint.Handler.Parameter.QueryParam -> {
                                                        add("ctx.queryParam(%S)", parameter.name)
                                                        if (parameter.required) add("!!")
                                                    }

                                                    is Endpoint.Handler.Parameter.Body.AsString ->
                                                        add("ctx.body()")
                                                }
                                                if (idx < handler.parameters.size - 1) add(", ")
                                            }
                                            add(")")
                                            add(" }")
                                            if (idx < endpoint.handlers.size - 1) add("\n")
                                        }
                                    }
                                    .build()
                            )
                            .build()
                    )
                    .addType(
                        TypeSpec.classBuilder("Factory")
                            .addSuperinterface(EndpointAdapter.Factory::class)
                            .addProperty(
                                PropertySpec.builder(
                                    name = "supportedEndpoint",
                                    type = ClassName("java.lang", "Class")
                                        .parameterizedBy(
                                            WildcardTypeName.producerOf(
                                                ClassName(
                                                    io.mzlnk.javalin.xt.routing.Endpoint::class.java.`package`.name,
                                                    io.mzlnk.javalin.xt.routing.Endpoint::class.java.simpleName
                                                )
                                            )
                                        ),
                                    modifiers = listOf(KModifier.OVERRIDE)
                                )
                                    .initializer(
                                        "%T::class.java",
                                        ClassName(endpoint.type.packageName, endpoint.type.name)
                                    )
                                    .build()
                            )
                            .addFunction(
                                FunSpec.builder(name = "create")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addParameter(
                                        name = "endpoint",
                                        type = ClassName(
                                            io.mzlnk.javalin.xt.routing.Endpoint::class.java.`package`.name,
                                            io.mzlnk.javalin.xt.routing.Endpoint::class.java.simpleName
                                        )
                                    )
                                    .returns(EndpointAdapter::class)
                                    .addStatement(
                                        "return %T(endpoint as %T)",
                                        ClassName(endpoint.type.packageName, "${endpoint.type.name}Adapter"),
                                        ClassName(endpoint.type.packageName, endpoint.type.name)
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        return GeneratedFile(
            name = "${endpoint.type.name}Adapter",
            extension = "kt",
            packageName = endpoint.type.packageName,
            content = file.toString().trimEnd()
        )
    }
}

private fun Endpoint.Handler.Method.toJavalinMethod(): String = when (this) {
    Endpoint.Handler.Method.GET -> "get"
    Endpoint.Handler.Method.POST -> "post"
    Endpoint.Handler.Method.PUT -> "put"
    Endpoint.Handler.Method.DELETE -> "delete"
    Endpoint.Handler.Method.PATCH -> "patch"

}
