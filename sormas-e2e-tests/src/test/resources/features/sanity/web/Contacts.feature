@UI @Sanity @Contacts
Feature: Contacts end to end tests

  Scenario: Create simple contact
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click SAVE a new contact
    Then I check the created data is correctly displayed on Edit Contact page
    Then I open Contact Person tab
    And I check the created data is correctly displayed on Edit Contact Person page

  Scenario: Delete created contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
    Then I delete the contact
    And I check that number of displayed contact results is 0

  Scenario: Edit a created contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
    And I change all contact fields and save
    And I navigate to the last created contact via the url
    Then I check the edited data is correctly displayed on Edit Contact page after editing

  @issue=SORDEV-5476
    Scenario: Add a task from contact and verify the fields
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click SAVE a new contact
    And I click on the Tasks button from navbar
    Then I search created task by Contact first and last name
    And I open the last created UI Contact
    Then I check the created data is correctly displayed on Edit Contact page

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

  @issue=SORDEV-5490
  Scenario: Create a contact and create a case for contact person
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click SAVE a new contact
    And I click on CONFIRMED CONTACT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    And I create a new case for contact with specific data
    And I check case created from created contact is correctly displayed on Edit Case page

  @issue=SORDEV-5496
  Scenario: Generate contact document
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I open the first contact from contacts list
    And I click on the Create button from Contact Document Templates
    When I create a contact document from template
    Then I verify that the contact document is downloaded and correctly named

    @issue=SORDEV-5470
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
    And I click SAVE a new contact
    Then I check the linked case information is correctly displayed
    And I check the created data for complex contact is correctly displayed on Edit Contact page
    Then I open Contact Person tab
    And I check the created data is correctly displayed on Edit Contact Person page

  @issue=SORDEV-5641
  Scenario: Fill the epidemiological data tab in Contacts
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I open the last created contact
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

  @issue=SORDEV-5640
  Scenario: Fill an exposure data
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
    And I select all options in Type of activity from Combobox in Exposure form
    Then  I select a Type of activity Other option in Exposure form
    And I fill a Type of activity details in Exposure by some type of activity details TEXT
    Then  I select a Type of activity Gathering option in Exposure form
    And I select all Type of gathering from Combobox in Exposure form
    And I select a type of gathering Other from Combobox in Exposure form
    And I fill a type of gathering details in Exposure form by type of gathering details TEXT
    Then  I check all Type of place from Combobox in Exposure form
    Then I fill Location form for Type of place by options excluded Other and Facility
    And I click on save button from Epidemiological Data
    And I click on edit Exposure vision button
    And I select Work option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by Other option
    And I click on save button from Epidemiological Data
    And I click on edit Exposure vision button
    And I select Travel option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by Facility option
    And I click on save button from Epidemiological Data
