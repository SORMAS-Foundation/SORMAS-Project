@UI @Sanity @Case @Visit
Feature: Follow-up new visit functionality

  Scenario: Create a new visit from case follow-up
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I navigate to fallow-up tab
    And I click on new Visit button
    When I create a new Visit with specific data
    And I click on edit Visit button
    Then I validate all fields from Visit

