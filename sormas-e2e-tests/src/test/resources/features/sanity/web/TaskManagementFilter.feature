@UI @Sanity @TaskManagementFilter
Feature: Tasks filtering functionalities

  @tmsLink=SORDEV-5688 @env_main
  Scenario Outline: Check the filter of tasks context
    Given I log in as a National User
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

  @tmsLink=SORDEV-5688 @env_main
  Scenario Outline: Check the filter of tasks status
    Given I log in as a National User
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

  @#8554 @env_main
  Scenario: Verify task displays District and Region in results grid from Case used to create the task
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Surveillance Officer
    Then I navigate to the last created case via the url
    And I click on New Task from Case page
    And I create a new task with "Nat USER" as a assigned user
    Then I click on the Tasks button from navbar
    And I search task by last Case created via API UUID
    And I collect the task column objects
    And I check displayed tasks District and Region are taken from API created Case