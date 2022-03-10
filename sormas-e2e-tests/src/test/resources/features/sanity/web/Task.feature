@UI @Sanity @Task
Feature: Tasks functionalities

  @env_main @check
  Scenario: Create and check a new task data
    Given I log in with National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
    And I open last created task from Tasks Directory
    Then I check the created task is correctly displayed on Edit task page

  @issue=SORDEV-5476 @env_main @check
  Scenario: Check the edit of task from Case
    Given I log in as a Surveillance Officer
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
    
  @env_main @check
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