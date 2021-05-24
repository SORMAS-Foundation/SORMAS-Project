@Sanity @Persons
Feature: Edit Persons

  Scenario: Edit existent person
    Given I log in with the user
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    Then I create a new contact
    And I open Contact Person tab
    Then I fill all default empty fields from Contact Person tab



    And I click on the Persons button from navbar