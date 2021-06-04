@Sanity @Case @API
Feature: Case business oriented scenarios through API

  @S1
  Scenario: Create a new case with valid data
    Given I create a person
    Given I try to enter valid data for a new case
    When I create a new case
    Then I get 200 OK response back
    And I can query case by UUID

  @S2
  Scenario: Create a new case with invalid person data
    Given I try to enter invalid user for a new case
    When I create a new case
    Then I get the error message TOO_OLD

  @S3
  Scenario: Create a new case with lower case disease name
    Given I try to enter invalid disease for a new case
    When I create a new case
    Then I get the error message Unknown disease and all the valid values are shown

  @S4
  Scenario: Create a new case with lower case facility name
    Given I try to enter all lower case healthFacility for a new case
    When I create a new case
    Then I get a general error message ERROR
