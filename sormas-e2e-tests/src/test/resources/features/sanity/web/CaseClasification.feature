@UI @Sanity @Case @Classification
Feature: Case Classification functionality

  Scenario: Case Classification for a suspect case
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I navigate to symptoms tab
    When I check Yes Option for Soar Throat on Symptoms tab page
    And I select sore throat option
    And I click on save button
    And I click on the Cases button from navbar
    Then I am checking that the case classification is changed to Suspect Case