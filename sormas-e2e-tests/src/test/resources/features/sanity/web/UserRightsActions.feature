@UI @Sanity @Users
Feature: User actions based on their rights

  @tmsLink=SORDEV-5964 @env_main
  Scenario: Bulk mode for Activate/deactivate user accounts
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I create 2 new users with National User via UI
    And I pick and count amount a users that was created on the same period of time
    And I click Enter Bulk Edit Mode on Users directory page
    And I click checkbox to choose all User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Disable" from Bulk Actions combobox on User Directory Page
    And I pick "Inactive" value for Active filter in User Directory
    And I check that all Users are changed Active field value to opposite
    And I click checkbox to choose all User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Enable" from Bulk Actions combobox on User Directory Page
    And I pick "Active" value for Active filter in User Directory
    And I check that all Users are changed Active field value to opposite

    #TODO implement rest of the user roles!
  @#7470 @env_main
  Scenario: Validate that non-admin users can't access users directory
    Given I log in as a National User
    Then I Verify Users Navigation link is not present in the navigation bar

  @#8564 @env_main
  Scenario Outline: Check user <user> can see batch edit mode button for event participants
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a <user>
    Then I open the last created event via api
    And I click on the Event participant tab
    Then I check if Enter Bulk Edit mode button is present in Event Participants Tab

    Examples:
      | user                    |
      | Contact Supervisor      |
      | Surveillance Supervisor |