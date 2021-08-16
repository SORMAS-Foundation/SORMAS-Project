@Sanity @Person @API
Feature: Create person and attach immunizations via API requests

  Scenario: Create Person and attach immunizations
    Given API: I create a new person
    Then API: I check that POST person call body is "OK"
    When API: I create 1 new immunizations for last created person
    Then API: I check that POST immunization call body is "OK"
