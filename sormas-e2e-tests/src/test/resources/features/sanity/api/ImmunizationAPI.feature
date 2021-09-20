@Sanity @Person @API
Feature: Create person and attach immunizations via API requests

  @ignore
  Scenario Outline: Create Person and attach <numberOfImmunizations> immunizations
    Given API: I create a new person
    Then API: I check that POST person call body is "OK"
    And API: I check that POST person call status code is 200
    When API: I create <numberOfImmunizations> new immunizations for last created person
    Then API: I check that POST immunization call body is "OK"
    And API: I check that POST immunization call status code is 200

    Examples:
      | numberOfImmunizations |
      | 1                     |
      | 2                     |
      | 3                     |
      | 4                     |
      | 5                     |

  @ignore
  Scenario: Count persons and immunizations
    Given API: I receive all person ids
    Given API: I receive all immunizations ids


  @PersonsAndImmunizations
  Scenario: Create multiple Person and attach immunizations to them
    When API: I create 100 persons
    Then API: I check that POST person call body is "OK"
    And API: I check that POST person call status code is 200
    Then API: I create 1-5 new immunizations for each person from last created persons list
    Then API: I check that POST immunization call body is "OK"
    And API: I check that POST immunization call status code is 200

