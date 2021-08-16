@Sanity @Person @API
Feature: Create person and attach immunizations via API requests

  Scenario: Create Person and attach immunizations
    Given API: I create a new person
    Then API: I check that POST person call body is "OK"
    #add also immunization for created person
