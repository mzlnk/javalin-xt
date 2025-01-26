package io.mzlnk.javalin.xt.routing.internal.processing.inbound.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.xt.routing.internal.processing.Project
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Endpoint.Handler.Parameter
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test

class ResolverProjectLoaderTest {

    @Test
    fun `should load project when there is endpoint defined`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        assertThat(project).isEqualTo(
            Project(
                endpoints = listOf(
                    Project.Endpoint(
                        type = Project.Type("test", "TestEndpoint"),
                        handlers = emptyList()
                    )
                )
            )
        )
    }

    @Test
    fun `should load project when there is endpoint defined with single handler`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod() {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        assertThat(project).isEqualTo(
            Project(
                endpoints = listOf(
                    Project.Endpoint(
                        type = Project.Type("test", "TestEndpoint"),
                        handlers = listOf(
                            Project.Endpoint.Handler(
                                methodName = "testMethod",
                                method = Project.Endpoint.Handler.Method.GET,
                                path = "/test",
                                parameters = emptyList()
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `should load project when there is endpoint defined with multiple handlers`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test1")
                    fun testMethod1() {
                        
                    }
                    
                    @Post
                    @Path("/test2")
                    fun testMethod2() {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        assertThat(project).isEqualTo(
            Project(
                endpoints = listOf(
                    Project.Endpoint(
                        type = Project.Type("test", "TestEndpoint"),
                        handlers = listOf(
                            Project.Endpoint.Handler(
                                methodName = "testMethod1",
                                method = Project.Endpoint.Handler.Method.GET,
                                path = "/test1",
                                parameters = emptyList()
                            ),
                            Project.Endpoint.Handler(
                                methodName = "testMethod2",
                                method = Project.Endpoint.Handler.Method.POST,
                                path = "/test2",
                                parameters = emptyList()
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `should load endpoint handler parameter related to header value marked as required`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod(@Header("header") header: String) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.Header(name = "header", required = true))
    }

    @Test
    fun `should load endpoint handler parameter related to header value marked as optional`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod(@Header("header") header: String?) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.Header(name = "header", required = false))
    }

    @Test
    fun `should load endpoint handler parameter related to query parameter marked as required`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod(@QueryParameter("param") param: String) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.QueryParam(name = "param", required = true))
    }

    @Test
    fun `should load endpoint handler parameter related to query parameter marked as optional`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod(@QueryParameter("param") param: String?) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.QueryParam(name = "param", required = false))
    }

    @Test
    fun `should load endpoint handler parameter related to path variable`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test/:param")
                    fun testMethod(@PathVariable("param") param: String) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.PathVariable(name = "param"))
    }

    @Test
    fun `should load endpoint handler parameter related to body marked as string`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Post
                    @Path("/test")
                    fun testMethod(@Body body: String) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.Body.AsString)
    }

    @Test
    fun `should load endpoint handler parameter related to javalin context`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.javalin.http.*
            import io.mzlnk.javalin.xt.routing.*

            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test")
                    fun testMethod(ctx: Context) {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.parameters).contains(Parameter.Context)
    }

    @Test
    fun `should load endpoint handler path when path is defined at endpoint class level`() {
        // given:
        val endpointFile = SourceFile.kotlin(
            "endpoint.kt",
            """
            package test

            import io.mzlnk.javalin.xt.routing.*

            @Path("/test1")
            class TestEndpoint : Endpoint {
                    
                    @Get
                    @Path("/test2")
                    fun testMethod() {
                        
                    }
            }
            """
        )

        // when:
        val project = process(routingFile, javalinFile, endpointFile)

        // then:
        val handler = project!!
            .endpoints.find { it.type.name == "TestEndpoint" }!!
            .handlers.find { it.methodName == "testMethod" }!!

        assertThat(handler.path).isEqualTo("/test1/test2")
    }

    @OptIn(ExperimentalCompilerApi::class)
    private fun process(vararg sources: SourceFile): Project? {
        val verifier = ResolverProjectLoaderVerifier()

        val result = KotlinCompilation().apply {
            this.sources = sources.toList()

            symbolProcessorProviders = listOf(
                object : SymbolProcessorProvider {
                    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = verifier
                }
            )

            classpaths = emptyList()
            messageOutputStream = System.out
        }.compile()

        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        return verifier.generatedProject
    }

    private companion object {

        private val routingFile = SourceFile.kotlin(
            name = "annotations.kt",
            """
            package io.mzlnk.javalin.xt.routing

            annotation class Get
            annotation class Post
            annotation class Put
            annotation class Delete
            annotation class Patch
            annotation class Path(val value: String)
            annotation class QueryParameter(val value: String)
            annotation class Header(val value: String)
            annotation class PathVariable(val value: String)
            annotation class Body

            interface Endpoint
            """
        )

        private val javalinFile = SourceFile.kotlin(
            name = "javalin.kt",
            """
            package io.javalin.http
            
            class Context
            """
        )

    }

}

private class ResolverProjectLoaderVerifier : SymbolProcessor {

    private var _generatedProject: Project? = null

    val generatedProject: Project? get() = _generatedProject

    override fun process(resolver: Resolver): List<KSAnnotated> {
        _generatedProject = ResolverProjectLoader.load(resolver)
        return emptyList()
    }
}