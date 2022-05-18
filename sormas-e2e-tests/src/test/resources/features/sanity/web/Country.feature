@UI @Sanity @About
Feature: Country end to end tests

  @issue=SORDEV-7463 @env_main
  Scenario: Test configuration for country and its Subcontinent association
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to countries tab in Configuration
    And I fill search filter with "Germany" country name on Country Configuration Page
    Then I check the subcontinent name for Germany on Country Configuration Page