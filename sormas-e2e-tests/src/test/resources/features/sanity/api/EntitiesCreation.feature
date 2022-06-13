@API @DataCreation
Feature: Create person and attach immunizations via API requests

  @ignore @env_performance
  Scenario: Count persons and immunizations
    Given API: I receive all person ids
    Given API: I receive all immunizations ids

  @ignore @env_performance
  Scenario: Count contacts and cases
    Given API: I receive all contacts ids
    Given API: I receive all cases ids

  @PersonsAndImmunizations @env_performance
  Scenario: Create multiple Person and attach immunizations to them
    When API: I create 10 persons
    Then API: I check that POST call body for bulk request is "OK"
    And API: I check that POST call status code is 200
    Then API: I create 1-5 new immunizations for each person from last created persons list
    Then API: I check that POST call body for bulk request is "OK"
    And API: I check that POST call status code is 200

  @ContactsLinkedToCases @env_performance
  Scenario: Create multiple Cases and link 2 Contacts to each
    When API: I create 100 persons
    Then API: I check that POST call body for bulk request is "OK"
    And API: I check that POST call status code is 200
    Then API: I create 100 cases
    Then API: I check that POST call body for bulk request is "OK"
    And API: I check that POST call status code is 200
    Then API: I create and link 2 Contacts to each case from previous created cases
    Then API: I check that POST call body for bulk request is "OK"
    And API: I check that POST call status code is 200