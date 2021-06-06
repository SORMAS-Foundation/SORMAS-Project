@Sanity @Case @EpidemiologicalData
Feature: Epidemiological data coverage

  Scenario: Edit all fields from Epidemiological data tab
    Given API: I create a new person
    And API: I create a new case
    Given I log in with the user
    When I am accessing the Epidemiological data tab of the created case
    Then I create a new Exposure and fill all the data
    Then I create a new Activity and fill all the data
    And I click on save
    When I am accessing the Epidemiological data tab of the created case
    And I am checking all Exposure data is saved and displayed
    And I am checking all Activity data is saved and displayed