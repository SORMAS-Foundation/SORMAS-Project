@UI @Sanity @Configuration @Outbreaks
Feature: Outbreaks Configuration end to end tests

  @env_main @#7468
  Scenario: Validate Outbreaks Configuration section
    Given I log in as a National User
    When I click on the Configuration button from navbar
    When I click on the Outbreaks Tab in the Configuration Page
    Then I Verify the presence of all Regions in Outbreaks Configuration Page
    Then I Verify the presence of all Diseases in Outbreaks Configuration Page
    Then I Verify the presence of Matrix in Outbreaks Configuration Page

  @env_main @#7468
  Scenario: Check disease configuration box from Outbreaks Configuration
    Given I log in as a National User
    When I click on the Configuration button from navbar
    When I click on the Outbreaks Tab in the Configuration Page
    When I click on one of the Outbreaks Matrix element in Outbreaks Configuration Page
    Then I verify the Disease-Region popup elements are displayed in Outbreaks Configuration Page
    Then I Click the Save button in Outbreaks Configuration Page
    When I click on one of the Outbreaks Matrix element in Outbreaks Configuration Page
    Then I Click the Discard button in Outbreaks Configuration Page
