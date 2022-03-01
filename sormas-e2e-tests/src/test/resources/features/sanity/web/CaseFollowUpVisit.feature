@UI @Sanity @Case @Visit
Feature: Follow-up new visit functionality

  @env_main @ignore
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
    And I navigate to follow-up tab
    And I click on new Visit button
    When I create a new Visit with specific data
    And I click on edit Visit button
    Then I validate all fields from Visit

    #please rename it to reflect the precise aim of the test because the same test exists in Case.feature
  @issue=SORDEV-5528 @env_main @ignore
  Scenario: Fill the therapy tab
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I navigate to follow-up tab
    And I click on new Visit button
    Then I set Person available and cooperative to UNAVAILABLE
    And I set Date and time of visit
    Then I save the Visit data
    And I click on edit Visit button
    Then I check last Person status and date with time
    Then I set Person available and cooperative to AVAILABLE, BUT UNCOOPERATIVE
    And I set Date and time of visit
    Then I save the Visit data
    And I click on edit Visit button
    Then I check last Person status and date with time
    And I fill the specific data of visit with Set cleared to No option to all symptoms
    Then I save the Visit data
    Then I navigate to symptoms tab
    And I check if created data is correctly displayed in Symptoms tab for Set cleared to NO
    Then I clear Clinical Signs and Symptoms list
    And I navigate to follow-up tab
    And I am saving clear Clinical Signs and Symptoms list
    Then I click on edit Visit button
    And I fill the specific data of visit with Set cleared to Unknown option to all symptoms
    Then I save the Visit data
    And I navigate to symptoms tab
    Then I check if created data is correctly displayed in Symptoms tab for Set cleared to UNKNOWN