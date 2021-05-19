@Sanity @Task
Feature: Tasks functionalities

  Scenario: Check a new case data
    Given I log in with the user
  * I click on the Tasks button from navbar
  * I click on the NEW TASK button
  * I create a new task with specific data
     When I open last created task
     Then I check the created task is correctly displayed on Edit task page
  
  @newCreated
  Scenario: Check the edit of task from Case
    Given I log in with the user
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

