@Sanity @API
Feature: Check basic POSTs RestApi endpoints

  @env_main @precon
  Scenario: Create a new person
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_main @precon
  Scenario: Create new case
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_main @precon
  Scenario: Create a new contact
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_main @precon
  Scenario: Create a new contact linked to a case
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_main @precon
  Scenario: Create a new event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_main @precon
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

  @env_main @precon
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

  @env_main @precon
  Scenario Outline: Create Person and attach <numberOfImmunizations> immunizations
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create <numberOfImmunizations> new immunizations for last created person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

    Examples:
      | numberOfImmunizations |
      | 1                     |
      | 5                     |

  @env_de @precon
  Scenario: Create a new person on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create new case on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create a new contact on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create a new contact linked to a case on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create a new event on DE market
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create a new sample on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario: Create a new task on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @precon
  Scenario Outline: Create Person and attach <numberOfImmunizations> immunizations on DE market
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create <numberOfImmunizations> new immunizations for last created person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

    Examples:
      | numberOfImmunizations |
      | 1                     |
      | 5                     |

  @env_de @oldfake
  Scenario: Create new case with creation date 10 years ago
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case with creation date 3653 days ago
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @oldfake
  Scenario: Create a new contact with creation date 5 years ago
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact with creation date 1827 days ago
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @oldfake
  Scenario: Create a new event with creation date 5 years ago
    Given API: I create a new event with creation date 1827 days ago
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @oldfake
  Scenario: Create Person and attach immunizations with creation date 10 years ago
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new immunizations for last created person with creation date 3653 days ago
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @oldfake
  Scenario: Create Event participant with creation date 5 years ago
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new event
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new event participant with creation date 1827 days ago
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

  @env_de @oldfake
  Scenario: Create Travel entry with creation date 14 days ago
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new travel entry with creation date 16 days ago
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200

