@UI @Sanity @Case @Visit @Follow-up
Feature: Follow-up new visit functionality

  @env_main
  Scenario: Create a new visit from case follow-up
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And I log in as a National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I navigate to follow-up tab
    And I click on new Visit button
    When I create a new Visit with specific data
    And I click on edit Visit button
    Then I validate all fields from Visit

  @tmsLink=SORDEV-5528 @env_main
  Scenario: Fill the therapy tab for follow-up test
    When API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call status code is 200
    And I log in as a National User
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

  @tmsLink=SORDEV-5084 @env_main
  Scenario: Test Link phone-numbers in the follow-up to tel
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case and save phone number
    Then I navigate to follow-up tab
    And I click on new Visit button
    Then I check if phone number is displayed in Create new visit popup

  @tmsLink=SORDEV-12444 @env_main
  Scenario: User name from the 'Visit Origin' column is missing when exporting visits
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Given API: I create a new case

    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I navigate to the last created case via the url
    Then I navigate to follow-up tab
    And I click on new Visit button
    Then I set Person available and cooperative to UNAVAILABLE
    And I set Date and time of visit
    Then I save the Visit data
    And I click on a EXPORT button in the follow-up tab
    Then I check if downloaded file has correct data in origin record