package io.mzlnk.javalin.xt.internal.properties

import io.mzlnk.javalin.xt.internal.common.EnvironmentVariablesProvider
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.PropertyNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URL

class ApplicationPropertiesTest {

    @TempDir
    lateinit var tempDir: File

    private val basePropertiesFile: File by lazy { File(tempDir, "application.yml") }
    private val profilePropertiesFile: File by lazy { File(tempDir, "application-profile.yml") }

    private val factory: ApplicationPropertiesFactory by lazy {
        ApplicationPropertiesFactory(
            fileResolver = TestApplicationPropertiesFileResolver(baseDir = tempDir),
            environmentVariablesProvider = TestEnvironmentVariablesProvider(/* no env vars */)
        )
    }

    @Test
    fun `should create properties when values are only in base properties file`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1
            |property2: value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties.getOrNull("property1")).isEqualTo(StringProperty("value1"))
        assertThat(properties.getOrNull("property2")).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should create properties when values are only in profile properties file`() {
        // given:
        profilePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1
            |property2: value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create(config(profile = "profile" ))

        // expect:
        assertThat(properties.getOrNull("property1")).isEqualTo(StringProperty("value1"))
        assertThat(properties.getOrNull("property2")).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should create properties when values are in both base and profile properties files`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1-base
            |property2: value2-base
            """.trimMargin()
        )

        profilePropertiesFile.writeText(
            // language=yaml
            """
            |property3: value3-profile
            |property4: value4-profile
            """.trimMargin()
        )

        // when:
        val properties = factory.create(config(profile = "profile" ))

        // expect:
        assertThat(properties.getOrNull("property1")).isEqualTo(StringProperty("value1-base"))
        assertThat(properties.getOrNull("property2")).isEqualTo(StringProperty("value2-base"))
        assertThat(properties.getOrNull("property3")).isEqualTo(StringProperty("value3-profile"))
        assertThat(properties.getOrNull("property4")).isEqualTo(StringProperty("value4-profile"))
    }

    @Test
    fun `should create properties with values from base properties file when value in profile file does not exist`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1-base
            |property2: value2-base
            """.trimMargin()
        )

        // and:
        profilePropertiesFile.writeText(
            // language=yaml
            """
            |property2: value2-profile
            """.trimMargin()
        )

        // when:
        val properties = factory.create(config(profile = "profile"))

        // expect:
        assertThat(properties.getOrNull("property1")).isEqualTo(StringProperty("value1-base"))
        assertThat(properties.getOrNull("property2")).isEqualTo(StringProperty("value2-profile"))
    }

    @Test
    fun `should get string property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(StringProperty("value1"))
    }

    @Test
    fun ` should get number property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 1
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(NumberProperty(1))
    }

    @Test
    fun `should get boolean property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: true
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(BooleanProperty(true))
    }

    @Test
    fun `should get object property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  property2: value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(ObjectProperty)
    }

    @Test
    fun `should get string list property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - value1
            |  - value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(StringListProperty(listOf("value1", "value2")))
    }

    @Test
    fun `should get number list property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - 1
            |  - 2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(NumberListProperty(listOf(1, 2)))
    }

    @Test
    fun `should get boolean list property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - true
            |  - false
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(BooleanListProperty(listOf(true, false)))
    }

    @Test
    fun `should get object list property when list items are objects`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - property2: value2
            |  - property3: value3
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(ObjectListProperty)
    }

    @Test
    fun `should get object list property when list items are different types`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - "value1"
            |  - 1
            |  - true
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(ObjectListProperty)
    }

    @Test
    fun `should get nested property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  property2: value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1.property2"]).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should get item from list property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - value1
            |  - value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1[0]"]).isEqualTo(StringProperty("value1"))
        assertThat(properties["property1[1]"]).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should get nested value in list property`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: 
            |  - property2: value2
            """.trimMargin()
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1[0].property2"]).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should throw exception when get property that does not exist`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: value1
            """.trimMargin()
        )

        // when:
        val exception = assertThatThrownBy {
            factory.create()["property2"]
        }

        // expect:
        exception.isInstanceOf(PropertyNotFoundException::class.java)
        exception.hasMessage("Property with key `property2` not found.")
    }

    @Test
    fun `should resolve environment variables when resolving is enabled`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: ${'$'}{ENV_VAR1}
            |property2:
            |  - ${'$'}{ENV_VAR2}
            """.trimMargin()
        )

        // and:
        val factory = ApplicationPropertiesFactory(
            fileResolver = TestApplicationPropertiesFileResolver(baseDir = tempDir),
            environmentVariablesProvider = TestEnvironmentVariablesProvider(
                "ENV_VAR1" to "value1",
                "ENV_VAR2" to "value2"
            )
        )

        // when:
        val properties = factory.create()

        // expect:
        assertThat(properties["property1"]).isEqualTo(StringProperty("value1"))
        assertThat(properties["property2[0]"]).isEqualTo(StringProperty("value2"))
    }

    @Test
    fun `should not resolve environment variables when resolving is disabled`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: ${'$'}{ENV_VAR1}
            |property2:
            |  - ${'$'}{ENV_VAR2}
            """.trimMargin()
        )

        // and:
        val factory = ApplicationPropertiesFactory(
            fileResolver = TestApplicationPropertiesFileResolver(baseDir = tempDir),
            environmentVariablesProvider = TestEnvironmentVariablesProvider(
                "ENV_VAR1" to "value1",
                "ENV_VAR2" to "value2"
            )
        )

        // when:
        val properties = factory.create(config(resolveEnvironmentVariables = false))

        // expect:
        assertThat(properties["property1"]).isEqualTo(StringProperty("\${ENV_VAR1}"))
        assertThat(properties["property2[0]"]).isEqualTo(StringProperty("\${ENV_VAR2}"))
    }

    @Test
    fun `should throw exception when resolving environment variables is enabled and environment variable is not found`() {
        // given:
        basePropertiesFile.writeText(
            // language=yaml
            """
            |property1: ${'$'}{ENV_VAR1}
            """.trimMargin()
        )

        // and:
        val factory = ApplicationPropertiesFactory(
            fileResolver = TestApplicationPropertiesFileResolver(baseDir = tempDir),
            environmentVariablesProvider = TestEnvironmentVariablesProvider(/* no env vars */)
        )

        // when:
        val exception = assertThatThrownBy {
            factory.create()
        }

        // expect:
        exception.isInstanceOf(IllegalArgumentException::class.java)
        exception.hasMessage("Environment variable `ENV_VAR1` not found")
    }

    @Test
    fun `should throw exception when profile properties file is not found`() {
        // when:
        val exception = assertThatThrownBy {
            factory.create(config(profile = "profile"))
        }

        // expect:
        exception.isInstanceOf(IllegalArgumentException::class.java)
        exception.hasMessage("Application properties file for profile `profile` not found.")
    }


}

private fun config(
    resolveEnvironmentVariables: Boolean = true,
    profile: String? = null
): ApplicationProperties.Configuration {
    return ApplicationProperties.Configuration(
        resolveEnvironmentVariables = resolveEnvironmentVariables,
        profile = profile
    )
}

/**
 * Stub implementation of [ApplicationPropertiesFileResolver] that resolves files from a given directory.
 */
private class TestApplicationPropertiesFileResolver(private val baseDir: File) : ApplicationPropertiesFileResolver {

    override fun resolve(fileName: String): URL? = File(baseDir, fileName).takeIf { it.exists() }?.toURI()?.toURL()

}

/**
 * Stub implementation of [EnvironmentVariablesProvider] that provides values from a given map.
 */
private class TestEnvironmentVariablesProvider(vararg values: Pair<String, String>) : EnvironmentVariablesProvider {

    private val valuesByKey = values.toMap()

    override fun get(key: String): String? = valuesByKey[key]

}