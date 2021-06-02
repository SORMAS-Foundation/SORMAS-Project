@Sanity @Case
Feature: Cases end to end tests

  Background: Create new case
   Given I log in with the user
   And I click on the Cases button from navbar
   And I click on the NEW CASE button
   When I create a new case with specific data

  Scenario:Check a new case data
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  Scenario: Edit, save and check all fields of a new case
    And I change all Case fields and save
    And I click on the Dashboard button from navbar
    And I open last edited case by link
    And I check the edited data is correctly displayed on Edit case page

  Scenario: Delete created case
    When API: I create a new person
    Then API: I create a new case
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I delete the case
    Then I check that number of displayed cases results is 0



