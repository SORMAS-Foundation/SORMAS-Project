@UI @Sanity @Case @CaseCommunities
Feature: Case communities end to end tests

@issue=SORDEV-8050 @env_main
Scenario: Test A user viewing a case containing e.g. an archived region has no indication about the fact the region was archived
  Given I log in as a Admin User
  Then I click on the Configuration button from navbar
  Then I click on Communities button in Configuration tab
  And I click on New Entry button in Communities tab in Configuration
  Then I fill new community with specific data
  And I click on the Cases button from navbar
  And I click on the NEW CASE button
  Then I create new case with created community
  And I check if created case with specific community is created correctly
  Then I click on the Configuration button from navbar
  And I click on Communities button in Configuration tab
  And I filter by last created community
  Then I click on edit button for filtered community
  Then I archive chosen community
  And I click on the Cases button from navbar
  Then I filter last created Case by external ID
  Then I click on the first Case ID from Case Directory
  And I check if community chosen in case is changed to inactive
  Then I clear Community from Responsible Community in Case Edit Tab
  When I click on save case button
  Then I check if archived community is unavailable in Case Edit Tab
  Then I click on the Configuration button from navbar
  And I click on Communities button in Configuration tab
  And I filter Communities by Archived communities
  And I filter by last created community
  Then I click on edit button for filtered community
  Then I de-archive chosen community
  And I click on the Cases button from navbar
  Then I filter last created Case by external ID
  Then I click on the first Case ID from Case Directory
  And I set last created community in Responsible Community in Case Edit tab
  When I click on save case button
  And I check if created case with specific community is created correctly