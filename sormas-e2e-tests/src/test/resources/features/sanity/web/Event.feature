@Sanity @Event
Feature: Create events

  Scenario: Create a new event
    Given I log in as a National User
      And I click on the Events button from navbar
      And I click on the NEW EVENT button
      And I create a new event

  Scenario: Validate a new event creation
    Given I log in as a National User
      And I click on the Events button from navbar
      And I click on the NEW EVENT button
      And I validate all fields are rendered correctly on create a new event popup window

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

  Scenario: Create and check a new event data
    Given I log in as a National User
      And I click on the Events button from navbar
      And I click on the NEW EVENT button
     When I create a new event with specific data
      And I click on the Events button from navbar
      And I search for specific event in event directory
      And I click on the searched event
     Then I check the created data is correctly displayed in event edit page

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
      And I click on the Persons button from navbar
      And I search for specific person in person directory
      And I click on specific person in person directory
     Then I check if event is available at person information

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