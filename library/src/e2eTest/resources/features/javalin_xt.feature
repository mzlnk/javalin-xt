Feature: Javalin Xt

  Scenario: Running Javalin with Xt enabled
    Given project is set up
    And application with Xt enabled is created
    When run the application
    Then application starts successfully