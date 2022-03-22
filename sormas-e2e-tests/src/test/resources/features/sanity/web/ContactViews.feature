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