@UI @Sanity @Task
Feature: Tasks functionalities

  @env_main
  Scenario: Create and check a new task data
    Given I log in with National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
    And I open last created task from Tasks Directory
    Then I check the created task is correctly displayed on Edit task page

  @issue=SORDEV-5476 @env_main
  Scenario: Check the edit of task from Case
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Task from Case page
    And I create a new task with specific data
    And I click on the Tasks button from navbar
    And I search last created task by Case UUID and open it
    Then I change all Task's fields and save
    And I click on the Cases button from navbar
    And Search for Case using Case UUID from the created Task
    When I open last created case
    When I click on first edit Task
    Then I check the created task is correctly displayed on Edit task page

  @env_main
  Scenario: Check all fields from the created Task in the Task Management table
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Surveillance Officer
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID
    And I collect the task column objects
    Then I am checking if all the fields are correctly displayed in the Task Management table

  @issue=SORDEV-6080 @env_main
  Scenario: Bulk deleting tasks in Task Directory
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID
    And I check that number of displayed tasks results is 3
    And I click on Enter Bulk Edit Mode from Tasks Directory
    And I select first 3 results in grid in Task Directory
    And I click on Bulk Actions combobox in Task Directory
    And I click on Delete button from Bulk Actions Combobox in Task Directory
    And I click yes on the CONFIRM REMOVAL popup from Task Directory page
    And I check if popup message is "All selected tasks have been deleted"
    And I check that number of displayed tasks results is 0

  @issue=SORDEV-6080 @env_main
  Scenario: Bulk archiving tasks in Task Directory
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID
    And I check that number of displayed tasks results is 3
    And I click on Enter Bulk Edit Mode from Tasks Directory
    And I select first 3 results in grid in Task Directory
    And I click on Bulk Actions combobox in Task Directory
    And I click on Archive button from Bulk Actions Combobox in Task Directory
    And I click yes on the CONFIRM REMOVAL popup from Task Directory page
    And I check if popup message is "All selected tasks have been archived"
    And I check that number of displayed tasks results is 0

  @issue=SORDEV-6080 @env_main
  Scenario: Bulk editing tasks in Task Directory
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new task
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID
    And I check that number of displayed tasks results is 3
    And I click on Enter Bulk Edit Mode from Tasks Directory
    And I select first 3 results in grid in Task Directory
    And I click on Bulk Actions combobox in Task Directory
    And I click on Edit button from Bulk Actions Combobox in Task Directory
    And I click to bulk change assignee for selected tasks
    And I click to bulk change priority for selected tasks
    And I click to bulk change status for selected tasks
    And I click on Save button in New Task form
    And I check if popup message after bulk edit is "All tasks have been edited"
    And I check that number of displayed tasks results is 0

  @issue=SORDEV-9428 @env_main
  Scenario: Test Allow users on national level or with no jurisdiction level to edit all tasks
    Given I log in as a Surveillance Supervisor
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Task from Case page
    And I create a new task with specific data for users excluding the National User
    And I log out from page
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I search last created task by Case UUID and open it
    Then I change all Task's fields and save
    And I click on the Cases button from navbar
    And Search for Case using Case UUID from the created Task
    When I open last created case
    When I click on first edit Task
    Then I check the created task is correctly displayed on Edit task page
