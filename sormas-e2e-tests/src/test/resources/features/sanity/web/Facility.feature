@UI @Sanity @Facility
Feature: Facility end to end tests

  @issue=SORDEV-5503 @env_main
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