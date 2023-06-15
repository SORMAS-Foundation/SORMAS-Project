@UI @Sanity @About
Feature: About Directory end to end tests

  @tmsLink=SORQA-219 @env_main @precon
  Scenario: Check current Sormas version is show
    Given I log in as a National User
    And I click on the About button from navbar
    Then I check that current Sormas version is shown on About directory page

  @env_main @#8399
  Scenario: Check all main important redirects in About section
    Given I log in as a National User
    Then I click on the About button from navbar
    Then I click on Sormas version in About directory and i get redirected to github
    Then I click on What's new in About directory and i get redirected to Sormas what's new page
    Then I click on Official SORMAS Website in About directory and i get redirected to the offical Sormas website
    Then I click on SORMAS Github in About directory and i get redirected to github page of sormas
    Then I click on Full Changelog in About directory and i get redirected to github project release page of sormas
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Data Dictionary file size is bigger than 0 bytes
    Then I delete Data Dictionary downloaded file from About Directory

  @env_de @#8399
  Scenario: Check important redirects in About section for DE
    Given I log in as a National User
    Then I click on the About button from navbar
    Then I click on Sormas version in About directory and i get redirected to github
    And I click on Deutsch Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Deutsch Data Dictionary file size is bigger than 0 bytes
    Then I delete Deutsch Data Dictionary downloaded file from About Directory
    Then I click on Case Classification Rules hyperlink and download HTML file in About directory
    Then I delete Case Classification Html downloaded file from About Directory

  @env_de @#9768
  Scenario: Check Data Protection Dictionary existence and download
    Given  I log in as a Admin User
    And I click on the About button from navbar
    And I click on Deutsch Data Protection Dictionary hyperlink and download XLSX file from About directory
    Then I check that Deutsch Data Protection Dictionary file size is bigger than 0 bytes
    And I validate data from downloaded XLSX Deutsch Data Protection Dictionary file
    And I delete Deutsch Data Protection Dictionary downloaded file from About Directory

  @env_main @#9768
  Scenario: Check Data Dictionary existence and download
    Given  I log in as a National User
    And I click on the About button from navbar
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Data Dictionary file size is bigger than 0 bytes
    And I validate data from downloaded XLSX Data Dictionary file
    And I delete Data Dictionary downloaded file from About Directory

  @tmsLink=SORDEV-10361 @env_main
  Scenario: Test Hide "buried" within Person present condition for Covid-19 for About
    Given I log in as a Admin User
    And I click on the About button from navbar
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Data Dictionary file size is bigger than 0 bytes
    Then I check if Data Dictionary in "Person.burialDate" record has no "COVID-19" as a disease
    Then I check if Data Dictionary in "Person.burialPlaceDescription" record has no "COVID-19" as a disease
    Then I check if Data Dictionary in "Person.burialConductor" record has no "COVID-19" as a disease
    And I delete Data Dictionary downloaded file from About Directory

  @env_main @tmsLink=SORDEV-10238
    Scenario: Check if sheets names are contained in entries in Excel file
    Given  I log in as a National User
    And I click on the About button from navbar
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Data Dictionary file size is bigger than 0 bytes
    And I check if Data Dictionary contains entries name in English
    And I delete Data Dictionary downloaded file from About Directory

  @env_main @tmsLink=SORDEV-10238 @LanguageRisk @ExcludedFromRelease @ignore
  Scenario: Check if data dictionary is in German when service language is set to German
    Given  I log in as a National Language User
    And I click on the About button from navbar
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on Data Dictionary hyperlink and download XLSX file from About directory
    Then I check that Data Dictionary file size is bigger than 0 bytes
    And I check if Data Dictionary contains sheets names in German
    And I delete Data Dictionary downloaded file from About Directory
    Then I click on the User Settings button from navbar
    And I select "English" language from Combobox in User settings