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

  @issue=SORDEV-5981
  Scenario: Check all filters are work properly in Samples directory
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Sample button from navbar
    Then fill a Full name of person from API
    And I select Test result filter among the filter options from API
    And I select Specimen condition filter among the filter options from API
    And I select Case clasification filter among the filter options from API
    And I click a apply button in Sample
    And I check that number of displayed sample results is 1


    And I fill all fields for a new case created for event participant
