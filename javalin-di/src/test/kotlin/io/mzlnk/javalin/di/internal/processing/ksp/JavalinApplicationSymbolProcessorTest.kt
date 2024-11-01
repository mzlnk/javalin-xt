package io.mzlnk.javalin.di.internal.processing.ksp

import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class JavalinApplicationSymbolProcessorTest {

    @Test
    @Disabled
    fun `sample test`() {
        // given:
        val source = SourceFile.kotlin(
            "test.kt",
            """
            package test

            import io.mzlnk.javalin.di.JavalinApplication
            
            @JavalinApplication
            class TestClass
            """
        )

//        // when:
//        val result = KotlinCompilation().apply {
//            sources = listOf(source)
//
//            symbolProcessorProviders = listOf(JavalinApplicationSymbolProcessorProvider())
//
//            classpaths = System.getProperty("java.class.path")
//                .split(File.pathSeparator)
//                .filter { it.endsWith("projects/javalin-di/javalin-di/build/classes/kotlin/main") }
//                .map { File(it) }
//            messageOutputStream = System.out
//        }.compile()
//
//        // then:
//        assertThat(result.exitCode).isEqualTo(ExitCode.OK)
    }

}