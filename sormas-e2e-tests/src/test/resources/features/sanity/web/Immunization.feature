@UI @Sanity @Immunization
Feature: Immunization end to end tests

  @issue=SORDEV-8705 @env_main
  Scenario:Check a new immunization data
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on Person tab from Immunization page
    Then I check the created data is correctly displayed on Edit immunization person page

  @issue=SORDEV-9312 @env_main
  Scenario: Reset the 'Overwrite immunization management status' by Discard button
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I open first immunization from grid from Immunization tab
    Then I check Overwrite immunization management status option
    Then I click on discard button from immunization tab
    And I check if Overwrite immunization management status is unchecked by Management Status

  @env_main @#8565
  Scenario: Check an archived immunization if its read only
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create 1 new immunizations for last created person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I navigate to last created immunization by API via URL
    Then I click on archive button from immunization tab
    Then I click on logout button from navbar
    Then I log in with National User
    Then I navigate to last created immunization by API via URL
    Then I check if editable fields are read only for an archived immunization