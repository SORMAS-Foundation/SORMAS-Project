@Sanity @Person @API
Feature: Check Person RestApi endpoints

  Scenario: Create and check successfully created Case
    Given API: I create a new person
    When API: I create a new case
    Then API: I check if the response contains OK
    And API: I check if the response has status 200

  Scenario: Create and check a case which is using an already created CASE UUID
    Given API: I create a new person
    When API: I create a new case
    When API: I create a new case with already created Case uuid
    Then API: I check if the response contains ERROR
    And API: I check if the response has status 200