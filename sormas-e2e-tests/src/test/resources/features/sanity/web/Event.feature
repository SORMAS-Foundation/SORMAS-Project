@UI @Sanity @Event @UI
Feature: Create events

  @env_main
  Scenario: Create a new event
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status CLUSTER

  @env_main
  Scenario: Create a new event and change its status multiple times
    Given I log in as a National User
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
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data is correctly displayed in event edit page

  @env_main
  Scenario: Add a participant to an event
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Given I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click on the first Person ID from Event Participants
    Then I check if event is available at person information

  @tmsLink=SORDEV-5475 @env_main
  Scenario: Verify error messages while adding a participant to an event
    Given I log in as a National User
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
    And I click on the first Person ID from Event Participants
    Then I check if event is available at person information

  @env_main
  Scenario: Create and edit a new event
    Given I log in as a National User
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

    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I open the last created event via api
    And I click on New Action button from Event tab
    Then I create New Action from event tab
    And I click on Event Actions tab
    And I open the Action recently created from Event tab
    And I check that Action created from Event tab is correctly displayed in Event Actions tab

    @tmsLink=SORDEV-5520 @env_main
  Scenario: Add a New action from Event Actions tab and verify the fields.
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
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

    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I navigate to Event Action tab for created Event
    And I click on New Action from Event Actions tab
    And I create New Action from event tab
    And I click on the Events button from navbar
    And I click on the Actions button from Events view switcher
    And I search last created Event by API using EVENT UUID and wait for 1 entries in the table
    And I collect the event actions from table view
    And I am checking if all the fields are correctly displayed in the Event directory Actions table

  @tmsLink=SORDEV-5476 @env_main
  Scenario: Add a Task from event and verify the fields
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I am accessing the event tab using the created event via api
    Then I click on New Task from event tab
    And I create a new task with specific data for an event
    And I click on the Tasks button from navbar
    And I am checking if the associated linked event appears in task management and click on it
    And I click on edit task icon of the first created task
    And I check the created task is correctly displayed on Edit task page

  @tmsLink=SORDEV-5496 @env_main
  Scenario: Generate and download Event document
    Given I log in as a National User
    And I click on the Events button from navbar
    And I open the first event from events list
    And I click on the Create button from Event Document Templates
    When I create and download an event document from template
    And I verify that the event document is downloaded and correctly named

  @tmsLink=SORDEV-5491 @env_main
  Scenario: Add a participant to an event and create case
    Given I log in as a National User
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

  @tmsLink=SORDEV-9426 @env_main
  Scenario: Filter for the report date of events
    Given I log in as a National User
    And I click on the Events button from navbar
    Then I click on Show more filters in Events
    And I select Report Date among Event Reference Date options
    And I fill in a date range in Date of Event From Epi Week and ...To fields
    And I apply on the APPLY FILTERS button from Event
    And I check that the dates of displayed Event results are correct

  @tmsLink=SORDEV-5571 @env_main
  Scenario: Event group screen from Event Directory Page
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on GROUPS Radiobutton on Event Directory Page
    And I open the first event from events list
    And I click on Link Event button on Event Directory Page
    And I fill Id filter with Id of last created event in Link Event to group form
    And I click on filtered Event in Link Event to group form
    And I click on SAVE button in Link Event to group form
    And I click on Unlink Event button on Event Directory Page

  @tmsLink=SORDEV-5571 @env_main
  Scenario: Event group screen using Group Id on Edit Event Page
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I open the first event from events list
    And I click on Link Event button on Edit Event Page
    And I click on first Event Group on the list in Link Event form
    And I click on SAVE button in Link Event to group form
    And I click on Linked Group Id on Edit Event Page
    And I click on Unlink Event button on Event Directory Page

  @tmsLink=SORDEV-5571 @env_main
  Scenario: Event group screen using Group Id in grid
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I open the first event from events list
    And I click on Link Event button on Edit Event Page
    And I click on first Event Group on the list in Link Event form
    And I click on SAVE button in Link Event to group form
    And I click on successfully linked to this event group message popup in Link Event to group form
    And I click on the Events button from navbar
    And I click on Group Id in Events result on Event Directory Page
    And I click on Unlink Event button on Event Directory Page

  @tmsLink=SORDEV-5570 @env_main
  Scenario: Testing Event screen Impact
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a National User
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

  @tmsLink=SORDEV-5572 @env_main
  Scenario: Testing Event group adding for new event
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
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

  @tmsLink=SORDEV-9355 @env_main
  Scenario: Day of birth filter for event participant test
    Given I log in as a National User
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

  @tmsLink=SORDEV-7138 @env_main
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
    And I click Enter Bulk Edit Mode on Event directory page
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click checkbox to choose all Event Participants results in Event Participant Tab
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create Contacts button from bulk actions menu in Event Participant Tab
    And I create a new Contacts from Event Participants using Line Listing
    And I save the new contacts from Event Participants using line listing feature in Event Participant tab

  @tmsLink=SORDEV-5480  @env_main
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

  @tmsLink=SORDEV-5569 @env_main
  Scenario: Testing Event groups view filters with sorting actions
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
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

  @tmsLink=SORDEV-5481 @env_main
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

  @tmsLink=SORDEV-10049  @env_main
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

  @tmsLink=SORDEV-10051  @env_main
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

    @tmsLink=SORDEV-10359 @env_main
    Scenario: Test Access to the event directory filtered on the events of a group
      Given API: I create a new event

      And API: I check that POST call status code is 200
      Given I log in as a National User
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

  @tmsLink=SORDEV-7461 @env_main
  Scenario: Testing bulk edit of Events
    Given API: I create a new event

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

  @tmsLink=SORDEV-5967 @env_de
  Scenario: Add evidence fields for event clusters
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
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

  @tmsLink=SORDEV-8048 @env_de
  Scenario: Test Default value for disease if only one is used by the server for Events and Pathogen test
    Given I log in as a National User
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

  @tmsLink=SORDEV-9477 @env_main
  Scenario: Add a person search option on creation forms
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Then API: I create a new person

    And API: I check that POST call status code is 200
    Then API: I create a new case

    And API: I check that POST call status code is 200
    And I log in as a National User
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
#    navigation popup dissapeared
#    And I confirm navigation popup
    And I navigate to EVENT PARTICIPANT from edit event page
#    And I confirm navigation popup
    Then I click on Apply filters button in event participant list
    Then I check if filtered participant for existing person appears in the event participants list
    And I open the last created Person via API
    And I check that SEE EVENTS FOR THIS PERSON button appears on Edit Person page

  @env_main @#8555
  Scenario: Add back a person to an event who was previously deleted as event participant
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Given API: I create a new case

    And API: I check that POST call status code is 200
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I am accessing the event tab using the created event via api
    Then I add a participant created by API create person
    Then I check if participant appears in the participants list of event created with API
    Then I delete an event participant created by API create person
    Then I add a participant created by API create person
    Then I check if participant appears in the participants list of event created with API

  @tmsLink=SORDEV-10265 @env_main
  Scenario: Manual archiving for events
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Given I add a participant to the event
    Then I click on Event Participant Data tab
    And I click on the Archive event participant button
    And I check if Archive event popup is displayed correctly
    Then I check the end of processing date in the archive popup
    And I check if Archive button changed name to De-Archive
    Then I click on the Event participant tab
    And I choose Archived event participants from combobox in the Event participant tab
    Then I check if participant appears in the event participants list
    And I click on the first row from archived event participant
    Then I check if Archive button changed name to De-Archive

  @tmsLink=SORDEV-10265 @env_main
  Scenario: Manual archiving for event participants
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Given I add a participant to the event
    Then I click on the Event participant tab
    And I choose Archived event participants from combobox in the Event participant tab
    Then I back to the Event tab
    Then I click on the Archive event button
    Then I check the end of processing date in the archive popup
    And I check if Archive button changed name to De-Archive
    Then I click on the Event participant tab
    Then I check if participant appears in the event participants list

  @tmsLink=SORDEV-10265 @env_main
  Scenario: Manual archiving for bulk event participants
    Given API: I create a new event
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
    And I click on last created UI result in grid in Event Directory for Bulk Action
    Given I add a participant to the event
    Then I click on the Event participant tab
    And I choose Archived event participants from combobox in the Event participant tab
    And I click on the Events button from navbar
    Then I click on the RESET FILTERS button from Event
    And I click on last created API result in grid in Event Directory for Bulk Action
    Given I add a participant to the event
    Then I click on the Event participant tab
    And I choose Archived event participants from combobox in the Event participant tab
    And I click on the Events button from navbar
    Then I click on the RESET FILTERS button from Event
    And I select last created UI result in grid in Event Directory for Bulk Action
    And I select last created API result in grid in Event Directory for Bulk Action
    And I click on Bulk Actions combobox on Event Directory Page
    Then I click on the Archive bulk events on Event Directory page
    And I confirm archive bulk events
    Then I set Relevance Status Filter to Archived events on Event Directory page
    And I click on last created UI result in grid in Event Directory for Bulk Action
    Then I click on the Event participant tab
    Then I check if participant added form UI appears in the event participants list
    And I click on the Events button from navbar
    And I click on last created API result in grid in Event Directory for Bulk Action
    Then I click on the Event participant tab
    Then I check if participant added form API appears in the event participants list

  @tmsLink=SORDEV-9786 @env_main
  Scenario: Test The "urine p.m." enum value should be hidden when Covid19 is selected as disease
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Given API: I create a new case

    And API: I check that POST call status code is 200
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I open the last created event via api
    And I check that the value selected from Disease combobox is "COVID-19" on Edit Event page
    And I navigate to Event Participants tab in Edit case page
    Then I add a participant to the event
    Then I click on Event Participant Data tab
    And I click on New Sample
    And I check if value "Urine p.m" is unavailable in Type of Sample combobox on Create new Sample page

  @tmsLink=SORDEV-8665 @env_main
  Scenario: Test Move the responsible user filter in the event directory next to the jurisdiction filters
    Given I log in as a National User
    Then I click on the Events button from navbar
    And I click on Show more filters in Events
    And I check that Responsible User Info icon is visible on Event Directory Page
    And I check the displayed message is correct after hover to Responsible User Info icon

  @tmsLink=SORDEV-9946 @env_de
  Scenario: Test Hide country specific fields in the 'Pick or create person' form of the duplicate detection pop-up, in German and French systems
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on Add Participant button
    Then I add Participant to an Event with same person data
    And I save Add participant form
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on Add Participant button
    Then I add Participant to an Event with same person data
    And I save Add participant form
    And I check if National Health Id, Nickname and Passport number do not appear in Pick or create person popup

  @env_main @tmsLink=SORDEV-7460
  Scenario: Test Extend the exposure and event startDate and endDate to include a startTime and endTime
    Given I log in as a National User
    Then I click on the Events button from navbar
    And I click on the NEW EVENT button
    Then I check Multi-day event checkbox and I pick Start date and End date on Create New Event Page
    And I fill event Title field on Create New Event Page
    And I click on save button on Create New Event Page
    Then I navigate to EVENT from edit event page
    And I collect the UUID displayed on Create New Event Page
    And I collect the Date of Event from Create New Event Page
    Then I navigate to EVENTS LIST from edit event page
    And  I search for the last event uuid created by UI
    Then I check that Date of EVENT displays event start date and event end date in table on event directory

  @tmsLink=SORDEV-8667 @env_main
  Scenario: Test Adjustments to the jurisdiction definition process of event participants
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    Then I open the last created event via api
    Then I click on the Event participant tab
    And I add only required data for event participant creation
    And I click on the Event participant tab
    Then I check that number of displayed Event Participants is 1
    And I back to the Event tab
    And I check if Country combobox on Edit Event page is disabled
    And I check that message appearing in hover of Info icon is equal to expected on Edit Event page
    And I click on the Event participant tab
    And I click on the first result in table from event participant
    And I edit participants responsible region and responsible district
    And I click on Save Button in Edit Event directory
    And I click on the Event participant tab
    And I back to the Event tab

  @tmsLink=SORDEV-10254 @env_main
  Scenario: Manual archive Event participants/Events
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with status EVENT
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I click on the Archive event button
    Then I check the end of processing date in the archive popup
    And I click on the Events button from navbar
    Then I set Relevance Status Filter to Archived events on Event Directory page
    And I search for specific event by uuid in event directory
    And I click on the searched event
    Then I click on the Event participant tab
    And I choose Archived event participants from combobox in the Event participant tab
    Then I check if participant appears in the event participants list
    Then I back to the Event tab
    Then I click on the De-Archive event button
    And I fill De-Archive event popup with test automation reason
    And I click on the Events button from navbar
    Then I set Relevance Status Filter to Active events on Event Directory page
    And I search for specific event by uuid in event directory
    And I click on the searched event
    Then I click on the Event participant tab
    And I choose Active event participants from combobox in the Event participant tab
    Then I check if participant appears in the event participants list

  @tmsLink=SORDEV-9788 @env_de
  Scenario: Test Hide country specific fields in the 'Person search option' pop-up in Event Participant directory
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    Then I click on ADD PARTICIPANT button
    And I click on the person search button in add new event participant form
    Then I check that National Health ID is not visible in Person search popup
    And I check that Passport Number is not visible in Person search popup
    And I check that Nickname is not visible in Person search popup

  @tmsLink=SORDEV-6076 @env_main
  Scenario: Test Make event report date editable
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with today for date of report and date of event
    Then I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the only mandatory created data is correctly displayed in event edit page
    Then I change the report event date for minus 1 day from today
    And I click on Save Button in Edit Event directory
    Then I check if date of report is set for 1 day ago from today
    Then I change the report event date for minus 0 day from today
    And I click on Save Button in Edit Event directory
    And I set the date of event for today
    And I change the report event date for minus 2 day from today
    Then I check if date of report has an error exclamation mark with correct error message
    And I click on Save Button in Edit Event directory
    Then I check if error popup is displayed with message Please check the input data
    And I check if error popup contains Date of report has to be after or on the same day as Start date
    And I check if error popup contains Start date has to be before or on the same day as Date of report

  @tmsLink=SORDEV-5563 @env_de
  Scenario: Add contact person details to facilities event participant
    Given I log in as a Admin User
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    And I click on New Entry button in Facilities tab in Configuration
    Then I set name, region and district in Facilities tab in Configuration
    And I set Facility Category to "Medizinische Einrichtung" and Facility Type to "Krankenhaus" in Facilities tab in Configuration
    And I set Facility Contact person first and last name with email address and phone number
    Then I click on Save Button in new Facility form
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data for DE version with created facility
    And I check if data for created facility is automatically imported to the correct fields
    Then I click on save button on Create New Event Page
    And I click on the Events button from navbar
    And I search for specific event with created facility in event directory
    And I click on the searched event with created facility
    And I check if data for created facility is automatically imported to the correct fields in Case Person tab
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only required data for event participant creation for DE
    Then I click on Event Participant Person tab
    And I set Region to "Voreingestellte Bundesländer" and District to "Voreingestellter Landkreis" in Event Participant edit page
    Then I set Facility Category to "Medizinische Einrichtung" and  Facility Type to "Krankenhaus"
    And I set facility name to created facility
    And I check if data for created facility is automatically imported to the correct fields in Case Person tab
    Then I click on save button on Create New Event Page
    Then I click on the Configuration button from navbar
    And I navigate to facilities tab in Configuration
    Then I search last created facility
    Then I click on edit button for the last searched facility
    And I archive facility

  @env_main @#8556
  Scenario: Add two positive Pathogen Test Result of different diseases to a Sample of an Event Participant
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Given API: I create a new case

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I am accessing the event tab using the created event via api
    Then I add a participant created by API create person
    Then I collect the event participant UUID displayed in event participants list
    Then I navigate to a specific Event Participant of an Event based on UUID
    Then I click on New Sample and discard changes is asked
    Then I collect the sample UUID displayed on create new sample page
    Then I create a new Sample with positive test result with COVID-19 as disease
    Then I confirm popup window
    Then I pick an existing case in pick or create a case popup
    Then I click on edit Sample
    Then I click on new test result for pathogen tests
    Then I create a new pathogen test result with Cholera as disease
    Then I confirm the Create case from contact with positive test result
    Then I create a new case with specific data for positive pathogen test result
    Then I save the new case
    Then I navigate to a specific Event Participant of an Event based on UUID
    Then I validate only one sample is created with two pathogen tests
    Then I click on edit Sample
    Then I validate the existence of "2" pathogen tests

  @env_main @#8565
  Scenario: Check an archived event if its read only
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I am accessing the event tab using the created event via api
    Then I click on the Archive event button
    Then I confirm Archive event popup
    Then I click on logout button from navbar
    Then I log in as a National User
    Then I am accessing the event tab using the created event via api
    Then I check if editable fields are read only for an archived event

  @tmsLink=SORDEV-7094 @env_main
  Scenario Outline: Test Event identification source fields
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with event identification source "<name>"
    And I back to the Event tab
    Then I check that checkbox Event Identification source with selected "<name>" have HTML value: "checked"

    Examples:
      | name             |
      | UNKNOWN          |
      | BACKWARD-TRACING |
      | FORWARD-TRACING  |

  @tmsLink=SORDEV-7095 @env_main
  Scenario: Test Addition of a Variant field in the "EVENT" part
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    Then I select random Disease filter among the filter options from API
    And I select "B.1.617.3" Disease Variant filter on Event Directory Page
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    And I select "B.1.617.1" Disease Variant filter on Event Directory Page
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I select "B.1.617.3" Disease Variant filter on Event Directory Page
    And I apply on the APPLY FILTERS button from Event

  @tmsLink=SORDEV-7467 @env_main
  Scenario Outline: Test Allow surveillance supervisors and contact supervisors to access bulk-edition in the event directory
    Given I log in as a <user>
    Then I click on the Events button from navbar
    And I click on the More button on Event directory page
    And I click Enter Bulk Edit Mode on Event directory page
    And I click on Bulk Actions combobox on Event Directory Page
    And I check that Edit option is visible in Bulk Actions dropdown
    And I check that Group option is visible in Bulk Actions dropdown
    And I check that Archive option is visible in Bulk Actions dropdown
    And I check that Delete option is not visible in Bulk Actions dropdown

    Examples:
      | user                      |
      | Contact Supervisor        |
      | Surveillance Supervisor   |

  @tmsLink=SORDEV-6609 @env_de
  Scenario: Test for event internal token
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    And I navigate to EVENT from edit event page
    When I fill in the Internal Token field in Edit Case page with SAMPLE TOKEN
    And I click on save button in the case popup
    And I click on the Events button from navbar
    And I check that the German Internal Token column is present
    And I filter for SAMPLE TOKEN in Events Directory
    Then I check that at least one SAMPLE TOKEN is displayed in table

  @tmsLink=SORDEV-11455 @env_main
  Scenario: Add reason for deletion to confirmation dialogue
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    And I navigate to the last created through API Event page via URL
    And I click Delete button on Edit Event page
    And I click on No option in Confirm deletion on Edit Event Page
    And I click Delete button on Edit Event page
    And I click on Yes option in Confirm deletion on Edit Event Page
    And I check that error message is equal to "Please choose a reason for deletion" in Reason for Deletion in popup
    And I click on No option in Confirm deletion on Edit Event Page
    And I click Delete button on Edit Event page
    And I set Reason for deletion to "Other reason" on Edit Event Page
    And I click on Yes option in Confirm deletion on Edit Event Page
    And I check that error message is equal to "Please add a reason for deletion" in Reason for Deletion in popup
    And I click on No option in Confirm deletion on Edit Event Page
    And I click Delete button on Edit Event page
    And I set Reason for deletion to "Entity created without legal reason" on Edit Event Page
    And I click on Yes option in Confirm deletion on Edit Event Page
    And I am accessing the event tab using the created event via api
    And I check if Reason for deletion is set to "Entity created without legal reason" on Edit Event Page
    And I check if Delete button on Edit Event Page is changed to Undo Deletion

    @tmsLink=SORDEV-8055 @env_main
    Scenario Outline: Allow users to select the used delimiter when importing files
      Given I log in as a Admin User
      And I click on the Events button from navbar
      When I click on the Import button from Events directory
      Then I check if default Value Separator is set to "Default (Comma)"
      And I check is possible to set Value Separator to <option>

      Examples:
        | option          |
        | Comma           |
        | Semicolon       |
        | Tab             |
        | Default (Comma) |

  @tmsLink=SORQA-7093 @env_main
  Scenario: Allow the admin surveillance supervisor to delete events
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I navigate to SORMAS login page
    Then I log in as a Admin Surveillance Supervisor
    And I click on the Events button from navbar
    And I navigate to the last created through API Event page via URL
    When I click Delete button on Edit Event page
    When I set Reason for deletion to "Deletion request by affected person according to GDPR" on Edit Event Page
    When I confirm popup window
    And I check that previous opened Event was deleted

  @tmsLink=SORQA-7093 @env_main
  Scenario: Allow the admin surveillance supervisor to archive events
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I navigate to SORMAS login page
    Then I log in as a Admin Surveillance Supervisor
    And I click on the Events button from navbar
    And I navigate to the last created through API Event page via URL
    When I click on the Archive event button
    When I confirm Archive event popup
    And I check event is it archived

  @tmsLink=SORDEV-11452 @env_main
  Scenario: Add reason for deletion to confirmation dialogue
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    Then I add a participant to the event
    And I copy url of current event participant
    Then I click on Event Participant Data tab
    Then I click on Delete button from event participant
    And I check if reason for deletion as "Deletion request by affected person according to GDPR" is available
    And I check if reason for deletion as "Deletion request by another authority" is available
    And I check if reason for deletion as "Entity created without legal reason" is available
    And I check if reason for deletion as "Responsibility transferred to another authority" is available
    And I check if reason for deletion as "Deletion of duplicate entries" is available
    And I check if reason for deletion as "Other reason" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from event participant
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please choose a reason for deletion" appears next to Reason for deletion
    When I set Reason for deletion as "Other reason"
    Then I check if "Reason for deletion details" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Please add a reason for deletion" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted event participant by url
    Then I click on Event Participant Data tab
    Then I check if reason of deletion is set to "Deletion request by affected person according to GDPR"
    Then I click on Event Participant Person tab
    And I check if General comment on event participant edit page is disabled
    And I check if Passport number input on event participant edit page is disabled

  @tmsLink=SORDEV-11452 @env_de
  Scenario: Add reason for deletion to confirmation dialogue for DE version
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on Add Participant button
    Then I add Participant to an Event with same person data
    And I click on save button in Add Participant form
    And I copy url of current event participant
    Then I click on Event Participant Data tab
    Then I click on Delete button from event participant
    And I check if reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO" is available
    And I check if reason for deletion as "Löschen auf Anforderung einer anderen Behörde" is available
    And I check if reason for deletion as "Entität ohne Rechtsgrund angelegt" is available
    And I check if reason for deletion as "Abgabe des Vorgangs wegen Nicht-Zuständigkeit" is available
    And I check if reason for deletion as "Löschen von Duplikaten" is available
    And I check if reason for deletion as "Anderer Grund" is available
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from contact
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte wählen Sie einen Grund fürs Löschen" appears next to Reason for deletion
    When I set Reason for deletion as "Anderer Grund"
    Then I check if "DETAILS ZUM GRUND DES LÖSCHENS" field is available in Confirm deletion popup in Immunization
    And I click on Yes option in Confirm deletion popup
    Then I check if exclamation mark with message "Bitte geben Sie einen Grund fürs Löschen an" appears next to Reason for deletion
    Then I click on No option in Confirm deletion popup
    Then I click on Delete button from immunization case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    When I back to deleted event participant by url
    Then I click on Event Participant Data tab
    Then I check if reason of deletion is set to "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    Then I click on Event Participant Person tab
    And I check if General comment on event participant edit page is disabled

  @tmsLink=SORDEV-9792 @env_de
  Scenario: Test CoreAdo: Introduce "end of processing date" for events
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    And I navigate to EVENT from edit event page
    Then I collect the UUID displayed on Edit event page
    Then I click on the Archive event button
    Then I check the end of processing date in the archive popup and select Archive event checkbox for DE version
    Then I click on the De-Archive event button
    And I fill De-Archive event popup with test automation reason
    Then I change date of event report for today for DE version
    And I click on Save Button in Edit Event directory
    Then I click on the Archive event button
    Then I check the end of processing date in the archive popup and select Archive event checkbox for DE version
    And I click on the Events button from navbar
    And I apply "Abgeschlossene Ereignisse" to combobox on Event Directory Page
    And I search for specific event by uuid in event directory
    And I check that number of displayed Event results is 1
    Then I click on the first Event ID from Event Directory
    Then I click on the De-Archive event button
    Then I click on confirm button in de-archive event popup
    And I check if exclamation mark with message "Bitte geben Sie einen Grund für die Wiedereröffnung an" appears while trying to de-archive without reason
    And I click on discard button in de-archive event popup
    Then I click on the De-Archive event button
    And I fill De-Archive event popup with test automation reason
    And I click on the Events button from navbar
    And I apply "Aktive Ereignisse" to combobox on Event Directory Page
    # And I search for specific event by uuid in event directory
    And I check that number of displayed Event results is 1

  @tmsLink=SORDEV-9792 @env_de
  Scenario: Test CoreAdo: Introduce "end of processing date" for event participants
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on Add Participant button
    Then I add Participant to an Event with same person data
    And I click on save button in Add Participant form
    Then I collect the UUID displayed on Edit event participant page
    Then I click on the Archive event participant button
    Then I check the end of processing date in the archive popup and select Archive event participant for DE version
    And I click on the Event participant tab
    Then I choose Abgeschlossene Ereignisteilnehmer from combobox in the Event participant tab
    And I check that number of displayed Event participants results is 1
    Then I click on the first row from archived event participant
    Then I click on the De-Archive event participant button
    Then I click on confirm button in de-archive event popup
    And I check if exclamation mark with message "Bitte geben Sie einen Grund für die Wiedereröffnung an" appears while trying to de-archive without reason
    And I click on discard button in de-archive event popup
    Then I click on the De-Archive event participant button
    And I fill De-Archive event popup with test automation reason
    And I click on the Event participant tab
    Then I choose Aktive Ereignisteilnehmer from combobox in the Event participant tab
    And I check that number of displayed Event participants results is 1

  @tmsLink=SORDEV-10227 @env_de
  Scenario: Test Permanent deletion for Person for Event Participant
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only required data for event participant creation for DE
    And I click on Event Participant Person tab
    And I collect the event participant person UUID displayed on Edit Event Participant page
    And I click on the Persons button from navbar
    Then I filter the last created person linked with Event Participant
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    And I click on All aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 1
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click on the first result in table from event participant
    Then I click on Delete button from event participant
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Persons button from navbar
    Then I filter the last created person linked with Event Participant
    And I click on Events aggregation button in Person Directory for DE specific
    And I check that number of displayed Person results is 0

  @tmsLink=SORDEV-5565 @env_de
  Scenario: Document Templates create quarantine order for Event Participant bulk DE
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only required data for event participant creation for DE
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only required data for event participant creation for DE
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I click Enter Bulk Edit Mode on Event directory page
    And I select first 2 results in grid in Event Participant Directory
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create Quarantine Order from Bulk Actions combobox on Event Participant Directory Page
    And I click on checkbox to upload generated document to entities in Create Quarantine Order form in Event Participant directory for DE
    And I select "ExampleDocumentTemplateEventParticipant.docx" Quarantine Order in Create Quarantine Order form in Event Participant directory
    And I click on Create button in Create Quarantine Order form DE
    And I click on close button in Create Quarantine Order form
    And I check if downloaded zip file for Quarantine Order is correct for DE version

  @tmsLink=SORDEV-10361 @env_main
  Scenario: Test Hide "buried" within Person present condition for Covid-19 for Events
    Given I log in as a Admin User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    When I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    Then I check the created data is correctly displayed in event edit page
    Given I add a participant to the event
    Then I check if Present condition of person combobox has value "Alive"
    And I check if Present condition of person combobox has value "Dead"
    And I check if Present condition of person combobox has value "Unknown"
    Then I check if Present condition of person combobox has no value "Buried"
    And I navigate to EVENT PARTICIPANT from edit event page
    And I back to the Event tab
    And I change disease to "Ebola Virus Disease" in the event tab
    Then I click on Save Button in Edit Event directory
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click on the first row from event participant list
    Then I click on Event Participant Person tab
    Then I check if Present condition of person combobox has value "Alive"
    And I check if Present condition of person combobox has value "Dead"
    And I check if Present condition of person combobox has value "Unknown"
    Then I check if Present condition of person combobox has value "Buried"
    Then I set Present condition of person to "Buried"
    And I check if "Date of burial" field is present in case person
    And I check if "Burial conductor" field is present in case person
    And I check if "Burial place description" field is present in case person
    Then I click on the Event participant tab
    And I navigate to EVENT PARTICIPANT from edit event page
    And I back to the Event tab
    And I change disease to "COVID-19" in the event tab
    Then I click on Save Button in Edit Event directory
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click on Create in Case ID row in event participant list
    Then I check if Present condition of person combobox has value "Alive"
    And I check if Present condition of person combobox has value "Dead"
    And I check if Present condition of person combobox has value "Unknown"
    Then I check if Present condition of person combobox has no value "Buried"

  @tmsLink=SORDEV-12439 @env_main
  Scenario: Test set 'All Event Participants' as the default value when an event is active
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I am accessing the event tab using the created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    And I check event participant filter dropdown on event participant page when event is active

  @tmsLink=SORDEV-12439 @env_main
  Scenario: Test set 'Active event participants' as the default value when an event is archived
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I am accessing the event tab using the created event via api
    Then I click on the Archive event button
    Then I check the end of processing date in the archive popup
    And I navigate to EVENT PARTICIPANT from edit event page
    And I check event participant filter dropdown on event participant page when event is archived

  @env_main @#7750
  Scenario: Check the map functionality in the Edit Event Page
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Then I log in as a National User
    Then I am accessing the event tab using the created event via api
    Then I Verify The Eye Icon opening the Map is disabled in the Edit Event Page
    And I Add the GPS Latitude and Longitude Values in the Edit Event Page
    Then I Verify The Eye Icon opening the Map is enabled in the Edit Event Page
    And I click on the The Eye Icon located in the Edit Event Page
    Then I verify that the Map Container is now Visible in the Edit Event Page

  @env_main @#8559
  Scenario: Confirm navigation' pop-up is triggered when a user creates a new entry for 'Contact information' and tries to navigate to another page
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I open the last created event via api
    When I click on the Event participant tab
    When I add a participant to the event
    Then I click on new entry button from Contact Information section
    When I click the Done button in Person Contact Details popup
    When I click on the Tasks button from navbar
    Then I click the Cancel Action button from the Unsaved Changes pop-up located in the Event Participant Page
    And I click on the NEW IMMUNIZATION button in Edit event participant
    Then I click the Cancel Action button from the Unsaved Changes pop-up located in the Event Participant Page

  @tmsLink=SORDEV-12441 @env_de
  Scenario: Hide citizenship and country of birth on Edit Event Participant Person Page
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I add only required data for event participant creation for DE

    @tmsLink=SORQA-667 @env_de @oldfake
    Scenario: Check automatic deletion of EVENT_PARTICIPANT created 1826 days ago
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new event

    And API: I check that POST call status code is 200
    Then API: I create a new event participant with creation date 1826 days ago

    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I check if participant created via API appears in the event participants list
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created event participant is available in API
    And API: I check that GET call status code is 204
    Then I open the last created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    Then I check if event participant created via API not appears in the event participant list

  @tmsLink=SORQA-666 @env_de @oldfake
    Scenario: Check automatic deletion of EVENT created 1826 days ago
    Given API: I create a new event with creation date 1826 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open the last created event via api
    And I copy uuid of current event
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created event is available in API
    And API: I check that GET call status code is 500
    Then I click on the Events button from navbar
    And I filter by last created event via api
    And I check the number of displayed Event results from All button is 0

  @tmsLink=SORQA-679 @env_de @oldfake
  Scenario: Check automatic deletion NOT of EVENT_PARTICIPANT created 1820 days ago
    Given API: I create a new person
    And API: I check that POST call status code is 200
    Then API: I create a new event

    And API: I check that POST call status code is 200
    Then API: I create a new event participant with creation date 1820 days ago

    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    And I check if participant created via API appears in the event participants list
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 30 seconds for system reaction
    Then I check if created event participant is available in API
    And API: I check that GET call status code is 200
    Then I open the last created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    Then I check if event participant created via API still appears in the event participant list

  @tmsLink=SORQA-680 @env_de @oldfake
  Scenario: Check automatic deletion NOT of EVENT created 1820 days ago
    Given API: I create a new event with creation date 1820 days ago
    And API: I check that POST call status code is 200
    Then I log in as a Admin User
    Then I open the last created event via api
    And I copy uuid of current event
    And I click on the Configuration button from navbar
    Then I navigate to Developer tab in Configuration
    Then I click on Execute Automatic Deletion button
    And I wait 60 seconds for system reaction
    Then I check if created event is available in API
    And API: I check that GET call status code is 200
    Then I click on the Events button from navbar
    And I filter by last created event via api
    And I check the number of displayed Event results from All button is 1

  @#10419 @env_main
  Scenario: Verify Warning message in Event Participants for Bulk actions, when no event is selected
    Given I log in as a Admin User
    And I click on the Events button from navbar
    When I click on the first row from event participant list
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click Enter Bulk Edit Mode in Event Participants Page
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create Contacts button from bulk actions menu in Event Participant Tab
    Then I verify the warning message 'No event participants selected' is displayed
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Delete button from bulk actions menu in Event Participant Tab
    Then I verify the warning message 'No event participants selected' is displayed
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create quarantine order documents from bulk actions menu in Event Participant Tab
    Then I verify the warning message 'No event participants selected' is displayed

  @#5762 @env_main
  Scenario: Link Event to a Case
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Then API: I create a new case

    And API: I check that POST call status code is 200
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    And I open the last created Case via API
    Then I click Link Event button on Edit Case Page
    And I fill Event Id filter in Link to Event form with last created via API Event uuid
    And I click first result in grid on Link to Event form
    And I click on SAVE button in Link Event to group form
#    Then I click on save button in Add Participant form
    Then I click Save in Add Event Participant form on Edit Contact Page
    And I validate last created via API Event data is displayed under Linked Events section

  @tmsLink=SORDEV-10280 @env_main
  Scenario Outline: Test Allow "surveillance supervisor" and "contact supervisor" profiles to access the batch edit mode of the directory of participating events
    Given API: I create a new person

    And API: I check that POST call status code is 200
    Then API: I create a new event

    And API: I check that POST call status code is 200
    Then API: I create a new event participant with creation date 2 days ago

    And API: I check that POST call status code is 200
    Then I log in as a <user>
    Then I open the last created event via api
    Then I navigate to EVENT PARTICIPANT from edit event page
    Then I click Enter Bulk Edit Mode on Event Participant directory page
    And I select first 1 results in grid in Event Participant Directory
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    And I click on Create Contacts button from bulk actions menu in Event Participant Tab
    Then I check if Create Contacts Line listing window appears
    And I click on discard button in line listing
    And I click on Bulk Actions combobox in Event Parcitipant Tab
    Then I click on Create Quarantine Order from Bulk Actions combobox on Event Participant Directory Page by button text
    And I select "ExampleDocumentTemplateEventParticipant.docx" Quarantine Order in Create Quarantine Order form in Event Participant directory

    Examples:
      | user                      |
      | Contact Supervisor        |
      | Surveillance Supervisor   |