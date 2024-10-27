package io.mzlnk.javalin.di.internal.processing.service

import io.mzlnk.javalin.di.internal.processing.Project
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServiceFileGeneratorTest {

    @Test
    fun `should generate service file`() {
        // given:
        val rootPackageName = "test.package1.package2"

        // and:
        val project = Project(
            rootPackageName = rootPackageName,
            classes = emptyList()
        )

        // when:
        val generatedFile = ServiceFileGenerator.generate(project)

        // then:
        assertThat(generatedFile.name).isEqualTo("META-INF/services/io.mzlnk.javalin.di.spi.JavalinRunnerProvider")
        assertThat(generatedFile.packageName).isNull()
        assertThat(generatedFile.extension).isEqualTo("")
        assertThat(generatedFile.content).isEqualTo("${project.rootPackageName}.JavalinRunnerProviderImpl")
    }

}