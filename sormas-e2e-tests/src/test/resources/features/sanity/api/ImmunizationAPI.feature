@Sanity @Person @API
Feature: Create person and attach immunizations via API requests

  @PersonsAndImmunizations
  Scenario Outline: Create Person and attach <numberOfImmunizations> immunizations
    Given API: I create a new person
    Then API: I check that POST person call body is "OK"
    When API: I create <numberOfImmunizations> new immunizations for last created person
    Then API: I check that POST immunization call body is "OK"

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