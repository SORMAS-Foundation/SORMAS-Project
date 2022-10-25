@UI @Sanity @Configuration @Outbreaks
Feature: Region end to end tests

  @env_main @#7468
  Scenario: Validate Outbreaks Configuration section
    Given I log in as a National User
    When I click on the Configuration button from navbar
    When I click on the Outbreaks Tab in the Configuration Page
    Then I Verify the presence of all Regions in Outbreaks Configuration Page
    Then I Verify the presence of all Diseases in Outbreaks Configuration Page
    Then I Verify the presence of Matrix in Outbreaks Configuration Page
