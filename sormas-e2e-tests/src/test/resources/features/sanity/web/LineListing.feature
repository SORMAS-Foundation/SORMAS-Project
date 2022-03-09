@UI @Sanity @Case
Feature: Cases using Line Listing feature

  #please address
  @env_main @check
  Scenario: Create cases using Line Listing feature
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I click on Case Line Listing button
    And I create a new case in line listing feature popup
    And I save the new line listing case
    Then I click on the Cases button from navbar
    And I check that case created from Line Listing is saved and displayed in results grid

    #please address
  @env_main @check
  Scenario: Create contact using Line Listing feature
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I create a new Contact with specific data through Line Listing
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    And I check that contact created from Line Listing is saved and displayed in results grid



