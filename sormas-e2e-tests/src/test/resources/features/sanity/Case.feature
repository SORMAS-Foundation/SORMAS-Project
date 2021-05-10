@Sanity @Case
Feature: Create cases

  Scenario: Create and check a new case data
    Given I log in with the user
      And I click on the Cases button from navbar
      And I click on the NEW CASE button
     When I create a new case with specific data
     Then I check the created data is correctly displayed on Edit case page
      And I check the created data is correctly displayed on Edit case person page
