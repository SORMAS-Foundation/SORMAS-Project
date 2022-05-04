@API @ApiMeasurements @PublishApiCustomReport
Feature: APIs loading time

  @env_performance
  Scenario: Check response time for person creation
   Given API: I check response time for person creation is less than 1000 milliseconds

  @env_performance
  Scenario: Check response time for event creation
    Given API: I check response time for event creation is less than 1000 milliseconds

  @env_performance
  Scenario: Check response time for case creation
    Given API: I check response time for case creation is less than 2000 milliseconds

  @env_performance
  Scenario: Check response time for contact creation
    Given API: I check response time for contact creation is less than 2000 milliseconds

  @env_performance
  Scenario: Check response time for sample creation
    Given API: I check response time for sample creation is less than 2000 milliseconds