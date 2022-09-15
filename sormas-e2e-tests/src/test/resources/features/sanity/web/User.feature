@UI @Sanity @Users
Feature: Create user

  @env_main
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

  @env_main
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

  @tmsLink=SORQA-461 @env_main
  Scenario Outline: Filter of User folder for user roles
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I set user role to "<user>"

    Examples:
    | user                          |
    | Admin                         |
    | National User                 |
    | Surveillance Supervisor       |
    | Admin Surveillance Supervisor |
    | Surveillance Officer          |
    | Hospital Informant            |
    | Community Officer             |
    | Clinician                     |
    | Case Officer                  |
    | Contact Supervisor            |
    | Contact Officer               |
    | Event Officer                 |
    | Lab Officer                   |
    | National Observer             |
    | Region Observer               |
    | District Observer             |
    | National Clinician            |
    | POE Informant                 |
    | POE Supervisor                |
    | POE National User             |
    | Import User                   |
    | External Visits User          |
    | Sormas to Sormas Client       |
    | BAG User                      |
    | Community Informant           |
    | External Lab Officer          |

  @tmsLink=SORQA-461 @env_main
  Scenario Outline: Filter of User folder for regions
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I set region filter to "<region>"

    Examples:
    | region                          |
    | Baden-Württemberg               |
    | Bayern                          |
    | Berlin                          |
    | Brandenburg                     |
    | Bremen                          |
    | Hamburg                         |
    | Hessen                          |
    | Mecklenburg-Vorpommern          |
    | Niedersachsen                   |
    | Nordrhein-Westfalen             |
    | Rheinland-Pfalz                 |
    | Saarland                        |
    | Sachsen                         |
    | Sachsen-Anhalt                  |
    | Schleswig-Holstein              |
    | Thüringen                       |
    | Voreingestellte Bundesländer    |

  @tmsLink=SORQA-461 @env_main
  Scenario: Filter of User folder for automation_admin user and active filter
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I search user "automation_admin"
    And I check if displayed user name is equal with searched "automation_admin"
    And I pick "Active" value for Active filter in User Directory
    And I pick "Inactive" value for Active filter in User Directory

  @tmsLink=SORQA-457 @env_keycloak
  Scenario: Create a new SORMAS user, check login and disable
    Given I log in as Admin User in Keycloak enabled environment
    And I click on the Users from navbar
    Then I click on the NEW USER button
    Then I create new Test user for test on DE specific
    And I click on logout button from navbar
    And I login first time as a new created user from keycloak instance
    And I click on logout button from navbar
    Then I log in as Admin User in Keycloak enabled environment
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
    And I click View all users button
    Then I search for last created user from SORMAS in grid in Keycloak Admin Page
    And I open last created user from SORMAS in Keycloak Admin Page
    And I check if user is disabled in Keycloak Admin Page

  @tmsLink=SORQA-460 @env_keycloak
  Scenario: Change password of SORMAS user (by admin)
    Given I log in as Admin User in Keycloak enabled environment
    And I click on the Users from navbar
    Then I search user "PasswordUser"
    And I select first user from list
    Then I create new user password and save it on Edit User page
    Then I click on logout button from navbar
    And I login first time as a last edited user from keycloak instance
    Then I click on logout button from navbar

  @#10111 @env_main
  Scenario: Change user password and login
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I select first user from list
    Then I create new user password and save it on Edit User page
    Then I click on logout button from navbar
    And I login with last edited user
    Then I click on logout button from navbar