@UI @Sanity @Keycloak
Feature: Keycloak tests

  @tmsLink=SORQA-458 @env_keycloak
  Scenario:SORMAS - Keycloak Synchronisation (manual sync)
    Given I log in as Admin User in Keycloak enabled environment
    Then I click on the Users from navbar
    Then I click on Sync Users button
    And I click on Sync button from Sync Users popup
    And I check if sync message is correct in German
    And I count the number of users displayed in User Directory
    Given I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    And I navigate to Users tab in Keycloak Administrator Console
    And I click View all users button
    Then I count the number of users displayed in Users tab in Keycloak Administrator Console
    And I check that number of users from SORMAS is equal to number of users in Keycloak Administrator Console

  @tmsLink=SORQA-459 @env_keycloak
  Scenario:SORMAS - Keycloak Synchronisation (automatic sync)
    Given I log in as Admin User in Keycloak enabled environment
    Then I click on the Users from navbar
    Then I click on Sync Users button
    And I click on Sync button from Sync Users popup
    And I check if sync message is correct in German
    And I count the number of users displayed in User Directory
    Given I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    And I navigate to Users tab in Keycloak Administrator Console
    And I click View all users button
    Then I count the number of users displayed in Users tab in Keycloak Administrator Console
    And I check that number of users from SORMAS is equal to number of users in Keycloak Administrator Console