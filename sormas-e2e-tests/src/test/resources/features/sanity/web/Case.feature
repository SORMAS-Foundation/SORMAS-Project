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
    And I click on save button in case edit

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
