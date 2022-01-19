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
    Then I apply Case origin "Point of Entry"
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 0
    When I filter by CaseID on Case directory page
    Then I apply Case origin "In-Country"
    And I check that number of displayed cases results is 1