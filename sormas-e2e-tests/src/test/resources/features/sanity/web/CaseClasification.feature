@UI @Sanity @Case @Classification
Feature: Case Classification functionality

  @env_de
  Scenario: Change Case classification from Not Yet Classified to Suspect Case by confirming Sore Throat for DE
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And I log in as a National User
    Then I open last edited case by API via URL navigation
    And For the current Case the Case Classification value should be "Not yet classified"
    And I navigate to symptoms tab
    When I check Yes Option for Soar Throat on Symptoms tab page
    And I select sore throat option
    And I click on save button from Edit Case page
    Then I click on Case tab from Symptoms tab directory
    Then For the current Case the Case Classification value should be "Suspect case"

  @env_de
  Scenario: Change Case classification from Suspect Case to Not Yet Classified for DE
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And I log in as a National User
    Then I open last edited case by API via URL navigation
    And I navigate to symptoms tab
    When I check Yes Option for Soar Throat on Symptoms tab page
    And I select sore throat option
    And I click on save button from Edit Case page
    Then I click on Case tab from Symptoms tab directory
    Then For the current Case the Case Classification value should be "Suspect case"
    And I navigate to symptoms tab
    When I click on Clear all button From Symptoms tab
    And I click on save button from Edit Case page
    Then I click on Case tab from Symptoms tab directory
    And I click on save button from Edit Case page
    Then For the current Case the Case Classification value should be "Not yet classified"