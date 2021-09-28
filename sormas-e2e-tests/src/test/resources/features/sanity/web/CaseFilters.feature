@UI @Sanity @Case @Filters
Feature: Case filter functionality

  Scenario: Check Cases on Sample page work as expected
    Given API: I create several new cases
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with the user
    And I click on the Cases button from navbar
    When I search for cases created with the API
    Then I check the displayed Case Outcome filter dropdown
    When I search for cases created with the API
    Then I check the displayed Disease filter dropdown
    When I search for cases created with the API
    Then I check the displayed Case Classification filter dropdown