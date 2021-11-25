@UI @Sanity @Contacts
Feature: Contacts end to end tests

  Scenario: Create simple contact
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I create a new contact
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
