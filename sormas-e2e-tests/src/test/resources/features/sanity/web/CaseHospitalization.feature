@UI @Sanity @Case @Hospitalization
Feature: Case hospitalization tab e2e test cases

  Scenario: Edit all fields from Hospitalization tab
    Given API: I create a new person
    And API: I create a new case
    And I log in with National User
    And I navigate to hospitalization tab using of created case via api
    And I change all hospitalization fields and save
    And I navigate to hospitalization tab using of created case via api
    Then I check the edited and saved data is correctly displayed on Hospitalization tab page
    When I add a previous hospitalization and save
    Then I check the edited and saved data is correctly displayed in previous hospitalization window