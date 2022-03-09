@UI @Sanity @Persons
Feature: Edit Persons

  @env_main @ignore
  Scenario: Edit existent person
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    Then I fill a new contact form
    And I click on SAVE new contact button
    And I open Contact Person tab
    Then I complete all default empty fields from Contact Person tab
    When I click on new entry button from Contact Information section
    Then I complete all fields from Person Contact Details popup and save
    Then I click on save button from Contact Person tab
    Then I navigate to the last created Person page via URL
    Then I check that previous created person is correctly displayed in Edit Person page
    And While on Person edit page, I will edit all fields with new values
    And I edit all Person primary contact details and save
    Then I click on save button from Edit Person page
    And I check that previous edited person is correctly displayed in Edit Person page

  @issue=SORDEV-8466 @env_main
  Scenario: Check Filters on Person page work as expected
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in with National User
    When I click on the Persons button from navbar
    Then I fill Year of birth filter in Persons with the year of the last created person via API
    And I fill Month of birth filter in Persons with the month of the last created person via API
    And I fill Day of birth filter in Persons with the day of birth of the last created person via API
    Then I fill UUID of the last created person via API
    And I select present condition field with condition of the last created person via API
    And I choose random value of Region in Persons for the last created person by API
    And I choose random value of District in Persons for the last created person by API
    And I choose random value of Community in Persons for the last created person by API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    And I fill Year of birth filter in Persons with wrong value for last created Person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    Then I fill Year of birth filter in Persons with the year of the last created person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    And I fill Month of birth filter in Persons with wrong value for last created Person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    And I fill Month of birth filter in Persons with the month of the last created person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    And I fill Day of birth filter in Persons with wrong value for last created Person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    And I fill Day of birth filter in Persons with the day of birth of the last created person via API
    Then I apply on the APPLY FILTERS button
    And  I search after last created person from API by factor "full name" in Person directory
    And I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    Then I change "full name" information data field for Person
    And I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    Then I fill UUID of the last created person via API
    And I change present condition filter to other than condition of last created via API Person
    And I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    And I select present condition field with condition of the last created person via API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    And I change REGION filter to "Berlin" for Person
    And I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    And I choose random value of Region in Persons for the last created person by API
    And I choose random value of District in Persons for the last created person by API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    Then I change Community filter to "Community2" for Person
    And I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 0
    And I choose random value of Community in Persons for the last created person by API
    Then I apply on the APPLY FILTERS button
    And I check that number of displayed Person results is 1
    And I apply on the APPLY FILTERS button
    And I click on the RESET FILTERS button for Person

  @issue=SORDEV-8468 @env_main @ignore @check
  Scenario: Edit existent person and provoke errors in the Edit Person page
    Given I log in with National User
    When I click on the Persons button from navbar
    And I filter for persons who are alive
    And I apply on the APPLY FILTERS button
    And I click on first person in person directory
    And I clear the mandatory Person fields
    And I click on save button from Edit Person page
    Then I check that an invalid data error message appears
    When I fill in the home address, facility category and type in the Home Address section of the Edit Person Page
    And I clear Region and District fields from Person
    Then I check that an error highlight appears above the facility combobox
    When I click on new entry button from Contact Information section
    And I enter an incorrect phone number in Person Contact Details popup
    And I click the Done button in Person Contact Details popup
    Then I check that an invalid data error message appears
    When I enter an incorrect email in Person Contact Details popup
    And I click the Done button in Person Contact Details popup
    Then I check that an invalid data error message appears

  @issue=SORDEV-8469 @env_main
  Scenario: Test for navigating through Case, Contact and Immunization cards on Edit Person Page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create 1 new immunizations for last created person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Contacts button from navbar
    Then I navigate to the last created via api Person page via URL
    And I click on See Cases for this Person button from Edit Person page
    And I check that number of displayed cases results is 1
    Then I navigate to the last created via api Person page via URL
    And I click on See CONTACTS for this Person button from Edit Person page
    And I check that number of displayed contact results is 1
    Then I navigate to the last created via api Person page via URL
    And I click on Edit Case button from Cases card on Edit Person page
    Then I navigate to the last created via api Person page via URL
    And I click on Edit Contact button from Contacts card on Edit Person page
    Then I navigate to the last created via api Person page via URL
    And I click on Edit Immunization button for Immunization created through API from Immunization card on Edit Person page
    Then I navigate to the last created via api Person page via URL

