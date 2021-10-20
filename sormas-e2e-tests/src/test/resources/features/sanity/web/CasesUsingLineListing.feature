@UI @Sanity @Case
Feature: Cases using Line Listing feature

  Scenario: Create cases using Line Listing feature and validate the entries
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I click on Case Line Listing button
    And I create a new case using line listing feature
    Then I click on add line button
    And I create the second case using listing feature in new line
    And I save the new case using line listing feature
    Then I click on the Cases button from navbar
    And I am checking all data created from Case Line Listing option is saved and displayed