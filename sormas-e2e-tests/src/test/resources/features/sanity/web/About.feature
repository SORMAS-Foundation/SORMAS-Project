@UI @Sanity @About
Feature: About end to end tests

  @issue=SORDEV-6474 @env_main
  Scenario: Check language options in Data Dictionary depending on the user language setting
    Given  I log in as a National Language User
    And I click on the About button from navbar
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on Data Dictionary hyperlink and download XLSX file in About directory
    And I read data from downloaded XLSX file
    And I detect language for XLSX file content
    And I delete exported file from About Directory
    Then I click on the User Settings button from navbar
    And I set on default language as English in User settings