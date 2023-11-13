@UI @Sanity @Invalid
Feature: Application invalid actions end to end tests

  @env_main
  Scenario: UI Login without credentials
    Given I try to log in with "" and password ""
    Then Login failed message should be displayed

  @env_main
  Scenario: UI Login without password
    Given I try to log in with "NatUser" and password ""
    Then Login failed message should be displayed

  @env_main
  Scenario: UI Login without username
    Given I try to log in with "" and password "macarena"
    Then Login failed message should be displayed

  @env_main
  Scenario: UI Login with invalid user
    Given I try to log in with "Donald" and password "Duck781$"
    Then Login failed message should be displayed

  @env_main
  Scenario: UI Login with inactive user
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I set active inactive filter to Inactive in User Management directory
    And I select first user from list
    Then I create new user password and save it on Edit User page
    Then I click on logout button
    And I login with last edited user
    Then Login failed message should be displayed

  @env_main
  Scenario: Navigate to specific URL without being logged in
    Given I navigate to /events via URL append
    Then Login page should be displayed

  @env_main
  Scenario: Navigate to specific URL after logout
    Given I log in as a Admin User
    Then I click on logout button
    Given I navigate to /events via URL append
    Then Login page should be displayed

  @env_main @API
  Scenario: Perform API call with an empty user
    Given API: I GET persons uuids without user credentials
    And API: I check that POST call status code is 401

  @env_main @API
  Scenario: Perform API call with an invalid user
    Given API: I GET persons uuids with invalid user credentials
    And API: I check that POST call status code is 401


