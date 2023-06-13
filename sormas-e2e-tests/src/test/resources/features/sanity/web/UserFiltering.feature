@UI @Sanity @Users
Feature: User directory filters checks

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

  @#7470 @env_main
  Scenario: Verify active user filter functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I Verify the number of Active, Inactive and Total users in the User Management Page

  @#7470 @env_main
  Scenario: Verify User Roles filter functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I Verify The User Role filter in the User Management Page

  @#7470 @env_main
  Scenario: Verify Region filter functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    Then I Verify Region filter in the User Management Page