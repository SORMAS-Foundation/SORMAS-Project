@UI @Sanity @env_survnet
Feature: Survnet tests

  @tmsLink=SORQA-963 @precon
  Scenario: Test SurvNet Converter installed correctly
    Given I log in as a Admin User
    When I click on the About button from navbar
    Then I check that the Survnet Converter version is not an unavailable on About directory
    And I check that the Survnet Converter version is correctly displayed on About directory

  @tmsLink=SORQA-1031 @precon
  Scenario: Check user login and language settings of SurvNet User in SORMAS
    Given I log in as a Survnet
    And I check that Surveillance Dashboard header is correctly displayed in German language
    When I click on the User Settings button from navbar
    Then I check that Deutsch language is selected in User Settings

  @tmsLink=SORQA-957
  Scenario: Test send simple Case from SORMAS to "Meldesoftware"
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if "date of report" in SORMAS generated XML file is correct
    And I check if sex in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1006
  Scenario: XML Check of simple Test Case from SORMAS to "Meldesoftware"
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I check the SORMAS generated XML file structure with XSD Schema file
    And I compare the SORMAS generated XML file with the example one
    And I click on the About button from navbar
    And I collect SORMAS VERSION from About page
    And I open SORMAS generated XML file for single case message
    And I check if software info in SORMAS generated XML file is correct
    Then I check if "date of report" in SORMAS generated XML file is correct
    And I check if "change at date" in SORMAS generated XML file is correct
    And I check if "tracked at date" in SORMAS generated XML file is correct
    And I check if "created at date" in SORMAS generated XML file is correct
    And I check if sex in SORMAS generated single XML file is correct
    And I check if external person uuid in SORMAS generated XML file is correct

  @tmsLink=SORQA-1011
  Scenario: Automate "Bulk sending cases from SORMAS to "Meldesoftware"
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I collect uuid of the case
    And I navigate to case person tab
    And I collect sex of the person from Edit Person page
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I collect uuid of the case
    And I navigate to case person tab
    And I collect sex of the person from Edit Person page
    And I click on the Cases button from navbar
    Then I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    When I select 2 last created UI result in grid in Case Directory for Bulk Action
    And I click on Bulk Actions combobox on Case Directory Page
    And I click Send to reporting tool button on Case Directory page
    And I click Leave Bulk Edit Mode on Case directory page
    And I filter with first Case ID
    And I click on the first Case ID from Case Directory
    And I collect case external UUID from Edit Case page
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    And I back to the cases list from edit case
    And I filter with second Case ID
    And I click on the first Case ID from Case Directory
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    And I open SORMAS generated XML file for bulk case message
    Then I check if sex for all 2 cases in SORMAS generated bulk XML file is correct
    And I check if external person uuid for all 2 cases in SORMAS generated bult XML file is correct
    And I check if "date of report" for all 2 cases in SORMAS generated bulk XML file is correct

  @tmsLink=SORQA-1027
  Scenario: Calculated age in case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and birth date for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if age computed field in SORMAS generated XML file is correct

  @tmsLink=SORQA-1030
  Scenario: Check Event type Cluster when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Events button from navbar
    And I create a new cluster event for DE version
    And I add only required data for event participant creation for DE
    And I click on the Event participant tab
    And I add only required data for event participant creation for DE
    And I click on the Event participant tab
    And I back to the Event tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect event external UUID from Edit Event page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for event single message
    And I check if event external UUID in SORMAS generated XML file is correct
    And I check if "created at date" in SORMAS generated XML file is correct
    And I check if "tracked at date" in SORMAS generated XML file is correct
    And I check if "created at date" in SORMAS generated XML file is correct