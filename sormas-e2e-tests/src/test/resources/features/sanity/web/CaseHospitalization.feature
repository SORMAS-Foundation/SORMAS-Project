@UI @Sanity @Case @Hospitalization
Feature: Case hospitalization tab e2e test cases

  @env_main
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

  @issue=SORDEV-8414 @env_main
  Scenario: Hospitalization refinements with changed place of stay from home to facility
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    Then I click on Facility as place of stay in Case Edit tab
    And In Case Edit tab I set Facility as a Standard Einrichtung
    And I click only on save button from Edit Case page
    Then I check if Current Hospitalization popup is displayed
    And I set Patient Admitted at the facility as an inpatient as YES
    Then I click on Save and open hospitalization in current hospitalization popup

  @issue=SORDEV-8414 @env_main
  Scenario: Hospitalization refinements
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    And I navigate to Hospitalization tab in Cases
    When I set Patient Admitted at the facility as an inpatient as YES
    Then I save data in Hospitalization
    Then I check if Place of stay in hospital popup is displayed
    And I choose Facility in Place of stay in hospital popup in Case Hospitalization as Standard Einrichtung
    Then I save the data in Place of stay in hospital popup
    Then From hospitalization tab I click on the Case tab button
    And I check if place of stay data was updated in the Case edit tab with Standard Einrichtung