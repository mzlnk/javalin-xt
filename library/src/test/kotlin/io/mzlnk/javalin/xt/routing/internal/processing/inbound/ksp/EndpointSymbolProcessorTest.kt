package io.mzlnk.javalin.xt.routing.internal.processing.inbound.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.xt.context.internal.processing.ksp.ModuleSymbolProcessorProvider
import io.mzlnk.javalin.xt.routing.internal.processing.EndpointProcessor
import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject
import io.mzlnk.javalin.xt.routing.internal.processing.Project
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@OptIn(ExperimentalCompilerApi::class)
class EndpointSymbolProcessorTest {

    @Test
    fun `should generate files`() {
        // given:
        val endpoint1File = SourceFile.kotlin(
            name = "moduleA.kt",
            """
            package a.b
            
            import io.mzlnk.javalin.xt.routing.*
            
            class TestEndpoint1 : Endpoint
            """
        )

        val endpoint2File = SourceFile.kotlin(
            name = "moduleB.kt",
            """
            package c.d
            
            import io.mzlnk.javalin.xt.routing.*
            
            class TestEndpoint2 : Endpoint
            """
        )

        // when:
        val compilation = KotlinCompilation().apply {
            sources = listOf(endpoint1File, endpoint2File, routingFile)

            symbolProcessorProviders = listOf(
                EndpointSymbolProcessorProvider(processor = TestEndpointProcessor())
            )

            messageOutputStream = System.out
        }

        val result = compilation.compile()

        // then:
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedFiles = compilation.kspSourcesDir.walk().filter { it.isFile }.toList()
        assertThat(generatedFiles).hasSize(3)

        val adapter1File = generatedFiles
            .find { it.name == "adapter-TestEndpoint1.kt" }
            ?: fail("TestEndpoint1 adapter file not found")

        assertThat(adapter1File.absolutePath).endsWith("kotlin/a/b/adapter-TestEndpoint1.kt")
        assertThat(adapter1File.readText()).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |class TestEndpoint1Adapter
            """.trimMargin()
        )

        val adapter2File = generatedFiles
            .find { it.name == "adapter-TestEndpoint2.kt" }
            ?: fail("TestEndpoint2 adapter file not found")

        assertThat(adapter2File.absolutePath).endsWith("kotlin/c/d/adapter-TestEndpoint2.kt")
        assertThat(adapter2File.readText()).isEqualTo(
            // language=kotlin
            """
            |package c.d
            |
            |class TestEndpoint2Adapter
            """.trimMargin()
        )

        val serviceFile = generatedFiles.find { it.name == "test-service-file" } ?: fail("service file not found")
        assertThat(serviceFile.absolutePath).endsWith("resources/META-INF/services/test-service-file")
        assertThat(serviceFile.readText()).isEqualTo("test-content")
    }

    private companion object {

        private val routingFile = SourceFile.kotlin(
            name = "routing.kt",
            """
            package io.mzlnk.javalin.xt.routing
            
            interface Endpoint
            """
        )

    }

}

private class TestEndpointProcessor : EndpointProcessor {

    override fun process(project: Project): GeneratedProject {
        return GeneratedProject(
            adapters = project.endpoints.map {
                GeneratedProject.GeneratedFile(
                    name = "adapter-${it.type.name}",
                    extension = "kt",
                    packageName = it.type.packageName,
                    content =
                        """
                        package ${it.type.packageName}
                        
                        class ${it.type.name}Adapter
                        """.trimIndent()
                )
            },
            service = GeneratedProject.GeneratedFile(
                name = "META-INF/services/test-service-file",
                extension = "",
                packageName = "",
                content = "test-content"
            )
        )
    }
}