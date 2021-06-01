@Sanity @Case
Feature: Cases end to end tests

  Scenario: Create and check a new case data
    Given I log in with the user
      And I click on the Cases button from navbar
      And I click on the NEW CASE button
     When I create a new case with specific data
     Then I check the created data is correctly displayed on Edit case page
      And I check the created data is correctly displayed on Edit case person page

  Scenario: Delete created case
    Given I log in with the user
    When API: I create a new person
    Then API: I create a new case
    And I click on the Cases button from navbar
    And I open the last created Case via API
    And I delete the case
    Then I check that number of displayed contact results is 0


    
