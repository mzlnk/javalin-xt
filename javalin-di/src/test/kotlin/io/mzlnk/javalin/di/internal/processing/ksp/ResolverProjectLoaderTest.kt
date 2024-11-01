package io.mzlnk.javalin.di.internal.processing.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.mzlnk.javalin.di.internal.ksp.ResolverProjectLoader
import io.mzlnk.javalin.di.internal.processing.Clazz
import io.mzlnk.javalin.di.internal.processing.Method
import io.mzlnk.javalin.di.internal.processing.Project
import io.mzlnk.javalin.di.internal.processing.Type
import io.mzlnk.javalin.di.internal.processing.Annotation
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ResolverProjectLoaderTest {

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
        assertThat(project!!.modules)
            .hasSize(1)
            .first()
            .usingRecursiveComparison()
            .isEqualTo(
                _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Clazz(
                    type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                        packageName = "test",
                        name = "TestClass"
                    ),
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        assertThat(clazz.annotations).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation1"
                )
            ),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation2"
                )
            )
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        assertThat(clazz.methods).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Method(
                name = "methodA",
                returnType = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "kotlin",
                    name = "Unit"
                ),
            ),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Method(
                name = "methodB",
                returnType = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "TypeA"
                ),
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")
        assertThat(method.annotations).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation1"
                )
            ),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation2"
                )
            )
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")

        assertThat(method.parameters).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Method.Parameter(name = "param1", type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                packageName = "test",
                name = "TypeA"
            )
            ),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Method.Parameter(name = "param2", type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                packageName = "test",
                name = "TypeB"
            )
            )
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val method = clazz.methods.find { it.name == "methodA" } ?: fail("Method not found")
        val parameter = method.parameters.find { it.name == "param" } ?: fail("Parameter not found")

        assertThat(parameter.annotations).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation1"
                )
            ),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation(
                type = _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Type(
                    packageName = "test",
                    name = "Annotation2"
                )
            )
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
        Assertions.assertThat(project).isNotNull

        val clazz = project!!.modules.find { it.type.name == "TestClass" } ?: fail("Class not found")
        val annotation = clazz.annotations.find { it.type.name == "Annotation" } ?: fail("Annotation not found")
        assertThat(annotation.arguments).containsExactlyInAnyOrder(
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation.Argument(name = "arg1", value = "value1"),
            _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Annotation.Argument(name = "arg2", value = "value2")
        )
    }

    @OptIn(ExperimentalCompilerApi::class)
    private fun process(vararg sources: SourceFile): _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Project? {
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

        Assertions.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

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

    private var _generatedProject: _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Project? = null

    val generatedProject: _root_ide_package_.io.mzlnk.javalin.di.internal.processing.Project? get() = _generatedProject

    override fun process(resolver: Resolver): List<KSAnnotated> {
        _generatedProject = ResolverProjectLoader.load(resolver)
        return emptyList()
    }
}