package io.mzlnk.javalin.xt.context.internal.processing.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.xt.context.internal.processing.Project
import io.mzlnk.javalin.xt.context.internal.processing.Singleton
import io.mzlnk.javalin.xt.context.internal.processing.Type
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
        assertThat(module.type).isEqualTo(Type(packageName = "test", name = "TestModule", nullable = false))
        assertThat(module.singletons).hasSize(2)
    }

    @Test
    fun `should load details of singleton definition with singleton dependencies`() {
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
        val singleton = module.singletons.firstOrNull { it.methodName == "provideTypeA" } ?: fail("Singleton not found")

        assertThat(singleton.methodName).isEqualTo("provideTypeA")
        assertThat(singleton.type).isEqualTo(Type(packageName = "test", name = "TypeA", nullable = false))
        assertThat(singleton.dependencies).hasSize(1)

        val parameter = singleton.dependencies.firstOrNull() ?: fail("Singleton parameter not found")
        assertThat(parameter.type).isEqualTo(Type(packageName = "test", name = "TypeB", nullable = false))
    }

    @Test
    fun `should load details of singleton definition with property dependencies`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            import io.mzlnk.javalin.xt.context.Property
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeC(
                    @Property("propertyA") propertyA: TypeA,
                    @Property("propertyB") propertyB: TypeB?
                ): TypeC = TypeC()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.firstOrNull { it.methodName == "provideTypeC" } ?: fail("Singleton not found")

        assertThat(singleton.methodName).isEqualTo("provideTypeC")
        assertThat(singleton.type).isEqualTo(Type(packageName = "test", name = "TypeC", nullable = false))
        assertThat(singleton.dependencies).hasSize(2)

        val dependencyA = singleton.dependencies.firstOrNull { it.type.name == "TypeA" } ?: fail("Dependency not found")
        dependencyA as Singleton.Dependency.Property
        assertThat(dependencyA.type).isEqualTo(Type(packageName = "test", name = "TypeA", nullable = false))
        assertThat(dependencyA.key).isEqualTo("propertyA")
        assertThat(dependencyA.required).isEqualTo(true)

        val dependencyB = singleton.dependencies.firstOrNull { it.type.name == "TypeB" } ?: fail("Dependency not found")
        dependencyB as Singleton.Dependency.Property
        assertThat(dependencyB.type).isEqualTo(Type(packageName = "test", name = "TypeB", nullable = true))
        assertThat(dependencyB.key).isEqualTo("propertyB")
        assertThat(dependencyB.required).isEqualTo(false)
    }

    @Test
    fun `should load details of singleton with list dependency`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.*
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeA(typesB: List<TypeB>): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.find { it.methodName == "provideTypeA" } ?: fail("Singleton not found")

        assertThat(singleton.dependencies[0]).isEqualTo(
            Singleton.Dependency.Singleton.List(
                type = Type(
                    packageName = "kotlin.collections",
                    name = "List",
                    typeParameters = listOf(Type(packageName = "test", name = "TypeB", nullable = false)),
                    nullable = false,
                )
            )
        )
    }

    @Test
    fun `should load details of singleton with named dependency`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.*
            
            @Module
            class TestModule {
                
                @Singleton
                fun provideTypeA(@Named("B") typeB: TypeB): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.find { it.methodName == "provideTypeA" } ?: fail("Singleton not found")

        assertThat(singleton.dependencies[0]).isEqualTo(
            Singleton.Dependency.Singleton.Singular(
                type = Type(packageName = "test", name = "TypeB", nullable = false),
                name = "B"
            )
        )
    }

    @Test
    fun `should load details of singleton definition with conditions`() {
        // given:
        val moduleFile = SourceFile.kotlin(
            name = "module.kt",
            """
            package test
            
            import io.mzlnk.javalin.xt.context.Conditional
            import io.mzlnk.javalin.xt.context.Module
            import io.mzlnk.javalin.xt.context.Singleton
            import io.mzlnk.javalin.xt.context.Property
            
            
            @Module
            class TestModule {
                
                @Singleton
                @Conditional.OnProperty(property = "property", havingValue = "value")
                fun provideTypeA(): TypeA = TypeA()
                
            }
            """
        )

        // when:
        val project = process(annotationsFile, typesFile, moduleFile)

        // then:
        val module = project?.modules?.firstOrNull() ?: fail("Module not found")
        val singleton = module.singletons.firstOrNull { it.methodName == "provideTypeA" } ?: fail("Singleton not found")

        assertThat(singleton.conditionals).containsExactly(
            Singleton.Conditional.OnProperty(
                key = "property",
                havingValue = "value"
            )
        )
    }

    @Test
    fun `should load details of singleton with generic type details`() {
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

        assertThat(singleton.methodName).isEqualTo("provideTypeEA")
        assertThat(singleton.type).isEqualTo(
            Type(
                packageName = "test",
                name = "TypeE",
                nullable = false,
                typeParameters = listOf(Type(packageName = "test", name = "TypeA", nullable = false))
            )
        )
        assertThat(singleton.dependencies).hasSize(1)

        val parameter = singleton.dependencies.firstOrNull() ?: fail("Singleton parameter not found")
        assertThat(parameter.type).isEqualTo(
            Type(
                packageName = "test",
                name = "TypeE",
                nullable = false,
                typeParameters = listOf(Type(packageName = "test", name = "TypeB", nullable = false))
            )
        )
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
            annotation class Property(val key: String)
            annotation class Conditional {
                annotation class OnProperty(val property: String, val havingValue: String)
            }
            annotation class Named(val name: String)
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