@UI @Sanity @Case @Hospitalization
Feature: Case hospitalization tab e2e test cases

  Scenario: Edit all fields from Hospitalization tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I navigate to hospitalization tab for case created via api
    And I complete all hospitalization fields and save
    And I navigate to hospitalization tab for case created via api
    Then I check the edited and saved data is correctly displayed on Hospitalization tab page
    When I add a previous hospitalization and save
    Then I check the edited and saved data is correctly displayed in previous hospitalization window