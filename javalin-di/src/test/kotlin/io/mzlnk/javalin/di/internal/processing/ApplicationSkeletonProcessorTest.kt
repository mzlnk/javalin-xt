package io.mzlnk.javalin.di.internal.processing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApplicationSkeletonProcessorTest {

    @Test
    fun `should generate skeleton files`() {
        // given:
        val project = Project(
            rootPackageName = "test",
            classes = emptyList()
        )

        // when:
        val skeleton = ApplicationSkeletonProcessor.process(project)

        // then:
        assertThat(skeleton.generatedFiles).hasSize(2)
    }

}