package io.mzlnk.javalin.di.internal.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.di.internal.processing.*
import io.mzlnk.javalin.di.internal.processing.Annotation
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ResolverProjectLoaderTest {

    @Test
    fun `should load root package`() {
        // given:
        val file = SourceFile.kotlin(
            name = "app.kt",
            """
            package test.package1.package2
            
            fun main(args: Array<String>) {}
            """
        )

        // when:
        val project = process(file)

        // then:
        assertThat(project).isNotNull
        assertThat(project!!.rootPackageName).isEqualTo("test.package1.package2")
    }

    @Test
    fun `should load class`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            class TestClass
            """
        )

        // when:
        val project = process(appFile, file)

        // then:
        assertThat(project).isNotNull
        assertThat(project!!.classes)
            .hasSize(1)
            .first()
            .usingRecursiveComparison()
            .isEqualTo(
                Clazz(
                    type = Type(packageName = "test", name = "TestClass"),
                )
            )
    }

    @Test
    fun `should load class annotations`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            @Annotation1
            @Annotation2
            class TestClass
            """
        )

        // when:
        val project = process(appFile, annotationsFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        assertThat(clazz.annotations).containsExactlyInAnyOrder(
            Annotation(type = Type(packageName = "test", name = "Annotation1")),
            Annotation(type = Type(packageName = "test", name = "Annotation2"))
        )
    }

    @Test
    fun `should load class methods`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            class TestClass {
            
                fun methodA() {}
                fun methodB() : TypeA = TypeA()
            
            }
            """
        )

        // when:
        val project = process(appFile, typesFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        assertThat(clazz.methods).containsExactlyInAnyOrder(
            Method(
                name = "methodA",
                returnType = Type(packageName = "kotlin", name = "Unit"),
            ),
            Method(
                name = "methodB",
                returnType = Type(packageName = "test", name = "TypeA"),
            )
        )
    }

    @Test
    fun `should load class method annotations`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            class TestClass {
            
                @Annotation1
                @Annotation2
                fun methodA() {}
            }
            """
        )

        // when:
        val project = process(appFile, annotationsFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")
        assertThat(method.annotations).containsExactlyInAnyOrder(
            Annotation(type = Type(packageName = "test", name = "Annotation1")),
            Annotation(type = Type(packageName = "test", name = "Annotation2"))
        )
    }

    @Test
    fun `should load method parameters`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            class TestClass {
            
                fun methodA(param1: TypeA, param2: TypeB) {}
            }
            """
        )

        // when:
        val project = process(appFile, typesFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")

        assertThat(method.parameters).containsExactlyInAnyOrder(
            Method.Parameter(name = "param1", type = Type(packageName = "test", name = "TypeA")),
            Method.Parameter(name = "param2", type = Type(packageName = "test", name = "TypeB"))
        )
    }

    @Test
    fun `should load method parameter annotations`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            class TestClass {
            
                fun methodA(@Annotation1 @Annotation2 param: TypeA) {}
            }
            """
        )

        // when:
        val project = process(appFile, annotationsFile, typesFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")
        val parameter = method.parameters.find { it.name == "param" } ?: fail("Parameter not found")

        assertThat(parameter.annotations).containsExactlyInAnyOrder(
            Annotation(type = Type(packageName = "test", name = "Annotation1")),
            Annotation(type = Type(packageName = "test", name = "Annotation2"))
        )
    }

    @Test
    fun `should load annotation arguments`() {
        // given:
        val file = SourceFile.kotlin(
            name = "class.kt",
            """
            package test
            
            annotation class Annotation(val arg1: String, val arg2: String)

            @Annotation("value1", "value2")
            class TestClass
            """
        )

        // when:
        val project = process(appFile, file)

        // then:
        assertThat(project).isNotNull

        val clazz = project!!.classes.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val annotation = clazz.annotations.find { it.type.name == "Annotation" } ?: fail("Annotation not found")
        assertThat(annotation.arguments).containsExactlyInAnyOrder(
            Annotation.Argument(name = "arg1", value = "value1"),
            Annotation.Argument(name = "arg2", value = "value2")
        )
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

        private val appFile = SourceFile.kotlin(
            name = "app.kt",
            """
            package test
            
            fun main(args: Array<String>) {}
            """
        )

        private val annotationsFile = SourceFile.kotlin(
            name = "annotations.kt",
            """
            package test

            annotation class Annotation1
            annotation class Annotation2
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