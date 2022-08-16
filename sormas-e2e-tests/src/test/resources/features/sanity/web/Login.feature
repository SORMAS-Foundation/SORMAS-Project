@UI @Sanity @Login @precon
Feature: Login with different type of users

  @env_main @LoginMain
  Scenario Outline: Login with <user> user on Main Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in with name <user>
    And I check that English word for User Settings is present in the left main menu
    Then I click on the User Settings button from navbar
    Then I check that English language is selected in User Settings
    And I click on logout button

    Examples:
      | user                      |
      | National User             |
      | National Language User    |
      | Contact Supervisor        |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Laboratory Officer        |
      | Point of Entry Supervisor |
      | Admin User                |
      | Rest AUTOMATION           |

  @env_de @LoginDe @precon
  Scenario Outline: Login with <user> user on German Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in with name <user>
    And I check that German word for User Settings is present in the left main menu
    Then I click on the User Settings button from navbar
    And I check that Deutsch language is selected in User Settings
    And I click on logout button

    Examples:
      | user                      |
      | National User             |
      | National Language User    |
      | Contact Supervisor        |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Laboratory Officer        |
      | Point of Entry Supervisor |
      | Admin User                |
      | Rest AUTOMATION           |

  @tmsLink=SORQA-69 @env_de
  Scenario: Check German language setting
    Given I log in as a National User
    Then I check that German word for Configuration is present in the left main menu

  @tmsLink=SORDEV-12126 @env_main
  Scenario: Test new language (Urdu-Pk)
    Given I log in as a Admin User
    When I click on the User Settings button from navbar
    And I select "Urdu" language from Combobox in User settings
    And I click on the User Settings button from navbar
    Then I check that Surveillance Dashboard header is correctly displayed in Urdu language
    And I select "انگریزی" language from Combobox in User settings
