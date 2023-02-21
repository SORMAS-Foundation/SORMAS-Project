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
    And I set Epi Year from filter to "2000" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2000 (12/27 - 1/2)" in mSers directory page
    And I set Epi Year to filter to "2000" in mSers directory page
    Then I set Epi week to filter to "Wk 2-2000 (1/3 - 1/9)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I set Epi Year from filter to "2003" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2003 (12/30 - 1/5)" in mSers directory page
    And I set Epi Year to filter to "2003" in mSers directory page
    Then I set Epi week to filter to "Wk 2-2003 (1/6 - 1/12)" in mSers directory page
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
    And I navigate to Report data tab
    And I set Epi Year from filter to "2003" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2003 (12/30 - 1/5)" in mSers directory page
    And I set Epi Year to filter to "2003" in mSers directory page
    Then I set Epi week to filter to "Wk 2-2003 (1/6 - 1/12)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check aggregate reports and delete them if they are listed
    When I click on the NEW AGGREGATE REPORT button
    Then I check if Region combobox is set to "Voreingestellte Bundesländer" and is not editable in Create New Aggregate Report popup
    And I check if District combobox is set to "Voreingestellter Landkreis" and is not editable in Create New Aggregate Report popup
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data
    And I click to save aggregated report
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
    And I navigate to Report data tab
    And I set Epi Year from filter to "2004" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2004 (12/29 - 1/4)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check aggregate reports and delete them if they are listed
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
    And I set Epi Year from filter to "2004" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2004 (12/29 - 1/4)" in mSers directory page
    And I click on checkbox to display only duplicate reports
    And I click on the APPLY FILTERS button
    Then I check if there are delete and edit buttons for report and duplicates in the grid
    And I delete first duplicated result in grid
    And I click on checkbox to display only duplicate reports
    And I click on the APPLY FILTERS button
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report

  @tmsLink=SORDEV-12132 @env_main
  Scenario: Test Limited disease user property should not be applied to mSERS aggregated reporting
    Given I log in as a National User
    When I click on the mSERS button from navbar
    Then I navigate to Report data tab
    And I click on the APPLY FILTERS button
    And I check aggregate reports and delete them if they are listed
    When I click on the NEW AGGREGATE REPORT button
    Then I set Region combobox to "Baden-Württemberg" in Create New Aggregate Report popup
    And I set District combobox to "LK Alb-Donau-Kreis" in Create New Aggregate Report popup
    And I fill a new aggregate report with specific data for one disease
    And I click to save aggregated report
    Then I select ARI (Acute Respiratory Infections) disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Buruli Ulcer disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Chikungunya disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Diarrhea w/ Blood (Shigella) disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Diarrhea w/ Dehydration (< 5) disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Diphteria disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select FHA (Functional Hypothalamic Amenorrhea) disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select HIV disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Leprosy disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Leprosy disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Lymphatic Filariasis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Malaria disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Maternal Deaths disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Neonatal Tetanus disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Non-Neonatal Tetanus disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Onchocerciasis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Perinatal Deaths disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Pertussis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Post-immunization adverse events mild disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Post-immunization adverse events severe disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Post-immunization adverse events severe disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Rubella disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Schistosomiasis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Snake Bite disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Soil-Transmitted Helminths disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Trachoma disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Trypanosomiasis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Tuberculosis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Typhoid Fever disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Yaws and Endemic Syphilis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Yaws and Endemic Syphilis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    Then I select Acute Viral Hepatitis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report

  @tmsLink=SORDEV-12440 @env_main
  Scenario:Add a duplicate warning when creating and editing aggregate reports
    Given I log in as a Surveillance Officer
    When I click on the mSERS button from navbar
    And I navigate to Report data tab
    And I set Epi Year from filter to "2004" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2004 (12/29 - 1/4)" in mSers directory page
    And I set Epi Year to filter to "2004" in mSers directory page
    Then I set Epi week to filter to "Wk 1-2004 (12/29 - 1/4)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check aggregate reports and delete them if they are listed
    When I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates
    And I click to save aggregated report
    When I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates with different disease
    Then I check if message about duplicated reports is visible
    And I click to save aggregated report
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 2
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report
    And I click on the APPLY FILTERS button
    Then I check if there number of results in grid in mSers directory is 0

  @tmsLink=SORDEV-12442 @env_main
  Scenario: Test Prevent submission of future aggregate reports
    Given I log in as a Admin User
    When I click on the mSERS button from navbar
    And I click on the NEW AGGREGATE REPORT button
    And I set report period to "SPECIFY" on Create a new aggregated report form
    And I check if Epi week filed is enabled on Create a new aggregated report form
    Then I check if last listed week from Epi week combobox is the current week of the year

  @tmsLink=SORDEV-12443 @env_main
  Scenario: Check that region and district are required for aggregate reports
    Given I log in as a Admin User
    When I click on the mSERS button from navbar
    And I click on the NEW AGGREGATE REPORT button
    And I set 3 as the quantity for Snake Bite suspected cases in Create a new aggregated report
    And I check that District combobox is disabled in Create New Aggregate Report popup
    And I click to save aggregated report
    Then I check if popup message is "You have to specify a valid region"
    When I close popup message window in Create New Aggregate Report popup
    And I set Region combobox to "Bayern" in Create New Aggregate Report popup
    And I click to save aggregated report
    Then I check if popup message is "You have to specify a valid district"
    When I close popup message window in Create New Aggregate Report popup

  @tmsLink=SORDEV-11693 @env_main
  Scenario: Test Group aggregated reporting data (mSERS) by jurisdiction and epi week
    Given I log in as a Admin User
    When I click on the mSERS button from navbar
    And I navigate to Report data tab
    And I check aggregate reports and delete them if they are listed
    Then I click on aggregate reporting tab
    Then I check that Grouping filter is visible in mSers directory
    And I check that Region filter is visible in mSers directory
    And I check that District filter is visible in mSers directory
    And I check that Facility filter is visible in mSers directory
    And I check that Point of Entry filter is visible in mSers directory
    And I check that Disease filter is visible in mSers directory
    And I check that Epi Year from filter is visible in mSers directory
    And I check that Epi Week from filter is visible in mSers directory
    And I check that Epi Year to filter is visible in mSers directory
    And I check that Epi Week to filter is visible in mSers directory
    Then I check that Disease is visible as a column header in mSers directory
    And I check that Year is visible as a column header in mSers directory
    And I check that Epi Week is visible as a column header in mSers directory
    And I check that Age Group is visible as a column header in mSers directory
    And I check that Suspected cases is visible as a column header in mSers directory
    And I check that Lab confirmations is visible as a column header in mSers directory
    And I check that Deaths is visible as a column header in mSers directory
    Then I select "Region" from Grouping combobox in mSers directory
    And I click on the APPLY FILTERS button
    And I check that Region is visible as a column header in mSers directory
    Then I select "District" from Grouping combobox in mSers directory
    And I click on the APPLY FILTERS button
    And I check that Region is visible as a column header in mSers directory
    And I check that District is visible as a column header in mSers directory
    Then I select "Facility" from Grouping combobox in mSers directory
    And I click on the APPLY FILTERS button
    And I check that Region is visible as a column header in mSers directory
    And I check that District is visible as a column header in mSers directory
    And I check that Health Facility Name is visible as a column header in mSers directory
    Then I select "Point of entry" from Grouping combobox in mSers directory
    And I click on the APPLY FILTERS button
    And I check that Region is visible as a column header in mSers directory
    And I check that District is visible as a column header in mSers directory
    And I check that Point Of Entry Name is visible as a column header in mSers directory
    Then I select "Region" from Grouping combobox in mSers directory
    When I click on the NEW AGGREGATE REPORT button
    And I fill a new aggregate report with "aabcöäüp" to provoke error and check error message
    And I fill a new aggregate report with "'§?=-,." to provoke error and check error message
    Then I set Region combobox to "Baden-Württemberg" in Create New Aggregate Report popup
    And I set District combobox to "LK Alb-Donau-Kreis" in Create New Aggregate Report popup
    And I fill a new aggregate report with specific data for one disease
    And I click to save aggregated report
    And I check if there number of results in grid in mSers directory is 1
    Then I click Show 0-rows for grouping checkbox
    And I click on the APPLY FILTERS button
    And I check that number of results in grid in mSers directory greater than 1
    Then I click Show 0-rows for grouping checkbox
    And I select Baden-Württemberg from Region combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    And I select Bayern from Region combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I select Baden-Württemberg from Region combobox in mSers directory page
    And I select LK Alb-Donau-Kreis from District combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    And I select Baden-Württemberg from Region combobox in mSers directory page
    And I select LK Biberach from District combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I select Voreingestellte Bundesländer from Region combobox in mSers directory page
    And I select Voreingestellter Landkreis from District combobox in mSers directory page
    And I select Standard Einrichtung from Facility combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I click on the RESET FILTERS button from Event
    And I select Voreingestellte Bundesländer from Region combobox in mSers directory page
    And I select Voreingestellter Landkreis from District combobox in mSers directory page
    And I select Voreingestellter Flughafen from Point Of Entry combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I click on the RESET FILTERS button from Event
    Then I select Acute Viral Hepatitis disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    Then I select Buruli Ulcer disease from Disease combobox in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I click on the RESET FILTERS button from Event
    And I set Epi Year from filter to "2000" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2000 (12/27 - 1/2)" in mSers directory page
    And I set Epi Year to filter to "2000" in mSers directory page
    Then I set Epi week to filter to "Wk 2-2000 (1/3 - 1/9)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 0
    And I click on the RESET FILTERS button from Event
    And I click to Export aggregate report
    And I navigate to Report data tab
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report
    And I check if exported aggregate report for last created report is correct
    And I delete exported report

  @tmsLink=SORDEV-12130 @env_main
  Scenario: Verify that the Aggregate Report View does not sum the Aggregate Report Numbers with those of sub jurisdictions
    Given I log in as a Admin User
    When I click on the mSERS button from navbar
    And I navigate to Report data tab
    And I set Epi Year from filter to "2005" in mSers directory page
    Then I set Epi week from filter to "Wk 6-2005 (1/31 - 2/6)" in mSers directory page
    And I set Epi Year to filter to "2005" in mSers directory page
    And I set Epi week to filter to "Wk 6-2005 (1/31 - 2/6)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check aggregate reports and delete them if they are listed
    And I click on aggregate reporting tab
    And I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates with jurisdiction
    And I click to save aggregated report
    And I set Epi Year from filter to "2005" in mSers directory page
    Then I set Epi week from filter to "Wk 6-2005 (1/31 - 2/6)" in mSers directory page
    And I set Epi Year to filter to "2005" in mSers directory page
    And I set Epi week to filter to "Wk 6-2005 (1/31 - 2/6)" in mSers directory page
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    And I check if displayed numbers of suspected cases are equal to those previously entered for first result in mSers directory page
    When I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates with jurisdiction
    Then I check if message about duplicated reports is visible
    And I click to save aggregated report
    And I check if there number of results in grid in mSers directory is 1
    And I check if displayed numbers of suspected cases are equal to those previously entered for first result in mSers directory page
    And I click on the NEW AGGREGATE REPORT button
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific data for duplicates with jurisdiction
    And I click to save aggregated report
    And I check if there number of results in grid in mSers directory is 1
    And I check if displayed numbers of suspected cases are equal to those previously entered for first result in mSers directory page
    And I navigate to Report data tab
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 3
    Then I check if there are delete and edit buttons for report and duplicates in the grid
    And I delete first duplicated result in grid
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 2
    And I delete first duplicated result in grid
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 1
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report
    And I click on the APPLY FILTERS button
    Then I check if there number of results in grid in mSers directory is 0

  @tmsLink=SORDEV-11692 @env_main
  Scenario: Addition of age categories to aggregate module (mSERS)
    Given I log in as a Admin User
    When I click on the mSERS button from navbar
    And I navigate to Report data tab
    And I set Epi Year from filter to "2012" in mSers directory page
    Then I set Epi week from filter to "Wk 1-2012 (12/26 - 1/1)" in mSers directory page
    And I set Epi Year to filter to "2012" in mSers directory page
    And I set Epi week to filter to "Wk 1-2012 (12/26 - 1/1)" in mSers directory page
    And I click on the APPLY FILTERS button
    Then I check aggregate reports and delete them if they are listed
    And I click on the NEW AGGREGATE REPORT button
    And I check if age groups are visible for "Acute Viral Hepatitis"
    And I check if age groups are visible for "HIV"
    And I check if age groups are visible for "Malaria"
    Then I click on SPECIFY Radiobutton in Create Aggregated Report form
    And I fill a new aggregate report with specific age groups
    And I click to save aggregated report
    And I click on the APPLY FILTERS button
    And I check if there number of results in grid in mSers directory is 3
    And I check that Age group for 1 result in grid in mSers directory is "16+ years"
    And I check that Age group for 2 result in grid in mSers directory is "0-28 days"
    And I check that Age group for 3 result in grid in mSers directory is "3-12 months"
    And I click to Export aggregate report
    And I navigate to Report data tab
    Then I click to edit 1 result in mSers directory page
    And I click to delete aggregated report
    And I check if exported aggregate report contains correct age groups
    And I delete exported report