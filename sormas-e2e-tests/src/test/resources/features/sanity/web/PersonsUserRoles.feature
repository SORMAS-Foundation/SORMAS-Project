@UI @Sanity @PersonsUserRoles @add_userroles
Feature: Edit Persons for different user roles

@tmsLink=SORDEV-8469 @env_main
Scenario Outline: Test for navigating through Case, Contact and Immunization cards on Edit Person Page
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
  Given I log in as a <user>
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

  Examples:
    | user                      |
    | Admin User                |
    | Contact Officer           |
    | Surveillance Officer      |
    | Surveillance Supervisor   |
    | Community Officer         |
    | Hospital Informant        |
    | Clinician                 |

  @tmsLink=SORDEV-8466 @env_main
  Scenario Outline: Check Filters on Person page work as expected
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a <user>
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

    Examples:
      | user                      |
      | Admin User                |
      | Contact Officer           |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Community Officer         |
      | Clinician                 |

  @tmsLink=SORDEV-8466 @env_main
  Scenario: Check Filters on Person page work as expected for Hospital Informant
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Hospital Informant
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