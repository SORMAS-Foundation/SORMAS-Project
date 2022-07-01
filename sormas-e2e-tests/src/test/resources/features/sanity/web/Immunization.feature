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

  @env_main @#8565
  Scenario: Check an archived immunization if its read only
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create 1 new immunizations for last created person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I navigate to last created immunization by API via URL
    Then I click on archive button from immunization tab
    Then I click on logout button from navbar
    Then I log in with National User
    Then I navigate to last created immunization by API via URL
    Then I check if editable fields are read only for an archived immunization

  @issue=SORDEV-7041 @env_main
  Scenario:Test Immunizations III: Immunization list for person forms
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I click on Person tab from Immunization page
    Then I check the created data is correctly displayed on Edit immunization person page
    Then I click on the Persons button from navbar
    And I filter by Person full name from Immunization on Person Directory Page
    And I click on the APPLY FILTERS button
    And I click Immunization aggregation button on Person Directory Page
    Then I click on first person in person directory
    And I check if data of created immunization is in Immunization tab on Edit Person Page

  @issue=SORDEV-11454 @env_main
  Scenario: Add reason for deletion to confirmation dialogue
    Given I log in as a Admin User
    And I click on the Immunizations button from navbar
    And I click on the NEW IMMUNIZATION button
    When I create a new immunization with specific data
    Then I check the created data is correctly displayed on Edit immunization page
    And I copy url of current immunization case
    And I click SAVE button on Edit Immunization Page
    Then I click on Delete button from immunization case
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Deletion request by another authority" is available
    And I check if reason for deletion as "Entity created without legal reason" is available
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Responsibility transferred to another authority" is available
    And I check if reason for deletion as "Deletion of duplicate entries" is available
    And I check if reason for deletion as "Other reason" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please choose a reason for deletion" appears next to Reason for deletion
    When I set Reason for deletion as "Other reason"
    Then I check if "Reason for deletion details" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please add a reason for deletion" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted immunization case by url
    Then I check if reason of deletion is set to "Deletion request by affected person according to GDPR"
    And I check if External ID input on immunization edit page is disabled
    And I check if Additional details text area on immunization edit page is disabled