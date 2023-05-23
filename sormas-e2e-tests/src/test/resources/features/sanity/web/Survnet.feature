@UI @Sanity @env_survnet
Feature: Survnet tests

  @tmsLink=SORQA-963 @precon
  Scenario: Test SurvNet Converter installed correctly
    Given I log in as a Admin User
    When I click on the About button from navbar
    Then I check that the Survnet Converter version is not an unavailable on About directory
    And I check that the Survnet Converter version is correctly displayed on About directory

  @tmsLink=SORQA-957
  Scenario: Test send simple Case from SORMAS to "Meldesoftware"
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    Then I wait 60 seconds for system reaction
    Then I check if date of report in SurvNet generated XML file is correct
    And I check if sex in SurvNet generated XML file is correct