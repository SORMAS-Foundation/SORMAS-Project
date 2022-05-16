@UI @Sanity @TravelEntries @Filters
Feature: Travel entry filters

@issue=SORDEV-8267 @env_de
Scenario: Check Travel Entry filters
  Given I log in as a National User
  And I click on the Entries button from navbar
  And I click on the New Travel Entry button from Travel Entries directory
  When I fill the required fields in a new travel entry form
  And I click on Save button from the new travel entry form
  Then I check the created data is correctly displayed on Edit travel entry page for DE version
  And I navigate to person tab in Edit travel entry page
  And I check the created data is correctly displayed on Edit travel entry person page for DE version
  And I click on the Entries button from navbar
  And I filter by Person ID on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 1
  And I click "Nur genesene Einreisende" checkbox on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 0
  And I click "Nur genesene Einreisende" checkbox on Travel Entry directory page
  And I click "Nur geimpfte Einreisende" checkbox on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 0
  And I click "Nur geimpfte Einreisende" checkbox on Travel Entry directory page
  And I click "Nur negativ getestete Einreisende" checkbox on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 0
  And I click "Nur negativ getestete Einreisende" checkbox on Travel Entry directory page
  And I click "Nur in Fälle konvertierte Einreisen" checkbox on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 0
  And I click "Nur in Fälle konvertierte Einreisen" checkbox on Travel Entry directory page
  And I apply "Abgeschlossene Einreisen" to aggregation combobox on Travel Entry directory page
  And I check that number of displayed Travel Entry results is 0
  And I apply "Alle Einreisen" to aggregation combobox on Travel Entry directory page
  And I check that number of displayed Travel Entry results is 1
  And I apply "Aktive Einreisen" to aggregation combobox on Travel Entry directory page
  And I check that number of displayed Travel Entry results is 1
  And I click on SHOW MORE FILTERS BUTTON Travel Entry directory page
  And I fill Travel Entry from input to 2 days before UI Travel Entry created on Travel Entry directory page
  And I fill Travel Entry to input to 5 days after UI Travel Entry created on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 1
  And I fill Travel Entry from input to 3 days after before UI Travel Entry created on Travel Entry directory page
  And I click APPLY BUTTON in Travel Entry Directory Page
  And I check that number of displayed Travel Entry results is 0

  @issue=SORDEV-9787 @env_de
  Scenario: Check that Add reporting period filter for Travel Entry work properly
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version
    And I click on the Entries button from navbar
    And I filter by Person ID on Travel Entry directory page
    And I open last created Travel Entry
    And I change a Report Date for previous week date
    And I click on Save button from the edit travel entry form
    And I click on the Entries button from navbar
    And I filter by Person ID on Travel Entry directory page
    And I click on SHOW MORE FILTERS BUTTON Travel Entry directory page
    And I fill Travel Entry from input to 9 days before UI Travel Entry created on Travel Entry directory page
    And I fill Travel Entry to input to 1 days after UI Travel Entry created on Travel Entry directory page
    And I click APPLY BUTTON in Travel Entry Directory Page
    And I check that number of displayed Travel Entry results is 1
    And I fill Travel Entry to input to 10 days before UI Travel Entry created on Travel Entry directory page
    And I click APPLY BUTTON in Travel Entry Directory Page
    And I check that number of displayed Travel Entry results is 0
    And I apply "Nach Epi Woche" to data filter option combobox on Travel Entry directory page
    And I apply the last epi week for week from combobox on Travel Entry directory page
    And I apply the last epi week for week to combobox on Travel Entry directory page
    And I click APPLY BUTTON in Travel Entry Directory Page
    And I check that number of displayed Travel Entry results is 1
    Then I apply the week before the last epi week for week to combobox on Travel Entry directory page
    And I click APPLY BUTTON in Travel Entry Directory Page
    And I check that number of displayed Travel Entry results is 0