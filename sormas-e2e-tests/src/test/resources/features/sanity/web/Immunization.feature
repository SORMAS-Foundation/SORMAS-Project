@UI @Sanity @Immunization
Feature: Immunization end to end tests

  Scenario:Check a new immunization data
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page