@UI @Sanity @Immunization
Feature: Immunization end to end tests

  @issue=SORDEV-8705 @env_main
  Scenario:Check a new immunization data
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on Person tab from Immunization page
    Then I check the created data is correctly displayed on Edit immunization person page

  @issue=SORDEV-9312 @env_main
  Scenario: Reset the 'Overwrite immunization management status' by Discard button
    Given I log in as a Surveillance Officer
    And I click on the Immunizations button from navbar
    And I open first immunization from grid from Immunization tab
    Then I check Overwrite immunization management status option
    Then I click on discard button from immunization tab
    And I check if Overwrite immunization management status is unchecked by Management Status

  @issue=SORDEV-7038 @env_main
  Scenario:Test Immunizations III: Vaccination lists and forms
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I check that Vaccination ID is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccination date is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine name is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine manufacturer is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine type is visible in Vaccinations tab on Edit Immunization Page
    And I check that Vaccine dose is visible in Vaccinations tab on Edit Immunization Page
    And I click the header of column 2 of Vaccination table
    And I click the header of column 3 of Vaccination table
    And I click the header of column 4 of Vaccination table
    And I click the header of column 5 of Vaccination table
    And I click the header of column 6 of Vaccination table
    And I click the header of column 7 of Vaccination table
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    Then I check that number of added Vaccinations is 1
    Then I set Number of doses to 11 on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    Then I check if exceeded number of doses error popup message appeared
    And I set Number of doses to 3 on Edit Immunization Page
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Ongoing"
    And I check if Immunization status is set to "Pending"
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    And I click on New Entry button in Vaccination tab
    Then I fill new vaccination data in new Vaccination form
    And I click SAVE button in new Vaccination form
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Completed"
    And I check if Immunization status is set to "Acquired"
    And I click to edit 1 vaccination on Edit Immunization page
    Then I click Delete button in Vaccination form
    And I choose the reason of deletion in popup for Vaccination
    And I check that number of added Vaccinations is 2
    And I click SAVE button on Edit Immunization Page
    Then I check if Immunization management status is set to "Ongoing"
    And I check if Immunization status is set to "Pending"