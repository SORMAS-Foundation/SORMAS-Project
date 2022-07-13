@UI @Sanity @Configuration
Feature: Continent end to end tests

  @issue=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for continents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I check that number of continents is 6
    And I check that Africa is correctly displayed
