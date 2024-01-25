@UI @Sanity @Sample
Feature: Sample filter functionality

  @env_main
  Scenario: Check Filters on Sample page work as expected
    Given API: I create 10 new cases with a new sample foreach of them
    And API: I check that POST call status code is 200
    And I log in as a National User
    And I click on the Sample button from navbar
    When I search for samples created with the API
    Then I check the displayed test results filter dropdown
    When I search for samples created with the API
    Then I check the displayed specimen condition filter dropdown
    When I search for samples created with the API
    Then I validate that number of displayed samples is correct for applied Voreingestelltes Labor filter

  @tmsLink=SORDEV-5981 @env_main
  Scenario: Check all filters are work properly in Samples directory
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I click on the Sample button from navbar
    Then I fill full name of last created via API Person into Sample Directory
    And I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory
    And I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory
    And I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory
    And I set Disease filter to disease value of last created via API Case in Sample Directory
    And I select Region filter value with the region value of the last created via API Case in Sample Directory
    And I select District filter value with the district value of the last created via API Case in Sample Directory
    And I select Laboratory filter value with the uuid value of the last created via API Sample in Sample Directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 1
    Then I select a Test result value different than the test result of the last created via API Sample Pathogen test result
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory
    Then I select "Not adequate" Specimen condition option among the filter options
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory
    Then I select a Case classification value different than the case classification value of last created via API Case in Sample Directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory
    Then I select Disease filter value different than the disease value of the last created via API case in Sample Directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I set Disease filter to disease value of last created via API Case in Sample Directory
    Then I change Region filter to "Berlin" option in Sample directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Region filter value with the region value of the last created via API Case in Sample Directory
    Then I change Region filter to "Region1" option in Sample directory
    And I change District filter to "District11" option in Sample directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Region filter value with the region value of the last created via API Case in Sample Directory
    And I select District filter value with the district value of the last created via API Case in Sample Directory
    Then I change Laboratory filter to "Other facility" option in Sample directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I click on reset filters button from Sample Directory
    Then I select "Not shipped" filter from quick filter
    And I select "Shipped" filter from quick filter
    And I select "Received" filter from quick filter
    And I select "Referred to other lab" filter from quick filter
    And I click on reset filters button from Sample Directory

  @tmsLink=SORDEV-5982 @env_de
  Scenario: Check all filters are work properly in Samples directory for DE version
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new case
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Sample button from navbar
    Then I fill full name of last created via API Person into Sample Directory
    And I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory for DE version
    And I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory for De version
    And I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory for DE version
    And I set Disease filter to disease value of last created via API Case in Sample Directory
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 1
    Then I select a Test result value different than the test result of the last created via API Sample Pathogen test result for DE version
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Test result filter value with the value for pathogen test result of last created via API Sample in Sample Directory for DE version
    Then I select "Nicht ausreichend" Specimen condition option among the filter options
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Specimen condition filter value with value for specimen condition of the last created via API Sample in Sample Directory for De version
    Then I select a Case classification value different than the case classification value of last created via API Case in Sample Directory for DE version
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 0
    And I select Case classification filter value with value for case classification of the last created via API Case in Sample Directory for DE version
    And I click on apply filters button from Sample Directory
    And I check that number of displayed sample results is 1
    And I click on reset filters button from Sample Directory
    Then I select "Nicht versendet" filter from quick filter for DE version
    And I select "Versandt" filter from quick filter for DE version
    And I select "Erhalten" filter from quick filter for DE version
    And I select "An ein anderes Labor weitergeleitet" filter from quick filter for DE version
    And I click on reset filters button from Sample Directory

  @tmsLink=HSP-6554 @env_main
  Scenario: [Samples] Filter for 'Person samples' with 'All active and archived samples' and 'Deleted samples' filters
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I click on New Sample
    And I create a new Sample with alternate purpose
    And I save the created sample
    And I click on New Sample
    And I create a new Sample with alternate purpose
    And I save the created sample
    Then I click on the Archive case button and confirm popup
    And I navigate to case person tab
    Then I collect person UUID from Edit Case Person page
    And I click on NEW CASE button from Edit Person page
    When I create a new case for the same person
    And I click on New Sample
    And I create a new Sample with alternate purpose
    And I save the created sample
    And I click on New Sample
    And I create a new Sample with alternate purpose
    And I save the created sample
    When I click on edit Sample
    Then I delete the sample
    When I click on the Persons button from navbar
    And I open the last new created person by UI in person directory
    Then I click on See Samples for this Person button from Edit Person page
    And I select "Active samples" Relevance Status option among the filter options
    And I check that number of displayed sample results is 1
    And I select "Archived samples" Relevance Status option among the filter options
    And I check that number of displayed sample results is 2
    And I select "Deleted samples" Relevance Status option among the filter options
    And I check that number of displayed sample results is 1