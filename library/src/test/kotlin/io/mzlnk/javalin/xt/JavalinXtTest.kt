package io.mzlnk.javalin.xt

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.context.JavalinContext
import io.mzlnk.javalin.xt.internal.JavalinXtProxy
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class JavalinXtTest {

    private val context: JavalinContext = mock()
    private val properties: ApplicationProperties = mock()

    @Test
    fun `should get application properties when javalin-xt is enabled`() {
        // given:
        val app = Javalin.create()
            .testXt() // simulate xt() call

        // when:
        val properties = app.properties

        // then:
        assertThat(properties).isEqualTo(this.properties)
    }

    @Test
    fun `should throw exception when get application properties with javalin-xt disabled`() {
        // given:
        val app = Javalin.create()

        // when:
        val exception = assertThatThrownBy {
            app.properties
        }

        // then:
        exception.isInstanceOf(IllegalStateException::class.java)
        exception.hasMessage("This is javalin-xt feature which has not been enabled. Call Javalin.xt() first.")
    }

    /*
     * This is a helper function to simulate xt() call on Javalin instance.
     */
    private fun Javalin.testXt(): Javalin {
        return JavalinXtProxy(this, this@JavalinXtTest.context, this@JavalinXtTest.properties)
    }

}

