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

  @env_main
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

  @issue=SORDEV-5475 @env_main
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

  @env_main
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
    Given I log in as a National User
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
  Scenario: Generate and download Event document
    Given I log in with National User
    And I click on the Events button from navbar
    And I open the first event from events list
    And I click on the Create button from Event Document Templates
    When I create and download an event document from template
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
    And I fill Event Group Id filter to one assigned to created event on Event Directory Page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 1

  @issue=SORDEV-5572 @env_main
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

  @issue=SORDEV-9355 @env_main
  Scenario: Day of birth filter for event participant test
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
    And I fill birth fields for participant in event participant list
    Then I click on Apply filters button in event participant list
    Then I check if filtered participant appears in the event participants list

  @issue=SORDEV-7138  @env_main
  Scenario: Add a participant to an event and bulk create contacts
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click checkbox to choose all Event Participants results in Event Participant Tab
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create Contacts button from bulk actions menu in Event Participant Tab
    And I create a new Contacts from Event Participants using Line Listing
    And I save the new contacts from Event Participants using line listing feature in Event Participant tab

  @issue=SORDEV-5480  @env_main
  Scenario: Import Events
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I read the UUIDs of the first four events in Events directory
    When I click on the Import button from Events directory
    And I select the Event CSV file in the file picker
    And I click on the Start Data Import button from Import Events popup
    Then I check that an import success notification appears in the Import Events popup
    And I close the Import Events popups
    And I check that four new events have appeared in Events directory

  @issue=SORDEV-5569 @env_main
  Scenario: Testing Event groups view filters with sorting actions
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the event tab using the created event via api
    And I click on link event group
    And I create a new event group
    When I am accessing the event tab using the created event via api
    Then I am checking event group name and id is correctly displayed
    And I click on the Events button from navbar
    And I click on GROUPS Radiobutton on Event Directory Page
    Then I search last created groups Event by "GROUP_ID" option filter in Event Group Directory
    Then I search last created Event by "TITLE" option filter in Event Group Directory
    And I chose Region option in Event Group Directory
    And I chose District option in Event Group Directory
    And I chose Community option in Event Group Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    And I chose Region "Berlin" option in Event Group Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I chose Region "Region1" option in Event Group Directory
    And I chose District "District11" option in Event Group Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I chose Region option in Event Group Directory
    And I chose District option in Event Group Directory
    And I chose Community "Community1" option in Event Group Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I chose Community option in Event Group Directory
    And I apply on the APPLY FILTERS button from Event
    And I chose "Active groups" option from Relevnce Status filter in Event Group Directory
    And I check that number of displayed Event results is 1
    And I chose "Archived groups" option from Relevnce Status filter in Event Group Directory
    And I check that number of displayed Event results is 0
    And I chose "All groups" option from Relevnce Status filter in Event Group Directory
    And I check that number of displayed Event results is 1
    And I chose "Active groups" option from Relevnce Status filter in Event Group Directory
    And I click on the RESET FILTERS button from Event
    And I sort all rows by Group ID in Event Group Directory
    And I sort all rows by Group NAME in Event Group Directory
    And I click on a Export button in Event Group Directory
    And I click on a Basic Export button from Export options in Event Group Directory

  @issue=SORDEV-5481 @env_main
  Scenario: Export and import event participant
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click Export button in Event Participant Directory
    And I click on Detailed Export button in Event Participant Directory
    And I close popup after export in Event Participant directory
    Then I click on the Import button from Event Participants directory
    And I select the event participant CSV file in the file picker
    And I click on the "START DATA IMPORT" button from the Import Event Participant popup
    And I confirm the save Event Participant Import popup
    And I check that an import success notification appears in the Import Event Participant popup
    Then I delete exported file from Event Participant Directory

  @issue=SORDEV-10049  @env_main
  Scenario: Test basic export of event participant
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click Export button in Event Participant Directory
    And I click on Basic Export button in Event Participant Directory

  @issue=SORDEV-10051  @env_main
  Scenario: Test custom export of event participant
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click Export button in Event Participant Directory
    And I click on Custom Export button in Event Participant Directory
    And I click on the New Export Configuration button in Custom Event Participant Export popup
    Then I fill Configuration Name field with Test Configuration Name
    And I select specific data of event participant to export in Export Configuration
    When I download created custom event participant export file
    And I delete created custom event participant export file
    Then I check if downloaded data generated by custom event option is correct
    Then I delete exported file from Event Participant Directory

    @issue=SORDEV-10359 @env_main
    Scenario: Test Access to the event directory filtered on the events of a group
      Given API: I create a new event
      Then API: I check that POST call body is "OK"
      And API: I check that POST call status code is 200
      Given I log in with National User
      When I am accessing the event tab using the created event via api
      And I click on link event group
      And I create a new event group
      When I am accessing the event tab using the created event via api
      Then I am checking event group name and id is correctly displayed
      And I click on the Events button from navbar
      And I click on GROUPS Radiobutton on Event Directory Page
      Then I search last created groups Event by "GROUP_ID" option filter in Event Group Directory
      And I apply on the APPLY FILTERS button from Event
      And I open the first event group from events list group
      Then I click on Edit event group button from event groups box
      And I click on the Navigate to event directory filtered on this event group
      And I check the if Event is displayed correctly in Events Directory table

  @issue=SORDEV-7461 @env_main
  Scenario: Testing bulk edit of Events
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data is correctly displayed in event edit page
    And I click on the Events button from navbar
    Then I click on the RESET FILTERS button from Event
    And I click on the More button on Event directory page
    And I click Enter Bulk Edit Mode on Event directory page
    And I select last created UI result in grid in Event Directory for Bulk Action
    And I select last created API result in grid in Event Directory for Bulk Action
    And I click on Bulk Actions combobox on Event Directory Page
    And I click on Edit Events from Bulk Actions combobox on Event Directory Page
    Then I click to bulk change event managements status for selected events
    And I click on SAVE button in Link Event to group form
    And I navigate to the last created through API Event page via URL
    Then I check if Event Management Status is set to "PENDING"
    And I navigate to the last created Event page via URL
    Then I check if Event Management Status is set to "PENDING"

  @issue=SORDEV-5967 @env_de
  Scenario: Add evidence fields for event clusters
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I check CLUSTER option on edit Event page
    And I select "Hauptsächlich von Mensch zu Mensch" option from Primary Mode Of Transmission Combobox on edit Event page
    And I click on Epidemiological evidence with UNBEKANNT option
    And I click on Epidemiological evidence with NEIN option
    And I click on Epidemiological evidence with JA option
    And I tick the all options for Study on Epidemiological evidence for De version
    Then I check that all options for Study on Epidemiological evidence appears and there are checked for De version
    And I tick the all options for Explorative survey of affected people on Epidemiological evidence for De version
    Then I check the all options for Explorative survey of affected people on Epidemiological evidence appears and there are checked for De version
    And I tick the all options for Descriptive analysis of ascertained data on Epidemiological evidence for De version
    Then I check the all options for Descriptive analysis of ascertained data on Epidemiological evidence appears and there are checked for De version
    And I tick the all options for Suspicion on Epidemiological evidence for De version
    Then I check the all options for Suspicion on Epidemiological evidence are visible and clickable for De version
    Then I click on Laboratory diagnostic evidence with UNBEKANNT option
    And I click on Laboratory diagnostic evidence with NEIN option
    And I click on Laboratory diagnostic evidence with JA option
    And I tick the all options for Verification of at least two infected or diseased persons on Laboratory diagnostic evidence for De version
    Then I check the all options for Verification of at least two infected or diseased persons on Laboratory diagnostic evidence appears and there are checked for De version
    And I tick the all options for Verification on materials on Laboratory diagnostic evidence for De version
    Then I check the all options for Verification on materials on Laboratory diagnostic evidence appears and there are checked for De version
    And I click on SAVE button in edit event form

  @issue=SORDEV-8048 @env_de
  Scenario: Test Default value for disease if only one is used by the server for Events and Pathogen test
    Given I log in with National User
    Then I click on the Events button from navbar
    When I click on the NEW EVENT button
    Then I check if default disease value is set for COVID-19
    Then I click on the NEW EVENT button
    When I create a new event with specific data for DE version
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data for DE version is correctly displayed in event edit page
    Then I click on the Sample button from navbar
    When I open created Sample
    Then I click on the new pathogen test from the Edit Sample page for DE version
    And I check if default disease value for new Pathogen test is set for COVID-19

  @issue=SORDEV-9477 @env_main
  Scenario: Add a person search option on creation forms
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And I log in with National User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I navigate to Event Participants tab in Edit case page
    And I add participant responsible region and responsible district only
    And I click on the person search button in add new event participant form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I click on the clear button in new add new event participant form
    And I click on the person search button in add new event participant form
    And I search for the last created person via Api by uuid in popup on Select Person window
    And I open the first found result in the popup of Select Person window
    And I save changes in participant window
    And I confirm navigation popup
    And I navigate to EVENT PARTICIPANT from edit event page
    And I confirm navigation popup
    Then I click on Apply filters button in event participant list
    Then I check if filtered participant for existing person appears in the event participants list
    When I click on the Persons button from navbar
    And I open the last created Person via API
    And I check that SEE EVENTS FOR THIS PERSON button appears on Edit Person page

  @env_main @#8555
  Scenario: Add back a person to an event who was previously deleted as event participant
    Given API: I create a new person
    And API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I am accessing the event tab using the created event via api
    Then I add a participant created by API create person
    Then I check if participant appears in the participants list of event created with API
    Then I delete an event participant created by API create person
    Then I add a participant created by API create person
    Then I check if participant appears in the participants list of event created with API

  @issue=SORDEV-8665 @env_main
  Scenario: Test Move the responsible user filter in the event directory next to the jurisdiction filters
    Given I log in with National User
    Then I click on the Events button from navbar
    And I click on Show more filters in Events
    And I check that Responsible User Info icon is visible on Event Directory Page
    And I check the displayed message is correct after hover to Responsible User Info icon