@Sanity @Contacts
Feature: Contacts end to end tests
  
  Scenario: Create simple contact
    Given I log in with the user
      And I click on the Contacts button from navbar
      And I click on the NEW CONTACT button
      And I create a new contact
      Then I check the created data is correctly displayed on Edit Contact page
      Then I open Contact Person tab
      And I check the created data is correctly displayed on Edit Contact Person page

    Scenario: Delete created contact
      Given I log in with the user
      When API: I create a new person
      Then API: I create a new contact
      When I click on the Contacts button from navbar
      Then I open the last created contact
      Then I delete the contact
      And I check that number of displayed contact results is 0


  Scenario: Edit a created contact
    Given I log in with the user
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I create a new contact
    Then I check the created data is correctly displayed on Edit Contact page
    And I click on the Contacts button from navbar
    And I search for Contact using Contact UUID from the last created Contact
    And I open the first displayed Contact result
    Then I check the created data is correctly displayed on Edit Contact page