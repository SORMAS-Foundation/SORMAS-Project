@UI @Sanity @Configuration
Feature: Region end to end tests

  @issue=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for regions
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to regions tab in Configuration
    And I select country Germany
    Then I check that number of regions is at least 17
    And I check that Voreingestellte Bundeslander is correctly displayed

  @issue=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for regions
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to regions tab in Configuration
    And I select country Deutschland
    Then I check that number of regions is at least 17
    And I check that Voreingestellte Bundeslander is correctly displayed in German