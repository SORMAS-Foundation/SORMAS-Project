@API @ApiMeasurements @PublishApiCustomReport
Feature: APIs loading time

  @env_performance
  Scenario: Check response time for person creation
   Given API: I check response time for person creation is less than 1000 milliseconds