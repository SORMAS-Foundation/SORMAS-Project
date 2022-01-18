@UI @Sanity @TaskManagementFilter
Feature: Tasks functionalities

  @issue=SORDEV-5688
  Scenario Outline: Check the filter of tasks context
    Given I log in with National User
    And I click on the Tasks button from navbar
    And I filter Task context by <filterType>
    And I am checking if filter works correctly for <filterType>
    And I reset filter

    Examples:
      | filterType     |
      | Case           |
      | Contact        |
      | Event          |
      | General        |

  @issue=SORDEV-5688
  Scenario Outline: Check the filter of tasks status
    Given I log in with National User
    And I click on the Tasks button from navbar
    Then I filter Task status <statusType>
    And I am checking if filter works for <statusType> status
    And I reset filter

    Examples:
      | statusType     |
      | pending        |
      | done           |
      | removed        |
      | not executable |
