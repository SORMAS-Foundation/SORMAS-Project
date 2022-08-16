@UI @Sanity @Login @precon @LanguageChecks
Feature: Application language checks

  @tmsLink=SORDEV-12126 @env_main
  Scenario: Test language Urdu-Pk
    Given I log in as a Admin User
    When I click on the User Settings button from navbar
    And I select "Urdu" language from Combobox in User settings
    Then I check that Surveillance Dashboard header is correctly displayed in Urdu language
    Then I click on the User Settings button from navbar
    And I select "انگریزی" language from Combobox in User settings

  @tmsLink=SORQA-69 @env_de
  Scenario: Check Settings directory is written in German for german market
    Given I log in as a National User
    When I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    Then I check that German word for Configuration is present in the left main menu

