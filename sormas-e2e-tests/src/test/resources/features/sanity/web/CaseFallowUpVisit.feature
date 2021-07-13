@UI @Sanity @Case @Visit
Feature: Fallow-up new visit functionality

  Scenario: Create a new visit from case fallow-up
    Given API: I create a new person
    And API: I create a new case
    And I log in with the user
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I navigate to fallow-up tab
    And I click on new Visit button
    When I create a new Visit with specific data
    And I click on edit Visit button
    Then I validate all fields from Visit

