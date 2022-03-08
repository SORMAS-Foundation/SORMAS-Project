@UI @Sanity @TravelEntries
Feature: Create travel entries

  @issue=SORDEV-8266 @env_de
  Scenario: Upload Travel Entry CSV
    Given I log in as a National User
    And I click on the Entries button from navbar
    When I click on the Import button from Travel Entries directory
    And I pick the travel entry CSV file
    And I click on the START DATA IMPORT button from the popup
    And I select to create new person from the popup
    And I confirm the save popup
    Then I check that an import success notification appears

  @issue=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I save the travel entry
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version

  @issue=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry through case view
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I open last created case
    And I navigate to epidemiological data tab in Edit case page
    And I click on the New Travel Entry button from Edit case page
    When I fill the required fields in a new case travel entry form
    And I save the travel entry
    Then I check the created data is correctly displayed on Edit travel entry page for DE version