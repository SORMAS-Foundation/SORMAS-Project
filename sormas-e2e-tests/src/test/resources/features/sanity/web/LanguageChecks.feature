@UI @Sanity @LanguageChecks
Feature: Application language checks

  @tmsLink=SORDEV-12126 @env_main @LanguageRisk @precon
  Scenario: Test language Urdu-Pk
    Given I log in as a National Language User
    When I click on the User Settings button from navbar
    And I select "Urdu" language from Combobox in User settings
    Then I check that Surveillance Dashboard header is correctly displayed in Urdu language
    Then I click on the User Settings button from navbar
    And I select "جرمن" language from Combobox in User settings

  @tmsLink=SORQA-69 @env_de @LanguageRisk @precon
  Scenario: Check Settings directory is written in German for german market
    Given I log in as a National User
    When I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    Then I check that German word for Configuration is present in the left main menu

      #fails due to known defect, downloaded file is not in german, is in english.
  @tmsLink=SORDEV-6474 @env_main @issue=8069 @About @LanguageRisk @ignore
  Scenario: Check language options in Data Dictionary depending on the user language setting
    Given  I log in as a National Language User
    And I click on the About button from navbar
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on Deutsch Data Dictionary hyperlink and download XLSX file from About directory
    And I validate data from downloaded XLSX Deutsch Data Dictionary file
    And I check if last downloaded XLSX from About Directory content is translated into German
    Then I delete Deutsch Data Dictionary downloaded file from About Directory
    Then I click on the User Settings button from navbar
    And I select "English" language from Combobox in User settings

  @tmsLink=SORQA-7139 @env_de @Configuration @LanguageRisk @precon
  Scenario: Check continent display language in German
    Given I log in as a National User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    And I check that continent list is correctly displayed in German
    Then I click on logout button from navbar
    Given I log in as a National Language User
    Then I click on the User Settings button from navbar
    And I select "English" language from Combobox in User settings
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    And I check that continent list is correctly displayed

  @tmsLink=SORQA-7139 @env_main @Configuration @LanguageRisk @precon
  Scenario: Check continent display language in English
    Given I log in as a National User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    And I check that continent list is correctly displayed
    Then I click on logout button from navbar
    Given I log in as a National Language User
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    And I check that continent list is correctly displayed in German

  @tmsLink=HSP-6583 @env_d2s @LanguageRisk @precon
  Scenario: Check Settings directory is written in German for german market [2]
    Given I log in as a National User
    When I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    Then I check that German word for Configuration is present in the left main menu