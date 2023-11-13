@UI @Sanity @Users
Feature: Create user

  @tmsLink=SORDEV-7456mk @env_main
  Scenario Outline: Create a new user
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on the NEW USER button
    And I create a new user with <rights>
    When I search for created user
    Then I check the created data is correctly displayed on Edit User Page for selected <rights>

    Examples:
      | rights                  |
      | National User           |
      | POE National User       |
      | Import User             |
      | External Visits User    |
      | Sormas to Sormas Client |
      | National Clinician      |

  @tmsLink=SORDEV-7456mk @env_main
  Scenario Outline: Edit user
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on the NEW USER button
    And I create a new user with <rights>
    When I search for created user
    And I change user data and save the changes
    And I search for recently edited user
    Then I check the edited data is correctly displayed on Edit User page

    Examples:
      | rights            |
      | National User     |
      | POE National User |

    @tmsLink=SORDEV-9366 @env_main
    Scenario: Users with limited disease
      Given I log in as a Admin User
      And I click on the Users from navbar
      And I click on the NEW USER button
      And I create new National User with limited disease to Cholera
      Then I click on logout button from navbar
      And As a new created user with limited disease view I log in
      Then I click on the Cases button from navbar
      And I check if user have limited disease view to Cholera only

  @tmsLink=SORQA-457 @env_keycloak
  Scenario: Create a new SORMAS user, check login and disable
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I click on the NEW USER button
    Then I create new Nationale*r Benutzer*in user for test on DE specific
    And I click on logout button from navbar
    And I login first time as a new created user from keycloak instance
    And I click on logout button from navbar
    Then I log in as a Admin User
    And I click on the Users from navbar
    And I filter last created user
    And I open first user from the list
    Then I set last created user to inactive
    And I click on logout button from navbar
    And As a new created user on Keycloak enabled instance I log in
    Then I check error message for disabled user is present
    And  I navigate to Keycloak Administrator Console Login page
    Then I log in as Keycloak Admin to Keycloak Administrator Console
    And I navigate to Users tab in Keycloak Administrator Console
    Then I search for last created user from SORMAS in grid in Keycloak Admin Page
    And I check if user is disabled in Keycloak Admin Page

  @tmsLink=SORQA-460 @env_keycloak
  Scenario: Change password of SORMAS user (by admin)
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I search user "PasswordUser"
    And I select first user from list
    Then I create new user password and save it on Edit User page
    Then I click on logout button from navbar
    And I login first time as a last edited user from keycloak instance
    And I check if GDPR message appears and close it if it appears
    Then I click on logout button from navbar

  @#10111 @env_main
  Scenario: Change user password and login
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on the NEW USER button
    And I create new National User with limited disease to Cholera
    And I click on the Users from navbar
    And I select first user from list
    Then I create new user password and save it on Edit User page
    Then I click on logout button from navbar
    And I login with last edited user
    Then I click on logout button from navbar

  @#7470 @env_main
  Scenario: Verify user set active set inactive functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    When I create a new disabled National User in the Create New User page
    When I search for created user in the User Management Page
    Then I verify that the Active value is Checked in the User Management Page
    When I select first user from list
    Then I click on the Active checkbox in the Edit User Page
    Then I verify that the Active value is Unchecked in the User Management Page

  @#7470 @env_main
  Scenario: Validate create new password functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    When I create 1 new users with National User via UI
    When I search for created user
    When I click Create New Password in Edit User page
    Then I click the Cancel button in the Update Password Modal located in the Edit User Page
    When I click Create New Password in Edit User page
    Then I click the Update button in the Update Password Modal located in the Edit User Page
    Then I Verify the New Password Modal in the Edit User Page

  @#7470 @env_main
  Scenario: Validate mandatory phone number field
    Given I log in as a Admin User
    And I click on the Users from navbar
    When I create 1 new users with National User via UI
    When I search for created user
    When I fill phone number with a wrong format in the Edit User Page
    And I click on the Save button in the Edit User Page
    Then I verify the error message is displayed in the Edit User Page