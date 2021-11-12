@UI @Sanity @Event @UI
Feature: Create events

  Scenario: Create a new event
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event

  Scenario: Create a new event and change its status multiple times
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with status CLUSTER
    And I click on the Events button from navbar
    And I search for specific event in event directory
    Then I check if it appears under Cluster filter in event directory
    When I change the event status to SCREENING
    And I click on the Events button from navbar
    And I search for specific event in event directory
    Then I check if it appears under Screening filter in event directory
    When I change the event status to EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    Then I check if it appears under Event filter in event directory
    When I change the event status to SIGNAL
    And I click on the Events button from navbar
    And I search for specific event in event directory
    Then I check if it appears under Signal filter in event directory
    When I change the event status to DROPPED
    And I click on the Events button from navbar
    And I search for specific event in event directory
    Then I check if it appears under Dropped filter in event directory

  Scenario: Create and check a new event data
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data is correctly displayed in event edit page

  Scenario: Add a participant to an event
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Given I add a participant to the event
    Then I check if participant appears in the event participants list
    And I navigate via URL to last Person created from edit Event page
    Then I check if event is available at person information

  Scenario: Create and edit a new event
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I change the fields of event and save
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the modified event data is correctly displayed

  Scenario: Add a New action from event and verify the fields.
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I click on New Action button from Event tab
    Then I create New Action from event tab
    And I click on Event Actions tab
    And I open the Action recently created from Event tab
    And I check that Action created from Event tab is correctly displayed in Event Actions tab

  Scenario: Add a New action from Event Actions tab and verify the fields.
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    Then I navigate to Event Action tab for created Event
    And I click on New Action from Event Actions tab
    And I create New Action from event tab
    Then I navigate to Event Action tab for created Event
    And I open the Action recently created from Event tab
    And I check that Action created from Event tab is correctly displayed in Event Actions tab

  Scenario: Add a New action for an Event and verify the Action in EventActions table
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    Then I navigate to Event Action tab for created Event
    And I click on New Action from Event Actions tab
    And I create New Action from event tab
    And I click on the Events button from navbar
    And I click on the Actions button from Events view switcher
    And I search last created Event by API using EVENT UUID and wait for 1 entries in the table
    And I collect the event actions from table view
    And I am checking if all the fields are correctly displayed in the Event directory Actions table

  @issue=7189
  Scenario: Add a Task from event and verify the fields
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the event tab using the created event via api
    Then I click on New Task from event tab
    And I create a new task with specific data for an event
    And I click on the Tasks button from navbar
    And I am checking if the associated linked event appears in task management and click on it
    And I click on edit task icon of the first created task
    And I check the created task is correctly displayed on Edit task page

  Scenario: Add a New Groups Event from event and verify the fields
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the event tab using the created event via api
    And I click on link event group
    And I create a new event group
    When I am accessing the event tab using the created event via api
    Then I am checking event group name and id is correctly displayed