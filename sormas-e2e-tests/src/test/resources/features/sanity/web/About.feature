@UI @Sanity @About
Feature: About end to end tests

  @issue=SORDEV-6474 @env_main
  Scenario: Check language options in Data Dictionary
    Given I log in with National User
    And I click on the About button from navbar
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on Data Dictionary hyperlink and download XLSX file in About directory
    And I read data from downloaded XLSX file
    And I detect language for XLSX file content
    And I delete exported file from About Directory
    Then I click on the User Settings button from navbar
#    And I select "English" language from Combobox in User settings

    And I set on default language in User settings