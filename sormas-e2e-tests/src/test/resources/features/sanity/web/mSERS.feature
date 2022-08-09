@UI @Sanity @mSERS
Feature: mSERS functionalities

  @tmsLink=SORDEV-11929 @env_main
  Scenario:Allow surveillance officer to create aggregate reports (mSERS) and view reports of their district
    Given I log in as a Surveillance Officer
    When I click on the mSERS button from navbar
    Then I check if Region combobox is set to "Voreingestellte Bundesländer" and is not editable on mSERS directory page
    And I check if District combobox is set to "Voreingestellter Landkreis" and is not editable on mSERS directory page
    When I click on the NEW AGGREGATE REPORT button
    Then I check if Region combobox is set to "Voreingestellte Bundesländer" and is not editable in Create New Aggregate Report popup
    And I check if District combobox is set to "Voreingestellter Landkreis" and is not editable in Create New Aggregate Report popup
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data
    And I click to save aggregated report
    And I navigate to Report data tab
    And I set Epi Year from filter to "2000"
    Then I set Epi week from filter to "Wk 1-2000 (12/27 - 1/2)"
    And I set Epi Year to filter to "2000"
    Then I set Epi week to filter to "Wk 2-2000 (1/3 - 1/9)"
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I set Epi Year from filter to "2003"
    Then I set Epi week from filter to "Wk 1-2003 (12/30 - 1/5)"
    And I set Epi Year to filter to "2003"
    Then I set Epi week to filter to "Wk 2-2003 (1/6 - 1/12)"
    And I click on the APPLY FILTERS button
    And I check that number of results in grid in mSers directory greater than 1
    Then I click to edit 1 result in mSers directory page
    And I check the created data is correctly displayed in new Aggregate Report form
    And I click to delete aggregated report

  @tmsLink=SORDEV-12129 @env_main
  Scenario:Test Allow editing and deletion of aggregate report data
    Given I log in as a Surveillance Officer
    When I click on the mSERS button from navbar
    Then I check if Region combobox is set to "Voreingestellte Bundesländer" and is not editable on mSERS directory page
    And I check if District combobox is set to "Voreingestellter Landkreis" and is not editable on mSERS directory page
    When I click on the NEW AGGREGATE REPORT button
    Then I check if Region combobox is set to "Voreingestellte Bundesländer" and is not editable in Create New Aggregate Report popup
    And I check if District combobox is set to "Voreingestellter Landkreis" and is not editable in Create New Aggregate Report popup
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data
    And I click to save aggregated report
    And I navigate to Report data tab
    And I set Epi Year from filter to "2003"
    Then I set Epi week from filter to "Wk 1-2003 (12/30 - 1/5)"
    And I click on the APPLY FILTERS button
    And I check that number of results in grid in mSers directory greater than 1
    Then I click to edit 1 result in mSers directory page
    And I change all fields of aggregate report
    And I click to save aggregated report
    Then I click to edit 1 result in mSers directory page
    And I check the edited data is correctly displayed in new Aggregate Report form
    And I click to delete aggregated report

  @tmsLink=SORDEV-12129 @env_main
  Scenario:Test Add a view to list aggregate report data and to highlight duplicates
    Given I log in as a Surveillance Officer
    When I click on the mSERS button from navbar
    When I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates
    And I click to save aggregated report
    When I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates
    Then I check if message about duplicated reports is visible
    And I click to save aggregated report
    And I navigate to Report data tab
    And I set Epi Year from filter to "2004"
    Then I set Epi week from filter to "Wk 1-2004 (12/29 - 1/4)"
    And I click on checkbox to display only duplicate reports
    And I click on the APPLY FILTERS button
    Then I check if there are delete and edit buttons for report and duplicates in the grid
    And I delete first duplicated result in grid
    And I click on checkbox to display only duplicate reports
    And I click on the APPLY FILTERS button
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report