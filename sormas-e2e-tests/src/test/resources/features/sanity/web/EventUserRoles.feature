@UI @Sanity @EventUserRoles @add_userroles
Feature: Create events for different user roles

  @tmsLink=SORDEV-10359 @env_main
  Scenario Outline: Test Access to the event directory filtered on the events of a group
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a <user>
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

    Examples:
      | user                      |
      | Admin User                |
      | Contact Officer           |
      | Surveillance Officer      |
      | Surveillance Supervisor   |
      | Community Officer         |