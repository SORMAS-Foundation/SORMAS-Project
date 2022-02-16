@UI @Sanity @WeeklyReports
Feature: Reports

  @Locale_DE
  Scenario: Reports directory layout
    Given I log in with National User
    When I click on the Reports button from navbar
    Then I check that all filter components for weekly reports are shown
    Then I check that info icon for weekly reports is shown
    Then I check that grid for weekly reports is shown
    Then I check that header names of grid for weekly reports are shown

  @Locale_DE
  Scenario: Reports filter work
    Given I log in with National User
    When I click on the Reports button from navbar
    When I choose "2020" as year for weekly reports
    Then I check that grid for weekly reports is shown
    When I choose "Wk 49-2020 (11/30 - 12/6)" as epi week for weekly reports
    Then I check that grid for weekly reports is shown
    When I click on the last epi week button for weekly reports
    Then I check that grid for weekly reports is shown

