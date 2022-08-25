@UI @Sanity @Configuration
Feature: Subcontinent end to end tests

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for subcontinents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to subcontinents tab in Configuration
    Then I check that number of subcontinents is at least 27
    And I check that Central Africa is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for subcontinents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to subcontinents tab in Configuration
    Then I check that number of subcontinents is at least 27
    And I check that Central Africa is correctly displayed in German