@UI @Sanity @Case
Feature: Cases using Line Listing feature

  @env_main
  Scenario: Create cases using Line Listing feature
    Given I log in as a National User
    And I click on the Cases button from navbar
    Then I click on Case Line Listing button
    And I create a new case in line listing feature popup
    And I save the new line listing case
    Then I click on the Cases button from navbar
    And I check that case created from Line Listing is saved and displayed in results grid

  @env_main
  Scenario: Create contact using Line Listing feature
    Given I log in as a National User
    When I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I create a new Contact with specific data through Line Listing
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    And I check that contact created from Line Listing is saved and displayed in results grid

  @env_main @#7468
  Scenario: Validate Line listing Configuration section
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    When I click on Line Listing button in Configuration tab
    Then I Verify the page elements are present in Line Listing Configuration Page

  @env_main @#7468
  Scenario: Add line listing setup for specific disease
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    When I click on Line Listing button in Configuration tab
    When I Select the disease Anthrax from the combo box in Line Listing Configuration Page
    When I click on the Enable Line Listing for Disease button in Line Listing Configuration Page
    Then I validate disease Anthrax configuration is enabled and displayed in Line Listing Configuration Page
    When I click on Disable All Line Listing button in Line Listing Configuration Page
    Then I validate the presence of the notification description confirming Line Listing is disabled in Line Listing Configuration Page
