package io.mzlnk.javalin.ext.e2e

import io.cucumber.docstring.DocString
import io.cucumber.java.After
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mzlnk.javalin.ext.e2e.utils.prependLineNumbers
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
        println("thread: ${Thread.currentThread().name}")
        this.project = Project.initialize()
    }

    @Given("application with EXT enabled is created")
    fun `application with DI enabled is created`() {
        project.copyResource(
            hostPath = Path.of("src/e2eTest/resources/files/kotlin/Application.kt"),
            targetPath = Path.of("./src/main/kotlin/io/mzlnk/javalin/di/e2e/app/Application.kt")
        )

        project.copyResource(
            hostPath = Path.of("src/e2eTest/resources/files/resources/logback.xml"),
            targetPath = Path.of("./src/main/resources/logback.xml")
        )
    }

    @Given("file {word} is created with content")
    fun `file filePath is created with content`(filePath: String, content: DocString) {
        project.createFile(
            ProjectFile(
                path = Path.of(filePath),
                content = content.content
            )
        )
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

    @Then("there are {int} components registered in DI context")
    fun `there are componentsCount components registered in DI context`(componentsCount: Int) {
        assertThat(application.loadedSingletons).isEqualTo(componentsCount)
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