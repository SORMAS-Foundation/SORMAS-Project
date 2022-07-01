@UI @Sanity @CaseView
Feature: Contact view tests

  @issue=SORDEV-8407 @env_main
  Scenario: Person ID check for Contact Directory
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I check that Person ID column is between Contact Status and First Name of Contact Person columns
    When I click on the first Person ID from Contacts Directory
    Then I check that I get navigated to the Edit Person page
    When I click on the Contacts button from navbar
    And I click on the first Contact ID from Contacts Directory
    Then I check that I get navigated to the Edit Contact page

  @issue=SORDEV-6101 @env_main
  Scenario Outline: Add a filter for the relationship with case at contact directory
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I click on the NEW CONTACT button
    And I fill only mandatory fields with and set relationship with case to <option>
    And I click on SAVE new contact button
    Then I collect the contact person UUID displayed on Edit contact page
    Then I click on the contacts list button
    And I click SHOW MORE FILTERS button on Contact directory page
    Then I set Relationship with case on <option>
    And I filter by last collected from UI specific Contact uuid
    Then I click on the first Contact ID from Contacts Directory
    And I check if collected contact UUID is the same in opened contact
    And I check if relationship with case is set to <option>

    Examples:
      | option                             |
      | Live in the same household         |
      | Other family member or friend      |
      | Work in the same environment       |
      | Provided medical care for the case |
      | Other                              |