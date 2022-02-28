@UI @Sanity @Case @Classification
Feature: Case Classification functionality

  @env_main
  Scenario: Case Classification change from Not Yet Classified to Suspect Case by confirming Sore Throat
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    Then I open last edited case by API via URL navigation
    And For the current Case the Case Classification value should be "Not yet classified"
    And I navigate to symptoms tab
    When I check Yes Option for Soar Throat on Symptoms tab page
    And I select sore throat option
    And I click on save button from Edit Case page
    Then From Symptoms Tab I click on Case tab
    And From Case page I click on Calculate Case Classification button
    And I click on save button from Edit Case page
    Then For the current Case the Case Classification value should be "Suspect case"

  @env_main
  Scenario: Case Classification change from Suspect Case to Not Yet Classified
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case classified as "Suspect"
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    Then I open last edited case by API via URL navigation
    And For the current Case the Case Classification value should be "Suspect case"
    And I navigate to symptoms tab
    When From Symptoms Tab I click on Clear All button
    And I click on save button from Edit Case page
    Then From Symptoms Tab I click on Case tab
    And From Case page I click on Calculate Case Classification button
    And I click on save button from Edit Case page
    Then For the current Case the Case Classification value should be "Not yet classified"