@Sanity @Event @UI
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