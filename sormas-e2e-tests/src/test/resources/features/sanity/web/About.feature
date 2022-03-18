@UI @Sanity @About
Feature: About end to end tests

  @issue=SORDEV-6474 @env_main
  Scenario: Check language options in Data Dictionary
    Given I log in with National User
    And I click on the About button from navbar
    And I click on Data Dictionary button
#    Then I click on the User Settings button from navbar
#    And I select "Deutsch" language from Combobox in User settings
    Then I click on Data Dictionary button