@UI @Sanity @PointsOfEntry
Feature: Points Of Entry Configuration end to end tests

  @env_main @#7468
  Scenario: Validate Points of Entry Configuration section
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I click on Points Of Entry button in Configuration tab
    Then I Verify the page elements are present in Points Of Entry Configuration Page
