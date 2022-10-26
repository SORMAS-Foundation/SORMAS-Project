@UI @Sanity @Configuration
Feature: Continent end to end tests

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for continents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I check that number of continents is 6
    And I check that Africa is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for continents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I check that number of continents is 6
    And I check that Africa is correctly displayed in German

  @env_main @#7468
  Scenario: Validate Continents Configuration section
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I Verify the Presence of the element Import Button in Continents Configuration page
    Then I Verify the Presence of the element Import Default Button in Continents Configuration page
    Then I Verify the Presence of the element Export Button in Continents Configuration page
    Then I Verify the Presence of the element New Entry Button in Continents Configuration page
    Then I Verify the Presence of the element Enter Bulk Edit Mode button in Continents Configuration page
    Then I Verify the Presence of the element Search Input in Continents Configuration page
    Then I Verify the Presence of the element Reset Filters in Continents Configuration page
    Then I Verify the Presence of the element Continents dropdown in Continents Configuration page
