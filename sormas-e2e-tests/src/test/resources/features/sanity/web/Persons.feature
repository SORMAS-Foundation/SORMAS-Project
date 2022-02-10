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

@issue=SORDEV-8468
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
    Then I check that an empty district highlight appears above the facility combobox
    When I click on new entry button from Contact Information section
    And I enter an incorrect phone number in Person Contact Details popup
    And I click the Done button in Person Contact Details popup
    Then I check that an invalid data error message appears
    When I enter an incorrect email in Person Contact Details popup
    And I click the Done button in Person Contact Details popup
    Then I check that an invalid data error message appears
