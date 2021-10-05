@UI @Sanity @Case @Listing
Feature: Contacts end to end tests

  Scenario: Create cases using Line Listing feature and validate the entries.
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I create a new Contact with specific data through Line Listing
    Then I click on Add Line button and fill all the details in new line
    And I click on save
    Then I click on the Contacts button from navbar
    And I am checking all data created from Line Listing option is saved and displayed