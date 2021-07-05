@UI @Sanity @Task
Feature: Tasks functionalities

  Scenario: Check the edit of task from Case
    Given I log in as a Surveillance Officer
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Task
    And I create a new task with specific data
    And I click on the Tasks button from navbar
    And I search last created task by Case UUID
    When I open last created task
    When I change all fields and save
    And I click on the Cases button from navbar
    And Search for Case using Case UUID from the created Task
    When I open last created case
    When I click on first edit Task
    Then I check the created task is correctly displayed on Edit task page

  Scenario: Check all fields from the created Task in the Task Management table
    Given API: I create a new person
    Given API: I create a new contact
    And API: I create a new task
    Given I log in as a Surveillance Officer
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID and wait for 3 results to be displayed
    And I collect the task column objects
    Then I am checking if all the fields are correctly displayed in the Task Management table