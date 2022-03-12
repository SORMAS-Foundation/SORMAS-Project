@UI @Sanity @Login
Feature: Login with different type of users

  @env_main
  Scenario Outline: Login with <user> user
    Given I navigate to SORMAS login page
    Then I log in as a <user>
    Then I am logged in with name <user>

    Examples:
      | user                      |
      | National User             |
      | Contact Supervisor        |
      | Laboratory Officer        |
      | Point of Entry Supervisor |
      | Surveillance Officer      |

  @issue=SORQA-69 @env_de
  Scenario: Check German language setting
    Given I log in with National User
    Then I check that German word for Configuration is present in the left main menu