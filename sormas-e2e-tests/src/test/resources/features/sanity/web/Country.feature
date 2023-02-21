@UI @Sanity @Configuration
Feature: Country end to end tests

  @tmsLink=SORDEV-7463 @env_main @precon
  Scenario: Test configuration for country and its Subcontinent association
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to countries tab in Configuration
    And I fill search filter with "Germany" country name on Country Configuration Page
    Then I check the "Western Europe" name for the country on Country Configuration Page

    @tmsLink=SORDEV-7464 @env_main
    Scenario: Test configuration for NCL and its Subcontinent association
      Given I log in as a Admin User
      Then I click on the Configuration button from navbar
      And I navigate to countries tab in Configuration
      And I fill search filter with "New Caledonia" country name on Country Configuration Page
      Then I check the "Western Europe" name for the country on Country Configuration Page

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for countries
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I check that number of countries is at least 195
    And I check that Albania is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for countries
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I check that number of countries is at least 195
    And I check that Albania is correctly displayed in German

  @env_main @#7468
  Scenario: Validate Countries Configuration section
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I Verify the page elements are present in Countries Configuration Page

  @env_main @#7468
  Scenario: Check Countries Configuration search and reset functionalities
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to countries tab in Configuration
    Then I verify the Search and Reset filter functionality in Countries Configuration page
    Then I verify the Subcontinent Australia (Subcontinent) combo box returns appropriate filter results in Countries Configuration page
    Then I verify the Subcontinent Central Africa combo box returns appropriate filter results in Countries Configuration page
    Then I verify the Subcontinent Central America combo box returns appropriate filter results in Countries Configuration page
    Then I verify the Subcontinent Central Asia combo box returns appropriate filter results in Countries Configuration page
    Then I verify the Subcontinent Central Europe combo box returns appropriate filter results in Countries Configuration page

  @tmsLink=SORQA-707 @env_main @precon
  Scenario: Test configuration for country and its Subcontinent association
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to countries tab in Configuration
    And I fill search filter with "Cameroon" country name on Country Configuration Page
    Then I check the "Central Africa" name for the country on Country Configuration Page