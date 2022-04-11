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
      | ReST User               |
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

    @issue=SORDEV-9366 @env_main
    Scenario: Users with limited disease
      Given I log in as a Admin User
      And I click on the Users from navbar
      And I click on the NEW USER button
      And I create new National User with limited disease to Cholera
      Then I click on logout button from navbar
      And As a new created user with limited disease view I log in
      Then I click on the Cases button from navbar
      And I check if user have limited disease view to Cholera only

  @issue=SORDEV-5964 @env_main
  Scenario: Bulk mode for Activate/deactivate user accounts
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on the NEW USER button
    And I create a new user with National User
    And I click on the NEW USER button
    And I create a new user with National User
#    maybe consider anoter solution to searc by date
    And I pick a users that was created on the same hour
    And I click Enter Bulk Edit Mode on Users directory page
    And I click checkbox to choose all User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Disable" from Bulk Actions combobox on User Directory Page
#    check active case
    And  I am collect ACTIVE checkbox
    And I am checking that ACTIVE checkbox are thick
    And I click checkbox to choose all User results
    And I click on Bulk Actions combobox on User Directory Page
    And I click on "Enable" from Bulk Actions combobox on User Directory Page


    And I am checking all Exposure data created by UI is saved and displayed in Cases

#    TODO popracowac nad :  And  I am collect ACTIVE checkbox
#    And I am checking that ACTIVE checkbox are thick