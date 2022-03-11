@UI @Sanity @Sample
Feature: Sample filter functionality

  @env_main
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

  @issue=SORDEV-5981 @env_main @check
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
    Then I fill a Full name of person from API
    And I select Test result filter among the filter options from API
    And I select Specimen condition filter among the filter options from API
    And I select Case classification filter among the filter options from API
    And I set Disease filter to disease value of last created via API Case in Sample Directory
    And I select Region filter among the filter options from API
    And I select District filter among the filter options from API
    And I select Laboratory filter among the filter options from API
    And I click a apply button on Sample
    And I check that number of displayed sample results is 1
    Then I select random Test result filter among the filter options
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I select Test result filter among the filter options from API
    Then I select "Not adequate" Specimen condition option among the filter options
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I select Specimen condition filter among the filter options from API
    Then I select random Case classification filter among the filter options
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I select Case classification filter among the filter options from API
    Then I select random Disease filter among the filter options in Sample directory
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I set Disease filter to disease value of last created via API Case in Sample Directory
    Then I change Region filter to "Berlin" option in Sample directory
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I select Region filter among the filter options from API
    Then I change Region filter to "Region1" option in Sample directory
    And I change District filter to "District11" option in Sample directory
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I select Region filter among the filter options from API
    And I select District filter among the filter options from API
    Then I change Laboratory filter to "Other facility" option in Sample directory
    And I click a apply button on Sample
    And I check that number of displayed sample results is 0
    And I click a Reset button on Sample
    Then I select "Not shipped" filter from quick filter
    And I select "Shipped" filter from quick filter
    And I select "Received" filter from quick filter
    And I select "Referred to other lab" filter from quick filter
    And I click a Reset button on Sample