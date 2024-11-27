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
                withTimeout(30.seconds) {
                    while (true) {
                        if (application.isStarted) break
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
        assertThat(application.assertionFailures)
            .withFailMessage("There were assertion failures:\n${application.assertionFailures.joinToString("\n")}")
            .isEmpty()
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