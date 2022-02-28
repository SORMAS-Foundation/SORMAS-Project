@UI @Sanity @Case @EpidemiologicalData
Feature: Epidemiological data coverage

  @env_main
  Scenario: Edit all fields from Epidemiological data tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    When I am accessing via URL the Epidemiological data tab of the created case
    Then I create a new Exposure for Epidemiological data tab and fill all the data
    Then I create a new Activity from Epidemiological data tab and fill all the data
    And I click on save button from Epidemiological Data
    When I am accessing via URL the Epidemiological data tab of the created case
    And I am checking all Exposure data is saved and displayed
    Then I click on discard button from Epidemiological Data Exposure popup
    And I open saved activity from Epidemiological Data
    Then I am checking all Activity data is saved and displayed

    @issue=SORDEV-5522 @env_main
  Scenario: Validate all fields are present and functional on Epidemiological page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    When I am accessing via URL the Epidemiological data tab of the created case
    And I click on Exposure details known with UNKNOWN option
    And I click on Exposure details known with NO option
    Then I create a new Exposure for Epidemiological data tab and fill all the data
    Then I click on Activity details known with UNKNOWN option
    And I click on Activity details known with NO option
    Then I create a new Activity from Epidemiological data tab and fill all the data
    Then I click on Residing or working in an area with high risk of transmission of the disease with UNKNOWN option
    And I click on Residing or working in an area with high risk of transmission of the disease with NO option
    And I click on Residing or working in an area with high risk of transmission of the disease with YES option
    Then I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with UNKNOWN option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with NO option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with YES option
    Then I click on Contacts with source case known with UNKNOWN option
    And I click on Contacts with source case known with NO option
    And I click on Contacts with source case known with YES option
    Then I check if Contacts of Source filed is available
    And I click on save button from Epidemiological Data
    When I am accessing via URL the Epidemiological data tab of the created case
    And I am checking if options in checkbox are displayed correctly
    And I am checking all Exposure data is saved and displayed
    Then I click on discard button from Epidemiological Data Exposure popup
    And I open saved activity from Epidemiological Data
    Then I am checking all Activity data is saved and displayed




