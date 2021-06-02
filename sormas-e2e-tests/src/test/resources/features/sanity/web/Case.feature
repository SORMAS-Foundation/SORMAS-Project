@Sanity @Case
Feature: Create cases

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
