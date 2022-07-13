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

  @env_main @ignore #un-ignore this when dataReceived fields are fixed in test-auto
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
    Then I set Quarantine Institutional
    And I check if Quarantine start field is available
    And I check if Quarantine end field is available
    Then I set Quarantine None
    Then I set Quarantine Unknown
    Then I set Quarantine Other
    And I check if Quarantine details field is available
    Then I set Vaccination Status as vaccinated
    Then I set Vaccination Status as unvaccinated
    Then I set Vaccination Status as unknown
    And I click on save button from Edit Case page with current hospitalization
    Then I check if the specific data is correctly displayed

  @issue=SORDEV-5517 @env_de
  Scenario: Fill the case tab (DE specific)
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I select German Investigation Status Done
    And I check if date of investigation filed is available
    Then I select German Investigation Status Pending
    Then I select German Investigation Status Discarded
    And I check if date of investigation filed is available
    Then I select German Investigation Status Pending
    Then I select German Outcome Of Case Status Deceased
    And I check if date of outcome filed is available
    Then I select German Outcome Of Case Status Recovered
    And I check if date of outcome filed is available
    And I click on the German option for Yes in Sequelae
    And I check if Sequelae Details field is available
    And I click on the German option for No in Sequelae
    And I click on the German option for Unknown in Sequelae
    Then I select German Outcome Of Case Status Unknown
    And I check if date of outcome filed is available
    And I click on the German option for Yes in Sequelae
    And I check if Sequelae Details field is available
    And I click on the German option for No in Sequelae
    And I click on the German option for Unknown in Sequelae
    Then I click on Place of stay of this case differs from its responsible jurisdiction
    And I check if region combobox is available and I select Responsible Region
    And I check if district combobox is available and i select Responsible District
    And I check if community combobox is available
    Then I click on Facility as German place of stay
    And I check if Facility Category combobox is available
    And I check if Facility Type combobox is available
    Then I set Facility in German as a Other facility
    And I fill Facility name and description filed by dummy description
    And I check if Facility name and description field is available
    Then I set German Quarantine Home
    And I check if Quarantine start field is available
    And I check if Quarantine end field is available
    Then I select Quarantine ordered verbally checkbox
    And I check if Date of verbal order field is available
    Then I select Quarantine ordered by official document checkbox
    And I check if Date of the official document ordered field is available
    Then I select Official quarantine order sent
    And I check if Date official quarantine order was sent field is available
    Then I set German Quarantine Institutional
    And I check if Quarantine start field is available
    And I check if Quarantine end field is available
    And I check if Date of verbal order field is available
    And I check if Date of the official document ordered field is available
    And I check if Date official quarantine order was sent field is available
    Then I set German Quarantine None
    Then I set German Quarantine Unknown
    Then I set German Quarantine Other
    And I check if Quarantine details field is available
    Then I set German Vaccination Status as vaccinated
    Then I set German Vaccination Status as unvaccinated
    Then I set German Vaccination Status as unknown
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

  @issue=SORQA-100 @env_main
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
    When I create a new case with disease "ANTHRAX"
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

  @issue=SORDEV-5529 @env_main
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
    And I apply uuid filter for last created via API Person in Case directory page
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
    And I apply uuid filter for last created via API Person in Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I click checkbox to choose all Case results
    And I click on Bulk Actions combobox on Case Directory Page
    And I click on Link to Event from Bulk Actions combobox on Case Directory Page
    And I fill Event Id filter in Link to Event form with last created via API Event uuid
    And I click first result in grid on Link to Event form
    And I click on SAVE button in Link Event to group form
    And I navigate to the last created through API Event page via URL
    And I check that number of displayed Event Participants is 1

  @issue=SORDEV-6843 @env_main
  Scenario: Refine the update mechanism between case outcome and case filters
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case with for one person with specified date for month ago
    Then I click on save case button
    Then I collect uuid of the case
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I fill second new case with for one person with specified date for present day
    And I confirm changes in selected Case
    And I confirm Pick person in Case
    Then I collect uuid of the case
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I select first created case for person from Cases list
    And I select Deceased as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I reset filter from Case Directory
    Then I filter by Dead user condition
    Then I filter with first Case ID
    And I check if created person is on filtered list with Deceased status
    Then I reset filter from Case Directory
    Then I filter by Dead user condition
    Then I select second created case for person from Cases list
    And I select Recovered as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I filter by unspecified user condition
    Then I filter with second Case ID
    And I check if created person is on filtered list with Recovered status
    Then I reset filter from Case Directory
    Then I select first created case for person from Cases list
    And I select Recovered as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I reset filter from Case Directory
    Then I select second created case for person from Cases list
    And I select Deceased as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I reset filter from Case Directory
    Then I filter with second Case ID
    And I check if created person is on filtered list with Deceased status
    Then I reset filter from Case Directory
    Then I select first created case for person from Cases list
    And I select No outcome yet as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I reset filter from Case Directory
    Then I select second created case for person from Cases list
    And I select No outcome yet as Outcome Of Case Status
    Then I confirm changes in selected Case
    And I back to the cases list from edit case
    Then I click on the Persons button from navbar
    And I filter Persons by created person name in cases
    And I click on first person in person directory
    And I set Present condition of Person to Dead in Person tab
    Then I set death date for person 1 month ago
    And I click on save button from Edit Person page
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I filter by Dead user condition
    Then I select first created case for person from Cases list
    And I back to the cases list from edit case
    And I check if created person is on filtered list with No Outcome Yet status
    Then I reset filter from Case Directory
    Then I select second created case for person from Cases list
    And I back to the cases list from edit case
    Then I filter by Dead user condition
    And I check if created person is on filtered list with No Outcome Yet status

  @issue=SORDEV-6843 @env_main
  Scenario: Refine the update mechanism between case outcome and person death date
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case with for one person with specified date for month ago
    Then I click on save case button
    And I collect uuid of the case
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I fill second new case with for one person with specified date for present day
    And I confirm changes in selected Case
    And I confirm Pick person in Case
    And I collect uuid of the case
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I select second created case for person from Cases list
    And I select Deceased as Outcome Of Case Status
    Then I fill the Date of outcome to yesterday
    Then I confirm changes in selected Case
    Then I click on the Persons button from navbar
    And I filter Persons by created person name in cases
    And I click on first person in person directory
    And I check if Date of dead for specified case is correct
    And I check if Cause of death is Epidemic disease
    Then I set death date for person 1 month ago
    And I click on save button from Edit Person page
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I filter by Dead user condition
    Then I select second created case for person from Cases list
    And I check if date of outcome is updated for 1 month ago
    Then I fill the Date of outcome to yesterday
    Then I confirm changes in selected Case
    Then I click on the Persons button from navbar
    And I filter Persons by created person name in cases
    And I click on first person in person directory
    And I check if Date of dead for specified case is correct

  @issue=SORDEV-6843 @env_main
  Scenario: Refine the update mechanism between case outcome and person other cause date
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case with for one person with specified date for month ago
    Then I click on save case button
    And I collect uuid of the case
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I fill second new case with for one person with specified date for present day
    And I confirm changes in selected Case
    And I confirm Pick person in Case
    And I collect uuid of the case
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I select second created case for person from Cases list
    And I select Deceased as Outcome Of Case Status
    Then I fill the Date of outcome to yesterday
    Then I confirm changes in selected Case
    Then I click on the Persons button from navbar
    And I filter Persons by created person name in cases
    And I click on first person in person directory
    And I check if Date of dead for specified case is correct
    And I change Cause of death to Other cause
    Then I set death date for person 20 days ago
    And I click on save button from Edit Person page
    Then I click on the Cases button from navbar
    And I filter Cases by created person name
    Then I filter by Dead user condition
    Then I select second created case for person from Cases list
    And I check if date of outcome is updated for 20 days ago
    Then I fill the Date of outcome to yesterday
    Then I confirm changes in selected Case
    Then I click on the Persons button from navbar
    And I filter Persons by created person name in cases
    And I click on first person in person directory
    And I check if Cause of death is Other cause
    And I check if Date of dead for specified case is correct

  @issue=SORDEV-6612 @env_main @ignore
  Scenario: Manually triggered calculation of case classification
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on INFO button on Case Edit page
    When I am accessing the Symptoms tab using of created case via api
    And I change all symptoms fields to "YES" option field and save
    And I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page
    And I click on Case tab from Symptoms tab directory
    And I check that Case Classification has "Suspect case" value
    Then I click on save case button
    Then I navigate to symptoms tab
    Then I change Other symptoms to "YES" option
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields to "NO_AND_OTHER_SYMPTOMS_TO_YES" option field and save
    And I am accessing the Symptoms tab using of created case via api
    And I check the created data that describes Clinical signs and Symptoms are correctly displayed for No or UNKNOWN option in Symptoms tab page
    And  I click on Case tab from Symptoms tab directory
    And I check that Case Classification has "Not yet classified" value
    Then I click on save case button
    When I am accessing the Symptoms tab using of created case via api
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields to "YES" option field and save
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page
    And I click on Case tab from Symptoms tab directory
    And I change Epidemiological confirmation Combobox to "Yes" option
    Then I click on save case button
    And I check that Case Classification has "Probable case" value
    Then I click on save case button
    When I am accessing the Symptoms tab using of created case via api
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields to "YES" option field and save
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page
    And I click on Case tab from Symptoms tab directory
    Then I click on save case button
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with for COVID alternative purpose
    And I click on edit Sample
    And I click on the new pathogen test from the Edit Sample page
    And I fill all fields from Pathogen test for COVID-19 disease result popup and save
    Then I check that the created Pathogen is correctly displayed
    And I save the created sample
    And I click on Case tab from Symptoms tab directory
    Then I click on save case button in Symptoms tab
    And I check that Case Classification has "Confirmed case" value
    When I am accessing the Symptoms tab using of created case via api
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields to "NO" option field and save
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data that describes Clinical signs and Symptoms are correctly displayed for No or UNKNOWN option in Symptoms tab page
    Then I click on save case button in Symptoms tab
    And I click on Case tab from Symptoms tab directory
    And I collect the case person UUID displayed on Edit case page
    And I click on Case tab from Symptoms tab directory
    Then I click on save case button in Symptoms tab
    And I check that Case Classification has "Confirmed case with no symptoms" value
    When I am accessing the Symptoms tab using of created case via api
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields to "UNKNOWN" option field and save
    When I am accessing the Symptoms tab using of created case via api
    And I check the created data that describes Clinical signs and Symptoms are correctly displayed for No or UNKNOWN option in Symptoms tab page
    And I click on Case tab from Symptoms tab directory
    Then I click on save case button in Symptoms tab
    And I collect the case person UUID displayed on Edit case page
    And I click on Case tab from Symptoms tab directory
    Then I click on save case button
    And I check that Case Classification has "Confirmed case with unknown symptoms" value
    When I am accessing the Symptoms tab using of created case via api
    And I click on Clear all button From Symptoms tab
    And I change all symptoms fields and save
    And I am accessing the Symptoms tab using of created case via api
    And I check the created data is correctly displayed on Symptoms tab page
    And I click on Case tab from Symptoms tab directory
    And I check that Case Classification has "Confirmed case" value
    Then I click on save case button
    And I change the Case Classification field for "NOT_CLASSIFIED" value
    And I click on save case button
    And From Case page I click on Calculate Case Classification button
    And I click on save case button
    And I check that Case Classification has "Confirmed case" value

  @issue=SORDEV-8048 @env_de
  Scenario: Test Default value for disease if only one is used by the server for Cases
    Given I log in with National User
    Then I click on the Cases button from navbar
    When I click on the NEW CASE button
    Then I check if default disease value is set for COVID-19
    And I click on Case Line Listing button
    And I check if default disease value in the Line listing is set for COVID-19

  @issue=SORDEV-9353 @env_main
  Scenario: Deselecting the "Enter home address of the case person now" test regression
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case form with specific data
    When I click on Enter Home Address of the Case Person Now in the Create New Case popup
    And I fill specific address data in Case Person tab
    Then I click on Enter Home Address of the Case Person Now in the Create New Case popup
    Then I click on save case button
    Then I check the created data is correctly displayed on Edit case page
    And I check the created data is correctly displayed on Edit case person page

  @issue=SORDEV-7466 @env_de
  Scenario: Check reference definition for cases
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with only the required data for DE version
    Then I check that case classification is set to not yet classified in German on Edit case page
    And I check that case reference definition is not editable on Edit case page
    And I check that case reference definition is set to not fulfilled in German on Edit case page
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample
    Then I check that case classification is set to one of the confirmed classifications in German on Edit case page
    And I check that case reference definition is set to not fulfilled in German on Edit case page
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample
    Then I check that case classification is set to one of the confirmed classifications in German on Edit case page
    And I check that case reference definition is set to not fulfilled in German on Edit case page
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Isolation as Type of Test in the Create New Sample popup
    And I save the created sample
    Then I check that case classification is set to one of the confirmed classifications in German on Edit case page
    And I check that case reference definition is set to fulfilled in German on Edit case page
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for PCR RT-PCR as Type of Test in the Create New Sample popup
    And I save the created sample
    Then I check that case classification is set to one of the confirmed classifications in German on Edit case page
    And I check that case reference definition is set to fulfilled in German on Edit case page
    When I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I click Only cases with fulfilled reference definition checkbox in Cases directory additional filters
    And I click APPLY BUTTON in Case Directory Page
    Then I check that only cases with fulfilled reference definition are being shown in Cases directory
    When I click on the Dashboard button from navbar and access Surveillance Dashboard
    And I click on the Time Period combobox from Surveillance Dashboard
    And I choose yesterday from the Surveillance Dashboard Time Period combobox
    And I click on the APPLY FILTERS button
    Then I check that the number of cases fulfilling the reference definition is larger than 0

  @issue=SORDEV-5479 @env_main
  Scenario: Test for exporting and importing case contact
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to export from Cases Contacts tab
    And I open the Case Contacts tab
    And I click Export button in Case Contacts Directory
    And I click on Detailed Export button in Case Contacts Directory
    And I close popup after export in Case Contacts directory
    Then I click on the Import button from Case Contacts directory
    And I select the case contact CSV file in the file picker
    And I click on the "START DATA IMPORT" button from the Import Case Contacts popup
    And I select first existing person from the Case Contact Import popup
    And I confirm the save Case Contact Import popup
    And I select first existing contact from the Case Contact Import popup
    And I check that an import success notification appears in the Import Case Contact popup
    Then I delete exported file from Case Contact Directory

  @issue=SORDEV-7456 @env_de
  Scenario: Check different facility types depending on type of place in Epidemiological Tab
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    And I navigate to Epidemiological Data tab on Edit Case Page
    And I click on JA Radiobutton on Epidemiological Data Page
    And I click on new entry button from Epidemiological Data tab
    And I set Facility to "Einrichtung (§ 23 IfSG)" from New Entry popup
    And I set Facility Type to "Krankenhaus" from New Entry popup
    And I set Facility Type to "Einrichtung für ambulantes Operieren" from New Entry popup
    And I set Facility Type to "Reha Einrichtung" from New Entry popup
    And I set Facility Type to "Dialyseeinrichtung" from New Entry popup
    And I set Facility Type to "Tagesklinik" from New Entry popup
    And I set Facility Type to "Entbindungseinrichtung" from New Entry popup
    And I set Facility Type to "Andere medizinische Einrichtung" from New Entry popup
    And I set Facility Type to "Arztpraxis" from New Entry popup
    And I set Facility Type to "Zahnarztpraxis" from New Entry popup
    And I set Facility Type to "Praxis sonstiger humanmedizinischer Heilberufe" from New Entry popup
    And I set Facility Type to "Einrichtung des ÖGD zur Diagnostik, Prävention, Therapie" from New Entry popup
    And I set Facility Type to "Mobiler/Ambulanter Pflegedienst" from New Entry popup
    And I set Facility Type to "Rettungsdienst" from New Entry popup
    And I set Facility to "Gemeinschaftseinrichtung (§ 33 IfSG)" from New Entry popup
    And I set Facility Type to "Kindertageseinrichtung" from New Entry popup
    And I set Facility Type to "Kindertagespflege" from New Entry popup
    And I set Facility Type to "Schule" from New Entry popup
    And I set Facility Type to "Kinderheim" from New Entry popup
    And I set Facility Type to "Ferienlager" from New Entry popup
    And I set Facility Type to "Kinderhort" from New Entry popup
    And I set Facility Type to "Andere Betreuungs- und Bildungseinrichtung" from New Entry popup
    And I set Facility to "Einrichtung (§ 36 IfSG)" from New Entry popup
    And I set Facility Type to "Andere Pflegeeinrichtung" from New Entry popup
    And I set Facility Type to "Pflegeeinrichtung für ältere Menschen" from New Entry popup
    And I set Facility Type to "Pflegeeinrichtung für Menschen mit Behinderung" from New Entry popup
    And I set Facility Type to "Pflegeeinrichtung für pflegebedürftige Menschen" from New Entry popup
    And I set Facility Type to "Obdachlosenunterkunft" from New Entry popup
    And I set Facility Type to "Flüchtlingsunterkunft/Erstaufnahmeeinrichtung" from New Entry popup
    And I set Facility Type to "Massenunterkunft (z.B. Gast- und Erntearbeiter)" from New Entry popup
    And I set Facility Type to "Justizvollzugsanstalt" from New Entry popup
    And I set Facility Type to "Mobiler/Ambulanter Pflegedienst" from New Entry popup
    And I set Facility Type to "Aufsuchende ambulante Hilfen" from New Entry popup
    And And I click on Discard button from New Entry popup

  @issue=SORQA-123 @env_main
  Scenario: Import Documentation for Cases Test
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the import button for Cases in Case tab
    Then I click on the detailed button from import Case tab
    And I click on the Download Import Guide button in Import Cases
    Then I check if Import Guide for cases was downloaded correctly
    And And I click on the Download Data Dictionary button in Import Cases
    Then I check if Data Dictionary for cases was downloaded correctly

  @issue=SORDEV-5526 @env_main
    Scenario: Create a contact with source case
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I navigate to Epidemiological Data tab on Edit Case Page
    When I select NO from Contacts With Source Case Known
    Then I check that Contacts of Source filed is not available
    When I select UNKNOWN from Contacts With Source Case Known
    Then I check that Contacts of Source filed is not available
    When I select YES from Contacts With Source Case Known
    Then I check if Contacts of Source filed is available
    When I click on the NEW CONTACT button on Epidemiological Data Tab of Edit Case Page
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup if it appears
    And I click on the CHOOSE CASE button in Create new contact form in Exposure for Epidemiological data tab in Cases
    And I search for the last case uuid in the CHOOSE SOURCE popup of Create Contact window
    And I open the first found result in the CHOOSE SOURCE popup of Create Contact window
    And I click on SAVE new contact button in the CHOOSE SOURCE popup of Create Contact window
    Then I check that Selected case is listed as Source Case in the CONTACTS WITH SOURCE CASE Box

  @issue=SORDEV-9124 @env_main
  Scenario: Document Templates create quarantine order
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on Create button in Document Templates box in Edit Case directory
    And I click on checkbox to upload generated document to entity in Create Quarantine Order form in Edit Case directory
    And I select "ExampleDocumentTemplateCases.docx" Quarantine Order in Create Quarantine Order form in Edit Case directory
    And I click on Create button in Create Quarantine Order form
    Then I navigate to the last created case via the url
    And I check if downloaded file is correct for "ExampleDocumentTemplateCases.docx" Quarantine Order in Edit Case directory
    And I check if generated document based on "ExampleDocumentTemplateCases.docx" appeared in Documents tab for API created case in Edit Case directory
    And I delete downloaded file created from "ExampleDocumentTemplateCases.docx" Document Template

  @issue=SORDEV-9124 @env_main
  Scenario: Document Templates create quarantine order for Case bulk
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    Then I check the created data is correctly displayed on Edit case page
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Case report date" on Case directory page
    And I fill Cases from input to 1 days before mocked Case created on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I select last created UI result in grid in Case Directory for Bulk Action
    And I select last created API result in grid in Case Directory for Bulk Action
    And I click on Bulk Actions combobox on Case Directory Page
    And I click on Create Quarantine Order from Bulk Actions combobox on Case Directory Page
    And I click on checkbox to upload generated document to entities in Create Quarantine Order form in Case directory
    And I select "ExampleDocumentTemplateCases.docx" Quarantine Order in Create Quarantine Order form in Case directory
    And I click on Create button in Create Quarantine Order form
    And I click on close button in Create Quarantine Order form
    And I check if downloaded zip file for Quarantine Order is correct
    Then I click Leave Bulk Edit Mode on Case directory page
    And I open the last created Case via API
    And I check if generated document based on "ExampleDocumentTemplateCases.docx" appeared in Documents tab for API created case in Edit Case directory
    Then I click on the Cases button from navbar
    And I filter by CaseID of last created UI Case on Case directory page
    Then I open last created case
    And I check if generated document based on "ExampleDocumentTemplateCases.docx" appeared in Documents tab for UI created case in Edit Case directory
    And I delete downloaded file created from Quarantine order

  @issue=SORDEV-9477 @env_main
  Scenario: Add a person search option on creation forms
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case form with chosen data without personal data on Case directory page
    And I click on the person search button in new case form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on the clear button in new case form
    And I click on the person search button in new case form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    Then I click on Save button in Case form
    And I Pick an existing case in Pick or create person popup in Case entry
    And I check the created data for existing person is correctly displayed on Edit case page
    And I click on Save button in Case form
    When I click on the Persons button from navbar
    And I open the last created Person via API
    And I check that SEE CASES FOR THIS PERSON button appears on Edit Person page
    Then I click on the Cases button from navbar
    And I open last created case
    And I navigate to Contacts tab in Edit case page
    Then I click on the NEW CONTACT button
    And I fill a new contact form with chosen data without personal data
    And I click on the person search button in create new contact form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on the clear button in new contact form
    And I click on the person search button in create new contact form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    Then I click on SAVE new contact button
    Then I check the created data for existing person is correctly displayed on Edit Contact page based on Case
    When I click on the Persons button from navbar
    And I click on the RESET FILTERS button for Person
    And I open the last created Person via API
    And I check that SEE CONTACTS FOR THIS PERSON button appears on Edit Person page

  @issue=SORDEV-9088 @env_main
  Scenario: Check if all sexes have pregnancy attributes
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data and new person
    And I navigate to case person tab
    And I set case person's sex as Male
    And I click on save button to Save Person data in Case Person Tab
    When I navigate to case tab
    And I set pregnancy to YES
    And I check that trimester field is present
    And I click on save button from Edit Case page
    When I navigate to case person tab
    And I set case person's sex as Other
    And I click on save button to Save Person data in Case Person Tab
    When I navigate to case tab
    And I check that trimester field is present

  @issue=SORDEV-10265 @env_main
  Scenario: Manual archiving for case contacts
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I open the Case Contacts tab of the created case via api
    Then I click on new contact button from Case Contacts tab
    Then I create a new contact from Cases Contacts tab
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on the Archive case button
    Then I check the end of processing date in the archive popup and select Archive contacts checkbox
    And I check if Archive button changed name to De-Archive
    Then I click on the Contacts button from navbar
    When I choose Archived contacts form combobox on Contact Directory Page
    Then I filter by last created contact via api
    Then I open the first contact from contacts list
    And I check if Archive button changed name to De-Archive

  @issue=SORDEV-10265 @env_main
  Scenario: Manual archiving for bulk case contacts
    When API: I create 2 new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I open the Case Contacts tab of the first created case via api
    Then I click on new contact button from Case Contacts tab
    And I create a new contact from Cases Contacts tab
    When I open the Case Contacts tab of the second created case via api
    Then I click on new contact button from Case Contacts tab
    And I create a new contact from Cases Contacts tab
    And I click on the Cases button from navbar
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Case report date" on Case directory page
    And I fill Cases from input to 0 days before mocked two Case created on Case directory page via api
    And I click APPLY BUTTON in Case Directory Page
    Then I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    Then I select two last created API result in grid in Case Directory for Bulk Action
    Then I click on Bulk Actions combobox on Case Directory Page
    And I click on the Archive bulk cases on Case Directory page
    Then I confirm archive bulk cases and select Archive related contacts checkbox
    Then I click on the Contacts button from navbar
    When I choose Archived contacts form combobox on Contact Directory Page
    Then I click on first created contact in Contact directory page by UUID
    And I check if Archive button changed name to De-Archive
    Then I click on the Contacts button from navbar
    When I choose Archived contacts form combobox on Contact Directory Page
    Then I click on second created contact in Contact directory page by UUID
    And I check if Archive button changed name to De-Archive

  @issue=SORDEV-9786 @env_main
  Scenario: Test The "urine p.m." enum value should be hidden when Covid19 is selected as disease
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I check that the value selected from Disease combobox is "COVID-19" on Edit Case page
    Then I click on New Sample
    And I check if value "Urine p.m" is unavailable in Type of Sample combobox on Create new Sample page

  @issue=SORDEV-9155 @env_main
  Scenario: Test Vaccinations get lost when merging cases with duplicate persons
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case data for duplicates merge with for one person data
    And I click on Save button in Case form
    Then I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case data for duplicates merge with for one person data
    And I click on Save button in Case form
    And I Pick a new person in Pick or create person popup during case creation
    Then I check the created data for duplicated case is correctly displayed on Edit case page
    And I set Vaccination status to "Vaccinated" on Edit Case page
    And I click on save button from Edit Case page
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I apply Report on onset date type filter to "Case report date" on Merge duplicate cases page
    And I fill date from input to today on Merge Duplicate Cases page
    Then I click to CONFIRM FILTERS on Merge Duplicate Cases page
    And I click on Merge button of leading case in Merge Duplicate Cases page
    Then I click to Confirm action in Merge Duplicates Cases popup
    And I click on the Cases button from navbar
    And I filter Cases by created person name
    And I open last created case
    And I check if Vaccination Status is set to "Vaccinated" on Edit Case page

  @issue=SORDEV-7460 @env_main
  Scenario: Test Extend the exposure and event startDate and endDate to include a startTime and endTime
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I navigate to Epidemiological Data tab on Edit Case Page
    And I click on Exposure details known with YES option
    Then I click on New Entry in Exposure Details Known in Cases directory
    And I set Start and End of activity by current date in Exposure form
    And I select a Type of activity Work option in Exposure for Epidemiological data tab in Cases
    And I click on SAVE button in Exposure form
    And I collect the Date of Start and End Exposure from Exposure page
    Then I check that Date field displays start date and end date in table Exposure on Epidemiological data tab

     @issue=SORDEV-5613 @env_main
      Scenario: Option to attach document like pdf, word, jpeg to cases
      Given I log in with National User
      When I click on the Cases button from navbar
      Then I click on the NEW CASE button
      And I create a new case with specific data
      Then I click on START DATA IMPORT button from New document in case tab
      And I upload pdf file to the case
      And I check if pdf file is available in case documents
      Then I download last updated document file from case tab
      And I check if pdf file is downloaded correctly
      Then I delete last uploaded document file from case tab
      And I check if last uploaded file was deleted from document files in case tab
      Then I click on START DATA IMPORT button from New document in case tab
      And I upload docx file to the case
      And I check if docx file is available in case documents
      Then I download last updated document file from case tab
      And I check if docx file is downloaded correctly
      Then I delete last uploaded document file from case tab
      And I check if last uploaded file was deleted from document files in case tab
      Then I click on START DATA IMPORT button from New document in case tab
      And I upload jpg file to the case
      And I check if jpg file is available in case documents
      Then I download last updated document file from case tab
      And I check if jpg file is downloaded correctly
      Then I delete last uploaded document file from case tab
      And I check if last uploaded file was deleted from document files in case tab

  @issue=SORDEV-9151 @env_de
  Scenario: Check if specific Case fields are hidden (DE specific)
    Given I log in with National User
    And I click on the Cases button from navbar
    When I open last created case
    And I navigate to case person tab
    Then I check that Passport Number is not visible
    And I check that National Health ID is not visible
    And I check that Education is not visible
    And I check that Community Contact Person is not visible
    And I check that Nickname is not visible
    And I check that Mother's Maiden Name is not visible
    And I check that Mother's Name is not visible
    And I check that Father's Name is not visible

  @issue=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Case directory
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I click on the NEW CASE button
    And I click on the person search button in new case form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup

  @issue=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Case Contact directory
    Given I log in with National User
    And I click on the Cases button from navbar
    When I open last created case
    And I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I click on the person search button in create new contact form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup

  @issue=SORDEV-9946 @env_de
  Scenario: Test Hide country specific fields in the 'Pick or create person' form of the duplicate detection pop-up, in German and French systems
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill a new case form with same person details for DE version
    And I click on Save button in Case form
    Then I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill a new case form with same person details for DE version
    And I click on Save button in Case form
    Then I check if National Health Id, Nickname and Passport number appear in Pick or create person popup
    And I open the Case Contacts tab
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data for DE version
    And I click on SAVE new contact button
    And I open the Case Contacts tab
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data for DE version
    And I click on SAVE new contact case button
    Then I check if National Health Id, Nickname and Passport number appear in Pick or create person popup

  @issue=SORDEV-8413 @env_main
  Scenario: Test Hide specific enum values based on the related disease
    Given I log in with National User
    When I click on the Cases button from navbar
    Then I click on the NEW CASE button
    And I create a new case with specific data
    Then I click on New Task from Case page
    And I check if Task Type has not a environmental health activities option
    And I check if Task Type has not a safe burial / cremation option
    And I check if Task Type has not a depopulation of animals option
    And I check if Task Type has not a testing of animals option
    Then I click on discard button from new task
    And I navigate to epidemiological data tab in Edit case page
    And I click on Exposure details known with YES option
    Then I click on New Entry in Exposure Details Known in Cases directory
    And I check if Exposure Type of activity has not a Burial option
    And I check if Exposure details has a Animal contact option
    Then I click on discard button from Epidemiological Data Exposure popup
    And I navigate to case tab
    Then I click yes on the DISCARD UNSAVED CHANGES popup if it appears
    Then I click on New Sample
    And I check if Type of sample has not a stool option
    And I check if Type of sample has not a rectal swab option
    And I check if Type of sample has not a crust option
    And I check if Type of sample has not a urine option
    And I check if Type of sample has not a nurchal skin biopsy option
    And I check if Type of sample has not a brain tissue option
    Then I create a new Sample with specific data and save
    And I click on edit Sample
    And I click on the new pathogen test from the Edit Sample page
    Then I set Test Disease as COVID-19 in new pathogen result
    And I check if Type of test in new pathogen results has no incubation time option
    And I check if Type of test in new pathogen results has no Indirect Fluorescent Antibody time option
    And I check if Type of test in new pathogen results has no Direct Fluorescent Antibody time option
    And I check if Type of test in new pathogen results has no Microscopy time option
    And I check if Type of test in new pathogen results has no Gram Stain time option
    And I check if Type of test in new pathogen results has no Latex Agglutination time option

  @issue=SORDEV-9496 @env_de
  Scenario: Test Handle person related fields and search button for travel entry forms
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    Then I click on the Cases button from navbar
    When I open the last created Case via API
    Then I navigate to Epidemiological Data tab on Edit Case Page
    And I click on new entry button from Epidemiological Data tab for DE
    When I fill the required fields in a new travel entry form without disease and person data
    Then I check that First Name is not visible in New Travel Entry popup
    And I check that Last Name is not visible in New Travel Entry popup
    And I check that Sex is not visible in New Travel Entry popup
    And I check that disease in New Travel Entry popup is disabled
    And I click on Save button from the new travel entry form
    Then I click on the Cases button from navbar
    When I open the last created Case via API
    Then I navigate to Epidemiological Data tab on Edit Case Page
    Then I check if added travel Entry appeared in Epi Data tab
    And I navigate to the last created via api Person page via URL
    Then I click on new entry button on Edit Person Page for DE
    When I fill the required fields in a new travel entry form without disease and person data
    Then I check that First Name is not visible in New Travel Entry popup
    And I check that Last Name is not visible in New Travel Entry popup
    And I check that Sex is not visible in New Travel Entry popup
    And I check that disease in New Travel Entry popup is enabled
    And I click on Save button from the new travel entry form
    Then I navigate to the last created via api Person page via URL
    And I check if added travel Entry appeared on Edit Person Page

  @issue=SORDEV-5623 @env_de
  Scenario: Show date and responsible user of last follow-up status change
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with only the required data for DE version
    Then I check that follow-up status is set to Under follow-up in German on Edit case page
    When I click on the Cancel Follow-up button from Edit case page
    Then I provide follow-up status comment from Edit case page
    And I click on save button from Edit Case page
    And I check that Date of Follow-up Status Change and Responsible User are correctly displayed on Edit case page
    When I click on the Resume Follow-up button from Edit case page
    And I click on save button from Edit Case page
    And I check that Date of Follow-up Status Change and Responsible User are correctly displayed on Edit case page
    And I click on the Lost to Follow-up button from Edit case page
    Then I provide follow-up status comment from Edit case page
    And I click on save button from Edit Case page
    And I check that Date of Follow-up Status Change and Responsible User are correctly displayed on Edit case page
    And I check that Expected Follow-up Until Date is correctly displayed on Edit case page
    When I select Overwrite Follow-up Until Date checkbox on Edit case page
    And I set the Follow-up Until Date to exceed the Expected Follow-up Until Date on Edit case page
    And I click on save button from Edit Case page
    Then I check if the Follow-up Until Date is correctly displayed on Edit case page

  @issue=SORDEV-5563 @env_de
  Scenario: Add contact person details to facilities case person
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    And I click on New Entry button in Facilities tab in Configuration
    Then I set name, region and district in Facilities tab in Configuration
    And I set Facility Category to "Medizinische Einrichtung" and Facility Type to "Krankenhaus" in Facilities tab in Configuration
    And I set Facility Contact person first and last name with email address and phone number
    Then I click on Save Button in new Facility form
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data using created facility
    Then I click on save case button
    Then I navigate to case person tab
    Then I click yes on the DISCARD UNSAVED CHANGES popup if it appears
    Then I set Facility Category to "Medizinische Einrichtung" and  Facility Type to "Krankenhaus"
    And I set Region to "Voreingestellte Bundesländer" and District to "Voreingestellter Landkreis"
    And I set facility name to created facility
    And I check if data for created facility is automatically imported to the correct fields in Case Person tab
    Then I click SAVE button on Edit Contact Page
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    Then I search last created facility
    Then I click on edit button for the last searched facility
    And I archive facility

  @env_main @#8556
  Scenario: Add two positive Pathogen Test Result of different diseases to a Sample of a Case
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    Then I navigate to the last created case via the url
    Then I click on New Sample
    Then I create a new Sample with positive test result with Guinea Worm as disease
    Then I confirm the Create case from contact with positive test result
    Then I navigate to the last created case via the url
    Then I click on edit Sample
    Then I click on new test result for pathogen tests
    Then I create a new pathogen test result with Dengue Fever as disease
    Then I confirm the Create case from contact with positive test result
    Then I navigate to the last created case via the url
    Then I validate only one sample is created with two pathogen tests
    Then I click on edit Sample
    Then I validate the existence of two pathogen tests


  @env_main @#8565
  Scenario: Check an archived case if its read only
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open last edited case by API via URL navigation
    Then I click on the Archive case button and confirm popup
    Then I click on logout button from navbar
    Then I log in with National User
    Then I open last edited case by API via URL navigation
    Then I check if editable fields are read only for an archived case

  @env_main @issue=SORDEV-7453
  Scenario: Check cases order after case edit
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    When I click on save button in the case popup
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    When I click on save button in the case popup
    And I click on the Cases button from navbar
    Then I get two last cases ID from cases list
    And I open 2 case in order from list
    And I fill general comment in case edit page with EDITED
    When I click on save button in the case popup
    And I click on the Cases button from navbar
    Then I compare previous first case ID on the list with actually second case ID on list

  @issue=SORDEV-6614 @env_de
  Scenario: Provide a search alternative aside from the duplicate recognizing
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill a new case form with same person details for DE version
    And I click on Save button in Case form
    Then I click on the Cases button from navbar
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill a new case form with same person details for DE version
    And I click on Save button in Case form
    And I click to select another person from Pick or create person popup for DE
    And I check if name and name prefilled in Pick or create person are equal to one used in case creation
    And I check that External Id is visible in Pick or Create Person popup for De
    And I check that Reset is visible in Pick or Create Person popup for De
    And I check that Search is visible in Pick or Create Person popup for De
    And I click on Reset filters in Pick or create Person popup
    And I click on Search in Pick or create Person popup
    And I check that error message is equal to "Mindestens ein Namensfeld oder ein anderes Feld sollte ausgefüllt werden" in Pick or Create person in popup
    And I fill first and last name with last created peron data in Pick or Create person in popup
    And I click on Search in Pick or create Person popup
    And I click on first result in Pick or create Person popup

  @issue=SORDEV-6609 @env_main
  Scenario: Test for case internal token
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data
    When I fill in the Internal Token field in Edit Case page with SAMPLE TOKEN
    And I click on save button in the case popup
    And I click on the Cases button from navbar
    And I check that the Internal Token column is present
    And I filter for SAMPLE TOKEN in Cases Directory
    Then I check that at least one SAMPLE TOKEN is displayed in table

  @issue=SORDEV-11422 @env_main
    Scenario: Add reason for deletion to confirmation dialogue
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data
    When I copy url of current case
    Then I click on Delete button from case
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Deletion request by another authority" is available
    And I check if reason for deletion as "Entity created without legal reason" is available
    And I check if reason for deletion as "Responsibility transferred to another authority" is available
    And I check if reason for deletion as "Deletion of duplicate entries" is available
    And I check if reason for deletion as "Other reason" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please choose a reason for deletion" appears next to Reason for deletion
    When I set Reason for deletion as "Other reason"
    Then I check if "Reason for deletion details" field is available in Confirm deletion popup in Edit Case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please add a reason for deletion" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from case
    And I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted case by url
    Then I check if reason of deletion is set to "Deletion request by affected person according to GDPR"
    And I check if EPID number input is disabled in Edit Case
    And I check if General comment test area is disabled in Edit Case

  @issue=SORDEV-11422 @env_de
  Scenario: Add reason for deletion to confirmation dialogue in DE version
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data for DE version
    When I copy url of current case
    Then I click on Delete button from case
    And I check if reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO" is available
    And I check if reason for deletion as "Löschen auf Anforderung einer anderen Behörde" is available
    And I check if reason for deletion as "Entität ohne Rechtsgrund angelegt" is available
    And I check if reason for deletion as "Abgabe des Vorgangs wegen Nicht-Zuständigkeit" is available
    And I check if reason for deletion as "Löschen von Duplikaten" is available
    And I check if reason for deletion as "Anderer Grund" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte wählen Sie einen Grund fürs Löschen" appears next to Reason for deletion
    When I set Reason for deletion as "Anderer Grund"
    Then I check if "DETAILS ZUM GRUND DES LÖSCHENS" field is available in Confirm deletion popup in Edit Case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte geben Sie einen Grund fürs Löschen an" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted case by url
    Then I check if reason of deletion is set to "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I check if General comment test area is disabled in Edit Case