@UI @Sanity @TravelEntries
Feature: Create travel entries

  @issue=SORDEV-8266 @env_de
  Scenario: Upload Travel Entry CSV
    Given I log in as a National User
    And I click on the Entries button from navbar
    When I click on the Import button from Travel Entries directory
    And I select the German travel entry CSV file in the file picker
    And I click on the START DATA IMPORT button from the Import Travel Entries popup
    And I select to create new person from the Import Travel Entries popup
    And I confirm the save Travel Entries Import popup
    Then I check that an import success notification appears in the Import Travel Entries popup

  @issue=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
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
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit case travel entry page for DE version

    @issue=SORDEV-8268 @env_de
    Scenario: Create a case for a travel entry
      Given I log in as a National User
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form
      And I click on Save button from the new travel entry form
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      When I click on new case button for travel entry
      Then I check if data from travel entry for new case is correct
      And I save the new case for travel entry
      Then I check if data in case based on travel entry is correct
      Then I navigate to epidemiological data tab in Edit case page
      And I click on edit travel entry button form case epidemiological tab
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      And I check if first and last person name for case in travel entry is correct
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form for previous created person
      And I click on Save button from the new travel entry form
      Then I check Pick an existing case in Pick or create person popup in travel entry
      And I click confirm button in popup from travel entry
      When I click on new case button for travel entry
      Then I choose an existing case while creating case from travel entry
      And I click confirm button in popup from travel entry
      Then I navigate to epidemiological data tab in Edit case page
      And I check if created travel entries are listed in the epidemiological data tab

  @issue=SORQA-199 @env_de
  Scenario: Test Inactive Destrict Feauture for Travel Entries
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I click on Districts button in Configuration tab
    And I click on New Entry button in Districts tab in Configuration
    Then I fill new district with specific data for DE version
    Then I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    And I create new travel entry with created district for DE version
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page with specific district for DE version
    When I click on new case button for travel entry
    Then I check if data from travel entry for new case is correct with specific district
    And I save the new case for travel entry
    Then I check if data with created district in case based on travel entry is correct
    Then I click on the Configuration button from navbar
    And I click on Districts button in Configuration tab
    Then I filter by last created district
    And I click on edit button for filtered district
    And I archive chosen district
    Then I click on the Entries button from navbar
    And I filter by Person ID on Travel Entry directory page with specific district
    Then I click on first filtered record in Travel Entry
    And I check if archived district is marked as a inactive
    Then I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    Then I check if archived district is unavailable

  @issue=SORDEV-9477 @env_de
  Scenario: Add a person search option on creation forms
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form without personal data
    Then I click on the person search button in create new travel entry form
    And I search for the last created person by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window for DE version
    And I click on the clear button in new case form
    And I click on the person search button in new case form
    And I search for the last created person by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window for DE version
    And I click on Save button from the new travel entry form
    And I collect travel UUID from travel entry
    And I check the created data is correctly displayed on Edit travel entry page for DE version
    When I click on the Persons button from navbar
    And I open the last created person linked with Case
    And I check that EDIT TRAVEL ENTRY button appears on Edit Person page

  @issue=SORDEV-10360 @env_de
  Scenario: Test add Date of arrival to Travel Entry and fill it when importing DEA information
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I change a Date of Arrival for wrong date from next day
    And I click on Save button from the new travel entry form
    And I check that Date of Arrival validation popup is appear
    And I change a Date of Arrival for correct date
    Then I check that word Date of arrival is appropriate translated to German language
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I click on the Entries button from navbar
    Then I click on the Import button from Travel Entries directory
    And I select the attached CSV file in the file picker from Travel Entries directory
    And I click on the START DATA IMPORT button from the Import Travel Entries popup
    And I acquire the first name and last name imported person
    And I select to create new person from the Import Travel Entries popup
    And I confirm the save Travel Entries Import popup
    Then I check that an import success notification appears in the Import Travel Entries popup
    And I close Data import popup for Travel Entries
    And I close Import Travel Entries form
    Then I filter by Person full name on Travel Entry directory page
    And I open the imported person on Travel entry directory page
    And I check the information about Dates for imported travel entry on Edit Travel entry page

  @issue=SORDEV-9818 @env_de
  Scenario: Bulk deleting entries in Travel Entry Directory
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    And I click on Enter Bulk Edit Mode from Travel Entry Directory
    And I select 3 results in grid in Travel Entry Directory
    And I click on Bulk Actions combobox in Travel Entry Directory
    And I click on Delete button from Bulk Actions Combobox in Travel Entry Directory
    And I click yes on the CONFIRM REMOVAL popup from Task Directory page
    And I choose the reason of deletion in popup for Travel Entry

  @issue=SORDEV-9818 @env_de
  Scenario: Deleting entry assigned to a person in Travel Entry Directory
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I collect travel UUID from travel entry
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version
    And I navigate to Edit travel entry page
    When I click on new case button for travel entry
    Then I check if data from travel entry for new case is correct
    And I save the new case for travel entry
    And I click on the Entries button from navbar
    And I click on Enter Bulk Edit Mode from Travel Entry Directory
    And I click "Nur in Fälle konvertierte Einreisen" checkbox on Travel Entry directory page
    And I click APPLY BUTTON in Travel Entry Directory Page
    And I select last created UI result in grid in Travel Entry Directory for Bulk Action
    And I click on Bulk Actions combobox in Travel Entry Directory
    And I click on Delete button from Bulk Actions Combobox in Travel Entry Directory
    And I click yes on the CONFIRM REMOVAL popup from Task Directory page
    And I choose the reason of deletion in popup for Travel Entry
    When I click on the Persons button from navbar
    And I fill UUID of the collected person from last created Travel Entry
    Then I apply on the APPLY FILTERS button
    And I click on first person in person directory
    Then I check if there is no travel entry assigned to Person

  @issue=SORDEV-9946 @env_de
  Scenario: Test Hide country specific fields in the 'Pick or create person' form of the duplicate detection pop-up, in German and French systems
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form with same person data
    And I click on Save button from the new travel entry form
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form with same person data
    And I click on Save button from the new travel entry form
    Then I check if National Health Id, Nickname and Passport number appear in Pick or create person popup
    @issue=SORDEV-7166 @env_de
    Scenario: Test Link DEA TravelEntries to cases
      Given I log in as a Admin User
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form
      And I click on Save button from the new travel entry form
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      And I collect travel UUID from travel entry
      When I click on new case button for travel entry
      Then I check if data from travel entry for new case is correct
      And I save the new case for travel entry
      Then I navigate to epidemiological data tab in Edit case page
      Then I check if created travel entries are listed in the epidemiological data tab
      And I click on edit travel entry button form case epidemiological tab
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      And I click on Open case of this travel entry on Travel entry tab for DE version
      Then I navigate to epidemiological data tab in Edit case page
      And I click on the New Travel Entry button from Epidemiological data tab in Case directory
      When I fill the required fields for new case in existing travel entry form
      And I click on Save button from the new travel entry form
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      And I collect travel UUID from travel entry
      And I click on the Entries button from navbar
      Then I search for first created travel entry by UUID for person in Travel Entries Directory
      And I check if first Travel Entry UUID is available in Travel Entries Directory List
      Then I search for second created travel entry by UUID for person in Travel Entries Directory
      And I check if second Travel Entry UUID is available in Travel Entries Directory List

  @issue=SORDEV-7162 @env_de
  Scenario: Test column structure in Travel Entries directory
    Given I log in with National User
    And I click on the Entries button from navbar
    Then I check that the Entries table structure is correct DE specific

  @issue=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Travel Entry directory
    Given I log in with National User
    And I click on the Entries button from navbar
    Then I click on the New Travel Entry button from Travel Entries directory
    And I click on the person search button in create new travel entry form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup

  @issue=SORDEV-8411 @env_de
  Scenario: Test Travel Entry conversion to case
    Given I log in with National User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I select "EINREISEORT" as a Case Origin in Case Popup
    Then I check if Different Point Of Entry Jurisdiction checkbox appears
    When I select Different Point Of Entry Jurisdiction checkbox
    Then I check if additional Point Of Entry fields appear
    When I create a new case with Point Of Entry for DE version
    And I click on save button in the case popup
    And I check that Point Of Entry information is displayed as read-only on Edit case page
    And I refer case from Point Of Entry
    And I check that Point Of Entry and Place Of Stay information is correctly display on Edit case page
    Then I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    And I check if Different Point Of Entry Jurisdiction checkbox appears in New Travel Entry popup
    And I select Different Point Of Entry Jurisdiction checkbox
    And I check if additional Point Of Entry fields appear
    When I create new travel entry with Different Point Of Entry Jurisdiction for DE
    And I click on Save button from the new travel entry form
    And I check the created Different Point Of Entry data is correctly displayed on Edit travel entry page for DE
    And I convert the Travel Entry into a case
    Then I check that differing Point Of Entry is correctly displayed on Edit case page
    And I check that Case Origin is set to Point Of Entry