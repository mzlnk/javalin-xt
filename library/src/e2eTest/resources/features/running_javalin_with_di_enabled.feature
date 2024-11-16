Feature: Running Javalin with DI enabled

  Scenario: Running Javalin with DI enabled
    Given project is set up
    And application with DI enabled is created
    When run the application
    Then application starts successfully