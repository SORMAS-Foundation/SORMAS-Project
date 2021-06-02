@Sanity @Sample @bip
Feature: Sample filter functionality

  Scenario: Check Filters on Sample page work as expected
    Given API: I create several new cases with a new sample foreach of them
    Given I log in with the user
    Then I click on the Sample button from navbar