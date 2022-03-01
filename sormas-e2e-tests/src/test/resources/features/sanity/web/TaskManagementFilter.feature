@UI @Sanity @TaskManagementFilter
Feature: Tasks filtering functionalities

  @issue=SORDEV-5688 @env_main @ignore
  Scenario Outline: Check the filter of tasks context
    Given I log in with National User
    And I click on the Tasks button from navbar
    Then I filter Task context by <taskContext>
    And I collect the task column objects
    And I check displayed task's context is <taskContext>
    And I reset filter from Tasks Directory

    Examples:
      | taskContext |
      | Case        |
      | Contact     |
      | Event       |
      | General     |

  @issue=SORDEV-5688 @env_main @ignore
  Scenario Outline: Check the filter of tasks status
    Given I log in with National User
    And I click on the Tasks button from navbar
    Then I filter Task status <statusType>
    And I collect the task column objects
    And I check displayed task's status is <statusType>
    And I reset filter from Tasks Directory

    Examples:
      | statusType     |
      | pending        |
      | done           |
      | removed        |
      | not executable |