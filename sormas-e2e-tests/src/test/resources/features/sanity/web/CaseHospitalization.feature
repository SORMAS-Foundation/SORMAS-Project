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

  @issue=SORDEV-5521
  Scenario: Fill the symptoms tab
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I navigate to symptoms tab
    And I fill specific data of symptoms with Set cleared to No option to all Clinical Signs and Symptoms
    When I save the Symptoms data
    Then I check if created data is correctly displayed in Symptoms tab for Set cleared to NO
    And I fill specific data of symptoms with Set cleared to Unknown option to all Clinical Signs and Symptoms
    When I save the Symptoms data
    Then I check if created data is correctly displayed in Symptoms tab for Set cleared to UNKNOWN
    Then I set Other clinican symptomps to YES
    And I check if Specify Other Symptoms field is available and I fill it
    Then I set Feeling Ill Symptoms to YES
    Then I set Chills and Sweats Symptoms to YES
    Then I set Fever Symptoms to YES
    And I set First Symptom as Fever
    And I set First Symptom as Feeling ill
    And I set First Symptom as Chills or sweats
    And I set First Symptom as Other clinical symptoms
    And I set Date of symptom onset
    When I save the Symptoms data
    Then I navigate to Hospitalization tab in Cases
    And I set Patient Admitted at the facility as an inpatient as YES
    And I set specific Date of visit or admission
    And I save data in Hospitalization
    Then I check if error in Hospitalization data is available