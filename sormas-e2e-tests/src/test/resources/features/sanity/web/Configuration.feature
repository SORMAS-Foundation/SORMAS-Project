@UI @Sanity @Configuration
Feature: Configuration tab tests

  @tmsLink=SORQA-662 @env_de @oldfake @precon
  Scenario: Check if Execute automation deletion option is available in the developer mode
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    And I check if Execute Automatic Deletion button is available