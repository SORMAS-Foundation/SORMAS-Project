@UI @Sanity @Vaccination @Filters
Feature: Vaccination tests

  @tmsLink=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Contact
    When API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Contact
    And I pick the "VaccinationGenerationTest_Contacts.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    Then I open the last created contact via API
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_Contacts.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Contact based on "VaccinationGenerationTest_Contacts.docx" was downloaded properly
    And I check if generated document for contact based on "VaccinationGenerationTest_Contacts.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_Contacts.docx" Document Template for Contact

  @tmsLink=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Case
    When API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Case
    And I pick the "VaccinationGenerationTest_Cases.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    And I open the last created Case via API
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_Cases.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Case based on "VaccinationGenerationTest_Cases.docx" was downloaded properly
    And I check if generated document for Case based on "VaccinationGenerationTest_Cases.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_Cases.docx" Document Template

  @tmsLink=SORDEV-9585 @env_de
  Scenario: Test Add reduced vaccination module to document creation for Event
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    And I navigate to document templates tab
    When I click on the UPLOAD TEMPLATE button from Document Templates Event Participant
    And I pick the "VaccinationGenerationTest_EventParticipants.docx" file
    And I click on the UPLOAD TEMPLATE button from the popup
    And I confirm the document template overwrite popup
    Then I check that an upload success notification appears
    And I click to close UPLOAD TEMPLATE popup
    And I open the last created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    Then I add only required data for event participant creation for DE
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    Then I click on Create button in Document Templates box for DE
    And I select "VaccinationGenerationTest_EventParticipants.docx" template in Document Template form
    And I click on Create button in Document Templates popup for DE
    Then I check if generated document for Event Participant based on "VaccinationGenerationTest_EventParticipants.docx" was downloaded properly
    And I check if generated document for Event Participant based on "VaccinationGenerationTest_EventParticipants.docx" contains all required fields
    Then I delete downloaded file created from "VaccinationGenerationTest_EventParticipants.docx" Document Template for Event Participant

  #leading case - only vaccination date and name
  #discarded case - all fields and same vaccination name and date
  #expected - one vaccination updated with the information given by the vaccination of the discarded case
  @tmsLink=SORDEV-11570 @env_de
  Scenario: Duplicate detection for vaccinations when merging cases[1]
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I collect the leading case UUID displayed on Case Directory Page
    And I open 1 case in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 2 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Fallmeldedatum" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case created through line listing in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 1 case in order from list
    And I check that number of added Vaccinations is 1
    And I click to edit 1 vaccination on Edit Case page
    And I check that displayed data in form is equal to whole data from duplicated entry

  #leading case - all fields and same vaccination name and date
  #discarded case - all fields with different value than leading case and same vaccination name and date
  #expected - one vaccination updated with the information given by the vaccination of the leading case
  @tmsLink=SORDEV-11570 @env_de
  Scenario: Duplicate detection for vaccinations when merging cases[2]
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I collect the leading case UUID displayed on Case Directory Page
    And I open 1 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 2 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I set new vaccination name the same as duplicate for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Fallmeldedatum" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case created through line listing in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 1 case in order from list
    And I check that number of added Vaccinations is 1
    And I click to edit 1 vaccination on Edit Case page
    And I check that displayed vaccination date in form is equal to name from duplicated entry

  #leading case - only vaccination date
  #discarded case - only vaccination date same as leading
  #expected - two vaccinations with same vaccination date
  @tmsLink=SORDEV-11570 @env_de
  Scenario: Duplicate detection for vaccinations when merging cases[3]
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I collect the leading case UUID displayed on Case Directory Page
    And I open 1 case in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 2 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Fallmeldedatum" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case created through line listing in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 1 case in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Case page
    And I check that displayed vaccination date in form is equal to date from duplicated entry
    And I close vaccination form in Edit Case directory
    And I click to edit 2 vaccination on Edit Case page
    And I check that displayed vaccination date in form is equal to date from duplicated entry

  #leading case - only vaccination name
  #discarded case - only vaccination name same as leading
  #expected - two vaccinations with same vaccination name
  @tmsLink=SORDEV-11570 @env_de
  Scenario: Duplicate detection for vaccinations when merging cases[4]
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I collect the leading case UUID displayed on Case Directory Page
    And I open 1 case in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 2 case in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Fallmeldedatum" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case created through line listing in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 1 case in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Case page
    And I check that displayed vaccination date in form is equal to name from duplicated entry
    And I close vaccination form in Edit Case directory
    And I click to edit 2 vaccination on Edit Case page
    And I check that displayed vaccination date in form is equal to name from duplicated entry

  #leading case - everything but vaccination date and name
  #discarded case - everything but vaccination date and name
  #expected - two vaccinations without vaccination name and date
  @tmsLink=SORDEV-11570 @env_de
  Scenario: Duplicate detection for vaccinations when merging cases[5]
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    When I click on the Cases button from navbar
    Then I click on Line Listing button
    And I create a new duplicate case in line listing feature popup for DE version
    And I save the new line listing case
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I collect the leading case UUID displayed on Case Directory Page
    And I open 1 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I click SAVE button in new Vaccination form
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 2 case in order from list
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I click SAVE button in new Vaccination form
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Fallmeldedatum" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case created through line listing in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I filter by Person's full name of last created duplicated line listing case on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I open 1 case in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Case page
    And I check that displayed vaccination form has empty vaccination date and name
    And I close vaccination form in Edit Case directory
    And I click to edit 2 vaccination on Edit Case page
    And I check that displayed vaccination form has empty vaccination date and name

  #leading contact - only vaccination date and name
  #discarded contact - all fields and same vaccination name and date
  #expected - one vaccination updated with the information given by the vaccination of the discarded contact
  @tmsLink=SORDEV-11753 @env_de
  Scenario: Duplicate detection for vaccinations when merging contacts [1]
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 2 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I click on the Contacts button from navbar
    And I collect the leading contact UUID displayed on Contact Directory Page
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Contact page
    And I click on Merge button of leading duplicated line listing Contact in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I check that number of added Vaccinations is 1
    And I click to edit 1 vaccination on Edit Contact page
    And I check that displayed data in form is equal to whole data from duplicated entry

  #leading contact - all fields and same vaccination name and date
  #discarded contact - all fields with different value than leading contact and same vaccination name and date
  #expected - one vaccination updated with the information given by the vaccination of the leading contact
  @tmsLink=SORDEV-11753 @env_de
  Scenario: Duplicate detection for vaccinations when merging contacts [2]
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    When I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 2 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I set new vaccination name the same as duplicate for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Contacts button from navbar
    And I collect the leading contact UUID displayed on Contact Directory Page
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Contact page
    And I click on Merge button of leading duplicated line listing Contact in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I check that number of added Vaccinations is 1
    And I click to edit 1 vaccination on Edit Contact page
    And I check that displayed vaccination date in form is equal to name from duplicated entry

  #leading contact - only vaccination date
  #discarded contact - only vaccination date same as leading
  #expected - two vaccinations with same vaccination date
  @tmsLink=SORDEV-11753 @env_de
  Scenario: Duplicate detection for vaccinations when merging contacts [3]
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 2 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data for duplicates in new Vaccination form for DE
    And I set new vaccination date the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Contacts button from navbar
    And I collect the leading contact UUID displayed on Contact Directory Page
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Contact page
    And I click on Merge button of leading duplicated line listing Contact in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Contact page
    And I check that displayed vaccination date in form is equal to date from duplicated entry
    And I close import popup in Edit Contact directory
    And I click to edit 2 vaccination on Edit Contact page
    And I check that displayed vaccination date in form is equal to date from duplicated entry

  #leading contact - only vaccination name
  #discarded contact - only vaccination name same as leading
  #expected - two vaccinations with same vaccination name
  @tmsLink=SORDEV-11753 @env_de
  Scenario: Duplicate detection for vaccinations when merging contacts[4]
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    When I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 2 contact in order from list
    And I click NEW VACCINATION button for DE
    And I set new vaccination name the same as duplicate for DE
    And I click SAVE button in new Vaccination form
    And I click on the Contacts button from navbar
    And I collect the leading contact UUID displayed on Contact Directory Page
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I filter by Case ID used during Contact creation
    And I click APPLY BUTTON in Merge Duplicates View on Contact Directory Page
    And I click on Merge button of first leading Contact in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Contact page
    And I check that displayed vaccination date in form is equal to name from duplicated entry
    And I close import popup in Edit Contact directory
    And I click to edit 2 vaccination on Edit Contact page
    And I check that displayed vaccination date in form is equal to name from duplicated entry

  #leading contact - everything but vaccination date and name
  #discarded contact - everything but vaccination date and name
  #expected - two vaccinations without vaccination name and date
  @tmsLink=SORDEV-11753 @env_de
  Scenario: Duplicate detection for vaccinations when merging contacts[5]
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I create a new Contact with specific data through Line Listing with duplicated data for De
    And I save the new contact using line listing feature
    And I Pick a new person in Pick or create person popup during contact creation for DE
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I click SAVE button in new Vaccination form
    When I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 2 contact in order from list
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I click SAVE button in new Vaccination form
    And I click on the Contacts button from navbar
    And I collect the leading contact UUID displayed on Contact Directory Page
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I filter by Case ID used during Contact creation
    And I click APPLY BUTTON in Merge Duplicates View on Contact Directory Page
    And I click on Merge button of first leading Contact in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I filter by Person's full name of last created duplicated line listing contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open 1 contact in order from list
    And I check that number of added Vaccinations is 2
    And I click to edit 1 vaccination on Edit Contact page
    And I check that displayed vaccination form has empty vaccination date and name
    And I close import popup in Edit Contact directory
    And I click to edit 2 vaccination on Edit Contact page
    And I check that displayed vaccination form has empty vaccination date and name

  @tmsLink=SORDEV-12127 @env_de
  Scenario: Test Vaccinations without a "vaccination date" should also be marked as relevant and included in the status calculation (Case)
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data for DE version with date 16 days ago
    And I click SAVE button on Create New Case form
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I change the report vaccination date for minus 9 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I change the report vaccination date for minus 1 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "greyed out" on Edit case page
    And I navigate to symptoms tab
    And I set Fever Symptoms to "JA" on the Symptoms tab
    And I set Date of symptom onset to 15 days before today
    And I save the Symptoms data
    And I navigate to case tab
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I change the report vaccination date for minus 0 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page

  @tmsLink=SORDEV-12286 @env_de
  Scenario: Test Vaccinations without a "vaccination date" should also be marked as relevant and included in the status calculation (Contact)
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new Contact form with specific data for DE version with date 16 days ago
    And I click on SAVE new contact button
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit contact page
    And I check that displayed vaccination name is "enabled" on Edit contact page
    And I click on the Edit Vaccination icon on vaccination card on Edit contact page
    And I change the report vaccination date for minus 9 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit contact page
    And I check that displayed vaccination name is "enabled" on Edit contact page
    And I check if Vaccination Status is set to "Geimpft" on Edit Contact page
    And I click on the Edit Vaccination icon on vaccination card on Edit contact page
    And I change the report vaccination date for minus 1 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit contact page
    And I check that displayed vaccination name is "greyed out" on Edit contact page
    And I set the last contact date for minus 17 days from today for DE version
    And I click SAVE button on Edit Contact Page
    And I click on the Edit Vaccination icon on vaccination card on Edit contact page
    And I change the report vaccination date for minus 9 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit contact page
    And I check that displayed vaccination name is "enabled" on Edit contact page
    And I set the last contact date for minus 17 days from today for DE version
    And I click SAVE button on Edit Contact Page
    And I click on the Edit Vaccination icon on vaccination card on Edit contact page
    And I change the report vaccination date for minus 1 day from today
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit contact page
    And I check that displayed vaccination name is "greyed out" on Edit contact page

  @tmsLink=SORDEV-12605 @env_de
  Scenario Outline: Test that the vaccination status changes from VACCINATED to blank after the vaccination is removed
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data for DE version with date 14 days ago
    And I click SAVE button on Create New Case form
    And I check if date of report is set for 14 day ago from today on Edit Case page for DE version
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination for DE version
    And I choose "JA" in Vaccination Status update popup for DE version
    And I check if Vaccination Status is set to "" on Edit Case page
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination for DE version
    And I choose "NEIN" in Vaccination Status update popup for DE version
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check that vaccination is removed from vaccination card on Edit Case page
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data for DE version with date 14 days ago
    And I click SAVE button on Create New Case form
    And I check if date of report is set for 14 day ago from today on Edit Case page for DE version
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 16 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "enabled" on Edit case page
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 0 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)" on Edit case page
    And I check that displayed vaccination name is "greyed out" on Edit case page
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination for DE version
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination for DE version
    And I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on the Edit Vaccination icon on vaccination card on Edit Case page
    And I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination for DE version
    And I choose "<option>" in Vaccination Status update popup for DE version
    And I check if Vaccination Status is set to <result> on Edit Case page

    Examples:
      | option | result |
      | JA | ""  |
      | NEIN | "Geimpft" |

  @tmsLink=SORQA-238 @env_de
  Scenario: Test Adjusted vaccination status calculation for Case
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data and report date set to yesterday for DE version
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "Comirnaty (COVID-19-mRNA Impfstoff)"
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    When I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I set the vaccination date to 1 days before today
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page

  @tmsLink=SORQA-238 @env_de
  Scenario: Test Adjusted vaccination status calculation for Contact
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new Contact form with specific data for DE version with date 0 days ago
    And I set the last contact date for minus 1 days from today for DE version
    And I click on SAVE new contact button
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "Comirnaty (COVID-19-mRNA Impfstoff)"
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    When I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I set the vaccination date to 1 days before today
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check if vaccination name for vaccine number 2 in the vaccination card is "greyed out"
    When I remove tha last contact date on Edit Contact page
    And I click SAVE button on Edit Contact Page
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check if vaccination name for vaccine number 2 in the vaccination card is "enabled"
    When I click SAVE button on Edit Contact Page
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I set the vaccination date to 3 days before today
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page

  @tmsLink=SORQA-238 @env_de
  Scenario: Test Adjusted vaccination status calculation for Event
    When I log in as a Admin User
    And I click on the Events button from navbar
    And I create a new event with mandatory fields for DE version
    And I add a participant to the event in DE
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    When I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I set the vaccination date to 1 days before today
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check if vaccination name for vaccine number 2 in the vaccination card is "greyed out"
    When I navigate to Event Participants tab in Edit case page
    And I back to the Event tab
    And I remove the event date on Edit Event page
    And I click on Save Button in Edit Event directory
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click on the first row from event participant
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I check if vaccination name for vaccine number 2 in the vaccination card is "enabled"
    And I navigate to EVENT PARTICIPANT from edit event page
    And I back to the Event tab
    And I set event date field to 1 days before today on Event Edit page for DE
    And I click on Save Button in Edit Event directory
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click on the first row from event participant
    And I click NEW VACCINATION button for DE
    And I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name
    And I set new vaccination name to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I set the vaccination date to 3 days before today
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page