@UI @Sanity @Immunization
Feature: Immunization end to end tests

  @tmsLink=SORDEV-8705 @env_main
  Scenario:Check a new immunization data
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on Person tab from Immunization page
    Then I check the created data is correctly displayed on Edit immunization person page

  @tmsLink=SORDEV-9312 @env_main
  Scenario: Reset the 'Overwrite immunization management status' by Discard button
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I open first immunization from grid from Immunization tab
    Then I check Overwrite immunization management status option
    Then I click on discard button from immunization tab
    And I check if Overwrite immunization management status is unchecked by Management Status

  @tmsLink=SORDEV-7038 @env_main
  Scenario:Test Immunizations III: Vaccination lists and forms
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I check that Vaccination ID is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccination date is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine name is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine manufacturer is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine type is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine dose is visible in Vaccinations tab on Edit Immunization Page
    And I click the header of column 2 of Vaccination table
    And I click the header of column 3 of Vaccination table
    And I click the header of column 4 of Vaccination table
    And I click the header of column 5 of Vaccination table
    And I click the header of column 6 of Vaccination table
    And I click the header of column 7 of Vaccination table
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    Then I check that number of added Vaccinations is 1
    Then I set Number of doses to 11 on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    Then I check if exceeded number of doses error popup message appeared
    And I set Number of doses to 3 on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Ongoing"
    And I check if Immunization status is set to "Pending"
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Completed"
    And I check if Immunization status is set to "Acquired"
    And I click to edit 1 vaccination on Edit Immunization page
    Then I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination
    And I check that number of added Vaccinations is 2
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Ongoing"
    And I check if Immunization status is set to "Pending"

  @env_main @#8565
  Scenario: Check an archived immunization if its read only
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call status code is 200
    Then API: I create 1 new immunizations for last created person
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I navigate to last created immunization by API via URL
    Then I click on archive button from immunization tab
    Then I click on logout button from navbar
    Then I log in as a National User
    Then I navigate to last created immunization by API via URL
    Then I check if editable fields are read only for an archived immunization

  @tmsLink=SORDEV-7041 @env_main
  Scenario:Test Immunizations III: Immunization list for person forms
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on Person tab from Immunization page
    Then I check the created data is correctly displayed on Edit immunization person page
    Then I click on the Persons button from navbar
    And I filter by Person full name from Immunization on Person Directory Page
    And I click on the APPLY FILTERS button
    And I click Immunization aggregation button on Person Directory Page
    Then I click on first person in person directory
    And I check if data of created immunization is in Immunization tab on Edit Person Page

  @tmsLink=SORDEV-11454 @env_main
  Scenario: Add reason for deletion to confirmation dialogue
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I copy url of current immunization case
    And I click SAVE button on Edit Immunization Page
    Then I click on Delete button from immunization case
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Deletion request by another authority" is available
    And I check if reason for deletion as "Entity created without legal reason" is available
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Responsibility transferred to another authority" is available
    And I check if reason for deletion as "Deletion of duplicate entries" is available
    And I check if reason for deletion as "Other reason" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please choose a reason for deletion" appears next to Reason for deletion
    When I set Reason for deletion as "Other reason"
    Then I check if "Reason for deletion details" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please add a reason for deletion" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted immunization case by url
    Then I check if reason of deletion is set to "Deletion request by affected person according to GDPR"
    And I check if External ID input on immunization edit page is disabled
    And I check if Additional details text area on immunization edit page is disabled

  @tmsLink=SORDEV-8059 @env_main
  Scenario: Check the extension of the case form, contact form and event participant form with immunization list
    Given I log in as a National user
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data
    And I collect uuid of the case
    And I click on the NEW IMMUNIZATION button in Edit case
    When I fill mandatory fields and immunization period in a new immunization popup
    Then I check the specific created data with immunization period is correctly displayed on Edit immunization page
    And I click on the Cases button from navbar
    And I filter with first Case ID
    And I click on the first Case ID from Case Directory
    And I validate immunization period is present on immunization card
    And I validate immunization status is present on immunization card
    And I validate management status is present on immunization card
    And I validate means of immunization is present on immunization card
    And I validate immunization UUID is present on immunization card
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I collect the contact person UUID displayed on Edit contact page
    And I click on the NEW IMMUNIZATION button in Edit contact
    And I fill mandatory fields and immunization period in a new immunization popup
    And I check the specific created data with immunization period is correctly displayed on Edit immunization page
    And I click on the Contacts button from navbar
    And I filter by last collected from UI specific Contact uuid
    And I click on the first Contact ID from Contacts Directory
    And I validate immunization period is present on immunization card
    And I validate immunization status is present on immunization card
    And I validate management status is present on immunization card
    And I validate means of immunization is present on immunization card
    And I validate immunization UUID is present on immunization card
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I back to the Event tab
    And I collect the UUID displayed on Edit event page
    And I set Disease combobox to "" value from Edit Event Page
    And I click on Save Button in Edit Event directory
    And I add a participant to the event
    And I click on Event Participant Person tab
    And I check that New immunization card is not available
    And I click on the Event participant tab
    And I back to the Event tab
    And I set Disease combobox to "COVID-19" value from Edit Event Page
    And I click on Save Button in Edit Event directory
    And I click on the Event participant tab
    And I click on the created event participant from the list
    And I click on the NEW IMMUNIZATION button in Edit event participant
    And I fill mandatory fields and immunization period in a new immunization popup
    And I check the specific created data with immunization period is correctly displayed on Edit immunization page
    And I click on the Events button from navbar
    And I search for the collected event uuid
    And I click on the searched event
    And I click on the Event participant tab
    And I click on the first result in table from event participant
    And I validate immunization period is present on immunization card
    And I validate immunization status is present on immunization card
    And I validate management status is present on immunization card
    And I validate means of immunization is present on immunization card
    And I validate immunization UUID is present on immunization card

  @tmsLink=SORDEV-8061 @env_main
  Scenario: Immunizations V: Link recovery immunizations to recovered cases
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect uuid of the case
    Then In created case I select Outcome Of Case Status to Recovered
    And I check if date of outcome filed is available
    Then I fill the Date of outcome to yesterday
    And I click on Save button in Case form
    And I click on New Sample
    Then I create a new Sample with positive test result with COVID-19 as disease
    And I confirm case with positive test result
    And I click on yes in Confirm case popup window
    And I click on the NEW IMMUNIZATION button in Edit case
    Then I fill only mandatory fields in immunization popup with means of immunization as a "Recovery"
    Then I click on Link Case button
    And I fill filed with collected case in Search specific case popup
    And I click on Search case in Search specific case popup in immunization Link Case
    Then I check if case was found in Link Case
    And I click Okay in Case Found in Immunization Link Case popup
    Then I check if Open Case button exists in Immunization edit page
    And I check if Date of first positive result is equal with created pathogen test
    And I check if Date of recovery is equal with created case
    Then I click on Open Case button in Edit immunization
    Then I check if collected case UUID is equal with current

  @tmsLink=SORDEV-8536 @env_main
  Scenario: Test pseudonymization in immunization
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case form with specific data
    And I click on save case button
    And I check the created data is correctly displayed on Edit case page
    And I click on the NEW IMMUNIZATION button from Edit case page
    And I fill a new immunization form with "Berlin" as a responsible region and "SK Berlin Mitte" as a responsible district
    And I click on save button in New Immunization form
    And I check the specific created data is correctly displayed on Edit immunization page
    And I click on logout button from navbar
    And I log in as a Surveillance Officer
    Then I click on the Cases button from navbar
    And I filter by CaseID of last created UI Case on Case directory page
    And I click on the first Case ID from Case Directory
    And I navigate to linked immunization on Edit case page
    Then I check that Immunization data is displayed as read-only on Edit immunization page

  @tmsLink=SORDEV-8759 @env_de
  Scenario: Test Reduced entry option for vaccinations[1]
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    And I set current date as a date of report on Edit case page for DE version
    And I click on save button from Edit Case page
    And I collect uuid of the case
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form with vaccination date 7 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination card has correct vaccination date and name
    Then I check if an edit icon is available on vaccination card on Edit Case page
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I navigate to symptoms tab
    And I set Fever Symptoms to "JA" on the Symptoms tab
    And I set Date of symptom onset to 7 days before the vaccination date on the Symptoms tab for DE
    And I click on save case button in Symptoms tab
    And I navigate to case tab
    Then I check that vaccination entry is greyed out in the vaccination card
    And I check if an edit icon is available on vaccination card on Edit Case page
    And I check the displayed message is correct after hovering over the Vaccination Card Info icon on Edit Case Page for DE
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that the vaccination card displays "KEIN IMPFDATUM" in place of the vaccination date
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I set the vaccination date 7 days before the date of symptom in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination card has correct vaccination date and name
    Then I click Link Event button on Edit Case Page for DE
    And I click on New Event option in Link to Event Form
    And I click on SAVE button in Link Event form
    And I fill event Title field on Create New Event Page
    And I set event Date field on Create New Event form to current date for DE
    And I click SAVE button on Create New Event form
    And I click SAVE in Add Event Participant form on Edit Case Page for DE
    And I click on first Edit event button for in Events section
    And I click on the Event participant tab
    And I click on the first row from event participant
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check that displayed vaccination card has correct vaccination date and name
    And I click on the Event participant tab
    And I back to the Event tab
    And I set event Date on Edit Event Page to 7 days before the vaccination date for DE
    And I click on save button on Create New Event Page
    And I click on the Event participant tab
    And I click on the first row from event participant
    And I check that vaccination entry is greyed out in the vaccination card
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I set the vaccination date to the same date as the vaccination report date
    And I click SAVE button in new Vaccination form
    And I check that vaccination entry is greyed out in the vaccination card
    And I check the displayed message is correct after hovering over the Vaccination Card Info icon on Event Participant Directory for DE
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I set the vaccination date to 35 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I filter with first Case ID
    And I click on the first Case ID from Case Directory
    And I click on the Epidemiological data button tab in Case form
    And I click on Contacts with source case known with JA option for DE
    And I click on save button from Epidemiological Data
    And I check that Contacts with source case known card is available
    And I click on the NEW CONTACT button on Epidemiological Data Tab of Edit Case Page
    And I fill only mandatory fields in New contact from Contacts with source case card for DE
    And I click on SAVE button in create contact form
    And I click on Edit Contact button from Contacts with source case card for DE
    And I check that displayed vaccination card has correct vaccination date and name
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I set the last contact date to 7 days before the vaccination date
    And I click SAVE button on Edit Contact Page
    And I check that vaccination entry is greyed out in the vaccination card
    And I check the displayed message is correct after hovering over the Vaccination Card Info icon on Edit Contact Page for DE

  @tmsLink=SORDEV-8759 @env_de
  Scenario: Test Reduced entry option for vaccinations[2]
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I fill event Title field on Create New Event Page
    And I set event Date field on Create New Event form to current date for DE
    And I click SAVE button on Create New Event form
    And I add a participant to the event in DE
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form with vaccination date 7 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I click on the Event participant tab
    And I click Create Case for Event Participant
    And I fill only mandatory fields for a new case form for DE
    And I save the new case
    And I check that displayed vaccination card has correct vaccination date and name
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a mandatory fields for a new contact form for DE
    And I change a Report Date to the current date for DE
    And I click on SAVE button in create contact form
    And I select CONFIRMED CONTACT radio button on Contact Data tab for DE version
    And I click SAVE button on Edit Contact Page
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form with vaccination date 7 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I click Create Case from Contact button
    And I fill only mandatory fields to convert a contact into a case for DE
    And I click SAVE button on Create New Case form
    And I check that displayed vaccination card has correct vaccination date and name
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific mandatory fields with saved person details from contact for DE
    And I click on SAVE new contact button and choose same person in duplicate detection
    And I check if Vaccination Status is set to "" on Edit Case page
    And I check that vaccination entry is greyed out in the vaccination card

  @tmsLink=SORQA-668 @env_de @oldfake
    Scenario: Check automatic deletion of IMMUNIZATION created 3651 days ago
    Given API: I create a new person
    And API: I check that POST call status code is 200
    When API: I create a new immunizations for last created person with creation date 3651 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created immunization is available in API
    And API: I check that GET call status code is 204

  @tmsLink=SORQA-677 @env_de @oldfake
  Scenario: Check automatic deletion NOT of IMMUNIZATION created 3645 days ago
    Given API: I create a new person
    And API: I check that POST call status code is 200
    When API: I create a new immunizations for last created person with creation date 3645 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created immunization is available in API
    And API: I check that GET call status code is 204