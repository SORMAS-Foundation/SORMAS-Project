@UI @Sanity @Users
Feature: User actions based on their rights

  @tmsLink=SORDEV-5964 @env_main
  Scenario: Bulk mode for Activate/deactivate user accounts
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I create 2 new users with National User via UI
    And I search after users that were created on the same period of time
    And I click Enter Bulk Edit Mode on Users directory page
    And I click checkbox to choose first 2 User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Disable" from Bulk Actions combobox on User Directory Page
    And I pick "Inactive" value for Active filter in User Directory
    And I check that created users are displayed in results grid
    And I click checkbox to choose first 2 User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Enable" from Bulk Actions combobox on User Directory Page
    And I pick "Active" value for Active filter in User Directory
    And I check that created users are displayed in results grid

  @#7470 @env_main
  Scenario Outline: Validate that non-admin user <user> can't access users directory
    Given I log in as a <user>
    Then I Verify Users Directory is not present in the navigation bar

    Examples:
      | user                          |
      | National User                 |
      | Contact Supervisor            |
      | Surveillance Officer          |
      | Surveillance Supervisor       |
      | Laboratory Officer            |
      | Point of Entry Supervisor     |
      | Admin Surveillance Supervisor |
      | Contact Officer               |
      | Community Officer             |
      | Hospital Informant            |
      | Clinician                     |

  @#8564 @env_main
  Scenario Outline: Check user <user> can see batch edit mode button for event participants
    Given API: I create a new event
    And API: I check that POST call status code is 200
    Given I log in as a <user>
    Then I open the last created event via api
    And I click on the Event participant tab
    Then I check if Enter Bulk Edit mode button is present in Event Participants Tab

    Examples:
      | user                    |
      | Contact Supervisor      |
      | Surveillance Supervisor |

  @tmsLink=HSP-6402 @env_main
  Scenario: User with rights only for Users cannot see the User Role tab
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I check if there is any user with the "RightsChecker" role and change his role
    And I click on User roles tab from User Management Page
    And I check if the "RightsChecker" user role exist and delete it
    And I click on New user role button on User Roles Page
    And I choose "National User" as the user role template
    And I fill caption input as "RightsChecker" on Create New User Role form
    And I click SAVE button on User Role Page
    And I click checkbox to choose "View existing users"
    And I click checkbox to choose "Edit existing users"
    And I click checkbox to choose "Create new users"
    And I click SAVE button on User Role Page
    And I back to the User role list
    Then I click on User Management tab from User Roles Page
    And I click on the NEW USER button
    And I create new "RightsChecker" with english language for test
    Then I click on logout button from navbar
    And I login with new created user with chosen new role
    Then I click on the Users from navbar
    And I Verify User Management tab is present from User Roles Page
    And I Verify that User Roles is not present in the tab