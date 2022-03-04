@UI @Sanity @Users
Feature: Create user

  #please address - verify sormas to client scenarios
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

