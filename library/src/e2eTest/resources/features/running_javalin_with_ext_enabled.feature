Feature: Running Javalin with EXT enabled

  Scenario: Running Javalin with EXT enabled
    Given project is set up
    And application with EXT enabled is created
    When run the application
    Then application starts successfully