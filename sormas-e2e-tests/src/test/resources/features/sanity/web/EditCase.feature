@UI @Sanity @Case
Feature: Create cases

  Background: Create a case via API
    Given API: I create a new person
    Given API: I create a new case
    Given I log in with the user

  Scenario: Edit all fields from Symptoms tab
    When I am accessing the Symptoms tab using of created case via api
    And I change all symptoms fields and save
    And I click on the Dashboard button from navbar
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page