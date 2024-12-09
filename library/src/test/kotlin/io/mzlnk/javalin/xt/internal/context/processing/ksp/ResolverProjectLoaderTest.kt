package io.mzlnk.javalin.xt.internal.context.processing.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.xt.internal.context.processing.Project
import io.mzlnk.javalin.xt.internal.context.processing.Type
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ResolverProjectLoaderTest {

    @Test
    fun `should load project with no modules`() {
        // given:
        // no module files

        // when:
        val project = process(annotationsFile, typesFile)

        // then:
        assertThat(project).isNotNull
        assertThat(project?.modules).isEmpty()
    }

    @Test
    fun `should load project with single module`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeA(): TypeA = TypeA()
                
                @Singleton
                fun provideTypeB(): TypeB = TypeB()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        assertThat(project).isNotNull
        assertThat(project?.modules).hasSize(1)
    }

    @Test
    fun `should load project with multiple modules`() {
        // given:
        val moduleAFile = SourceFile.kotlin(
            name = "moduleA.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
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
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
            @Module
            class TestModuleB {
                
                @Singleton
                fun provideTypeB(): TypeB = TypeB()
                
                @Singleton
                fun provideTypeD(): TypeD = TypeD()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleAFile, moduleBFile)

        // then:
        assertThat(project).isNotNull
        assertThat(project?.modules).hasSize(2)
    }

    @Test
    fun `should load module details`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeA(): TypeA = TypeA()
                
                @Singleton
                fun provideTypeB(): TypeB = TypeB()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        assertThat(module.type).isEqualTo(Type(packageName = "test", name = "TestModule"))
        assertThat(module.singletons).hasSize(2)
    }

    @Test
    fun `should load singleton details`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeA(depB: TypeB): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.firstOrNull { it.name == "provideTypeA" } ?: fail("Singleton not found")

        assertThat(singleton.name).isEqualTo("provideTypeA")
        assertThat(singleton.returnType).isEqualTo(Type(packageName = "test", name = "TypeA"))
        assertThat(singleton.parameters).hasSize(1)

        val parameter = singleton.parameters.firstOrNull() ?: fail("Singleton parameter not found")
        assertThat(parameter.type).isEqualTo(Type(packageName = "test", name = "TypeB"))
        assertThat(parameter.name).isEqualTo("depB")
    }

    @Test
    fun `should load singleton with generic type details`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeEA(depEB: TypeE<TypeB>): TypeE<TypeA> = TypeE()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.firstOrNull() ?: fail("Singleton not found")

        assertThat(singleton.name).isEqualTo("provideTypeEA")
        assertThat(singleton.returnType).isEqualTo(
            Type(
                packageName = "test",
                name = "TypeE",
                typeParameters = listOf(Type(packageName = "test", name = "TypeA"))
            )
        )
        assertThat(singleton.parameters).hasSize(1)

        val parameter = singleton.parameters.firstOrNull() ?: fail("Singleton parameter not found")
        assertThat(parameter.type).isEqualTo(
            Type(
                packageName = "test",
                name = "TypeE",
                typeParameters = listOf(Type(packageName = "test", name = "TypeB"))
            )
        )
        assertThat(parameter.name).isEqualTo("depEB")
    }

    @Test
    fun `should not load class not annotated with @Module`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.di.Singleton
            
            class TestModule {
                
                @Singleton
                fun provideTypeA(): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        assertThat(project).isNotNull
        assertThat(project?.modules).isEmpty()
    }

    @Test
    fun `should not load method not annotated with @Singleton`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            
            @Module
            class TestModule {
                
                fun provideTypeA(): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        assertThat(project).isNotNull

        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        assertThat(module.singletons).isEmpty()
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

        private val annotationsFile = SourceFile.kotlin(
            name = "annotations.kt",
            """
            package io.mzlnk.javalin.xt.context

            annotation class Module
            annotation class Singleton
            """
        )

        private val typesFile = SourceFile.kotlin(
            name = "types.kt",
            """
            package test
            
            class TypeA
            class TypeB
            class TypeC
            class TypeD
            
            class TypeE<T>
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