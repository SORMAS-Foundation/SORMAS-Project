@UI @Sanity @Task
Feature: Tasks functionalities

  @tmsLink=SORDEV-5476mk @env_main
  Scenario: Create and check a new task data
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
    And I open last created task from Tasks Directory
    Then I check the created task is correctly displayed on Edit task page

  @tmsLink=SORDEV-5476 @env_main
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
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    Given I log in as a Surveillance Officer
    And I click on the Tasks button from navbar
    And I search last created task by API using Contact UUID
    And I collect the task column objects
    Then I am checking if all the fields are correctly displayed in the Task Management table

  @tmsLink=SORDEV-6080 @env_main
  Scenario: Bulk deleting tasks in Task Directory
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    And API: I create a new task
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
    And I check if popup message for deleting is "All selected eligible tasks have been deleted"
    And I check that number of displayed tasks results is 0

  @tmsLink=SORDEV-6080 @env_main
  Scenario: Bulk archiving tasks in Task Directory
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    And API: I create a new task
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
    And I check if popup message for archiving is "All selected entries have been archived"
    And I check that number of displayed tasks results is 0

  @tmsLink=SORDEV-6080 @env_main
  Scenario: Bulk editing tasks in Task Directory
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    And API: I create a new task
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
    And I check if popup message from Edit Task Form after bulk edit is "All entries have been edited"
    And I check that number of displayed tasks results is 0

  @tmsLink=SORDEV-9156 @env_main
  Scenario: Check the task observer is added
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Tasks button from navbar
    Then I open last created task by API using Contact UUID
    And I select "Surveillance SUPERVISOR" user from Observed by combobox on Edit Task page
    Then I open last created task by API using Contact UUID
    And I check that respected user is selected on Edit Task page

  @env_main @tmsLink=SORDEV-9474
  Scenario: Test Modify the field allowing to designate the observers of a task
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I fill a new task form with specific data
    And I select "National CLINICIAN" user from Observed by combobox in new Task form
    And I select "Surveillance SUPERVISOR" user from Observed by combobox in new Task form
    And I select "Contact OFFICER" user from Observed by combobox in new Task form
    And I delete "Contact OFFICER" user from Observed by in new Task form
    And I click on Save button in New Task form
    When I open last created task from Tasks Directory
    Then I check that National CLINICIAN is visible in Observed By on Edit Task Page
    And I check that Surveillance SUPERVISOR is visible in Observed By on Edit Task Page
    And I check that Contact OFFICER is not visible in Observed By on Edit Task Page

  @tmsLink=SORDEV-7423 @env_main
  Scenario: Test detailed task export
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
#    And I click on the Tasks button from navbar
 #   And I click on SHOW MORE FILTERS BUTTON on Task directory page
    And I filter out last created task from Tasks Directory
    And I click Export button in Task Directory
    When I click on the Detailed Task Export button
    When I check if downloaded data generated by detailed task export option is correct

  @tmsLink=SORDEV-7423 @env_main
  Scenario: Test custom task export
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
 #   And I click on the Tasks button from navbar
 #  And I click on SHOW MORE FILTERS BUTTON on Task directory page
    And I filter out last created task from Tasks Directory
    And I click Export button in Task Directory
    When I click on the Custom Event Export button
    When I click on the New Export Configuration button in Custom Task Export popup
    Then I fill Configuration Name field in Custom Task Export popup with generated name
    And I add "Task context" data to export in existing Export Configuration for Custom Task Export
    And I save Export Configuration for Custom Task Export
    When I download last created custom task export file
    When I check if downloaded data generated by new custom task export option is correct

  @tmsLink=SORDEV-7423 @env_main
  Scenario: Test custom task export edit
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
  #  And I click on the Tasks button from navbar
    And I filter out last created task from Tasks Directory
    And I click Export button in Task Directory
    When I click on the Custom Event Export button
    When I click on the New Export Configuration button in Custom Task Export popup
    Then I fill Configuration Name field in Custom Task Export popup with generated name
    And I add "Task context" data to export in existing Export Configuration for Custom Task Export
    And I save Export Configuration for Custom Task Export
    And I open last created Custom Export Configuration in Custom Export page
    And I add "Task type" data to export in existing Export Configuration for Custom Task Export
    And I save Export Configuration for Custom Task Export
    When I download last created custom task export file
    When I check if downloaded data generated by edited custom task export option is correct

  @tmsLink=SORDEV-7423 @env_main
  Scenario: Test custom task export delete
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click Export button in Task Directory
    When I click on the Custom Event Export button
    When I click on the New Export Configuration button in Custom Task Export popup
    Then I fill Configuration Name field in Custom Task Export popup with generated name
    And I add "Task context" data to export in existing Export Configuration for Custom Task Export
    And I save Export Configuration for Custom Task Export
    And I delete last created custom task export config

  @#8561 @env_main
  Scenario: Verify that Task assignee cannot be left empty via bulk edit mode
    Given I log in as a Admin User
    And I click on the Tasks button from navbar
    And I click on Enter Bulk Edit Mode from Tasks Directory
    And I select first 1 results in grid in Task Directory
    And I click on Bulk Actions combobox in Task Directory
    And I click on Edit button from Bulk Actions Combobox in Task Directory
    And I click the Change assignee Checkbox in the Edit Task Form
    And I click on Save button in New Task form
    And I check if popup message from Edit Task Form after bulk edit is "Please check the input data"

  @tmsLink=SORDEV-12438 @env_main
  Scenario: Test add task status Progress in task edit page
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Given API: I create a new contact
    And API: I check that POST call status code is 200
    And API: I create a new task
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Tasks button from navbar
    Then I open last created task by API using Contact UUID
    And I check that Pending button exist on task edit page

  @tmsLink=SORDEV-12438 @env_main
  Scenario: Test add task status Progress in new task page
    Given I log in as a National User
    And I click on the Tasks button from navbar
    Then I click on the NEW TASK button
    And I check that Pending button exist on task edit page

  @tmsLink=SORDEV-9428 @env_main
  Scenario: Test Allow users on national level or with no jurisdiction level to edit all tasks
    Given I log in as a Surveillance Officer
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data
    And I collect uuid of the case
    And I click on New Task from Case page
    And I create a new task with "Nat USER" as a assigned user
    And I click on logout button from navbar
    And I log in as a National User
    And I click on the Cases button from navbar
    And I check that I get navigated to the Case directory page
    And I filter with last created case using case UUID
    And I click on the first Case ID from Case Directory
    And I click on edit task icon of the 2 displayed task on Edit Case page
    Then I change all Task's fields and save
    And I click on the Cases button from navbar
    And I filter with last created case using case UUID
    When I open last created case
    And I check that I get navigated to the Edit Case page
    And I click on first edit Task
    Then I check the created task is correctly displayed on Edit task page

  @tmsLink=SORDEV-13927 @env_main
  Scenario: Test Manual archiving of tasks [2]
    Given I log in as a National User
    And I click on the Tasks button from navbar
    And I click on the NEW TASK button
    When I create a new task with specific data
    And I open last created task from Tasks Directory
    Then I check the created task is correctly displayed on Edit task page
    Then I click on the Archive task button
    And I click on No option in popup window
    Then I click on the Archive task button
    And I click on yes in archive task popup window
    And I apply "Archived tasks" to combobox on Task Directory Page
    And I open last created task from Tasks Directory without click on show more filters
    Then I check the created task is correctly displayed on Edit task page
    Then I click on De-Archive task button
    And I click on yes in de-archive task popup window
    And I apply "Active tasks" to combobox on Task Directory Page
    And I open last created task from Tasks Directory without click on show more filters
    Then I check the created task is correctly displayed on Edit task page