@Sanity @Sample
Feature: Create Sample

  Scenario: Create a new Sample
    Given I log in with the user
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check the created Sample is correctly displayed on Edit Sample page
    When I change all Sample fields and save
    And I click on the Cases button from navbar
    And Search for Case using Case UUID from the created Task
    When I open last created case
    When I click on edit Sample
    Then I check the created Sample is correctly displayed on Edit Sample page

  Scenario: Add a Pathogen test from Samples and verify the fields
    Given API: I create a new person
    And API: I create a new case
    And API: I create a new sample
    When I log in with the user
    And I am accessing the Sample page using the created Sample via api
    And I click on the new pathogen test from the Edit Sample page