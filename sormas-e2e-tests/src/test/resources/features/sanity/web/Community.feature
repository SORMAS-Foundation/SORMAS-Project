@UI @Sanity @Configuration
Feature: Community end to end tests

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for communities
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Communities button in Configuration tab
    And I select country Germany
    Then I check that Voreingestellte Gemeinde is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for communities
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Communities button in Configuration tab
    And I select country Deutschland
    Then I check that Voreingestellte Gemeinde is correctly displayed

  @env_main @#7468
  Scenario: Validate Communities Configuration section
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Communities button in Configuration tab
    Then I Verify the page elements are present in Communities Configuration Page

  @env_main @#7468
  Scenario: Check Communities Configuration search and reset functionalities
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I click on Communities button in Configuration tab
    Then I verify the Search and Reset filter functionality in Communities Configuration page
    Then I verify the Country dropdown functionality in Communities Configuration page
    Then I verify the Region dropdown functionality in Communities Configuration page
    Then I verify the District dropdown functionality in Communities Configuration page
