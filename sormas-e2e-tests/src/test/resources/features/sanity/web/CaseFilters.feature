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