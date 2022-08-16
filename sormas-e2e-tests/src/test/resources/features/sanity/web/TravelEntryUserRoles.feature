@UI @Sanity @TravelEntries @add_userroles
Feature: Create travel entries for different user roles

@tmsLink=SORDEV-10360 @env_de
Scenario Outline: Test add Date of arrival to Travel Entry and fill it when importing DEA information
  Given I log in as a <user>
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

  Examples:
    | user                      |
    | Admin User                |
    | Contact Officer           |
    | Surveillance Officer      |
    | Surveillance Supervisor   |
    | Community Officer         |