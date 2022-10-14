@UI @Sanity @Login @precon
Feature: Login with different type of users

  @env_main @LoginMain
  Scenario Outline: Login with <user> user on Main Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in
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

  @env_de @LoginDe
  Scenario Outline: Login with <user> user on German Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
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

  @env_keycloak @LoginKeycloak
  Scenario Outline: Login with <user> user on Keycloak Environment
    Given I navigate to SORMAS login page
    Then I log in as <user> in Keycloak enabled environment
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in German language
    And I click on logout button on Keycloak enabled environment

    Examples:
      | user                      |
      | Admin User                |
      | National User             |

  @env_keycloak @LoginKeycloak
  Scenario: Login on Keycloak Administrator Console
    Given I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    Then I am logged in
    And I click on logout button on Keycloak Administrator Console Page