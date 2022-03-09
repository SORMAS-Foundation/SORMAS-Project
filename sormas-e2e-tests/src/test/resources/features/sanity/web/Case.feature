@UI @Sanity @Case
Feature: Case end to end tests

  @env_main
  Scenario: Check a new case data
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  @env_main
  Scenario: Check that double clicking NEW CASE button does not cause a redundant action
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case form with specific data
    And I click on the NEW CASE button
    Then I click on save case button
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  @env_main
  Scenario: Edit, save and check all fields of a new case
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I change all Case fields and save
    And I click on the Dashboard button from navbar and access Surveillance Dashboard
    And I open last edited case by link
    And I check the edited data is correctly displayed on Edit case page

  @issue=SORDEV-7868 @env_main
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

  @env_main
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

  @issue=SORDEV-5530 @env_main
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

  @env_main @ignore
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

@issue=SORDEV-5496 @env_main
  Scenario: Generate and download Case document
    Given I log in with National User
    And I click on the Cases button from navbar
    And I open last created case
    And I click on the Create button from Case Document Templates
    When I create and download a case document from template
    Then I verify that the case document is downloaded and correctly named

  @issue=SORDEV-5527 @env_main
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
    Then I click on Save button from New Prescription popup
    Then I check if created data is correctly displayed in Perscription section
    And I choose Oral rehydration salts option as a Prescription type
    And I choose Blood transfusion option as a Prescription type
    And I choose Renal replacement therapy option as a Prescription type
    And I choose IV fluid therapy option as a Prescription type
    And I choose Oxygen therapy option as a Prescription type
    And I choose Invasive mechanical ventilation option as a Prescription type
    And I choose Vasopressors/Inotropes option as a Prescription type
    Then I click on Save button from New Prescription popup
    Then I check if created data is correctly displayed in Perscription section
    And I click on Save button from New Prescription popup
    Then I create and fill Treatment with specific data for drug intake
    And I choose Antimicrobial option as a Type of drug
    And I choose Antiviral option as a Type of drug
    And I choose Other option as a Type of drug
    And I choose Other option as a Treatment type
    Then I click on Save button from New Treatment popup
    Then I check if created data is correctly displayed in Treatment section
    And I choose Oral rehydration salts option as a Treatment type
    And I choose Blood transfusion option as a Treatment type
    And I choose Renal replacement therapy option as a Treatment type
    And I choose IV fluid therapy option as a Treatment type
    And I choose Oxygen therapy option as a Treatment type
    And I choose Invasive mechanical ventilation option as a Treatment type
    And I choose Vasopressors/Inotropes option as a Treatment type
    Then I click on Save button from New Treatment popup
    Then I check if created data is correctly displayed in Treatment section

    @issue=SORDEV-5518 @env_main
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

  @issue=SORDEV-5529 @env_main @ignore
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

  @issue=SORDEV-8412 @env_main
  Scenario: Change of Isolation/Quarantine should be documented
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I set place for Quarantine as Home
    And I set Start date of Quarantine 2 days ago
    And I set End date of Quarantine to 5 days
    Then I click on save case button
    And I set End date of Quarantine to 4 days
    And I check if Reduce quarantine popup is displayed
    Then I click on yes quarantine popup button
    Then I click on save case button
    And I set End date of Quarantine to 6 days
    And I check if Extend quarantine popup is displayed
    Then I discard changes in quarantine popup
    And I check if Quarantine End date stayed reduce to 4 days
    And I set the quarantine end to a date 1 day after the Follow-up until date
    Then I click on yes quarantine popup button
    Then I click on yes Extend follow up period popup button
    Then I click on save case button
    And I check if Quarantine Follow up until date was extended to 1 day
    And I fill Quarantine change comment field
    Then I click on save case button
    And I check if Quarantine change comment field was saved correctly
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I click on the Contacts button from navbar
    Then I search after last created contact via API by UUID and open
    Then I set place for Quarantine as Home
    And I set Start date of Quarantine 2 days ago
    And I set End date of Quarantine to 5 days
    Then I click on save Contact button
    And I set End date of Quarantine to 4 days
    And I check if Reduce quarantine popup is displayed
    Then I click on yes quarantine popup button
    Then I click on save Contact button
    And I set End date of Quarantine to 6 days
    And I check if Extend quarantine popup is displayed
    Then I discard changes in quarantine popup
    And I check if Quarantine End date stayed reduce to 4 days
    And I set the quarantine end to a date 1 day after the Follow-up until date
    Then I click on yes quarantine popup button
    Then I click on yes Extend follow up period popup button
    Then I click on save Contact button
    And I check if Quarantine Follow up until date was extended to 1 day
    And I fill Quarantine change comment field
    Then I click on save Contact button
    And I check if Quarantine change comment field was saved correctly

  @issue=SORDEV-9033 @env_main
  Scenario: Create case with directly entered home address
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case form with specific data
    When I click on Enter Home Address of the Case Person Now in the Create New Case popup
    And I fill specific address data in Case Person tab
    Then I click on save case button
    And I navigate to case person tab
    And I check if saved Person data is correct

  @issue=SORDEV-7452 @env_main
  Scenario: Bulk mode for linking/adding cases to new Event
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create 2 new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Case report date" on Case directory page
    And I fill Cases from input to 1 days before mocked Cases created on Case directory page
    And I apply last created api Person Id filter on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I click checkbox to choose all Case results
    And I click on Bulk Actions combobox on Case Directory Page
    And I click on Link to Event from Bulk Actions combobox on Case Directory Page
    And I click on New Event option in Link to Event Form
    And I click on SAVE button in Link Event to group form
    And I create a new event with status CLUSTER
    And I navigate to the last created Event page via URL
    And I check that number of displayed Event Participants is 1

  @issue=SORDEV-7452 @env_main
  Scenario: Bulk mode for linking/adding case to existing Event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When API: I create 2 new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Case report date" on Case directory page
    And I fill Cases from input to 1 days before mocked Cases created on Case directory page
    And I apply last created api Person Id filter on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I click checkbox to choose all Case results
    And I click on Bulk Actions combobox on Case Directory Page
    And I click on Link to Event from Bulk Actions combobox on Case Directory Page
    And I fill Event Id filter with last created EventId on Link to Event form
    And I click first result in grid on Link to Event form
    And I click on SAVE button in Link Event to group form
    And I navigate to the last created through API Event page via URL
    And I check that number of displayed Event Participants is 1

  @issue=SORDEV-8048 @env_de
  Scenario: Test Default value for disease if only one is used by the server
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    Then I back to Case Directory using case list button
    And I click on Case Line Listing button
    Then I create a new case in line listing feature popup for DE version
    And I save the new line listing case
    Then I click on the Cases button from navbar
    And I check that case created from Line Listing for DE version is saved and displayed in results grid
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version
    Then I click SAVE a new contact
    Then I check the created data for DE version is correctly displayed on Edit Contact page
    Then I click on the Contacts button from navbar
    Then I click on Line Listing button
    And I create a new Contact with specific data for DE version through Line Listing
    And I save the new contact using line listing feature
    Then I click on the Contacts button from navbar
    And I check that contact created from Line Listing is saved and displayed in results grid
    Then I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data for DE version
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data for DE version is correctly displayed in event edit page
    Then I click on the Sample button from navbar
    When I open created Sample
    Then I click on the new pathogen test from the Edit Sample page for DE version
    And I complete all fields from Pathogen test result popup for IgM test type for DE version and save