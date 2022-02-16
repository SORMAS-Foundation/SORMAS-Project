@UI @Sanity @Case @Symptoms
Feature: Case symptoms tab e2e test cases

  @issue=SORDEV-5521 @Locale_DE
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

  @issue=SORDEV-8350 @Locale_DE
  Scenario: Extend fever validation
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
    And I set Maximum body temperature as a 38.1
    And I save the Symptoms data
    Then I check if popup is displayed next to Fever in Symptoms if temperature is >=38
    And I set Fever Symptoms to NO
    And I save the Symptoms data
    Then I check if popup is displayed next to Fever in Symptoms if temperature is >=38
    And I set Fever Symptoms to YES
    Then I set Maximum body temperature as a 37.9
    And I set Fever Symptoms to YES
    And I save the Symptoms data
    Then I check if popup is displayed next to Fever in Symptoms if temperature is <=38