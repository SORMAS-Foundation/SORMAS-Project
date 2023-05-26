@UI @Sanity @Facility
Feature: Facility end to end tests

  @tmsLink=SORDEV-5503 @env_main
  Scenario: Import facility
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    And I select to export first record in facilities tab
    Then I export selected facility to csv from facilities tab
    And I read exported csv from facilities tab
    Then I click on the import button in facilities
    And I click on overwrite existing entries with imported data checkbox
    Then I pick the facilities test data file
    And I click on the "START DATA IMPORT" button from the Import Facilities Entries popup
    Then I check if csv file for facilities is imported successfully
    And I close import facilities popup window
    And I close facilities popup window
    Then I check if data from csv is correctly displayed in facilities tab
    And I delete downloaded csv file for facilities in facility tab

  @tmsLink=SORDEV-9206 @env_main
  Scenario: Checking availability of new categories and types of facility in Edit Case and Edit Case Person directories
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I fill new case form with specific data
    And I set Place of stay to "FACILITY", Facility Category to "Accommodation" and  Facility Type to "Hostel, dormitory" in Case creation
    Then I click on save case button
    And I check the created data is correctly displayed on Edit case person page
    And I set Facility Category to "Accommodation" and  Facility Type to "Hostel, dormitory"
    And I click on save button to Save Person data in Case Person Tab
    And I set Facility Category to "Educational facility" and  Facility Type to "Kindergarten/After school care"
    And I click on save button to Save Person data in Case Person Tab

  @tmsLink=SORDEV-9206 @env_main
  Scenario: Checking availability of new categories and types of facility in Edit Event directory
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    Then I open the last created event via api
    And I set Place of stay to "Facility", Facility Category to "Accommodation" and  Facility Type to "Hostel, dormitory" in Edit Event directory
    And I click on Save Button in Edit Event directory
    And I set Place of stay to "Facility", Facility Category to "Educational facility" and  Facility Type to "Kindergarten/After school care" in Edit Event directory
    And I click on Save Button in Edit Event directory

  @tmsLink=SORDEV-9206 @env_main
  Scenario: Creating new facilities with new data in Configuration directory
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I click on Facilities button in Configuration tab
    And I click on New Entry button in Facilities tab in Configuration
    Then I set name, region and district in Facilities tab in Configuration
    And I set Facility Category to "Accommodation" and Facility Type to "Hostel, dormitory" in Facilities tab in Configuration
    And I click on Save Button in new Facility form
    And I click on New Entry button in Facilities tab in Configuration
    Then I set name, region and district in Facilities tab in Configuration
    And I set Facility Category to "Educational facility" and Facility Type to "Kindergarten/After school care" in Facilities tab in Configuration
    And I click on Save Button in new Facility form

  @env_main @#7468
  Scenario: Validate Facilities Configuration section
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I click on Facilities button in Configuration tab
    Then I Verify the page elements are present in Facilities Configuration Page

  @tmsLink=SORQA-707 @env_main @precon
  Scenario: Check if Standard Einrichtung facility is available in the system
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    Then I click on Facilities button in Configuration tab
    And I filter facility by "Standard Einrichtung"
    Then I check that number of displayed Facilities results is 1

  @tmsLink=SORQA-707 @env_main @precon
  Scenario: Check if Voreingestelltes Labor facility is available in the system
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    Then I click on Facilities button in Configuration tab
    And I filter facility by "Voreingestelltes Labor"
    Then I check that number of displayed Facilities results is 1

  @tmsLink=SORQA-707 @env_s2s_1 @precon
  Scenario: Check if General Hospital facility is available in the system
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    Then I click on Facilities button in Configuration tab
    And I filter facility by "General Hospital"
    Then I check that number of displayed Facilities results is 1

  @tmsLink=SORQA-707 @env_main @precon
  Scenario: Check if Community111 facility is available in the system
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    Then I click on Facilities button in Configuration tab
    And I filter facility by "Community111"
    Then I check that number of displayed Facilities results is 1