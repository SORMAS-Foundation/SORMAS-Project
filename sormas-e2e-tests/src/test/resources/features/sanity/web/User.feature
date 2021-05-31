@Sanity @Users
Feature: Create user

  Scenario: Create a new user
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on the NEW USER button
    And I create a new user with <rights>
    When I select last created user
    Then I check the created data is correctly displayed on Edit User Page

#    Examples:
#      | rights                        |
#      | National User                 |
#      | Admin                         |
#      | Surveillance Supervisor       |
#      | Admin Surveillance Supervisor |
#      | Surveillance Officer          |
#      | Hospital Informant            |
#      | Community Officer             |
#      | Community Informant           |
#      | Clinician                     |
#      | Case Officer                  |
#      | Contact Supervisor            |
#      | Contact Officer               |
#      | Event Officer                 |
#      | Lab Officer                   |
#      | External Lab Officer          |
#      | National Observer             |
#      | Region Observer               |
#      | District Observer             |
#      | National Clinician            |
#      | POE Informant                 |
#      | POE Supervisor                |
#      | POE National User             |
#      | Import User                   |
#      | External Visits User          |
#      | ReST User                     |
#      | Sormas to Sormas Client       |

  Scenario: Check the edit User
