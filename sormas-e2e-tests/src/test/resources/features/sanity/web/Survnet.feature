@UI @Sanity @env_survnet
Feature: Survnet tests

  @tmsLink=SORQA-963 @precon
  Scenario: Test SurvNet Converter installed correctly
    Given I log in as a Admin User
    When I click on the About button from navbar
    Then I check that the Survnet Converter version is not an unavailable on About directory
    And I check that the Survnet Converter version is correctly displayed on About directory