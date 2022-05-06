@UI @Sanity @Case @Filters
Feature: Case filter functionality

  @env_main
  Scenario: Check Cases on Sample page work as expected
    Given API: I create 10 new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    When I search for cases created with the API using Person's name
    Then I apply Outcome of case filter "No Outcome Yet" on Case directory page
    And I check that all displayed cases have "Not yet classified" in grid Case Classification column
    When I search for cases created with the API using Person's name
    Then I apply Disease filter "COVID-19" on Case directory page
    And I check that all displayed cases have "COVID-19" in grid Disease column

  @issue=SORQA-30 @env_main
  Scenario: Check Person related fields filter in Case directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I apply uuid filter for last created via API Person in Case directory page
    And I filter by CaseID on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Present Condition filter on Case directory page to condition of last created person
    And I apply Year filter of last api created Person on Case directory page
    And I apply Month filter of last api created Person on Case directory page
    And I apply Day filter of last api created Person on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I apply mocked Person Id filter on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply uuid filter for last created via API Person in Case directory page
    And I apply Day filter different than Person has on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Day filter of last api created Person on Case directory page
    And I apply Month filter different than Person has on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Month filter of last api created Person on Case directory page
    And I apply Year filter different than Person has on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Year filter of last api created Person on Case directory page
    And I apply Present Condition filter on Case directory page to different than actual
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Present Condition filter on Case directory page to condition of last created person

  @issue=SORQA-30 @env_main
  Scenario: Check Case basic filters on Case directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I apply Case origin "In-Country" on Case directory page
    And I filter by CaseID on Case directory page
    And I apply Disease filter "COVID-19" on Case directory page
    And I apply Outcome of case filter "No Outcome Yet" on Case directory page
    And I apply Case classification filter "Not yet classified" on Case directory page
    And I apply Follow-up filter "Under follow-up" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    Then I apply Case origin "Point of Entry" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Case origin "In-Country" on Case directory page
    And I apply Disease filter "Cholera" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Disease filter "COVID-19" on Case directory page
    And I apply Outcome of case filter "Deceased" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Outcome of case filter "No Outcome Yet" on Case directory page
    And I apply Case classification filter "Suspect case" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Case classification filter "Not yet classified" on Case directory page
    And I apply Follow-up filter "Completed follow-up" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Follow-up filter "Under follow-up" on Case directory page
    And I filter by mocked CaseID on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I filter by CaseID on Case directory page

  @issue=SORQA-30 @env_main
  Scenario: Check Case region and facility related filters
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Region filter "Voreingestellte Bundesländer" on Case directory page
    And I apply District filter "Voreingestellter Landkreis" on Case directory page
    And I apply Community "Voreingestellte Gemeinde" on Case directory page
    And I apply Facility category filter "Medical facility" on Case directory page
    And I apply Facility type filter to "Hospital" on Case directory page
    And I apply Facility filter to "Standard Einrichtung" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I apply Region filter "Bayern" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Region filter "Voreingestellte Bundesländer" on Case directory page
    And I apply District filter "Voreingestellter Landkreis" on Case directory page
    And I apply Community "Voreingestellte Gemeinde" on Case directory page
    And I apply Facility category filter "Accommodation" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility category filter "Medical facility" on Case directory page
    And I apply Facility type filter to "Hospital" on Case directory page
    And I apply Facility filter to "Standard Einrichtung" on Case directory page
    And I apply Facility type filter to "Rehab facility" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility type filter to "Hospital" on Case directory page
    And I apply Facility filter to "Other facility" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility filter to "Standard Einrichtung" on Case directory page

  @issue=SORQA-30 @env_main
  Scenario: Check checkboxes filters on Case directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I click SHOW MORE FILTERS button on Case directory page
    And I click "Only cases without geo coordinates" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases without geo coordinates" checkbox on Case directory page
    And I click "Only cases without responsible officer" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases without responsible officer" checkbox on Case directory page
    And I click "Only cases with extended quarantine" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with extended quarantine" checkbox on Case directory page
    And I click "Only cases with reduced quarantine" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with reduced quarantine" checkbox on Case directory page
    And I click "Help needed in quarantine" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Help needed in quarantine" checkbox on Case directory page
    And I click "Only cases with events" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with events" checkbox on Case directory page
    And I click "Only cases from other instances" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases from other instances" checkbox on Case directory page
    And I click "Only cases with reinfection" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I click "Only cases with reinfection" checkbox on Case directory page
    And I click "Only cases with fulfilled reference definition" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with fulfilled reference definition" checkbox on Case directory page
    And I click "Only port health cases without a facility" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only port health cases without a facility" checkbox on Case directory page

  @issue=SORQA-30 @env_main
  Scenario: Check aggregation buttons on Case directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 1
    And I click All button in Case Directory Page
    And I check that number of displayed cases results is 1
    And I click on Investigation pending button on Case Directory Page
    And I check that number of displayed cases results is 1
    And I click on Investigation done button on Case Directory Page
    And I check that number of displayed cases results is 0
    And I click on Investigation discarded button on Case Directory Page
    And I check that number of displayed cases results is 0
    And I click All button in Case Directory Page
    And I apply "Archived cases" to combobox on Case Directory Page
    And I check that number of displayed cases results is 0

  @issue=SORQA-30 @env_main
  Scenario: Check Case report date filters on Case directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Case report date" on Case directory page
    And I fill Cases from input to 2 days before mocked Case created on Case directory page
    And I fill Cases to input to 5 days after mocked Case created on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I fill Cases from input to 3 days after before mocked Case created on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I fill Cases from input to 1 days before mocked Case created on Case directory page

  @issue=SORQA-30 @env_main
  Scenario: Check complex filters regarding responsibilities, vaccination, reinfection adn quarantine
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Region filter "Voreingestellte Bundesländer" on Case directory page
    And I apply Surveillance Officer filter "Surveillance OFFICER - Surveillance Officer" on Case directory page
    And I apply Reporting User filter "Rest AUTOMATION" on Case directory page
    And I apply Vaccination Status filter to "Vaccinated" on Case directory page
    And I apply Quarantine filter to "Home" on Case directory page
    And I apply Reinfection filter to "Confirmed reinfection" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I apply Reporting User filter "Surveillance OFFICER" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Reporting User filter "Rest AUTOMATION" on Case directory page
    And I apply Quarantine filter to "Institutional" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Quarantine filter to "Home" on Case directory page
    And I apply Vaccination Status filter to "Unvaccinated" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Vaccination Status filter to "Vaccinated" on Case directory page
    And I apply Reinfection filter to "Probable reinfection" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Reinfection filter to "Confirmed reinfection" on Case directory page

  @issue=SORQA-83 @env_de
  Scenario: German Case Directory filters
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    Then I back to Case Directory using case list button
    And I apply Person Id filter to one attached to last created UI Case on Case directory page
    And I filter by CaseID of last created UI Case on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Present Condition filter to "Lebendig" on Case directory page
    And I apply Year filter of Person attached to last created UI Case on Case directory page
    And I apply Month filter of Person attached to last created UI Case on Case directory page
    And I apply Day filter of Person attached to last created UI Case on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I apply mocked Person Id filter on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Person Id filter to one attached to last created UI Case on Case directory page
    And I apply Year filter other than Person attached has to last created UI Case on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Year filter of Person attached to last created UI Case on Case directory page
    And I apply Month filter other than Person attached has to last created UI Case on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Month filter of Person attached to last created UI Case on Case directory page
    And I apply Day filter other than Person attached has to last created UI Case on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Day filter of Person attached to last created UI Case on Case directory page
    And I apply Present Condition filter to "Verstorben" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0

  @issue=SORQA-83 @env_de
  Scenario: Check Case basic filters on Case directory page for DE version
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    Then I back to Case Directory using case list button
    And I filter by CaseID of last created UI Case on Case directory page
    And I apply Case origin "Im Land" on Case directory page
    And I apply Disease filter "COVID-19" on Case directory page
    And I apply Disease Variant filter "B.1.617.1" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I apply Case origin "Einreiseort" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Disease filter "COVID-19" on Case directory page
    And I apply Disease Variant filter "B.1.526.1" on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0

  @issue=SORQA-83 @env_de
  Scenario: Check checkboxes filters on Case directory page for DE specific
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    Then I back to Case Directory using case list button
    And I filter by CaseID of last created UI Case on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I click SHOW MORE FILTERS button on Case directory page
    And I click "Nur Fälle ohne Geo-Koordinaten" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I click "Nur Fälle ohne Geo-Koordinaten" checkbox on Case directory page
    And I click "Nur Fälle ohne verantwortlichen Beauftragten" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle ohne verantwortlichen Beauftragten" checkbox on Case directory page
    And I click "Nur Fälle mit verlängerter Isolation" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle mit verlängerter Isolation" checkbox on Case directory page
    And I click "Nur Fälle mit verkürzter Isolation" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle mit verkürzter Isolation" checkbox on Case directory page
    And I click "Maßnahmen zur Gewährleistung der Versorgung" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Maßnahmen zur Gewährleistung der Versorgung" checkbox on Case directory page
    And I click "Nur Fälle mit Ereignissen" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle mit Ereignissen" checkbox on Case directory page
    And I click "Nur Fälle von anderen Instanzen" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle von anderen Instanzen" checkbox on Case directory page
    And I click "Nur Fälle mit Reinfektion" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle mit Reinfektion" checkbox on Case directory page
    And I click "Nur Fälle mit erfüllter Referenzdefinition" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Fälle mit erfüllter Referenzdefinition" checkbox on Case directory page
    And I click "Nur Einreisefälle ohne zugewiesene Einrichtung" checkbox on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Nur Einreisefälle ohne zugewiesene Einrichtung" checkbox on Case directory page

  @issue=SORQA-83 @env_de
  Scenario: Check Case report date filters on Case directory page for De specific
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    Then I check the created data is correctly displayed on Edit case page for DE version
    Then I back to Case Directory using case list button
    And I filter by CaseID of last created UI Case on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I apply Date type filter to "Fallmeldedatum" on Case directory page
    And I fill Cases from input to 2 days before UI Case created on Case directory page
    And I fill Cases to input to 5 days after UI Case created on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
    And I fill Cases from input to 3 days after before UI Case created on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0

    
