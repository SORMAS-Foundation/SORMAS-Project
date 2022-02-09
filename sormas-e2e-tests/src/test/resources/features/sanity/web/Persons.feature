@UI @Sanity @Persons
Feature: Edit Persons
  Scenario: Edit existent person
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    Then I fill a new contact form
    And I click SAVE a new contact
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

  Scenario: Form card navigation in Edit Person Directory
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
    And I click on Edit Immunization button from Immunization card on Edit Person page
    Then I navigate to the last created via api Person page via URL

