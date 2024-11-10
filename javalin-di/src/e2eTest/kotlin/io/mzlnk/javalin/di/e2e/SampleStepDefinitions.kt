package io.mzlnk.javalin.di.e2e

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class SampleStepDefinitions {

    @Given("sample given step")
    fun sampleGivenStep() {
        println("sample given step")
    }

    @When("sample when step")
    fun sampleWhenStep() {
        println("sample when step")
    }

    @Then("sample then step")
    fun sampleThenStep() {
        println("sample then step")
    }

}