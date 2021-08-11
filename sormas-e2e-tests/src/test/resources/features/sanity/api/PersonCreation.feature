@Sanity @Person @API
Feature: Create person via API requests

  Scenario: Create Person
    Given API: I create a new person
    Then API: I check that POST person call body has is "OK"
    #add also immunization for created person
