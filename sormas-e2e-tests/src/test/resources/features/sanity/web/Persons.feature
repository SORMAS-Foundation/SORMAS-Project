@UI @Sanity @Persons
Feature: Edit Persons

  @env_main @tmsLink=SORQA-110
  Scenario: Edit existent person
    Given I log in as a National User
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
    And I check that new edited person is correctly displayed in Edit Person page

  @tmsLink=SORDEV-8466 @env_main
  Scenario: Check Filters on Person page work as expected
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call status code is 200
    When I log in as a National User
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

  @tmsLink=SORDEV-8468 @env_main
  Scenario: Edit existent person and provoke errors in the Edit Person page
    Given I log in as a National User
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

  @tmsLink=SORDEV-8469 @env_main
  Scenario: Test for navigating through Case, Contact and Immunization cards on Edit Person Page
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    Given API: I create a new contact linked to the previous created case
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given API: I create 1 new immunizations for last created person
    And API: I check that POST call status code is 200
    Given I log in as a National User
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

  @tmsLink=SORDEV-8467 @env_main
  Scenario: Test column structure in Person directory
    Given I log in as a National User
    And I click on the Persons button from navbar
    Then I check that the Person table structure is correct

  @tmsLink=SORDEV-5630 @env_de
  Scenario: Test a general comment field in person entity
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new case
    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I navigate to the last created via api Person page via URL
    And I check General comment field is enabled on Edit Person page

  @tmsLink=SORDEV-7424 @env_main
  Scenario: Test event participant person sex required
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    And I navigate to the last created through API Event page via URL
    And I click on the Event participant tab
    And I add only first and last name data and check is sex combobox required for event participant creation
    When I check if error display correctly expecting sex error

  @tmsLink=SORDEV-10227 @env_de
  Scenario: Test Permanent deletion for Person for Travel Entry, Event Participant, Case and Contact combined
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form without personal data
    And I fill person data in a new travel entry form with built person shared for all entities
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I collect travel UUID from travel entry
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only person data for event participant creation for DE with built person shared for all entities
    And I collect the event participant person UUID displayed on Edit Event Participant page
    Then I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case form with chosen data without personal data on Case directory page for DE
    And I click on the person search button in new case form
    And I search for the person data shared across all entities by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    Then I click on Save button in Case form
    And I collect uuid of the case
    Then I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    Then I fill a new contact form for DE version without person data
    And I click on the person search button in create new contact form
    And I search for the person data shared across all entities by First Name and Last Name in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on SAVE new contact button
    And I collect contact UUID displayed on Edit Contact Page
    And I click on the Persons button from navbar
    Then I filter by shared person data across all entities
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Case aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Contact aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I navigate to the last created UI travel entry via the url
    Then I click on Delete button from travel entry
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter by shared person data across all entities
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Case aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Contact aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on the first result in table from event participant
    Then I click on Delete button from event participant
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter by shared person data across all entities
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Case aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Contact aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on the Cases button from navbar
    And I filter with first Case ID
    And I open last created case
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter by shared person data across all entities
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Case aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Contact aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on the Contacts button from navbar
    Then I click on first created contact in Contact directory page by UUID
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter by shared person data across all entities
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Case aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Contact aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0
    And I click on Travel Entry aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0

  @#7751 @env_main
  Scenario: Verify map functionality in the Edit Person Page
    Given I log in as a National User
    When I click on the Persons button from navbar
    And I filter for persons who are alive
    And I apply on the APPLY FILTERS button
    And I click on first person in person directory
    And I clear the GPS Latitude and Longitude Fields from the Edit Person Page
    Then I Verify The Eye Icon opening the Map is disabled in the Edit Person Page
    And I Add the GPS Latitude and Longitude Values in the Edit Person Page
    Then I Verify The Eye Icon opening the Map is enabled in the Edit Person Page
    And I click on the The Eye Icon located in the Edit Person Page
    Then I verify that the Map Container is now Visible in the Edit Person Page

  @tmsLink=SORDEV-12441 @env_de
  Scenario: Hide citizenship and country of birth on Edit Person Page
    Given I log in as a National User
    When I click on the Persons button from navbar
    And I click on first person in person directory
    Then I check that Citizenship is not visible in Contact Information section for DE version
    And I check that Country of birth is not visible in Contact Information section for DE version

  @tmsLink=HSP-6460 @env_de
  Scenario: Person view has info cards of entities enabled in the system
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data with person name and "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    Then I click on save button in the case popup
    Then I collect uuid of the case
    And I click on New Sample in German
    And I create a new Sample with only required fields for DE version
    And I click on save sample button
    And I click NEW VACCINATION button for DE
    And I change the report vaccination date for minus 17 day from today
    And I fill new vaccination data in new Vaccination form for DE
    And I remove the vaccination date in displayed vaccination form
    And I click SAVE button in new Vaccination form
    And I navigate to case person tab
    Then I collect person UUID from Edit Case Person page
    Then I click on New Contact button in Case Person Tab for DE
    And I click on the NEW CONTACT button
    And I click CHOOSE CASE button
    Then I search for the last created case uuid by UI in the CHOOSE SOURCE Contact window for DE
    And I open the first found result in the CHOOSE SOURCE Contact window for De
    And I click on SAVE new contact button in the CHOOSE SOURCE popup of Create Contact window
    Then I click on the Persons button from navbar
    And I open the last new created person by UI in person directory
    Then I check that number of displayed cases with "pencil" icon is 1 for sample on Side Card
    And I check that SEE CASES FOR THIS PERSON button appears on Edit Person page for DE
    Then  I check that number of displayed contacts with "pencil" icon is 1 for sample on Side Card
    And I check that SEE CONTACTS FOR THIS PERSON button appears on Edit Person page for DE
    Then I check that number of displayed samples with "pencil" icon is 1 for sample on Side Card
    And I check if Sample card has available "see sample for this person" button on Edit Case Page for DE
    Then I check that number of displayed vaccinations with "pencil" icon is 1 for sample on Side Card
    And I click on the Events button from navbar
    And I click on GROUPS Radiobutton on Event Directory Page

