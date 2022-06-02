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

  @env_performance
  Scenario: Check response time for get persons uuids
    Given API: I check response time for get all persons uuids is less than 20000 milliseconds

  @env_performance
  Scenario: Check response time for get events uuids
    Given API: I check response time for get all events uuids is less than 1000 milliseconds

  @env_performance
  Scenario: Check response time for get cases uuids
    Given API: I check response time for get all cases uuids is less than 1000 milliseconds

  @env_performance
  Scenario: Check response time for get contacts uuids
    Given API: I check response time for get all contacts uuids is less than 1000 milliseconds

  @env_performance
  Scenario: Check response time for get samples uuids
    Given API: I check response time for get all samples uuids is less than 1000 milliseconds