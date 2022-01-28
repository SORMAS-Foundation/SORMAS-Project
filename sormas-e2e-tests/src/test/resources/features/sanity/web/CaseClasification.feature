@UI @Sanity @Case @Classification
Feature: Case Classification functionality

  Scenario: Case Classification change for a suspect case
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And For the current Case the Case Classification value should be "Not yet classified"
    And I navigate to symptoms tab
    When I check Yes Option for Soar Throat on Symptoms tab page
    And I select sore throat option
    And I click on save button from Edit Case page
    Then From Symptoms Tab I click on Case tab
    And From Case page I click on Calculate Case Classification button
    And I click on save button from Edit Case page
    Then For the current Case the Case Classification value should be "Suspect case"