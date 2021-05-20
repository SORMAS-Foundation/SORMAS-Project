@Sanity @Contacts
Feature: Create contacts
  
  Scenario: Create simple contact
    Given I log in with the user
      And I click on the Contacts button from navbar
      And I click on the NEW CONTACT button
      And I create a new contact
      Then I check the created data is correctly displayed on Edit Contact page
      And I check the created data is correctly displayed on Edit Contact Person page