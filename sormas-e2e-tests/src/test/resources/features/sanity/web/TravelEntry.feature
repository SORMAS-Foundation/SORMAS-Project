@UI @Sanity @TravelEntries
Feature: Create travel entries

  @tmsLink=SORDEV-8266 @env_de
  Scenario: Upload Travel Entry CSV
    Given I log in as a National User
    And I click on the Entries button from navbar
    When I click on the Import button from Travel Entries directory
    And I select the German travel entry CSV file in the file picker
    And I click on the START DATA IMPORT button from the Import Travel Entries popup
    And I select to create new person from the Import Travel Entries popup
    And I confirm the save Travel Entries Import popup
    Then I check that an import success notification appears in the Import Travel Entries popup

  @tmsLink=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version

  @tmsLink=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry through case view
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I open last created case
    And I navigate to epidemiological data tab in Edit case page
    And I click on the New Travel Entry button from Edit case page
    When I fill the required fields in a new case travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit case travel entry page for DE version

    @tmsLink=SORDEV-8268 @env_de
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

  @tmsLink=SORQA-199 @env_de
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

  @tmsLink=SORDEV-9477 @env_de
  Scenario: Add a person search option on creation forms
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form without personal data
    Then I click on the person search button in create new travel entry form
    And I search for the last created person by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on the clear button in new case form
    And I click on the person search button in new case form
    And I search for the last created person by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on Save button from the new travel entry form
    And I collect travel UUID from travel entry
    And I check the created data is correctly displayed on Edit travel entry page for DE version
    When I click on the Persons button from navbar
    And I open the last created person linked with Case
    And I check that EDIT TRAVEL ENTRY button appears on Edit Person page

  @tmsLink=SORDEV-10360 @env_de
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

  @tmsLink=SORDEV-9818 @env_de
  Scenario: Bulk deleting entries in Travel Entry Directory
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    And I click on Enter Bulk Edit Mode from Travel Entry Directory
    And I select 3 results in grid in Travel Entry Directory
    And I click on Bulk Actions combobox in Travel Entry Directory
    And I click on Delete button from Bulk Actions Combobox in Travel Entry Directory
    And I click yes on the CONFIRM REMOVAL popup from Task Directory page
    And I choose the reason of deletion in popup for Travel Entry

  @tmsLink=SORDEV-9818 @env_de
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
    And I collect the Travel Entry person UUID displayed on Travel Entry Person page
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

  @tmsLink=SORDEV-9946 @env_de
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

    @tmsLink=SORDEV-7166 @env_de
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

  @tmsLink=SORDEV-7162 @env_de
  Scenario: Test column structure in Travel Entries directory
    Given I log in as a National User
    And I click on the Entries button from navbar
    Then I check that the Entries table structure is correct DE specific

  @tmsLink=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Travel Entry directory
    Given I log in as a National User
    And I click on the Entries button from navbar
    Then I click on the New Travel Entry button from Travel Entries directory
    And I click on the person search button in create new travel entry form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup

  @tmsLink=SORDEV-8043 @env_de
  Scenario: Test Add TravelEntries to tasks
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I click NEW TASK in Edit Travel Entry page
    Then I fill a new task form with specific data for DE version
    And I click on Save button in New Task form
    Then I check if new task is displayed in Task tab on Edit Travel Entry page
    And I click on edit task icon of the first created task
    And I check that Discard option is visible in Edit Task form on Edit Travel Entry page
    And I check that Delete option is visible in Edit Task form on Edit Travel Entry page
    And I check that Save option is visible in Edit Task form on Edit Travel Entry page
    And I check that Task status option is visible in Edit Task form on Edit Travel Entry page
    And I click on Discard button in Task form
    And I click on the Tasks button from navbar
    And I filter Task context by Einreise
    And I check displayed task's context of first result is Einreise
    And I click on associated link to Travel Entry

  @tmsLink=SORDEV-8411 @env_de
  Scenario: Test Travel Entry conversion to case
    Given I log in as a National User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I select "EINREISEORT" as a Case Origin in Case Popup
    Then I check if Different Point Of Entry Jurisdiction checkbox appears
    When I select Different Point Of Entry Jurisdiction checkbox
    Then I check if additional Point Of Entry fields appear
    When I create a new case with Point Of Entry for DE version
    And I click on save button in the case popup
    And I check that Point Of Entry information is displayed as read-only on Edit case page
    And I refer case from Point Of Entry with Place of Stay ZUHAUSE
    And I check that Point Of Entry and Place Of Stay ZUHAUSE information is correctly display on Edit case page
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

  @tmsLink=SORDEV-7167 @env_de
  Scenario: Test DEA TravelEntry form
    Given I log in as a Admin User
    When I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    And I check that new travel entry form contains all the necessary fields
    And I clear report date and disease fields in the new travel entry form
    And I click on Save button from the new travel entry form
    Then I check that all required fields except person fields are mandatory in the new travel entry form DE specific
    And I close input data error popup
    When I fill all required fields except person-related fields in the new travel entry form DE specific
    And I click on Save button from the new travel entry form
    Then I check that person-related fields are mandatory in the new entry form DE specific
    And I close input data error popup
    And I fill the person-related required fields in the new entry form DE specific

  @tmsLink=SORDEV-8037 @env_de
  Scenario: Test Add documents and document templates to TravelEntries
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I click on Create Document button from Bulk Actions combobox on Edit Travel Entry Page
    And I click on checkbox to upload generated document to entities in Create Document form in Travel Entry directory
    And I select "ExampleDocumentTemplateTravelEntry.docx" Create Document form in Travel Entry directory
    And I click on Create button in Create Document form in Travel Entry directory
    And I click on close button in Create Document Order form
    Then I check if generated document based on "ExampleDocumentTemplateTravelEntry.docx" appeared in Documents tab in Edit Travel Entry directory
    And I check if downloaded file is correct for "ExampleDocumentTemplateTravelEntry.docx" in Edit Travel Entry directory
    And I delete downloaded file created from "ExampleDocumentTemplateTravelEntry.docx" Document Template for Travel Entry

  @tmsLink=SORDEV-7160 @env_de
  Scenario: Test TravelEntries III: TravelEntry list for person forms
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I navigate to person tab in Edit travel entry page
    And I collect the Travel Entry person UUID displayed on Travel Entry Person page
    When I click on the Persons button from navbar
    And I fill UUID of the collected person from last created Travel Entry
    Then I apply on the APPLY FILTERS button
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I click on first person in person directory
    Then I check if data of created Travel Entry is in Travel Entry tab on Edit Person Page for De specific

    @tmsLink=SORDEV-7161 @env_de
    Scenario: Test DEA TravelEntry import
      Given I log in as a Admin User
      And I click on the Entries button from navbar
      When I click on the Import button from Travel Entries directory
      Then I check if import Travel Entry popup has not import option in DE version
      And I select the specific German travel entry CSV file in the file picker with "DEA_TestImport.csv" file name
      And I click on the START DATA IMPORT button from the Import Travel Entries popup
      Then I check Pick an existing case in Pick or create person popup in travel entry
      And I select to create new person from the Import Travel Entries popup DE and Save popup if needed
      Then I check if csv file for travel entry is imported successfully
      And I close Data import popup for Travel Entries
      Then I close import popup in Travel Entry
      Then I check if the New Travel Entry button is displayed in Travel Entries directory

  @tmsLink=SORDEV-8053 @env_de
  Scenario: Test Allow DEA travel entries to be imported without specifying a point of entry
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    When I click on the Import button from Travel Entries directory
    And I select the specific German travel entry CSV file in the file picker with "travelEntries_noPointOfEntry.csv" file name
    And I click on the START DATA IMPORT button from the Import Travel Entries popup
    Then I check Pick an existing case in Pick or create person popup in travel entry
    And I select to create new person from the Import Travel Entries popup DE and Save popup if needed
    And I select to create new person from the Import Travel Entries popup DE and Save popup if needed
    Then I check if csv file for travel entry is imported successfully
    And I close Data import popup for Travel Entries
    Then I close import popup in Travel Entry
    And I click on first filtered record in Travel Entry
    And I check that Point of Entry and Point of Entry details are generated automatically by system and appear on Edit Travel Entry page

    @tmsLink=SORDEV-11453 @env_de
      Scenario: [Travel Entry] Add reason for deletion to confirmation dialogue
      Given I log in as a Admin User
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form
      And I click on Save button from the new travel entry form
      When I copy url of current travel entry
      Then I click on Delete button from travel entry
      And I check if reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO" is available
      And I check if reason for deletion as "Löschen auf Anforderung einer anderen Behörde" is available
      And I check if reason for deletion as "Entität ohne Rechtsgrund angelegt" is available
      And I check if reason for deletion as "Abgabe des Vorgangs wegen Nicht-Zuständigkeit" is available
      And I check if reason for deletion as "Löschen von Duplikaten" is available
      And I check if reason for deletion as "Anderer Grund" is available
      Then I click on No option in Confirm deletion popup
      Then I click on Delete button from travel entry
      And I click on Yes option in Confirm deletion popup
      Then I check if exclamation mark with message "Bitte wählen Sie einen Grund fürs Löschen" appears next to Reason for deletion
      When I set Reason for deletion as "Anderer Grund"
      Then I check if "DETAILS ZUM GRUND DES LÖSCHENS" field is available in Confirm deletion popup in Edit Case
      And I click on Yes option in Confirm deletion popup
      Then I check if exclamation mark with message "Bitte geben Sie einen Grund fürs Löschen an" appears next to Reason for deletion
      Then I click on No option in Confirm deletion popup
      Then I click on Delete button from travel entry
      And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
      And I click on Yes option in Confirm deletion popup
      When I back to deleted travel entry by url
      Then I check if reason of deletion is set to "Löschen auf Anforderung der betroffenen Person nach DSGVO"
      And I check if External ID input on travel entry edit page is disabled

  @tmsLink=SORDEV-10227 @env_de
  Scenario: Test Permanent deletion for Person for Travel Entry
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I collect travel UUID from travel entry
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version
    And I click on the Persons button from navbar
    Then I filter the last created person linked with Travel Entry
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I navigate to the last created UI travel entry via the url
    Then I click on Delete button from travel entry
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter the last created person linked with Travel Entry
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0


    @tmsLink=SORDEV-9792 @env_de
    Scenario: Test CoreAdo: Introduce "end of processing date" for travel entries
      Given I log in as a Admin User
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form
      And I click on Save button from the new travel entry form
      And I collect travel UUID from travel entry
      Then I click on the Archive travel entry button
      Then I check the end of processing date in the archive popup and select Archive travel entry for DE version
      And I click on Travel Entry list button
      Then I apply "Abgeschlossene Einreisen" to combobox on Travel Entry Directory Page
      And I search for first created travel entry by UUID for person in Travel Entries Directory
      And I check that number of displayed Travel Entry results is 1
      Then I click on first filtered record in Travel Entry
      Then I click on the De-archive travel entry button
      And I click on confirm button in de-archive travel entry popup
      And I check if exclamation mark with message "Bitte geben Sie einen Grund für die Wiedereröffnung an" appears while trying to de-archive without reason
      And I click on discard button in de-archive travel entry popup
      Then I click on the De-archive travel entry button
      And I fill De-archive travel entry popup with test automation reason
      And I click on Travel Entry list button
      Then I apply "Aktive Einreisen" to combobox on Travel Entry Directory Page
      And I search for first created travel entry by UUID for person in Travel Entries Directory
      And I check that number of displayed Travel Entry results is 1

  @tmsLink=SORDEV-12441 @env_de
  Scenario: Hide citizenship and country of birth on Edit Travel Entry Person Page
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    And I navigate to person tab in Edit travel entry page
    Then I check that Citizenship is not visible in Contact Information section for DE version
    And I check that Country of birth is not visible in Contact Information section for DE version

    @tmsLink=SORQA-669 @env_de @oldfake
    Scenario: Check automatic deletion of TRAVEL_ENTRY origin 16 days ago
      Given API: I create a new person
      And API: I check that POST call status code is 200
      Then API: I create a new travel entry with creation date 16 days ago
      And API: I check that POST call status code is 200
      Then I log in as a Admin User
      And I open the last created travel entry via api
      And I click on the Configuration button from navbar
      Then I navigate to Developer tab in Configuration
      Then I click on Execute Automatic Deletion button
      And I wait 30 seconds for system reaction
      Then I check if created travel entry is available in API
      And API: I check that GET call status code is 204
      Then I click on the Entries button from navbar
      And I filter by last created travel entry via API
      And I check that number of displayed Travel Entry results is 0

  @tmsLink=SORQA-678 @env_de @oldfake
  Scenario: Check automatic deletion NOT of TRAVEL_ENTRY origin 13 days ago
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new travel entry with creation date 13 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    And I open the last created travel entry via api
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created travel entry is available in API
    And API: I check that GET call status code is 200
    Then I click on the Entries button from navbar
    And I filter by last created travel entry via API
    And I check that number of displayed Travel Entry results is 1

  @tmsLink=HSP-6605 @env_de
  Scenario: Check that Travel Entry tasks display the correct region and district in Task Management Directory
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new travel entry with creation date 2 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    And I open the last created travel entry via api
    And I collect uuid of the new travel entry
    And I click NEW TASK in Edit Travel Entry page
    Then I fill a new task form with specific data for DE version
    And I click on Save button in New Task form
    Then I check if new task is displayed in Task tab on Edit Travel Entry page
    And I click on the Tasks button from navbar
    And I am search the last created travel Entry by API in task management directory