package io.mzlnk.javalin.di.e2e

import io.cucumber.java.After
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.assertj.core.api.Assertions.assertThat
import java.nio.file.Path
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ProjectStepDefinitions {

    private lateinit var project : Project
    private lateinit var application : Application


    @Given("project is set up")
    fun `gradle project is set up`() {
        this.project = Project.initialize()
    }

    @Given("application with DI enabled is created")
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

    @When("run the application")
    fun `run the application`() {
        this.application = project.startApplication()
    }

    @Then("application starts successfully")
    fun `application starts successfully`() {
        val result = runBlocking {
            runCatching {
                withTimeout(30.seconds) {
                    while(true) {
                        if(application.isStarted) break
                        delay(200.milliseconds)
                    }
                }
            }
        }

        if(result.isFailure) {
            application.printLogs()
        }

        assertThat(result.isSuccess).isTrue()
    }

    @After
    fun `clean up project`() {
        if(::project.isInitialized) project.destroy()
        if(::application.isInitialized) application.stop()
    }

}