@UI @Sanity @Case
Feature: Create cases

  Scenario: Edit all fields from Symptoms tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the Symptoms tab using of created case via api
    And I change all symptoms fields and save
    And I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page