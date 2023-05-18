@UI @Sanity @Login @precon
Feature: Login with different type of users

  @env_main @LoginMain
  Scenario Outline: Login with <user> user on Main Environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in English language
    Then I log in as a <user>
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in English language
    Then I click on the User Settings button from navbar
    Then I check that English language is selected in User Settings
    And I click on logout button

    Examples:
      | user                          |
      | National User                 |
      | Contact Supervisor            |
      | Surveillance Officer          |
      | Surveillance Supervisor       |
      | Laboratory Officer            |
      | Point of Entry Supervisor     |
      | Admin User                    |
      | Rest AUTOMATION               |
      | Admin Surveillance Supervisor |
      | Contact Officer               |
      | Community Officer             |
      | Hospital Informant            |
      | Clinician                     |

  @env_de @LoginDe
  Scenario Outline: Login with <user> user on German Environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in German language
    Then I log in as a <user>
    Then I am logged in
    Then I click on the User Settings button from navbar
    And I select "Deutsch" language from Combobox in User settings
    And I check that Surveillance Dashboard header is correctly displayed in German language
    Then I click on the User Settings button from navbar
    And I check that Deutsch language is selected in User Settings
    And I click on logout button

    Examples:
      | user                          |
      | National User                 |
      | Contact Supervisor            |
      | Surveillance Officer          |
      | Surveillance Supervisor       |
      | Laboratory Officer            |
      | Point of Entry Supervisor     |
      | Admin User                    |
      | Rest AUTOMATION               |
      | Admin Surveillance Supervisor |
      | Contact Officer               |
      | Community Officer             |
      | Hospital Informant            |
      | Clinician                     |

  @env_keycloak @LoginKeycloak
  Scenario Outline: Login with <user> user on Keycloak Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in German language
    And I click on logout button

    Examples:
      | user                      |
      | Admin User                |
 #     | National User             |

  @env_keycloak @LoginKeycloak
  Scenario: Login on Keycloak Administrator Console
    Given I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    Then I am logged in Keycloak Administrator Console page
    And I click on logout button on Keycloak Administrator Console Page

  @tmsLink=SORQA-772 @env_main
  Scenario: Automatize Login with National Language User on international environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in English language
    Then I log in as a National Language User
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in German language
    Then I click on the User Settings button from navbar
    Then I check that Deutsch language is selected in User Settings
    And I click on logout button

  @tmsLink=SORQA-772 @env_de
  Scenario: Automatize Login with National Language User on german environment
    Given I navigate to SORMAS login page
    And I check that Login page is correctly displayed in German language
    Then I log in as a National Language User
    Then I am logged in
    And I check that Surveillance Dashboard header is correctly displayed in English language
    Then I click on the User Settings button from navbar
    Then I check that English language is selected in User Settings
    And I click on logout button

  @env_sltest @StandardLogin
  Scenario Outline: Test <url> for default <user> credentials
    Given I navigate to address <url>
    When I log into current website as a <user>
    Then I check if I got logged into <url> as <user> - if so, I send an alert

    Examples:
      | url                                             | user                        |
      | https://test-pipeline.sormas.netzlink.com/      | Administrator               |
      | https://test-pipeline.sormas.netzlink.com/      | Surveillance Supervisor     |
      | https://test-pipeline.sormas.netzlink.com/      | Case Supervisor             |
      | https://test-pipeline.sormas.netzlink.com/      | Contact Supervisor          |      
      | https://test-pipeline.sormas.netzlink.com/      | Point of Entry Supervisor   |
      | https://test-pipeline.sormas.netzlink.com/      | Laboratory Officer          |
      | https://test-pipeline.sormas.netzlink.com/      | Event Officer               |   
      | https://test-pipeline.sormas.netzlink.com/      | National User               |
      | https://test-pipeline.sormas.netzlink.com/      | National Clinician          |
      | https://verify-sormas-two.sormas.netzlink.com/  | Administrator               |
      | https://verify-sormas-two.sormas.netzlink.com/  | Surveillance Supervisor     |
      | https://verify-sormas-two.sormas.netzlink.com/  | Case Supervisor             |
      | https://verify-sormas-two.sormas.netzlink.com/  | Contact Supervisor          |      
      | https://verify-sormas-two.sormas.netzlink.com/  | Point of Entry Supervisor   |
      | https://verify-sormas-two.sormas.netzlink.com/  | Laboratory Officer          |
      | https://verify-sormas-two.sormas.netzlink.com/  | Event Officer               |   
      | https://verify-sormas-two.sormas.netzlink.com/  | National User               |
      | https://verify-sormas-two.sormas.netzlink.com/  | National Clinician          |

