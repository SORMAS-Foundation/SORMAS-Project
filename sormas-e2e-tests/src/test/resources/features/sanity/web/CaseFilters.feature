@UI @Sanity @Case @Filters
Feature: Case filter functionality

  Scenario: Check Cases on Sample page work as expected
    Given API: I create 10 new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Cases button from navbar
    When I search for cases created with the API using Person's name
    Then I apply Outcome of case filter "No Outcome Yet"
    And I check that all displayed cases have "Not yet classified" in grid Case Classification column
    When I search for cases created with the API using Person's name
    Then I apply Disease filter "COVID-19"
    And I check that all displayed cases have "COVID-19" in grid Disease column

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
    And I apply Region filter "Voreingestellte Bundesländer"
    And I apply District filter "Voreingestellter Landkreis"
    And I apply Community "Voreingestellte Gemeinde"
    And I apply Surveillance Officer filter "Surveillance OFFICER - Surveillance Officer"
    And I apply Reporting User filter "Rest AUTOMATION - National User, ReST User"
    And I apply Year filter of last created person
    And I apply Month filter of last created person
    And I apply Day filter of last created person
    And I click APPLY BUTTON in Case Directory Page
    And I check that number of displayed cases results is 1
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



    And I apply Region filter "Voreingestellte Bundesländer"
    And I apply District filter "Voreingestellter Landkreis"
    And I apply Community "Voreingestellte Gemeinde"
    And I apply Surveillance Officer filter "Surveillance OFFICER - Surveillance Officer"
    And I apply Reporting User filter "Rest AUTOMATION - National User, ReST User"
    And I apply Year filter of last created person
    And I apply Month filter of last created person
    And I apply Day filter of last created person
