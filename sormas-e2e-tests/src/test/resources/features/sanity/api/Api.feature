@Sanity  @API
Feature: Check basic POSTs RestApi endpoints

  Scenario: Create a new person
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  Scenario: Create new case
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  Scenario: Create a new contact
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  Scenario: Create a new event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  Scenario: Create a new sample
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  Scenario: Create a new task
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
