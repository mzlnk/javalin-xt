package io.mzlnk.javalin.xt.e2e

import io.cucumber.docstring.DocString
import io.cucumber.java.After
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mzlnk.javalin.xt.e2e.utils.prependLineNumbers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.assertj.core.api.Assertions.assertThat
import java.nio.file.Path
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class E2EStepDefinitions {

    private lateinit var project: Project
    private lateinit var application: Application

    private lateinit var httpRequestBuilder: HttpRequest.Builder
    private lateinit var httpRespone: HttpResponse

    @Given("project is set up")
    fun `gradle project is set up`() {
        this.project = Project.initialize()
    }

    @Given("class {word} is created with content")
    fun `class classQualifiedName is created with content`(classQualifiedName: String, content: DocString) {
        val packageName = classQualifiedName.substringBeforeLast(".")
        val className = classQualifiedName.substringAfterLast(".")

        project.createFile(
            ProjectFile(
                path = Path.of("src/main/kotlin/${packageName.replace(".", "/")}/$className.kt"),
                content = content.content
            )
        )
    }

    @Given("resource {word} is created with content")
    fun `resource resourceName is created with content`(resourceName: String, content: DocString) {
        project.createFile(
            ProjectFile(
                path = Path.of("src/main/resources/$resourceName"),
                content = content.content
            )
        )
    }

    @Given("environment variable {word} is set to {word}")
    fun `environment variable envVarName is set to value`(envVarName: String, value: String) {
        project.setEnvironmentVariable(envVarName, value)
    }

    @When("run the application")
    fun `run the application`() {
        this.application = project.startApplication()
    }

    @Then("application starts successfully")
    fun `application starts successfully`() {
        val result = runBlocking {
            runCatching {
                withTimeout(120.seconds) {
                    while (true) {
                        if (application.isStarted) {
                            // TODO: fix this
                            // wait additional 200ms to make sure the application is fully started
                            delay(200.milliseconds)
                            break
                        }
                        if (!application.isRunning) throw IllegalStateException("Application failed to start")
                        delay(200.milliseconds)
                    }
                }
            }
        }

        if (result.isFailure) {
            application.printLogs()
        }

        assertThat(result.isSuccess).isTrue()
    }

    @Then("no assertions failed")
    fun `no assertions failed`() {
        assertThat(application.exceptionsInMain)
            .withFailMessage("There were exceptions in main thread:\n${application.exceptionsInMain.joinToString("\n")}")
            .isEmpty()

        assertThat(application.assertionFailures)
            .withFailMessage("There were assertion failures:\n${application.assertionFailures.joinToString("\n")}")
            .isEmpty()
    }

    @Then("HTTP request to application is created")
    fun `http request is created`() {
        this.httpRequestBuilder = HttpRequest.builder()

        this.httpRequestBuilder.host = "localhost"
        this.httpRequestBuilder.port = application.listeningPort
    }

    @Then("HTTP request method is {word}")
    fun `http request method is methodValue`(method: String) {
        this.httpRequestBuilder.method = when (method) {
            "GET" -> HttpRequest.Method.GET
            "POST" -> HttpRequest.Method.POST
            "PUT" -> HttpRequest.Method.PUT
            "DELETE" -> HttpRequest.Method.DELETE
            "PATCH" -> HttpRequest.Method.PATCH
            else -> throw IllegalArgumentException("Unsupported HTTP method: $method")
        }
    }

    @Then("HTTP request path is {word}")
    fun `http request path is pathValue`(path: String) {
        this.httpRequestBuilder.path = path.removePrefix("/")
    }

    @Then("HTTP request body is")
    fun `http request body is bodyValue`(body: DocString) {
        this.httpRequestBuilder.body = body.content
    }

    @Then("HTTP request header {word} is {word}")
    fun `http request header headerName is headerValue`(headerName: String, headerValue: String) {
        this.httpRequestBuilder.headers += headerName to headerValue
    }

    @Then("HTTP request query parameter {word} is {word}")
    fun `http request query parameter parameterName is parameterValue`(parameterName: String, parameterValue: String) {
        this.httpRequestBuilder.queryParams += parameterName to parameterValue
    }

    @Then("HTTP request is sent")
    fun `HTTP request is sent`() {
        this.httpRespone = HttpFacade.send(httpRequestBuilder.build())
    }

    @Then("HTTP response status code is {int}")
    fun `HTTP response status code is statusCode`(statusCode: Int) {
        assertThat(httpRespone.status).isEqualTo(statusCode)
    }

    @Then("HTTP response body is")
    fun `HTTP response body is bodyValue`(body: DocString) {
        assertThat(httpRespone.body).isEqualTo(body.content)
    }

    @After
    fun `print out generated sources on failure`(scenario: Scenario) {
        if (scenario.isFailed) {
            println("Generated sources:")

            project.path(Path.of("build/generated")).toFile().walkTopDown()
                .filter { it.isFile }
                .forEach { println("${it.absolutePath}:\n${it.readText().prependLineNumbers()}") }
        }
    }

    @After
    fun `clean up project`() {
        if (::project.isInitialized) project.destroy()
        if (::application.isInitialized) application.stop()
    }

}