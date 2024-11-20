package io.mzlnk.javalin.xt.internal.di.processing.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.xt.internal.di.processing.GeneratedFile
import io.mzlnk.javalin.xt.internal.di.processing.GeneratedProject
import io.mzlnk.javalin.xt.internal.di.processing.Project
import io.mzlnk.javalin.xt.internal.di.processing.ksp.ModuleSymbolProcessorProvider
import io.mzlnk.javalin.xt.internal.di.processing.SingletonDefinitionProcessor
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@OptIn(ExperimentalCompilerApi::class)
class ModuleSymbolProcessorTest {

    @Test
    fun `should generate files`() {
        // given:
        val moduleAFile = SourceFile.kotlin(
            name = "moduleA.kt",
            """
            package a.b
            
            import io.mzlnk.javalin.xt.di.Module
            import io.mzlnk.javalin.xt.di.Singleton
            
            class TypeA

            @Module
            class TestModuleA {
                
                @Singleton
                fun provideTypeA(): TypeA = TypeA()
                
            }
            """
        )

        val moduleBFile = SourceFile.kotlin(
            name = "moduleB.kt",
            """
            package c.d
            
            import io.mzlnk.javalin.xt.di.Module
            import io.mzlnk.javalin.xt.di.Singleton

            class TypeB
            
            @Module
            class TestModuleB {
                
                @Singleton
                fun provideTypeB(): TypeB = TypeB()
                
            }
            """
        )

        // when:
        val compilation = KotlinCompilation().apply {
            sources = listOf(moduleAFile, moduleBFile, annotationsFile)

            symbolProcessorProviders = listOf(
                ModuleSymbolProcessorProvider(processor = TestSingletonDefinitionProcessor())
            )

            messageOutputStream = System.out
        }

        val result = compilation.compile()

        // then:
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedFiles = compilation.kspSourcesDir.walk().filter { it.isFile }.toList()
        assertThat(generatedFiles).hasSize(3)

        val providerAFile =
            generatedFiles.find { it.name == "provider-TestModuleA.kt" } ?: fail("TestModuleA provider file not found")
        assertThat(providerAFile.absolutePath).endsWith("kotlin/a/b/provider-TestModuleA.kt")
        assertThat(providerAFile.readText()).contains("TestModuleASingletonDefinitionProvider")

        val providerBFile =
            generatedFiles.find { it.name == "provider-TestModuleB.kt" } ?: fail("TestModuleB provider file not found")
        assertThat(providerBFile.absolutePath).endsWith("kotlin/c/d/provider-TestModuleB.kt")
        assertThat(providerBFile.readText()).contains("TestModuleBSingletonDefinitionProvider")

        val serviceFile = generatedFiles.find { it.name == "e.f.TestService" } ?: fail("service file not found")
        assertThat(serviceFile.absolutePath).endsWith("resources/META-INF/services/e.f.TestService")
        assertThat(serviceFile.readText()).isEqualTo("test-content")
    }

    private companion object {

        private val annotationsFile = SourceFile.kotlin(
            name = "annotations.kt",
            """
            package io.mzlnk.javalin.xt.di

            annotation class Module
            annotation class Singleton
            """
        )

    }

}

private class TestSingletonDefinitionProcessor : SingletonDefinitionProcessor {

    override fun process(project: Project): GeneratedProject {
        return GeneratedProject(
            definitionProviders = project.modules.map { module ->
                GeneratedFile(
                    packageName = module.type.packageName,
                    name = "provider-${module.type.name}",
                    extension = "kt",
                    content =
                    // language=kotlin
                    """
                        package ${module.type.packageName}
                       
                        class ${module.type.name}SingletonDefinitionProvider
                        """.trimIndent()
                )
            },
            definitionProviderService = GeneratedFile(
                packageName = "",
                name = "META-INF/services/e.f.TestService",
                extension = "",
                content = "test-content"
            )
        )
    }

}