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
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create 1 new immunizations for last created person
    And API: I check that POST call body is "OK"
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
    Then I confirm case with positive test result
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
  Scenario: Test Reduced entry option for vaccinations
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
    And I click on New Event option in Link to Event Form for DE
    And I click on SAVE button in Link Event form
    And I fill event Title field on Create New Event Page
    And I set event Date filed on Create New Event form to current date for DE
    And I click SAVE button on Create New Event form
    And I click SAVE in Add Event Participant form on Edit Case Page for DE
    And I click on Edit event button for the first event in Events section
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
    Then I click Link Event button on Edit Case Page for DE
    And I click on New Event option in Link to Event Form for DE
    And I click on SAVE button in Link Event form
    And I fill event Title field on Create New Event Page
    And I set event Date filed on Create New Event form to current date for DE
    And I click SAVE button on Create New Event form
    And I click SAVE in Add Event Participant form on Edit Case Page for DE
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I set the vaccination date to 7 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I click on Edit event button for the first event in Events section
    And I click on the Event participant tab
    And I click Create Case for Event Participant
    And I pick a new case in pick or create a case popup for DE
    And I fill only mandatory fields for a new case form for DE
    And I save the new case
    And I pick a new case in pick or create a case popup for DE
    And I check that displayed vaccination card has correct vaccination date and name
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a mandatory fields for a new contact form for DE
    And I change a Report Date to the current date for DE
    And I click on SAVE button in create contact form
    And I click on CONFIRMED CONTACT radio button Contact Data tab for DE version
    And I click SAVE button on Edit Contact Page
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form with vaccination date 7 days before the current day for DE
    And I click SAVE button in new Vaccination form
    And I click Create Case from Contact button
    And I fill only mandatory fields for a new case form for DE
    And I click SAVE button on Create New Case form
    And I check that displayed vaccination card has correct vaccination date and name
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific mandatory fields with saved person details from contact for DE
    And I click on SAVE new contact button and choose same person in duplicate detection
    And I check that vaccination entry is greyed out in the vaccination card
    And I check if Vaccination Status is set to "" on Edit Case page

  @tmsLink=SORDEV-6775 @env_main
  Scenario: Test sorting columns in Immunization directory page
    Given I log in as a Admin User
    When I click on the Immunizations button from navbar
    And I check the grid for mandatory columns on the Immunization directory page
    And I sort all rows by "Immunization ID" in Immunization Directory
    And I check that column 1 is sorted alphabetically in ascending order
    And I sort all rows by "Person ID" in Immunization Directory
    And I check that column 2 is sorted alphabetically in ascending order
    And I sort all rows by "First name" in Immunization Directory
    And I check that column 3 is sorted alphabetically in ascending order
    And I sort all rows by "Last name" in Immunization Directory
    And I check that column 4 is sorted alphabetically in ascending order
    And I sort all rows by "Disease" in Immunization Directory
    And I check that column 5 is sorted alphabetically in ascending order
    And I sort all rows by "Age and birthdate" in Immunization Directory
    And I check that column 6 is sorted by age in ascending order
    And I sort all rows by "Sex" in Immunization Directory
    And I check that column 7 is sorted alphabetically in ascending order
    And I sort all rows by "District" in Immunization Directory
    And I check that column 8 is sorted alphabetically in ascending order
    And I sort all rows by "Means of immunization" in Immunization Directory
    And I check that column 9 is sorted alphabetically in ascending order
    And I sort all rows by "Management Status" in Immunization Directory
    And I check that column 10 is sorted alphabetically in ascending order
    And I sort all rows by "Immunization status" in Immunization Directory
    And I check that column 11 is sorted alphabetically in ascending order
    And I sort all rows by "Start date" in Immunization Directory
    And I check that column 12 is sorted by date in ascending order
    And I sort all rows by "End date" in Immunization Directory
    And I check that column 13 is sorted by date in ascending order
    And I sort all rows by "Type of last vaccine" in Immunization Directory
    And I check that column 14 is sorted alphabetically in ascending order
    And I sort all rows by "Date of recovery" in Immunization Directory
    And I check that column 15 is sorted by date in ascending order

  @tmsLink=SORDEV-6775 @env_main
  Scenario: Test filters in Immunization directory page
    Given I log in as a Admin User
    When I click on the Immunizations button from navbar
    And I apply Disease filter "COVID-19"
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "COVID-19" in column number 5
    And I click on the RESET FILTERS button from Immunization
    And I filter by "Thomas Boyde" as a Person's name on general text filter
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "Thomas" in column number 3
    And I click on the RESET FILTERS button from Immunization
    And I apply "Year" filter to "2020" on Immunization directory page
    And I apply "Month" filter to "1" on Immunization directory page
    And I apply "Day" filter to "1" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that row number 1 contains correct birthdate in column number 6
    And I check that row number 1 contains correct age in column number 6
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I apply "Region" filter to "Berlin" on Immunization directory page
    And I apply "District" filter to "SK Berlin Mitte" on Immunization directory page
    And I apply "Community" filter to "Zentrum" on Immunization directory page
    And I apply "Facility category" filter to "Medical facility" on Immunization directory page
    And I apply "Facility type" filter to "Hospital" on Immunization directory page
    And I apply "Facility" filter to "Other facility" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "SK Berlin Mitte" in column number 8
    And I click on the RESET FILTERS button from Immunization
    And I apply "Means of immunization" filter to "Vaccination" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "Vaccination" in column number 9
    And I click on the RESET FILTERS button from Immunization
    And I apply "Management status" filter to "Scheduled" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "Scheduled" in column number 10
    And I click on the RESET FILTERS button from Immunization
    And I apply "Immunization status" filter to "Pending" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "Pending" in column number 11
    And I click on the RESET FILTERS button from Immunization
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I apply "Immunization reference date" filter to "Date of report" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date To" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I open first immunization from grid from Immunization tab
    And I check if "date of report" field is set for 1 day ago from today on Edit Immunization page
    And I back to the immunization list
    And I click on the RESET FILTERS button from Immunization
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I apply "Immunization reference date" filter to "End of immunization" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains 1 day ago from today date in column number 13
    And I open first immunization from grid from Immunization tab
    And I fill VALID FROM to 1 day ago from today
    And I fill VALID UNTIL to 1 day ago from today
    And I click SAVE button on Edit Immunization Page
    And I close immunization data popup alert message in Edit Immunization page
    And I back to the immunization list
    And I click on the RESET FILTERS button from Immunization
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I apply "Immunization reference date" filter to "Valid until" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date To" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I open first immunization from grid from Immunization tab
    And I check if "valid until" field is set for 1 day ago from today on Edit Immunization page
    And I back to the immunization list
    And I apply "Immunization reference date" filter to "Date of recovery" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date To" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains 1 day ago from today date in column number 15
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    Then I check that number of added Vaccinations is 1
    Then I set Number of doses to 3 on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    And I back to the immunization list
    And I apply "Immunization reference date" filter to "Date of last vaccination" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date To" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I click on the RESET FILTERS button from Immunization
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I apply "Immunization reference date" filter to "Date of first vaccination" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date From" on Immunization directory page
    And I set 1 day ago from today as a Immunization reference "Date To" on Immunization directory page
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I click on the RESET FILTERS button from Immunization
    And I click SHOW MORE FILTERS button on Immunization directory page
    And I click on checkbox to only show persons with overdue immunization
    And I click on the APPLY FILTERS button
    And I check that number of displayed immunization results in grid is more than 0
    And I check that the row number 1 contains "Ongoing" in column number 10
    And I check that the row number 1 contains any date before current day in column 13

  @tmsLink=SORDEV-6775 @env_main
  Scenario: Test New Immunization form
    Given I log in as a Admin User
    When I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    And I check that required fields are marked as mandatory on Create new immunization form
    And I check if Management status is "read only" on Create new immunization form
    And I check if Management status is set to "Scheduled" on Create new immunization form
    And I check if Immunization status is set to "Pending" on Create new immunization form
    And I check if "MEANS OF IMMUNIZATION combobox" is available on Create new immunization form
    And I select "Other" means of immunization on Create new immunization form
    And I check if "MEANS OF IMMUNIZATION DETAILS field" is available on Create new immunization form
    And I select "Recovery" means of immunization on Create new immunization form
    And I check if Management status is set to "Completed" on Create new immunization form
    And I check if "OVERWRITE IMMUNIZATION MANAGEMENT STATUS checkbox" is available on Create new immunization form
    And I check Overwrite immunization management status option
    And I check if Management status is "editable" on Create new immunization form
    And I select "Ongoing" management status on Create new immunization form
    And I check if Immunization status is set to "Pending" on Create new immunization form
    And I select "Completed" management status on Create new immunization form
    And I check if Immunization status is set to "Acquired" on Create new immunization form
    And I select "Canceled" management status on Create new immunization form
    And I check if Immunization status is set to "Not acquired" on Create new immunization form
    And I check if "EXTERNAL ID field" is available on Create new immunization form
    And I check if "RESPONSIBLE REGION field" is available on Create new immunization form
    And I check if "RESPONSIBLE DISTRICT field" is available on Create new immunization form
    And I check if "RESPONSIBLE COMMUNITY field" is available on Create new immunization form
    And I check if "FACILITY field" is available on Create new immunization form
    And I check if "START DATE field" is available on Create new immunization form
    And I check if "END DATE field" is available on Create new immunization form
    And I fill a new immunization form with specific data
    And I click on SAVE new immunization button
    And I check the created data is correctly displayed on Edit immunization page

  @tmsLink=SORDEV-6775 @env_main
  Scenario: Test Edit Immunization page
    Given I log in as a National User
    When I click on the Immunizations button from navbar
    And I open first immunization from grid from Immunization tab
    Then I check that "IMMUNIZATION" tab is available
    And I check that "PERSON" tab is available
    And I check if Reporting user field is available and read only
    And I check if "Previous infection with this disease combobox" is available
    When I set Previous infection with this disease combobox to "YES" on Edit Immunization page
    Then I check if "Date of last infection field" is available
    When I set Previous infection with this disease combobox to "NO" on Edit Immunization page
    And I set Previous infection with this disease combobox to "UNKNOWN" on Edit Immunization page
    Then I check if "Additional details text area" is available
    And I set Means of immunization to "Vaccination" on Edit Immunization page
    Then I check if "Vaccination header" is available
    When I set Means of immunization to "Vaccination/Recovery" on Edit Immunization page
    Then I check if "Vaccination header" is available
    And I check if "Number of doses field" is available
    When I set Number of doses to value different than Integer on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    Then I confirm alert popup window informing that could not convert value to integer
    When I set Means of immunization to "Recovery" on Edit Immunization page
    Then I check if "Recovery header" is available
    And I check if "Positive test result date field" is available
    And I check if "Recovery date field" is available
    When I set Means of immunization to "Vaccination/Recovery" on Edit Immunization page
    Then I check if "Recovery header" is available
    And I check if "Positive test result date field" is available
    And I check if "Recovery date field" is available