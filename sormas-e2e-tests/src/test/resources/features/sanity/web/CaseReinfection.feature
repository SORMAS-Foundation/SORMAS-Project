@UI @Sanity @Case @Reinfection
Feature: Case reinfection end to end tests

@issue=SORDEV-9153 @env_de
Scenario: Test Add reinfection details and status to cases
  Given I log in with National User
  And I click on the Cases button from navbar
  And I click on the NEW CASE button
  When I create a new case with specific data for DE version with saved person details
  Then I check the created data is correctly displayed on Edit case page for DE version for reinfection
  Then I choose JA option in reinfection
  And I check if reinfection checkboxes for DE version are displayed correctly
  Then I click on save case button
  And I click on the Cases button from navbar
  And I click on the NEW CASE button
  Then I create a new case with specific data for DE version with saved person details with earlier report date
  Then I choose select a matching person in pick or create popup for DE case version
  Then I click on save button in the case popup
  And I choose create a new case for the same person for DE version
  Then I click on save button in the case popup
  Then I check the created data is correctly displayed on Edit case page for DE version for reinfection
  And I click on the Cases button from navbar
  And I search first created case with reinfection
  Then I click on the first Case ID from Case Directory
  And I check data from the eye icon in reinfection section in Edit case for DE version
  Then I check all checkboxes with genome sequence in reinfection section in Edit case for DE version
  And I check if reinfection status is set to Sichere/Bestätigte Reinfektion for DE version
  Then I click on save case button
  Then I back to Case Directory using case list button
  Then I reset filter from Case Directory
  Then I filter by reinfection status as a Sichere/Bestätigte Reinfektion
  And I check if created case with reinfection status is displayed in the Case table for DE version
  Then I reset filter from Case Directory
  And I search first created case with reinfection
  Then I click on the first Case ID from Case Directory
  Then I clear all checkboxes with genome sequence in reinfection section in Edit case for DE version
  Then I set checkboxes from Combination1 from the test scenario for reinfection in Edit case for DE version
  And I check if reinfection status is set to Wahrscheinliche Reinfektion for DE version
  Then I click on save case button
  Then I back to Case Directory using case list button
  Then I reset filter from Case Directory
  Then I filter by reinfection status as a Wahrscheinliche Reinfektion
  And I check if created case with reinfection status is displayed in the Case table for DE version
  Then I reset filter from Case Directory
  And I search first created case with reinfection
  Then I click on the first Case ID from Case Directory
  Then I clear all checkboxes for Combination1 from the test scenario for reinfection in Edit case for DE version
  Then I set checkboxes from Combination2 from the test scenario for reinfection in Edit case for DE version
  And I check if reinfection status is set to Wahrscheinliche Reinfektion for DE version
  Then I click on save case button
  Then I back to Case Directory using case list button
  Then I reset filter from Case Directory
  Then I filter by reinfection status as a Wahrscheinliche Reinfektion
  And I check if created case with reinfection status is displayed in the Case table for DE version
  Then I reset filter from Case Directory
  And I search first created case with reinfection
  Then I click on the first Case ID from Case Directory
  Then I clear all checkboxes for Combination2 from the test scenario for reinfection in Edit case for DE version
  Then I set checkboxes from Combination3 from the test scenario for reinfection in Edit case for DE version
  And I check if reinfection status is set to Mögliche Reinfektion for DE version
  Then I click on save case button
  Then I back to Case Directory using case list button
  Then I reset filter from Case Directory
  Then I filter by reinfection status as a Mögliche Reinfektion
  And I check if created case with reinfection status is displayed in the Case table for DE version
  Then I reset filter from Case Directory
  And I search first created case with reinfection
  Then I click on the first Case ID from Case Directory
  Then I clear all checkboxes for Combination3 from the test scenario for reinfection in Edit case for DE version
  Then I set checkboxes from Combination4 from the test scenario for reinfection in Edit case for DE version
  And I check if reinfection status is set to Mögliche Reinfektion for DE version
  Then I click on save case button
  Then I back to Case Directory using case list button
  Then I reset filter from Case Directory
  Then I filter by reinfection status as a Mögliche Reinfektion
  And I check if created case with reinfection status is displayed in the Case table for DE version