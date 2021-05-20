@Sanity @Task
Feature: Create Tasks

  Scenario: Create and check a new case data
	Given I log in with the user
	And I click on the Tasks button from navbar
	And I click on the NEW TASK button
	When I create a new task with specific data
	And I open last created task
	Then I check the created task is correctly displayed on Edit task page