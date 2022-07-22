@UI @Sanity @About
Feature: About Directory end to end tests

  #fails due to known defect, downloaded file is not in german, is in english.
  @issue=SORDEV-6474 @env_main
  Scenario: Check language options in Data Dictionary depending on the user language setting
    Given  I log in as a National Language User
    And I click on the About button from navbar
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on Deutsch Data Dictionary hyperlink and download XLSX file from About directory
    And I validate data from downloaded XLSX Deutsch Data Dictionary file
    And I detect and check language that was defined in User Settings for XLSX file content
    Then I delete Deutsch Data Dictionary downloaded file from About Directory
    Then I click on the User Settings button from navbar
    And I set on default language as English in User settings

  @issue=SORQA-219 @env_main @precon
  Scenario: Check current Sormas version is show
    Given I log in with National User
    And I click on the About button from navbar
    Then I check that current Sormas version is shown on About directory page

  @env_main @#8399
  Scenario: Check all main important redirects in About section
    Given I log in with National User
    Then I click on the About button from navbar
    Then I click on Sormas version in About directory and i get redirected to github
    Then I click on What's new in About directory and i get redirected to Sormas what's new page
    Then I click on Official SORMAS Website in About directory and i get redirected to the offical Sormas website
    Then I click on SORMAS Github in About directory and i get redirected to github page of sormas
    Then I click on Full Changelog in About directory and i get redirected to github project release page of sormas
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I delete Data Dictionary downloaded file from About Directory
    Then I click on Case Classification Rules hyperlink and download HTML file in About directory
    Then I delete Case Classification Html downloaded file from About Directory

  @env_main @#9768
  Scenario: Check Data Protection Dictionary existence and download
    Given  I log in as a Admin User
    And I click on the About button from navbar
    And I click on Data Protection Dictionary hyperlink and download XLSX file from About directory
    And I validate data from downloaded XLSX Data Protection Dictionary file
    And I delete Data Protection Dictionary downloaded file from About Directory

  @env_main @#9768
  Scenario: Check Data Dictionary existence and download
    Given  I log in as a National User
    And I click on the About button from navbar
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    And I validate data from downloaded XLSX Data Dictionary file
    And I delete Data Dictionary downloaded file from About Directory

  @issue=SORDEV-10361 @env_main @testIt
  Scenario: Test Hide "buried" within Person present condition for Covid-19 for About
    Given I log in as a Admin User
    And I click on the About button from navbar
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check if Data Dictionary in "Person.burialDate" record has no "COVID-19" as a disease
    Then I check if Data Dictionary in "Person.burialPlaceDescription" record has no "COVID-19" as a disease
    Then I check if Data Dictionary in "Person.burialConductor" record has no "COVID-19" as a disease
    And I delete Data Dictionary downloaded file from About Directory