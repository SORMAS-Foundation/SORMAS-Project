@UI @Sanity @Users
Feature: User roles checks

  @tmsLink=SORDEV-12303 @env_main
  Scenario: Edit and create user roles
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And I check if the "TestNatUser" user role exist and change it to enabled
    And I click on the Users from navbar
    And I check if there is any user with the "TestNatUser" role and change his role
    And I click on User roles tab from User Management Page
    And I check if the "TestNatUser" user role exist and delete it
    And I click on New user role button on User Roles Page
    And I choose "National User" as the user role template
    And I click SAVE button on Create New User Role form
    And I close input data error popup
    And I fill caption input as "TestNatUser" on Create New User Role form
    And I click SAVE button on Create New User Role form
    And I click checkbox to choose "Archive cases"
    And I click checkbox to choose "Archive contacts"
    And I click SAVE button on User Role Page
    And I back to the User role list
    And I check that "TestNatUser" is displayed in the User role column
    And I click on New user role button on User Roles Page
    And I choose "TestNatUser" as the user role template
    And I click DISCARD button on Create New User Role form
    And I click on User Management tab from User Roles Page
    And I set user role to "TestNatUser"
    And I click on the NEW USER button
    And I create new "TestNatUser" with english language for test
    And I filter last created user
    And I open first user from the list
    And I click DISCARD button on Create New User Role form
    Then I click on logout button from navbar
    And As a new created user I log in
    And I click on the Cases button from navbar
    And I click on the first Case ID from Case Directory
    And I click on the Archive case button
    And I click on discard button in de-archive case popup
    And I click on logout button from navbar
    And I log in as a Admin user
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And I filter user roles by "Archive contacts" user rights
    And I double click on "TestNatUser" from user role list
    And I click checkbox to choose "Archive cases"
    And I click SAVE button on User Role Page
    And I click on logout button from navbar
    And As a new created user I log in
    And I click on the Cases button from navbar
    And I click on the first Case ID from Case Directory
    And I check that the Archive case button is not available
    And I click on logout button from navbar
    And I log in as a Admin user
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And I filter user roles by "Archive contacts" user rights
    And I double click on "TestNatUser" from user role list
    And I click on the user role "Disable" button
    And I click SAVE button on User Role Page
    And I back to the User role list
    And I filter user roles by "Disabled"
    And I check that "TestNatUser" is displayed in the User role column
    And I click on User roles tab from User Management Page
    And I click on New user role button on User Roles Page
    And I check that "TestNatUser" is not available in the user role template dropdown menu
    And I click DISCARD button on Create New User Role form
    And I click on User Management tab from User Roles Page
    And I check that "TestNatUser" is not available in the user role filter
    And I click on the NEW USER button
    And I check that "TestNatUser" user role checkbox is not available in Create New User form
    And I click DISCARD button on Create New User Role form
    And I click on logout button from navbar
    And As a new created user I log in
    And I click on the Cases button from navbar
    And I click on the first Case ID from Case Directory
    And I check that the Archive case button is not available
    And I click on logout button from navbar
    And I log in as a Admin user
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And I filter user roles by "Disabled"
    And I double click on "TestNatUser" from user role list
    And I click on delete user role button
    And I confirm user role deletion
    And I check if Cannot delete user role popup message is displayed
    And I confirm Cannot delete user role popup message
    And I click on the user role "Enable" button
    And I click SAVE button on User Role Page
    And I back to the User role list
    And I click on User Management tab from User Roles Page
    And I filter users by "TestNatUser" user role
    And I double click on "TestNatUser" from user role list
    And I click checkbox to choose "TestNatUser" as a user role in Edit user form
    And I click checkbox to choose "National User" as a user role in Edit user form
    And I click on the Save button in the Edit User Page
    And I click on User roles tab from User Management Page
    And I filter user roles by "Enabled"
    And I filter user roles by "Archive contacts" user rights
    And I double click on "TestNatUser" from user role list
    And I click on delete user role button
    And I confirm user role deletion
    And I click on User Management tab from User Roles Page

  @#10422 @env_main
  Scenario: Validate newly created user role is present in filtering options
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I check if there is any user with the "TestNatUser10422" role and change his role
    And I click on User roles tab from User Management Page
    And I check if the "TestNatUser10422" user role exist and delete it
    And I click on New user role button on User Roles Page
    And I choose "National User" as the user role template
    And I fill caption input as "TestNatUser10422" on Create New User Role form
    And I click SAVE button on User Role Page
    And I back to the User role list
    And I click on User Management tab from User Roles Page
    And I check that "TestNatUser10422" is available in the user role filter in User management Page
    And I click on User roles tab from User Management Page
    And I check if the "TestNatUser10422" user role exist and delete it

  @#10420 @env_main
  Scenario: Validate Export User Role file download functionality
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And I click on the Export User Roles Button and verify User role file is downloaded and contains data in the User Role Page

  @#10421 @env_main
  Scenario: Validate newly created User Role cannot be deleted if assigned towards an user
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I check if there is any user with the "NationalTestUser" role and change his role
    And I click on User roles tab from User Management Page
    And I check if the "NationalTestUser" user role exist and delete it
    And I click on New user role button on User Roles Page
    And I choose "National User" as the user role template
    And I fill caption input as "NationalTestUser" on Create New User Role form
    And I click SAVE button on User Role Page
    And I back to the User role list
    And I click on User Management tab from User Roles Page
    And I click on the NEW USER button
    And I create new "NationalTestUser" with english language for test
    And I click on User roles tab from User Management Page
    And I check if the "NationalTestUser" user role cannot be deleted while assigned
    And I click on the Users from navbar
    And I check if there is any user with the "NationalTestUser" role and change his role
    And I click on User roles tab from User Management Page
    And I verify that the "NationalTestUser" user role exist and delete it

  @tmsLink=SOR-4735 @env_main
  Scenario: User with existing rights can see User Roles tab
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I click on User roles tab from User Management Page
    And Validate user can see User roles tab from User Management Page

  @tmsLink=HSP-6300 @env_main
  Scenario: Check Delete Case right working without Edit rights
    Given I log in as a Admin User
    And I click on the Users from navbar
    And I check if there is any user with the "NewTestUser" role and change his role
    And I click on User roles tab from User Management Page
    And I check if the "NewTestUser" user role exist and delete it
    And I click on New user role button on User Roles Page
    And I choose "National User" as the user role template
    And I fill caption input as "NewTestUser" on Create New User Role form
    And I click SAVE button on User Role Page
    Then I click checkbox to uncheck "Edit existing cases"
    Then I click checkbox to uncheck "Edit case investigation status"
    Then I click checkbox to uncheck "Edit case disease"
    Then I click checkbox to uncheck "Transfer cases to another region/district/facility"
    Then I click checkbox to uncheck "Edit case classification and outcome"
    Then I click checkbox to uncheck "Edit case epid number"
    Then I click checkbox to uncheck "Refer case from point of entry"
    Then I click checkbox to uncheck "Can be responsible for a case"
    Then I click checkbox to uncheck "Work with message"
    And I click SAVE button on User Role Page
    And I back to the User role list
    Then I click on User Management tab from User Roles Page
    And I click on the NEW USER button
    And I create new "NewTestUser" with english language for test
    Then I click on logout button from navbar
    And I login with new created user with chosen new role
    And I click on the Cases button from navbar
    And I open the first Case result in Case Directory
    Then I get the case person UUID displayed on Edit case page
    Then I click on Delete button from case
    And I click on Yes option in Confirm deletion popup
    When I set Reason for deletion as "Deletion request by affected person according to GDPR"
    And I click on Yes option in Confirm deletion popup
    And I set the Relevance Status Filter to "Deleted cases" on Case Directory page
    And I search for the last "deleted" case on Case directory page
    Then I open the first Case result in Case Directory
    And Total number of read only fields should be 14
    Then I check that "Discard" button is readonly on Edit case page
    And I check that "Save" button is readonly on Edit case page
    Then I click on Restore button from case
    And I set the Relevance Status Filter to "Active cases" on Case Directory page
    And I search for the last "restored" case on Case directory page
    And I check that number of displayed cases results is 1