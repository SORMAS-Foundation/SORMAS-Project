@UI @Sanity @Contacts
Feature: Contacts end to end tests

  @env_main
  Scenario: Create simple contact
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    Then I check the created data is correctly displayed on Edit Contact page
    Then I open Contact Person tab
    And I check the created data is correctly displayed on Edit Contact Person page

  @env_main
  Scenario: Delete created contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by name and uuid then open
    Then I delete the contact
    And I check that number of displayed contact results is 0

  @env_main
  Scenario: Edit a created contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by UUID and open
    And I change all contact fields and save
    And I navigate to the last created contact via the url
    Then I check the edited data is correctly displayed on Edit Contact page after editing

  @issue=SORDEV-5476 @env_main
    Scenario: Add a task from contact and verify the fields
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window
    Then I click SAVE button on Edit Contact Page
    And I click on the Tasks button from navbar
    Then I search created task by Contact first and last name
    And I open the last created UI Contact
    Then I check the created data is correctly displayed on Edit Contact page related with CHOSEN SOURCE CASE

  @env_main
  Scenario: Source case selected for contact
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I navigate to the last created contact via the url
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid in the CHOOSE SOURCE window
    And I open the first found result in the CHOOSE SOURCE window
    Then I check the linked case information is correctly displayed
    When I open the Case Contacts tab of the created case via api
    Then I check the linked contact information is correctly displayed

  @env_main
  Scenario: Change the source case contact and then delete
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I navigate to the last created contact via the url
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid in the CHOOSE SOURCE window
    And I open the first found result in the CHOOSE SOURCE window
    Then I check the linked case information is correctly displayed
    When I open the Case Contacts tab of the created case via api
    Then I check the linked contact information is correctly displayed
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I navigate to the last created contact via the url
    And I click on the CHANGE CASE button
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid in the CHOOSE SOURCE window
    And I open the first found result in the CHOOSE SOURCE window
    Then I check the linked case information is correctly displayed
    When I click on the Remove Case CTA
    And I click yes on the CONFIRM REMOVAL popup from CONTACT page
    Then I check the CHOOSE SOURCE CASE BUTTON is displayed

  @env_main
    Scenario: Create Contact and check details in Detailed view table
      Given API: I create a new person
      Then API: I check that POST call body is "OK"
      And API: I check that POST call status code is 200
      Then API: I create a new contact
      Then API: I check that POST call body is "OK"
      And API: I check that POST call status code is 200
      When I log in with National User
      Then I click on the Contacts button from navbar
      And I click on the DETAILED radiobutton from Contact directory
      And I filter by Contact uuid
      Then I am checking if all the fields are correctly displayed in the Contacts directory Detailed table

  @env_main
  Scenario: Edit all fields from Follow-up visits tab
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the Follow-up visits tab using of created contact via api
    Then I click on New visit button from Follow-up visits tab
    And I create a new Follow-up visit
    And I open the first displayed follow up
    Then I validate recently created follow up is correctly displayed
    And I click on discard button from follow up view
    And I open the first displayed follow up
    And I change Follow-up visit fields and save
    Then I check all changes from follow up are correctly displayed
    And I am accessing the contacts from New Visit
    And I open Follow up Visits tab from contact directory
    Then I am validating the From and To dates displayed

  @issue=SORDEV-5490 @env_main
  Scenario: Create a contact and create a case for contact person
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    And I check case created from created contact is correctly displayed on Edit Case page

  @issue=SORDEV-5496 @env_main
  Scenario: Generate and download Contact document
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I open the first contact from contacts list
    And I click on the Create button from Contact Document Templates
    When I create and download a contact document from template
    Then I verify that the contact document is downloaded and correctly named

    @issue=SORDEV-5470 @env_main
  Scenario: Create complex contact
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click CHOOSE CASE button
    And I search for the last case uuid in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE Contact window
    And I click on SAVE new contact button
    Then I check the linked case information is correctly displayed
    And I check the created data for complex contact is correctly displayed on Edit Contact page
    Then I open Contact Person tab
    And I check the created data is correctly displayed on Edit Contact Person page

  @issue=SORDEV-5641 @env_main
  Scenario: Fill the epidemiological data tab in Contacts
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by UUID and open
    And I click on the Epidemiological Data button tab in Contact form
    And I click on Exposure details known with UNKNOWN option
    And I click on Exposure details known with NO option
    Then I fill all the data in Exposure for Epidemiological data tab in Contacts
    Then I click on Residing or working in an area with high risk of transmission of the disease in Contact with UNKNOWN option
    And I click on Residing or working in an area with high risk of transmission of the disease in Contact with NO option
    And I click on Residing or working in an area with high risk of transmission of the disease in Contact with YES option
    Then I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission in Contact with UNKNOWN option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission in Contact with NO option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission in Contact with YES option
    And I click on save button from Epidemiological Data
    Then I am checking all Exposure data is saved and displayed in Contacts
    And I am checking if options in checkbox for Contact are displayed correctly

  @issue=SORDEV-5670 @env_main
  Scenario: Fill the follow-up tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I am accessing the Follow-up visits tab using of created contact via api
    And I click on new Visit button
    Then I set Person available and cooperative to UNAVAILABLE
    And I set Date and time of visit
    Then I save the Visit data
    And I click on edit Visit button
    Then I check last Person status and date with time
    Then I set Person available and cooperative to AVAILABLE, BUT UNCOOPERATIVE
    And I set Date and time of visit
    Then I save the Visit data
    And I click on edit Visit button
    Then I check last Person status and date with time
    And I fill the specific data of visit with Set cleared to No option to all symptoms
    Then I save the Visit data
    Then I click on edit Visit button
    And I fill the specific data of visit with Set cleared to Unknown option to all symptoms
    Then I save the Visit data

  @issue=SORDEV-7452 @env_main
  Scenario: Bulk mode for linking/adding contacts to new Event
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I apply Id of last api created Contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I click on the More button on Contact directory page
    And I click Enter Bulk Edit Mode on Contact directory page
    And I click checkbox to choose all Contact results on Contact Directory Page
    And I click on Bulk Actions combobox on Contact Directory Page
    And I click on Link to Event from Bulk Actions combobox on Contact Directory Page
    And I click on New Event option in Link to Event Form
    And I click on SAVE button in Link Event to group form
    And I create a new event with status CLUSTER
    And I navigate to the last created Event page via URL
    And I check that number of displayed Event Participants is 1

  @issue=SORDEV-7452 @env_main
  Scenario: Bulk mode for linking/adding contacts to existing Event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I apply Id of last api created Contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I click on the More button on Contact directory page
    And I click Enter Bulk Edit Mode on Contact directory page
    And I click checkbox to choose all Contact results on Contact Directory Page
    And I click on Bulk Actions combobox on Contact Directory Page
    And I click on Link to Event from Bulk Actions combobox on Contact Directory Page
    And I fill Event Id filter in Link to Event form with last created via API Event uuid
    And I click first result in grid on Link to Event form
    And I click on SAVE button in Link Event to group form
    And I navigate to the last created through API Event page via URL
    And I check that number of displayed Event Participants is 1

  @issue=SORDEV-7425 @env_main
  Scenario: Adopt the source case in the associated exposure after case conversion
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
    Then I click on the Epidemiological Data button tab in Contact form
    And I fill all the data in Exposure for Epidemiological data tab in Contacts
    And I click on save button from Epidemiological Data
    Then I click on the Contact tab in Contacts
    And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    Then I click on the Epidemiological data button tab in Case form
    And I am checking all Exposure data created by UI is saved and displayed in Cases

  @issue=SORDEV-5640 @env_main
  Scenario: Enter an exposure data in Contacts to testing all available options
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
    And I click on the Epidemiological Data navbar field
    And I click on Exposure details known with NO option
    And I click on Exposure details known with UNKNOWN option
    And I click on Exposure details known with YES option
    Then I click on New Entry in Exposure Details Known
    And I select all options in Type of activity from Combobox in Exposure for Epidemiological data tab in Contacts
    Then I select a Type of activity Other option in Exposure for Epidemiological data tab in Contacts
    And I fill a Type of activity details field in Exposure for Epidemiological data tab in Contacts
    Then I select a Type of activity Gathering option in Exposure for Epidemiological data tab in Contacts
    And I select all Type of gathering from Combobox in Exposure for Epidemiological data tab in Contacts
    And I select a type of gathering Other from Combobox in Exposure for Epidemiological data tab in Contacts
    And I fill a type of gathering details in Exposure for Epidemiological data tab in Contacts
    Then I check all Type of place from Combobox in Exposure for Epidemiological data tab in Contacts
    Then I fill Location form for Type of place by chosen "HOME" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Contacts
    And I click on save button from Epidemiological Data
    And I click on edit Exposure vision button
    And I select Work option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by chosen "OTHER" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Contacts
    And I click on save button from Epidemiological Data
    And I click on edit Exposure vision button
    And I select Travel option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by chosen "FACILITY" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Contacts
    And I click on save button from Epidemiological Data

  @env_main @#7768
  Scenario: Create new contact using line listing and select source case
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in with National User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    Then I click Choose Case button from Contact Directory Line Listing popup window
    And I search for the last case uuid in the CHOOSE SOURCE popup of Create Contact window
    And I open the first found result in the CHOOSE SOURCE popup of Create Contact window
    And I create a new Contact with specific data through Line Listing when disease prefilled
    And I save the new contact using line listing feature
    Then I check that contact created from Line Listing is saved and displayed in results grid

  @env_main @#7769
  Scenario: Create a new Contact via Line Listing and validate that the selected Source Case data is correctly displayed
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in with National User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    Then I click Choose Case button from Contact Directory Line Listing popup window
    And I search for the last case uuid in the CHOOSE SOURCE popup of Create Contact window
    And I open the first found result in the CHOOSE SOURCE popup of Create Contact window
    Then I check the name and uuid of selected case information is correctly displayed in new Contact Line Listing popup window
    Then I check disease dropdown is automatically filled with disease of selected Case in new Contact Line Listing popup window

  @issue=SORDEV-9124 @env_main
  Scenario: Document Templates create quarantine order in Contacts
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I navigate to the last created contact via the url
    Then I click on Create button in Document Templates box in Edit Contact directory
    And I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Contact directory
    And I select "ExampleDocumentTemplateContacts.docx" Quarantine Order in Create Quarantine Order form in Edit Contact directory
    And I click on Create button in Create Quarantine Order form
    Then I navigate to the last created contact via the url
    And I check if downloaded file is correct for "ExampleDocumentTemplateContacts.docx" Quarantine Order in Edit Contact directory
    And I check if generated document based on "ExampleDocumentTemplateContacts.docx" appeared in Documents tab in Edit Contact directory
    And I delete downloaded file created from "ExampleDocumentTemplateContacts.docx" Document Template for Contact

  @issue=SORDEV-9124 @env_main
  Scenario: Document Templates create quarantine order for Contact bulk
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    When I fill a new contact form
    And I click on SAVE new contact button
    Then I check the created data is correctly displayed on Edit Contact page
    And I click on the Contacts button from navbar
    And I click on the More button on Contact directory page
    And I click Enter Bulk Edit Mode on Contact directory page
    And I select last created UI result in grid in Contact Directory for Bulk Action
    And I select last created API result in grid in Contact Directory for Bulk Action
    And I click on Bulk Actions combobox on Contact Directory Page
    And I click on Create Quarantine Order from Bulk Actions combobox on Contact Directory Page
    And I click on checkbox to upload generated document to entities in Create Quarantine Order form in Contact directory
    And I select "ExampleDocumentTemplateContacts.docx" Quarantine Order in Create Quarantine Order form in Edit Contact directory
    And I click on Create button in Create Quarantine Order form
    And I click on close button in Create Quarantine Order form
    And I check if downloaded zip file for Quarantine Order is correct
    And I click on the More button on Contact directory page
    Then I click Leave Bulk Edit Mode on Contact directory page
    Then I navigate to the last created UI contact via the url
    And I check if generated document based on "ExampleDocumentTemplateContacts.docx" appeared in Documents tab for UI created contact in Edit Contact directory
    And I navigate to the last created contact via the url
    And I check if generated document based on "ExampleDocumentTemplateContacts.docx" appeared in Documents tab in Edit Contact directory
    And I delete downloaded file created from Quarantine order

  @issue=SORDEV-8048 @env_de
  Scenario: Test Default value for disease if only one is used by the server for Contacts
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on the NEW CONTACT button
    And I check if default disease value is set for COVID-19
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I check if default disease value for contacts in the Line listing is set for COVID-19

  @issue=SORDEV-9477 @env_main
  Scenario: Add a person search option on creation forms
    Then API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    Then I click on the Contacts button from navbar
    And  I click on the NEW CONTACT button
    And I fill a new contact form with chosen data without personal data on Contact directory page
    And I click on the person search button in create new contact form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on the clear button in new contact form
    And I click on the person search button in create new contact form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on SAVE new contact button
    Then I check the created data for existing person is correctly displayed on Edit Contact page
    When I click on the Persons button from navbar
    And I open the last created Person via API
    And I check that SEE CONTACTS FOR THIS PERSON button appears on Edit Person page

  @issue=SORDEV-6140 @env_main
  Scenario: Ask user to automatically convert some additional contacts and event participants to case
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on the NEW CONTACT button
    And I fill a new contact form with specific person data
    And I click on SAVE new contact button
    Then I check the created data is correctly displayed on Edit Contact page
    And I click Link Event button on Edit Contact Page
    And I select 2 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 3 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 5 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    When I navigate to the last created through API Event page via URL
    And I click on the Event participant tab
    And I add same person data as one used for Contact creation for event participant
    And I navigate to the last created UI contact via the url
   And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    And I click Yes, for some in conversion to case form
    And I click on checkbox to select all available options
    And I check if there are entities assigned to new created case from contact

  @issue=SORDEV-6140 @env_main
  Scenario: Ask user to automatically convert all additional contacts and event participants to case
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on the NEW CONTACT button
    And I fill a new contact form with specific person data
    And I click on SAVE new contact button
    Then I check the created data is correctly displayed on Edit Contact page
    And I click Link Event button on Edit Contact Page
    And I select 2 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 3 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 5 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    When I navigate to the last created through API Event page via URL
    And I click on the Event participant tab
    And I add same person data as one used for Contact creation for event participant
    And I navigate to the last created UI contact via the url
    And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    And I click Yes, for all in conversion to case form
    And I check if there are entities assigned to new created case from contact

  @issue=SORDEV-6140 @env_main
  Scenario: Ask user to automatically convert no additional contacts and event participants to case
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on the NEW CONTACT button
    And I fill a new contact form with specific person data
    And I click on SAVE new contact button
    Then I check the created data is correctly displayed on Edit Contact page
    And I click Link Event button on Edit Contact Page
    And I select 2 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 3 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    And I click Link Event button on Edit Contact Page
    And I select 5 event in Link Event popup and create and Event Participant
    And I click Save in Add Event Participant form on Edit Contact Page
    When I navigate to the last created through API Event page via URL
    And I click on the Event participant tab
    And I add same person data as one used for Contact creation for event participant
    And I navigate to the last created UI contact via the url
    And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    And I click No in conversion to case form
    And I check if there are no entities assigned to new created case from contact

    @issue=SORDEV-10265 @env_main
    Scenario: Manual archiving for contacts
      When API: I create a new person
      Then API: I check that POST call body is "OK"
      And API: I check that POST call status code is 200
      Then API: I create a new contact
      Then API: I check that POST call body is "OK"
      And API: I check that POST call status code is 200
      Given I log in as a Admin User
      When I click on the Contacts button from navbar
      Then I search after last created contact via API by UUID and open
      Then I click on the Archive contact button
      And I check if Archive contact popup is displayed correctly
      Then I check the end of processing date in the archive popup
      And I check if Archive button changed name to De-Archive
      Then I click on the Contacts button from navbar
      When I choose Archived contacts form combobox on Contact Directory Page
      Then I open the first contact from contacts list
      And I check if Archive button changed name to De-Archive

  @issue=SORDEV-9786 @env_main
  Scenario: Test The "urine p.m." enum value should be hidden when Covid19 is selected as disease
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by UUID and open
    And I check that the value selected from Disease combobox is "COVID-19" on Edit Contact page
    Then I click on New Sample
    And I check if value "Urine p.m" is unavailable in Type of Sample combobox on Create new Sample page

  @env_main @issue=SORDEV-9155
  Scenario: Test Vaccinations get lost when merging contacts with duplicate persons
    Then API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    When I fill a new contact form for duplicated contact with same person data
    And I click on SAVE button in create contact form
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window
    Then I click SAVE button on Edit Contact Page
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    When I fill a new contact form for duplicated contact with same person data
    And I click on SAVE button in create contact form
    And I Pick a new person in Pick or create person popup during contact creation
    When I check the created data for duplicated contact is correctly displayed on Edit Contact page
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window
    And I set Vaccination status to "Vaccinated" on Edit Contact page
    Then I click SAVE button on Edit Contact Page
    And I click on the Contacts button from navbar
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I click on Merge button of leading case in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I apply filter by duplicated contact Person data on Contact Directory Page
    Then I open the first contact from contacts list
    And I check if Vaccination Status is set to "Vaccinated" on Edit Contact page

  @issue=SORDEV-7460 @env_main
  Scenario: Test Extend the exposure and event startDate and endDate to include a startTime and endTime
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
    Then I click on the Epidemiological Data button tab in Contact form
    And I click on Exposure details known with YES option
    Then I click on New Entry in Exposure Details Known
    And I set Start and End of activity by current date in Exposure form
    And I select a Type of activity Work option in Exposure for Epidemiological data tab in Cases
    And I click on SAVE button in Exposure form
    And I collect the Date of Start and End Exposure from Exposure page
    Then I check that Date field displays start date and end date in table Exposure on Epidemiological data tab

  @issue=SORDEV-5613 @env_main
  Scenario: Option to attach document like pdf, word, jpeg to contacts
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I search after last created contact via API by name and uuid then open
    Then I click on START DATA IMPORT button from New document in contact tab
    And I upload pdf file to the contact
    And I check if pdf file is available in contact documents
    Then I download last updated document file from contact tab
    And I check if pdf file for contact is downloaded correctly
    Then I delete last uploaded document file from contact tab
    And I check if last uploaded file was deleted from document files in contact tab
    Then I click on START DATA IMPORT button from New document in contact tab
    And I upload docx file to the contact
    And I check if docx file is available in contact documents
    Then I download last updated document file from contact tab
    And I check if docx file for contact is downloaded correctly
    Then I delete last uploaded document file from contact tab
    And I check if last uploaded file was deleted from document files in contact tab
    Then I click on START DATA IMPORT button from New document in contact tab
    And I upload jpg file to the contact
    And I check if jpg file is available in contact documents
    Then I download last updated document file from contact tab
    And I check if jpg file for contact is downloaded correctly
    Then I delete last uploaded document file from contact tab
    And I check if last uploaded file was deleted from document files in contact tab

  @issue=SORDEV-10254 @env_main
    Scenario: Manual archive Cases and Contacts
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I open the Case Contacts tab of the created case via api
    Then I click on new contact button from Case Contacts tab
    Then I create a new contact from Cases Contacts tab
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on the Archive case button
    Then I check the end of processing date in the archive popup and not select Archive contacts checkbox
    And I check if Archive button changed name to De-Archive
    Then I click on the Contacts button from navbar
    When I choose Active contacts form combobox on Contact Directory Page
    Then I filter by last created contact via api
    Then I open the first contact from contacts list
    And I check if Archive button changed name to Archive

  @issue=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Contact directory
    Given I log in with National User
    And I click on the Contacts button from navbar
    When I click on the NEW CONTACT button
    And I click on the person search button in create new contact form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup
    
  @issue=SORDEV-9946 @env_de
  Scenario: Test Hide country specific fields in the 'Pick or create person' form of the duplicate detection pop-up, in German and French systems
    Given I log in as a Admin User
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data for DE version
    And I click on SAVE new contact button
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data for DE version
    And I click on SAVE new contact case button
    And I check if National Health Id, Nickname and Passport number appear in Pick or create person popup

  @issue=SORDEV-6434 @env_main
  Scenario: Check if username shows up in visit origin
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I navigate to follow-up visits tab
    When I click on new Visit button
    And I create a new Visit with specific data
    Then I check that username is displayed in the Visit Origin column

  @issue=SORDEV-5563 @env_de
  Scenario: Add contact person details to facilities contacts
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    And I click on New Entry button in Facilities tab in Configuration
    Then I set name, region and district in Facilities tab in Configuration
    And I set Facility Category to "Medizinische Einrichtung" and Facility Type to "Krankenhaus" in Facilities tab in Configuration
    And I set Facility Contact person first and last name with email address and phone number
    Then I click on Save Button in new Facility form
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a mandatory fields for a new contact form for DE
    And I click on SAVE new contact button
    Then I open Contact Person tab
    And I set Region to "Voreingestellte Bundesländer" and District to "Voreingestellter Landkreis"
    Then I set Facility Category to "Medizinische Einrichtung" and  Facility Type to "Krankenhaus"
    And I set facility name to created facility
    And I check if data for created facility is automatically imported to the correct fields in Case Person tab
    And I click on save button from Edit Person page
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    Then I search last created facility
    Then I click on edit button for the last searched facility
    And I archive facility

  @env_main @#8565
  Scenario: Check an archived contact if its read only
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open last edited contact by API via URL navigation
    Then I click on the Archive contact button and confirm popup
    Then I click on logout button from navbar
    Then I log in with National User
    Then I open last edited contact by API via URL navigation
    Then I check if editable fields are read only for an archived contact

  @env_main @issue=SORDEV-7453
  Scenario: Check contacts order after contact edit
    Given I log in as a National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I click on the Contacts button from navbar
    Then I get two last contacts ID from cases list
    And I open 2 contact in order from list
    And I fill general comment in contact edit page with EDITED
    And I click SAVE button on Edit Contact Page
    And I click on the Contacts button from navbar
    Then I compare previous first contact ID on the list with actually second contact ID on list

  @issue=SORDEV-6461 @env_main
  Scenario: Test the task type in the contact's new task form
    Given I log in as a National User
    Then I click on the Contacts button from navbar
    And I open the first contact from contacts list
    And I click on the NEW TASK button
    And I check if New task form is displayed correctly
    And I check that required fields are marked as mandatory
    And I clear Due Date field in the New task form
    And I click SAVE button on New Task form
    Then I check that all required fields are mandatory in the New task form
    When I close input data error popup in Contact Directory
    And I check that values listed in the task type combobox are correct
    And I choose Other task as described in comments option from task type combobox in the New task form
    Then I check that Comments on task field is mandatory in the New task form

  @issue=SORDEV-6609 @env_main
  Scenario: Test for contact internal token
    Given I log in as a National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    When I fill in the Internal Token field in Edit Case page with SAMPLE TOKEN
    And I click on save button in the case popup
    And I click on the Contacts button from navbar
    And I check that the Internal Token column is present
    And I filter for SAMPLE TOKEN in Contacts Directory
    Then I check that at least one SAMPLE TOKEN is displayed in table

  @issue=SORDEV-6102 @env_main
  Scenario: Merge duplicate contacts
    Then API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    When I fill a new contact form for duplicated contact with same person data
    And I click on SAVE button in create contact form
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window
    Then I click SAVE button on Edit Contact Page
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    When I fill a new contact form for duplicated contact with same person data
    And I click on SAVE button in create contact form
    And I Pick a new person in Pick or create person popup during contact creation
    When I check the created data for duplicated contact is correctly displayed on Edit Contact page
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window
    Then I click SAVE button on Edit Contact Page
    And I click on the Contacts button from navbar
    And I click on the More button on Contact directory page
    Then I click on Merge Duplicates on Contact directory page
    And I click on Merge button of leading case in Merge Duplicate Contact page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Contacts button from navbar
    And I apply filter by duplicated contact Person data on Contact Directory Page
    And I check that number of displayed contact results is 1

  @issue=SORDEV-11451 @env_main
  Scenario: Add reason for deletion to confirmation dialogue
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I copy url of current contact
    Then I click on Delete button from contact
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Deletion request by another authority" is available
    And I check if reason for deletion as "Entity created without legal reason" is available
    And I check if reason for deletion as "Responsibility transferred to another authority" is available
    And I check if reason for deletion as "Deletion of duplicate entries" is available
    And I check if reason for deletion as "Other reason" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from contact
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please choose a reason for deletion" appears next to Reason for deletion
    When I set Reason for deletion as "Other reason"
    Then I check if "Reason for deletion details" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please add a reason for deletion" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from contact
    And I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted contact by url
    Then I check if reason of deletion is set to "Deletion request by affected person according to GDPR"
    And I check if External token input on case edit page is disabled
    And I check if Case or event information text area on case edit page is disabled

  @issue=SORDEV-11451 @env_de
  Scenario: Add reason for deletion to confirmation dialogue for DE version
    Given I log in as a Admin User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version without multicontact
    And I click on SAVE new contact button
    And I copy url of current contact
    Then I click on Delete button from contact
    And I check if reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO" is available
    And I check if reason for deletion as "Löschen auf Anforderung einer anderen Behörde" is available
    And I check if reason for deletion as "Entität ohne Rechtsgrund angelegt" is available
    And I check if reason for deletion as "Abgabe des Vorgangs wegen Nicht-Zuständigkeit" is available
    And I check if reason for deletion as "Löschen von Duplikaten" is available
    And I check if reason for deletion as "Anderer Grund" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from contact
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte wählen Sie einen Grund fürs Löschen" appears next to Reason for deletion
    When I set Reason for deletion as "Anderer Grund"
    Then I check if "DETAILS ZUM GRUND DES LÖSCHENS" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte geben Sie einen Grund fürs Löschen an" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted contact by url
    Then I check if reason of deletion is set to "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I check if External token input on case edit page is disabled
    And I check if Case or event information text area on case edit page is disabled