@UI @Sanity @Event @UI
Feature: Create events

  @env_main
  Scenario: Create a new event
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status CLUSTER

  @env_main
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

  @env_main
  Scenario: Create and check a new event data
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data is correctly displayed in event edit page

    #please address
  @env_main @ignore
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

    #please address
  @issue=SORDEV-5475 @env_main @ignore
  Scenario: Verify error messages while adding a participant to an event
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Given I add empty participant data
    Then I save changes in participant window
    And I check if error display correctly expecting first name error
    And I add participant first name only
    Then I save changes in participant window
    And I check if error display correctly expecting last name error
    And I add participant first and last name only
    Then I save changes in participant window
    And I check if error display correctly expecting sex error
    And I discard changes in participant window
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I navigate via URL to last Person created from edit Event page
    Then I check if event is available at person information

  @env_main
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

  @env_main
  Scenario: Add a New action from event and verify the fields
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

    @issue=SORDEV-5520 @env_main
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
    Then I open the last created event via api
    And I check that number of actions in Edit Event Tab is 1

      #please address
  @env_main @ignore
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

  @issue=SORDEV-5476 @env_main
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

  @env_main
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

  @issue=SORDEV-5496 @env_main
  Scenario: Generate event document
    Given I log in with National User
    And I click on the Events button from navbar
    And I open the first event from events list
    And I click on the Create button from Event Document Templates
    When I create an event document from template
    And I verify that the event document is downloaded and correctly named

  @issue=SORDEV-5491 @env_main
  Scenario: Add a participant to an event and create case
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    Then I click Create Case for Event Participant
    And I fill all fields for a new case created for event participant
    And I click on save case button

    @issue=SORDEV-5915 @env_main @ignore
  Scenario: Check all filters are work properly in Event directory
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in with National User
    And I click on the Events button from navbar
    Then I select random Risk level filter among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random Risk level filter among the filter options
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I select random Disease filter among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random Disease filter among the filter options
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select Source Type among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random Source Type among the filter options
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select Type of Place field among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random Type of Place field among the filter options
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I select Signal filter from quick filter
    And I select Event filter from quick filter
    And I select Screening filter from quick filter
    And I select Cluster filter from quick filter
    And I select Dropped filter from quick filter
    And I click on the RESET FILTERS button from Event

  @issue=SORDEV-9426 @env_main
  Scenario: Filter for the report date of events
    Given I log in with National User
    And I click on the Events button from navbar
    Then I click on Show more filters in Events
    And I select Report Date among Event Reference Date options
    And I fill in a date range in Date of Event From Epi Week and ...To fields
    And I apply on the APPLY FILTERS button from Event
    And I check that the dates of displayed Event results are correct

  @issue=SORDEV-5571 @env_main
  Scenario: Event group screen from Event Directory Page
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on GROUPS Radiobutton on Event Directory Page
    And I open the first event from events list
    And I click on Link Event button on Event Directory Page
    And I fill Id filter with Id of last created event in Link Event to group form
    And I click on filtered Event in Link Event to group form
    And I click on SAVE button in Link Event to group form
    And I click on Unlink Event button on Event Directory Page

  @issue=SORDEV-5571 @env_main
  Scenario: Event group screen using Group Id on Edit Event Page
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I open the first event from events list
    And I click on Link Event button on Edit Event Page
    And I click on first Event Group on the list in Link Event form
    And I click on SAVE button in Link Event to group form
    And I click on Linked Group Id on Edit Event Page
    And I click on Unlink Event button on Event Directory Page

  @issue=SORDEV-5571 @env_main
  Scenario: Event group screen using Group Id in grid
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I open the first event from events list
    And I click on Link Event button on Edit Event Page
    And I click on first Event Group on the list in Link Event form
    And I click on SAVE button in Link Event to group form
    And I click on the Events button from navbar
    And I click on Group Id in Events result on Event Directory Page
    And I click on Unlink Event button on Event Directory Page

  @issue=SORDEV-5570 @env_main
  Scenario: Testing Event screen Impact
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I click on link event group
    And I choose select event group Radiobutton
    And I select the first row from table and I click on save button
    And I click on link event group
    And I create a new event group
    Then I unlinked the first chosen group by click on Unlink event group button
    And I click on Edit event group button from event groups box
    And I click on Edit event button for the first event in Events section
    And I click on the Navigate to event directory filtered on this event group
    And I check the number of displayed Event results from All button is 1

    #please address
  @issue=SORDEV-5572 @env_main @ignore
  Scenario: Testing Event group adding for new event
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I click on link event group
    And I create a new event group
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I hover to Event Groups column of the Event result
    And I click on the More button on Event directory page
    And I click Enter Bulk Edit Mode on Event directory page
    And I click checkbox to choose all Event results on Event Directory Page
    And I click on Bulk Actions combobox on Event Directory Page
    And I click on Group Events from Bulk Actions combobox on Event Directory Page
    And I create a new event group
    And I hover to Event Groups column of the Event result
    And I filter by last created group in Event Directory Page
    And I apply on the APPLY FILTERS button from Event
    And I hover to Event Groups column of the Event result
    And I check that name appearing in hover is equal to name of linked Event group
    And I check the number of displayed Event results from All button is 1


