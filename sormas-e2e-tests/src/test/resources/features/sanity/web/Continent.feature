@UI @Sanity @Configuration
Feature: Continent end to end tests

  @tmsLink=SORQA-343 @env_main @precon
  Scenario: Check infrastructure data for continents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I check that number of continents is 6
    And I check that Africa is correctly displayed

  @tmsLink=SORQA-344 @env_de @precon
  Scenario: Check German infrastructure data for continents
    Given I log in as a Admin User
    And I click on the Configuration button from navbar
    When I navigate to continents tab in Configuration
    Then I check that number of continents is 6
    And I check that Africa is correctly displayed in German

  @tmsLink=SORQA-7139 @env_main
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


  @tmsLink=SORQA-7139 @env_de
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
