@UI @Sanity @CaseView
Feature: Case view tests

  @env_main
  Scenario: Create a new Case and check details in Detailed view table
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in with National User
    Given I click on the Cases button from navbar
    When I click on the DETAILED button from Case directory
    And I filter by CaseID on Case directory page
    And I am checking if all the fields are correctly displayed in the Case directory Detailed table