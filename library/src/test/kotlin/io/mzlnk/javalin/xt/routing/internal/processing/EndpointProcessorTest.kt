package io.mzlnk.javalin.xt.routing.internal.processing

import io.mzlnk.javalin.xt.routing.internal.processing.Project.Endpoint
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Type
import io.mzlnk.javalin.xt.utils.testCase
import io.mzlnk.javalin.xt.utils.testCases
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class EndpointProcessorTest {

    @Test
    fun `should generate service file`() {
        // given
        val project = Project(endpoints = emptyList())

        // when
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then
        assertThat(generatedProject.service?.name).isEqualTo("META-INF/services/io.mzlnk.javalin.xt.routing.generated.EndpointAdapter\$Factory")
        assertThat(generatedProject.service?.extension).isEqualTo("")
        assertThat(generatedProject.service?.packageName).isNull()
    }

    @Test
    fun `should generate service file when there is no endpoints`() {
        // given
        val project = Project(
            endpoints = emptyList()
        )

        // when
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then
        assertThat(generatedProject.service?.content).isBlank()
    }

    @Test
    fun `should generate service file when there is single endpoint`() {
        // given
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = emptyList()
                )
            )
        )

        // when
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then
        assertThat(generatedProject.service?.content).isEqualTo(
            """
            a.b.TestEndpointAdapter${'$'}Factory
            """.trimIndent()
        )
    }

    @Test
    fun `should generate service file when there are multiple endpoints`() {
        // given
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint1"),
                    handlers = emptyList()
                ),
                Endpoint(
                    type = Type(packageName = "c.d", name = "TestEndpoint2"),
                    handlers = emptyList()
                )
            )
        )

        // when
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then
        assertThat(generatedProject.service?.content).isEqualTo(
            """
            a.b.TestEndpoint1Adapter${'$'}Factory
            c.d.TestEndpoint2Adapter${'$'}Factory
            """.trimIndent()
        )
    }

    @Test
    fun `should generate adapter file per endpoint`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint1"),
                    handlers = emptyList()
                ),
                Endpoint(
                    type = Type(packageName = "c.d", name = "TestEndpoint2"),
                    handlers = emptyList()
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters).hasSize(2)
    }

    @Test
    fun `should generate adapter file with file details`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = emptyList()
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].name).isEqualTo("TestEndpointAdapter")
        assertThat(generatedProject.adapters[0].extension).isEqualTo("kt")
        assertThat(generatedProject.adapters[0].packageName).isEqualTo("a.b")
        assertThat(generatedProject.adapters[0].content).isNotBlank()
    }

    @Test
    fun `should generate adapter file when endpoint has no handlers`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = emptyList()
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when endpoint has single handler`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = emptyList()
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet() }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when endpoint has multiple handlers`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path1",
                            method = Endpoint.Handler.Method.GET,
                            parameters = emptyList()
                        ),
                        Endpoint.Handler(
                            methodName = "handlePost",
                            path = "/path2",
                            method = Endpoint.Handler.Method.POST,
                            parameters = emptyList()
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path1") { ctx -> endpoint.handleGet() }
            |    log.info("Registered endpoint: GET /path1")
            |    javalin.post("/path2") { ctx -> endpoint.handlePost() }
            |    log.info("Registered endpoint: POST /path2")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @ParameterizedTest
    @MethodSource("params for generate adapter file when endpoint handler is for HTTP methods")
    fun `should generate adapter file when endpoint handler is for HTTP method`(
        method: Endpoint.Handler.Method,
        expectedHandlerCode: String,
        expectedLogCode: String
    ) {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handle",
                            path = "/path",
                            method = method,
                            parameters = emptyList()
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    $expectedHandlerCode
            |    $expectedLogCode
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has context parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.Context
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet(ctx) }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has path variable parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path/{id}",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.PathVariable("id")
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path/{id}") { ctx -> endpoint.handleGet(ctx.pathParam("id")) }
            |    log.info("Registered endpoint: GET /path/{id}")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has required header parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.Header("header-1", required = true)
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet(ctx.header("header-1")!!) }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has optional header parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.Header("header-1", required = false)
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet(ctx.header("header-1")) }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has required query parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.QueryParam("param-1", required = true)
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet(ctx.queryParam("param-1")!!) }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has optional query parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handleGet",
                            path = "/path",
                            method = Endpoint.Handler.Method.GET,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.QueryParam("param-1", required = false)
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.get("/path") { ctx -> endpoint.handleGet(ctx.queryParam("param-1")) }
            |    log.info("Registered endpoint: GET /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    @Test
    fun `should generate adapter file when handler has body as string parameter`() {
        // given:
        val project = Project(
            endpoints = listOf(
                Endpoint(
                    type = Type(packageName = "a.b", name = "TestEndpoint"),
                    handlers = listOf(
                        Endpoint.Handler(
                            methodName = "handlePost",
                            path = "/path",
                            method = Endpoint.Handler.Method.POST,
                            parameters = listOf(
                                Endpoint.Handler.Parameter.Body.AsString
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = EndpointProcessor.createForKotlin().process(project)

        // then:
        assertThat(generatedProject.adapters[0].content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.javalin.Javalin
            |import io.mzlnk.javalin.xt.routing.Endpoint
            |import io.mzlnk.javalin.xt.routing.generated.EndpointAdapter
            |import java.lang.Class
            |import org.slf4j.Logger
            |import org.slf4j.LoggerFactory
            |
            |public class TestEndpointAdapter(
            |  private val endpoint: TestEndpoint,
            |) : EndpointAdapter {
            |  public val log: Logger = LoggerFactory.getLogger(TestEndpoint::class.java)
            |
            |  override fun apply(javalin: Javalin) {
            |    javalin.post("/path") { ctx -> endpoint.handlePost(ctx.body()) }
            |    log.info("Registered endpoint: POST /path")
            |  }
            |
            |  public class Factory : EndpointAdapter.Factory {
            |    override val supportedEndpoint: Class<out Endpoint> = TestEndpoint::class.java
            |
            |    override fun create(endpoint: Endpoint): EndpointAdapter = TestEndpointAdapter(endpoint as TestEndpoint)
            |  }
            |}
            """.trimMargin()
        )
    }

    private companion object {

        @JvmStatic
        fun `params for generate adapter file when endpoint handler is for HTTP methods`() = testCases(
            // @formatter:off
            //      | method                         | expected handler code                                      | expected log code                                   |
            testCase( Endpoint.Handler.Method.GET    , """javalin.get("/path") { ctx -> endpoint.handle() }"""    , """log.info("Registered endpoint: GET /path")"""    ),
            testCase( Endpoint.Handler.Method.POST   , """javalin.post("/path") { ctx -> endpoint.handle() }"""   , """log.info("Registered endpoint: POST /path")"""   ),
            testCase( Endpoint.Handler.Method.PUT    , """javalin.put("/path") { ctx -> endpoint.handle() }"""    , """log.info("Registered endpoint: PUT /path")"""    ),
            testCase( Endpoint.Handler.Method.DELETE , """javalin.delete("/path") { ctx -> endpoint.handle() }""" , """log.info("Registered endpoint: DELETE /path")""" ),
            testCase( Endpoint.Handler.Method.PATCH  , """javalin.patch("/path") { ctx -> endpoint.handle() }"""  , """log.info("Registered endpoint: PATCH /path")"""  ),
            // @formatter:on
        )
    }

}