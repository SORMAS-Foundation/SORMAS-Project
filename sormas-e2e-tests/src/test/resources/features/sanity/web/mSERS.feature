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
    And I check if there number of results in grid in mSers directory is 0
    And I set Epi Year filter to "2003"
    Then I set Epi week from filter to "Wk 1-2003 (12/30 - 1/5)"
    And I click on the APPLY FILTERS button
    And I check that number of results in grid in mSers directory greater than 1
    Then I click to edit 1 result in mSers directory page
    And I check the created data is correctly displayed in new Aggregate Report form
    And I click to delete aggregated report