@UI @Sanity @Sample
Feature: Sample filter functionality

  Scenario: Check Filters on Sample page work as expected
    Given API: I create 10 new cases with a new sample foreach of them
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Sample button from navbar
    When I search for samples created with the API
    Then I check the displayed test results filter dropdown
    When I search for samples created with the API
    Then I check the displayed specimen condition filter dropdown
    When I search for samples created with the API
    Then I check the displayed Laboratory filter dropdown
