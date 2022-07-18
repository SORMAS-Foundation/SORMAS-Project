@UI @Sanity @Configuration
Feature: Country end to end tests

  @issue=SORDEV-7463 @env_main
  Scenario: Test configuration for country and its Subcontinent association
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to countries tab in Configuration
    And I fill search filter with "Germany" country name on Country Configuration Page
    Then I check the "Central Europe" name for the country on Country Configuration Page

    @issue=SORDEV-7464 @env_main
    Scenario: Test configuration for NCL and its Subcontinent association
      Given I log in as a Admin User
      Then I click on the Configuration button from navbar
      And I navigate to countries tab in Configuration
      And I fill search filter with "New Caledonia" country name on Country Configuration Page
      Then I check the "Western Europe" name for the country on Country Configuration Page

  @issue=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for countries
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I check that number of countries is at least 203
    And I check that Albania is correctly displayed

  @issue=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for countries
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I check that number of countries is at least 203
    And I check that Albania is correctly displayed in German