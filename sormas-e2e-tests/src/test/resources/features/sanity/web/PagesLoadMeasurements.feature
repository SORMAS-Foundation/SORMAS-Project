@UI @PagesMeasurements
Feature: Pages loading time

  Scenario: Check Tasks page loading time
    Given I log in with National User
    And I click on the Tasks button from navbar
    Then I wait for "Tasks" page to load and calculate elapsed time

  Scenario: Check Persons page loading time
    Given I log in with National User
    And I click on the Persons button from navbar
    Then I wait for "Persons" page to load and calculate elapsed time

  Scenario: Check Cases page loading time
    Given I log in with National User
    And I click on the Cases button from navbar
    Then I wait for "Cases" page to load and calculate elapsed time

  Scenario: Check Contacts page loading time
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I wait for "Contacts" page to load and calculate elapsed time

  Scenario: Check Events page loading time
    Given I log in with National User
    And I click on the Events button from navbar
    Then I wait for "Events" page to load and calculate elapsed time

  Scenario: Check Samples page loading time
    Given I log in with National User
    And I click on the Sample button from navbar
    Then I wait for "Samples" page to load and calculate elapsed time

  Scenario: Check Contacts Dashboard page loading time
    Given I log in with National User
    And I click on the Dashboard button from navbar and access Contacts Dashboard
    Then I wait for "Contacts Dashboard" page to load and calculate elapsed time

  Scenario: Check Immunizations page loading time
    Given I log in with National User
    And I click on the Immunizations button from navbar
    Then I wait for "Immunizations" page to load and calculate elapsed time

  @PublishCustomReport
  Scenario: Check Surveillance Dashboard page loading time
    Given I log in with National User
    And I click on the Dashboard button from navbar and access Surveillance Dashboard
    Then I wait for "Surveillance Dashboard" page to load and calculate elapsed time


