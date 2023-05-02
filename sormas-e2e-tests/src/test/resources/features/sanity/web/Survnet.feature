@UI @Sanity @survnet
Feature: Survnet tests

  @env_survnet
  Scenario: Test SurvNet Converter installed correctly
    Given I log in as a Admin User
    When I click on the About button from navbar