@UI @Sanity @Login
Feature: Login with different type of users

  @env_main @LoginMain
  Scenario Outline: Login with <user> user on Main Environment
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in with name <user>

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
    Then I am logged in with name <user>

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
  @issue=SORQA-69 @env_de
  Scenario: Check German language setting
    Given I log in with National User
    Then I check that German word for Configuration is present in the left main menu