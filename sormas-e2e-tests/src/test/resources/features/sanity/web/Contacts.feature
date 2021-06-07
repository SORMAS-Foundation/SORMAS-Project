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

  @EditContact @issue=5634
  Scenario: Edit a created contact
    Given I log in with the user
    When API: I create a new person
    And API: I create a new contact
    And I navigate to the last created contact via the url
    And I change all contact fields and save
    And I navigate to the last created contact via the url
    Then I check the edited data is correctly displayed on Edit Contact page after editing

  @FollowUpVisit
  Scenario: Edit all fields from Follow-up visits  tab
    Given API: I create a new contact
    Given I log in with the user
    When I am accessing the Follow-up visits tab using of created contact via api
    Then I check and fill all data
    And I click on save
    And I am accessing the contacts
    When I am accessing the Follow-up visits tab using of created contact via api
    And I am checking all data is saved and displayed

