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
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I click on the Tasks button from navbar
    Then I search created task by Contact first and last name
    And I open the last created UI Contact
    Then I check the created data is correctly displayed on Edit Contact page

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

  @issue=SORDEV-5490 @env_main @ignore
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
    And I click on the Epidemiological Data button
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

  @issue=SORDEV-5478 @env_main
  Scenario: Upload Contact CSV
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    And I apply filter ID to "UXEMTD"
    And I click APPLY BUTTON in Contact Directory Page
    And I click on Export button from Contacts directory
    And I click on Detailed Export button from Contacts directory
    And I close popup after export in Contacts directory
    And I click on the More button from Contacts directory
    When I click on the Import button from Contacts directory
    And I select the contact CSV file in the file picker
    And I click on the "START DATA IMPORT" button from the Import Contact popup
    And I select to create new person from the Import Contact popup
    And I confirm the save Contact Import popup
    And I select to create new contact from the Import Contact popup
    And I confirm the save Contact Import popup
    Then I check that an import success notification appears in the Import Contact popup
    And I delete exported file from Contact Directory