@UI @Sanity @Persons @add_userroles
Feature: Edit Persons for different user roles

  @tmsLink=SORDEV-8468 @env_main
  Scenario Outline: Edit existent person and provoke errors in the Edit Person page
    Given I log in as a <user>
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

    Examples:
      | user                      |
      | Admin User                |
      | Contact Officer           |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Community Officer         |
      | Hospital Informant        |
      | Clinician                 |

  @tmsLink=SORDEV-8467 @env_main
  Scenario Outline: Test column structure in Person directory
    Given I log in as a <user>
    And I click on the Persons button from navbar
    Then I check that the Person table structure is correct

    Examples:
      | user                      |
      | Admin User                |
      | Contact Officer           |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Community Officer         |
      | Hospital Informant        |
      | Clinician                 |