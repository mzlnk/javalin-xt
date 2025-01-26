package io.mzlnk.javalin.xt.routing.internal.processing.inbound.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import io.mzlnk.javalin.xt.routing.internal.processing.Project
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Endpoint
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Type

/**
 * Factory component that uses KSP API to load the project based on the source code.
 */
internal object ResolverProjectLoader {

    /**
     * Loads the project using information from the KSP resolver.
     *
     * @param resolver resolver to load the project from
     *
     * @return loaded project
     */
    fun load(resolver: Resolver): Project {
        val endpoints = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { classDeclaration ->
                classDeclaration.superTypes.any { superType ->
                    superType.resolve().declaration.qualifiedName?.asString() == io.mzlnk.javalin.xt.routing.Endpoint::class.java.canonicalName
                }
            }
            .map { it.asEndpoint }
            .toList()

        return Project(endpoints = endpoints)
    }

    private val KSClassDeclaration.asEndpoint: Endpoint
        get() = Endpoint(
            type = Type(
                packageName = this.packageName.asString(),
                name = this.simpleName.asString(),
            ),
            handlers = this.getAllFunctions()
                .filter { it.annotations.any { it.isEndpointHandlerMarker } }
                .map {
                    it.asHandler(
                        rootPath = this.annotations.firstOrNull { it.isTypeOf(io.mzlnk.javalin.xt.routing.Path::class.java) }
                            ?.arguments?.first()?.value as? String
                            ?: ""
                    )
                }
                .toList()
        )

    private fun KSFunctionDeclaration.asHandler(rootPath: String): Endpoint.Handler =
        Endpoint.Handler(
            methodName = this.simpleName.asString(),
            method = this.annotations.first { it.isEndpointHandlerMarker }
                .annotationType.resolve().declaration.qualifiedName!!.asString()
                .let {
                    when (it) {
                        io.mzlnk.javalin.xt.routing.Get::class.java.canonicalName -> Endpoint.Handler.Method.GET
                        io.mzlnk.javalin.xt.routing.Post::class.java.canonicalName -> Endpoint.Handler.Method.POST
                        io.mzlnk.javalin.xt.routing.Put::class.java.canonicalName -> Endpoint.Handler.Method.PUT
                        io.mzlnk.javalin.xt.routing.Delete::class.java.canonicalName -> Endpoint.Handler.Method.DELETE
                        io.mzlnk.javalin.xt.routing.Patch::class.java.canonicalName -> Endpoint.Handler.Method.PATCH
                        else -> throw IllegalArgumentException("Unsupported endpoint handler marker: $it")
                    }
                },
            path = rootPath + (
                    this.annotations.firstOrNull { it.isTypeOf(io.mzlnk.javalin.xt.routing.Path::class.java) }
                        ?.arguments?.first()?.value as? String
                        ?: ""
                    ),
            parameters = this.parameters.map { it.asParameter }.toList()
        )

    private val KSValueParameter.asParameter: Endpoint.Handler.Parameter
        get() = when {
            this.annotations.any { it.isTypeOf(io.mzlnk.javalin.xt.routing.PathVariable::class.java) } -> {
                Endpoint.Handler.Parameter.PathVariable(this.annotations.first { it.isTypeOf(io.mzlnk.javalin.xt.routing.PathVariable::class.java) }
                    .arguments.first().value as String)
            }

            this.annotations.any { it.isTypeOf(io.mzlnk.javalin.xt.routing.Header::class.java) } -> {
                Endpoint.Handler.Parameter.Header(
                    name = this.annotations.first { it.isTypeOf(io.mzlnk.javalin.xt.routing.Header::class.java) }.arguments.first().value as String,
                    required = !this.type.resolve().isMarkedNullable
                )
            }

            this.annotations.any { it.isTypeOf(io.mzlnk.javalin.xt.routing.QueryParameter::class.java) } -> {
                Endpoint.Handler.Parameter.QueryParam(
                    name = this.annotations.first { it.isTypeOf(io.mzlnk.javalin.xt.routing.QueryParameter::class.java) }.arguments.first().value as String,
                    required = !this.type.resolve().isMarkedNullable
                )
            }

            this.annotations.any { it.isTypeOf(io.mzlnk.javalin.xt.routing.Body::class.java) } && this.type.resolve().declaration.qualifiedName?.asString() == "kotlin.String" -> {
                Endpoint.Handler.Parameter.Body.AsString
            }

            this.type.resolve().declaration.qualifiedName?.asString() == "io.javalin.http.Context" -> {
                Endpoint.Handler.Parameter.Context
            }

            else -> throw IllegalArgumentException("Unsupported endpoint handler parameter: $this")
        }

    private val KSAnnotation.isEndpointHandlerMarker: Boolean
        get() =
            this.annotationType.resolve().declaration.qualifiedName?.asString() in ENDPOINT_HANDLER_MARKERS

    private val ENDPOINT_HANDLER_MARKERS = listOf(
        io.mzlnk.javalin.xt.routing.Get::class.java.canonicalName,
        io.mzlnk.javalin.xt.routing.Post::class.java.canonicalName,
        io.mzlnk.javalin.xt.routing.Put::class.java.canonicalName,
        io.mzlnk.javalin.xt.routing.Delete::class.java.canonicalName,
        io.mzlnk.javalin.xt.routing.Patch::class.java.canonicalName
    )

}

private fun KSAnnotation.isTypeOf(type: Class<*>): Boolean {
    return this.annotationType.resolve().declaration.qualifiedName?.asString() == type.canonicalName
}