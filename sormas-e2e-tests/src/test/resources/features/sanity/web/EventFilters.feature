@UI @Sanity @Event @UI
Feature: Event Directory filters check

  @tmsLink=SORDEV-5915 @env_main
  Scenario: Check all filters are working properly in Event directory
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    Then I select random Risk level filter among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random risk level value different than risk level value of last created via API Event in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I select random Disease filter among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select Disease filter value different than the disease value of the last created via API case in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select Source Type among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select source Type filter value different than the source type value of the last created via API case in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select Type of Place field among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select type of place filter value different than the type of place value of the last created via API case in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I click on the RESET FILTERS button from Event

  @tmsLink=SORDEV-5917 @env_de
  Scenario: Check all filters are working properly in Event directory for DE version
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    Then I select a German Risk level filter based on the event created with API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select random German risk level value different than risk level value of last created via API Event in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 0
    And I click on the RESET FILTERS button from Event
    Then I select random Disease filter among the filter options from API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select German Source Type based on the event created with API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select German source Type filter value different than the source type value of the last created via API case in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I click on the RESET FILTERS button from Event
    Then I click on Show more filters in Events
    Then I select German Type of Place field based on the event created with API
    And I fill EVENT ID filter by API
    And I apply on the APPLY FILTERS button from Event
    And I check that number of displayed Event results is 1
    Then I select German type of place filter value different than the type of place value of the last created via API case in Event Directory
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I click on the RESET FILTERS button from Event

  @tmsLink=SORQA-77 @env_main
  Scenario: Filters for Region, District, Community, Reporting user and Event statuses on Event Directory Page
    Given API: I create a new person

    And API: I check that POST call status code is 200
    When API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I open the last created event via api
    And I add a participant to the event
    And I open the last created event via api
    And I click on link event group
    And I create a new event group
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I fill Event Group Id filter to one assigned to created event on Event Directory Page
    And I click on Show more filters in Events
    And I apply Region filter to "Voreingestellte Bundesländer" on Event directory page
    And I apply District filter to "Voreingestellter Landkreis" on Event directory page
    And I apply Community filter to "Voreingestellte Gemeinde" on Event directory page
    And I apply Event Investigation Status filter to "Investigation pending" on Event directory page
    And I apply Event Management Status filter to "Ongoing" on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 1
    And I filter by mocked EventId on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I fill EVENT ID filter by API
    And I filter by mocked EventGroupId on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I fill Event Group Id filter to one assigned to created event on Event Directory Page
    And I fill Reporting User filter to "Surveillance Supervisor" on Event Directory Page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I apply Region filter to "Bayern" on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I apply Region filter to "Voreingestellte Bundesländer" on Event directory page
    And I apply District filter to "Voreingestellter Landkreis" on Event directory page
    And I apply Community filter to "Voreingestellte Gemeinde" on Event directory page
    And I apply Event Investigation Status filter to "Investigation done" on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I apply Event Investigation Status filter to "Investigation pending" on Event directory page
    And I apply Event Management Status filter to "Done" on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0

  @tmsLink=SORQA-77 @env_main
  Scenario: Date filters and aggregation buttons in Event Directory
    Given API: I create a new event

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Events button from navbar
    And I fill EVENT ID filter by API
    And I click on Show more filters in Events
    And I apply Date type filter to "Report date" on Event directory page
    And I fill Event from input to 2 days before mocked Event created on Event directory page
    And I fill Event to input to 5 days after mocked Event created on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 1
    And I fill Event from input to 2 days after before mocked Event created on Event directory page
    And I apply on the APPLY FILTERS button from Event
    And I check the number of displayed Event results from All button is 0
    And I fill Event from input to 2 days before mocked Event created on Event directory page
    And I apply on the APPLY FILTERS button from Event
    Then I select Signal filter from quick filter
    And I check the number of displayed Event results from All button is 1
    And I select Event filter from quick filter
    And I check the number of displayed Event results from All button is 0
    And I select Screening filter from quick filter
    And I check the number of displayed Event results from All button is 0
    And I select Cluster filter from quick filter
    And I check the number of displayed Event results from All button is 0
    And I select Dropped filter from quick filter
    And I check the number of displayed Event results from All button is 0
    Then I select Signal filter from quick filter
    And I check the number of displayed Event results from All button is 1
    And I apply "Archived events" to combobox on Event Directory Page
    And I check the number of displayed Event results from All button is 0

  @tmsLink=SORDEV-9426 @env_main
  Scenario: Filter for the report date of events
    Given I log in as a National User
    And I click on the Events button from navbar
    Then I click on Show more filters in Events
    And I select Report Date among Event Reference Date options
    And I fill in a date range in Date of Event From Epi Week and ...To fields
    And I apply on the APPLY FILTERS button from Event
    And I check that the dates of displayed Event results are correct

  @tmsLink=SORQA-5969 @env_de
  Scenario Outline: Test vaccination status filter <status> and columns to event
    Given I log in as a National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data for DE version
    And I navigate to EVENT from edit event page
    When I add a participant to the event in DE
    Then I set participant vaccination status to <status>
    Then I set event vaccination status filter to <status>
    And I apply event filters
    Then I check that created Event is visible with <status> status


    Examples:
      | status    |
      | Geimpft   |
      | Ungeimpft |
      | Unbekannt |