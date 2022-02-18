@UI @Sanity @Immunization
Feature: Immunization end to end tests

  @env_main
  Scenario:Check a new immunization data
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I check the created data is correctly displayed on Edit immunization person page

    @issue=SORDEV-9312
    Scenario: Reset the 'Overwrite immunization management status' by Discard button
      Given I log in as a Surveillance Officer
      And I click on the Immunizations button from navbar
      And I open first immunization from grid from Immunization tab
      Then I check Overwrite immunization management status option
      Then I click on discard button from immunization tab
      And I check if Overwrite immunization management status is unchecked by Management Status