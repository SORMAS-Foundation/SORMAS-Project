@UI @Sanity @Case
Feature: Case end to end tests

  Scenario: Check a new case data
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  Scenario: Check that double clicking NEW CASE button does not cause a redundant action
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case form with specific data
    And I click on the NEW CASE button
    Then I click on save case button
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  Scenario: Edit, save and check all fields of a new case
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I change all Case fields and save
    And I click on the Dashboard button from navbar and access Surveillance Dashboard
    And I open last edited case by link
    And I check the edited data is correctly displayed on Edit case page

  @issue=SORDEV-7868
  Scenario: Fill the case tab
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I select Investigation Status Done
    And I check if date of investigation filed is available
    Then I select Investigation Status Pending
    Then I select Investigation Status Discarded
    And I check if date of investigation filed is available
    Then I select Investigation Status Pending
    Then I select Outcome Of Case Status Deceased
    And I check if date of outcome filed is available
    Then I select Outcome Of Case Status Recovered
    And I check if date of outcome filed is available
    And I click on Yes option in Sequelae
    And I check if Sequelae Details field is available
    And I click on No option in Sequelae
    And I click on Unknown option in Sequelae
    Then I select Outcome Of Case Status Unknown
    And I check if date of outcome filed is available
    And I click on Yes option in Sequelae
    And I check if Sequelae Details field is available
    And I click on No option in Sequelae
    And I click on Unknown option in Sequelae
    Then I click on Place of stay of this case differs from its responsible jurisdiction
    And I check if region combobox is available and I select Responsible Region
    And I check if district combobox is available and i select Responsible District
    And I check if community combobox is available
    Then I click on Facility as place of stay
    And I check if Facility Category combobox is available
    And I check if Facility Type combobox is available
    Then I set Facility as a Other facility
    And I fill Facility name and description filed by dummy description
    And I check if Facility name and description field is available
    Then I set Quarantine Home
    And I check if Quarantine start field is available
    And I check if Quarantine end field is available
    Then I select Quarantine ordered verbally checkbox
    And I check if Date of verbal order field is available
    Then I select Quarantine ordered by official document checkbox
    And I check if Date of the official document ordered field is available
    Then I select Official quarantine order sent
    And I check if Date official quarantine order was sent field is available
    Then I set Quarantine Institutional
    And I check if Quarantine start field is available
    And I check if Quarantine end field is available
    And I check if Date of verbal order field is available
    And I check if Date of the official document ordered field is available
    And I check if Date official quarantine order was sent field is available
    Then I set Quarantine None
    Then I set Quarantine Unknown
    Then I set Quarantine Other
    And I check if Quarantine details field is available
    Then I set Vaccination Status as vaccinated
    Then I set Vaccination Status as unvaccinated
    Then I set Vaccination Status as unknown
    And I click on save button from Edit Case page with current hospitalization
    Then I check if the specific data is correctly displayed

  Scenario: Delete created case
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I delete the case
    Then I check that number of displayed cases results is 0

  Scenario: Edit all fields from Case Contacts tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I open the Case Contacts tab of the created case via api
    Then I click on new contact button from Case Contacts tab
    Then I create a new contact from Cases Contacts tab
    And I open the Case Contacts tab of the created case via api
    And I verify that created contact from Case Contacts tab is correctly displayed

  Scenario: Edit all fields from Symptoms tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the Symptoms tab using of created case via api
    And I change all symptoms fields and save
    And I click on the Dashboard button from navbar and access Surveillance Dashboard
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page

@issue=SORDEV-5496 @Mock
  Scenario: Generate case document
    Given I log in with National User
    And I click on the Cases button from navbar
    And I open last created case
    And I click on the Create button from Case Document Templates
    When I create a case document from template
    Then I verify that the case document is downloaded and correctly named

  @issue=SORDEV-5527
  Scenario: Fill the therapy tab
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I am accessing the Therapy tab of created case
    And I create and fill Prescriptions with specific data for drug intake
    And I choose Antimicrobial option as a Type of drug
    And I choose Antiviral option as a Type of drug
    And I choose Other option as a Type of drug
    And I choose Other option as a Prescription type
    Then I click on the popup Save button
    Then I check if created data is correctly displayed in Perscription section
    And I choose Oral rehydration salts option as a Prescription type
    And I choose Blood transfusion option as a Prescription type
    And I choose Renal replacement therapy option as a Prescription type
    And I choose IV fluid therapy option as a Prescription type
    And I choose Oxygen therapy option as a Prescription type
    And I choose Invasive mechanical ventilation option as a Prescription type
    And I choose Vasopressors/Inotropes option as a Prescription type
    Then I click on the popup Save button
    Then I check if created data is correctly displayed in Perscription section
    And I click on the popup Save button
    Then I create and fill Treatment with specific data for drug intake
    And I choose Antimicrobial option as a Type of drug
    And I choose Antiviral option as a Type of drug
    And I choose Other option as a Type of drug
    And I choose Other option as a Treatment type
    Then I click on the popup Save button
    Then I check if created data is correctly displayed in Treatment section
    And I choose Oral rehydration salts option as a Treatment type
    And I choose Blood transfusion option as a Treatment type
    And I choose Renal replacement therapy option as a Treatment type
    And I choose IV fluid therapy option as a Treatment type
    And I choose Oxygen therapy option as a Treatment type
    And I choose Invasive mechanical ventilation option as a Treatment type
    And I choose Vasopressors/Inotropes option as a Treatment type
    Then I click on the popup Save button
    Then I check if created data is correctly displayed in Treatment section

  @issue=SORDEV-5530
  Scenario: Fill the contacts tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I open the Case Contacts tab of the created case via api
    Then I click on new contact button from Case Contacts tab
    And I create a new contact from Cases Contacts tab base on Person created by API
    Then I pick Create a new person box in Pick or create person form
    And I click Save button in Pick or create person form
    Then I click on the Contacts button from navbar
    And I search last create contact by UUID case in Contact Directory
    Then I check that number of displayed cases results is 1
    Then I click on the Cases button from navbar
    And I search last create case by UUID in Contact Directory
    Then I check that number of displayed cases results is 1

    @issue=SORDEV-5518 @DE
  Scenario: Fill the case person tab
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page
    Then I set Present condition of Person to Dead in Case Person tab
    And I check if death data fields are available in Case Person tab
    Then I set Present condition of Person to Buried in Case Person tab
    And I check if buried data fields are available in Case Person tab
    Then I fill specific address data in Case Person tab
    Then I click on Geocode button to get GPS coordinates in Case Person Tab
    And I click on save button to Save Person data in Case Person Tab
    Then I check if saved Person data is correct

  @issue=SORDEV-5529
  Scenario: Fill the clinical course tab
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on Clinical Course tab from Edit Case page
    And I click on New Clinical Assesement button on Clinical Course page
    And I fill the specific data of clinical visit with Set cleared to No option to all symptoms
    Then I click Save button in New Clinical Assessement popup
    Then I navigate to symptoms tab
    And I check if created data is correctly displayed in Symptoms tab for Set cleared to NO
    Then I clear Clinical Signs and Symptoms list
    Then I click on Clinical Course tab from Edit Case page
    And I am saving clear Clinical Signs and Symptoms list
    And I click on Edit Clinical Visit button
    And I fill the specific data of clinical visit with Set cleared to Unknown option to all symptoms
    Then I click Save button in New Clinical Assessement popup
    Then I navigate to symptoms tab
    And I check if created data is correctly displayed in Symptoms tab for Set cleared to Unknown
    Then I click on Clinical Course tab from Edit Case page
    And I set Diabetes radio button to YES
    And I set Diabetes radio button to NO
    And I set Diabetes radio button to UNKNOWN
    And I set Diabetes radio button to UNKNOWN
    And I set Immunodeficiency including HIV radio button to YES
    And I set Immunodeficiency including HIV radio button to NO
    And I set Immunodeficiency including HIV radio button to UNKNOWN
    And I set Immunodeficiency including HIV radio button to UNKNOWN
    And I set Liver disease radio button to YES
    And I set Liver disease radio button to NO
    And I set Liver disease radio button to UNKNOWN
    And I set Liver disease radio button to UNKNOWN
    And I set Malignancy radio button to YES
    And I set Malignancy radio button to NO
    And I set Malignancy radio button to UNKNOWN
    And I set Malignancy radio button to UNKNOWN
    And I set Chronic pulmonary disease radio button to YES
    And I set Chronic pulmonary disease radio button to NO
    And I set Chronic pulmonary disease radio button to UNKNOWN
    And I set Chronic pulmonary disease radio button to UNKNOWN
    And I set Renal disease radio button to YES
    And I set Renal disease radio button to NO
    And I set Renal disease radio button to UNKNOWN
    And I set Renal disease radio button to UNKNOWN
    And I set Chronic neurological/neuromuscular disease radio button to YES
    And I set Chronic neurological/neuromuscular disease radio button to NO
    And I set Chronic neurological/neuromuscular disease radio button to UNKNOWN
    And I set Chronic neurological/neuromuscular disease radio button to UNKNOWN
    And I set Cardiovascular disease including hypertension radio button to YES
    And I set Cardiovascular disease including hypertension radio button to NO
    And I set Cardiovascular disease including hypertension radio button to UNKNOWN
    And I set Cardiovascular disease including hypertension radio button to UNKNOWN
    Then I click Save button on Clinical Course Tab
    And I check if Case saved popup appeared and close it