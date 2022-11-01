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

  @env_main @#7468
  Scenario: Validate Subcontinents Configuration section
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to subcontinents tab in Configuration
    Then I Verify the page elements are present in Subcontinents Configuration Page

  @env_main @#7468
  Scenario: Check Subcontinents Configuration search and reset functionalities
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to subcontinents tab in Configuration
    Then I verify the Search and Reset filter functionality in Subcontinents Configuration page
    Then I verify the Continent Africa combo box returns appropriate filter results in Subcontinents Configuration page
    Then I verify the Continent America combo box returns appropriate filter results in Subcontinents Configuration page
    Then I verify the Continent Asia combo box returns appropriate filter results in Subcontinents Configuration page
    Then I verify the Continent Australia (Continent) combo box returns appropriate filter results in Subcontinents Configuration page
    Then I verify the Continent Europe combo box returns appropriate filter results in Subcontinents Configuration page
