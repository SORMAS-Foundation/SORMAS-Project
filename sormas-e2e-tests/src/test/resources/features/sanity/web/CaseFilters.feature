@UI @Sanity @Case @Filters
Feature: Case filter functionality

#  Scenario: Check Cases on Sample page work as expected
#    Given API: I create 10 new cases
#    Then API: I check that POST call body is "OK"
#    And API: I check that POST call status code is 200
#    And I log in with National User
#    And I click on the Cases button from navbar
#    When I search for cases created with the API using Person's name
#    Then I apply Outcome of case filter "No Outcome Yet"
#    And I check that all displayed cases have "Not yet classified" in grid Case Classification column
#    When I search for cases created with the API using Person's name
#    Then I apply Disease filter "COVID-19"
#    And I check that all displayed cases have "COVID-19" in grid Disease column

  Scenario: Check Cases Origin filter
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I apply Person Id filter
    And I apply Case origin "In-Country"
    And I filter by CaseID on Case directory page
    And I apply Disease filter "COVID-19"
    And I apply Outcome of case filter "No Outcome Yet"
    And I apply Case classification filter "Not yet classified"
    And I apply Follow-up filter "Under follow-up"
    And I click SHOW MORE FILTERS button
    And I apply Present Condition filter
    And I click APPLY BUTTON in Case Directory Page
    And I apply Region filter "Voreingestellte Bundesländer"
    And I apply District filter "Voreingestellter Landkreis"
    And I apply Community "Voreingestellte Gemeinde"
    And I apply Surveillance Officer filter "Surveillance OFFICER - Surveillance Officer"
    And I apply Facility category filter "Medical facility"
    And I apply Facility type filter to "Hospital"
    And I apply Facility filter to "Standard Einrichtung"
    And I apply Reporting User filter "Rest AUTOMATION"
    And I apply Year filter of last created person
    And I apply Month filter of last created person
    And I apply Day filter of last created person
    And I apply Vaccination Status filter to "Vaccinated"
    And I apply Quarantine filter to "Home"
    And I apply Reinfection filter to "Confirmed reinfection"
    And I apply Date type filter to "Case report date"
    And I fill Cases from input to day before mocked case created
    And I fill Cases to input to day after mocked case created
    And I click APPLY BUTTON in Case Directory Page
  #  And I check that number of displayed cases results is 1
    Then I apply Case origin "Point of Entry"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Case origin "In-Country"
    And I apply Disease filter "Cholera"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Disease filter "COVID-19"
    And I apply Outcome of case filter "Deceased"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Outcome of case filter "No Outcome Yet"
    And I apply Case classification filter "Suspect case"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Case classification filter "Not yet classified"
    And I apply Follow-up filter "Completed follow-up"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Follow-up filter "Under follow-up"
    And I apply Region filter "Bayern"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Region filter "Voreingestellte Bundesländer"
    And I apply District filter "Voreingestellter Landkreis"
    And I apply Community "Voreingestellte Gemeinde"
    And I apply Facility category filter "Accommodation"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility category filter "Medical facility"
    And I apply Facility type filter to "Hospital"
    And I apply Facility filter to "Standard Einrichtung"
    And I apply Facility type filter to "Rehab facility"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility type filter to "Hospital"
    And I apply Facility filter to "Other facility"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Facility filter to "Standard Einrichtung"
    And I apply Surveillance Officer filter "Bas BEN - Surveillance Officer, Contact Officer"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Surveillance Officer filter "Surveillance OFFICER - Surveillance Officer"
    And I apply Reporting User filter "Surveillance OFFICER"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Reporting User filter "Rest AUTOMATION"
    And I apply Quarantine filter to "Institutional"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Quarantine filter to "Home"
    And I apply Vaccination Status filter to "Unvaccinated"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Vaccination Status filter to "Vaccinated"
    And I apply Reinfection filter to "Probable reinfection"
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Reinfection filter to "Confirmed reinfection"
    And I apply Day filter different than person has
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Day filter of last created person
    And I apply Month filter different than person has
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Month filter of last created person
    And I apply Year filter different than person has
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Year filter of last created person
    And I fill Cases from input to days after before mocked case created
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I fill Cases from input to day before mocked case created
    And I apply Random Person Id filter
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I apply Person Id filter
    And I filter by Random CaseID on Case directory page
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I filter by CaseID on Case directory page
    And I click "Only cases without geo coordinates" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases without geo coordinates" checkbox
    And I click "Only cases without responsible officer" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases without responsible officer" checkbox
    And I click "Only cases with extended quarantine" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with extended quarantine" checkbox
    And I click "Only cases with reduced quarantine" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with reduced quarantine" checkbox
    And I click "Help needed in quarantine" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Help needed in quarantine" checkbox
    And I click "Only cases with events" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with events" checkbox
    And I click "Only cases from other instances" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases from other instances" checkbox
    And I click "Only cases with reinfection" checkbox
    And I click APPLY BUTTON in Case Directory Page
   # And I check that number of displayed cases results is 1
    And I click "Only cases with reinfection" checkbox
    And I click "Only cases with fulfilled reference definition" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only cases with fulfilled reference definition" checkbox
    And I click "Only port health cases without a facility" checkbox
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 0
    And I click "Only port health cases without a facility" checkbox
    And I click All button in Case Directory Page
      # And I check that number of displayed cases results is 1
    And I click on Investigation pending button on Case Directory Page
    #And I check that number of displayed cases results is 1
    And I click on Investigation done button on Case Directory Page
    And I check that number of displayed cases results is 0
    And I click on Investigation discarded button on Case Directory Page
    And I check that number of displayed cases results is 0
    And I click All button in Case Directory Page
    And I apply "Archived cases" to combobox on Case Directory Page
    And I check that number of displayed cases results is 0




