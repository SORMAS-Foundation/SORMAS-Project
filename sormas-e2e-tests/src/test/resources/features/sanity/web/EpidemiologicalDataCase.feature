@Sanity @Case
Feature: Epidemiological data coverage

  Scenario: Edit all fields from Epidemiological data tab
    Given API: I create a new case
    Given I log in with the user
    When I am accessing the Epidemiological data tab of the created case
    Then I check and fill all data
    And I click on save
    And I am accessing the cases
    When I am accessing the Epidemiological data tab using of the created case
    And I am checking all data is saved and displayed