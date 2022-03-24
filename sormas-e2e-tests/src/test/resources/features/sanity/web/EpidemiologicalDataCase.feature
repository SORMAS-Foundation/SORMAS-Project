@UI @Sanity @Case @EpidemiologicalData
Feature: Epidemiological data coverage

  @env_main
  Scenario: Edit all fields from Epidemiological data tab
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    When I am accessing via URL the Epidemiological data tab of the created case
    Then I create a new Exposure for Epidemiological data tab and fill all the data
    Then I create a new Activity from Epidemiological data tab and fill all the data
    And I click on save button from Epidemiological Data
    When I am accessing via URL the Epidemiological data tab of the created case
    And I am checking all Exposure data is saved and displayed
    Then I click on discard button from Epidemiological Data Exposure popup
    And I open saved activity from Epidemiological Data
    Then I am checking all Activity data is saved and displayed

    #TODO to be investigated if is defect
    @issue=SORDEV-5522 @env_main @ignore
  Scenario: Validate all fields are present and functional on Epidemiological page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    When I am accessing via URL the Epidemiological data tab of the created case
    And I click on Exposure details known with UNKNOWN option
    And I click on Exposure details known with NO option
    Then I create a new Exposure for Epidemiological data tab and fill all the data
    Then I click on Activity details known with UNKNOWN option
    And I click on Activity details known with NO option
    Then I create a new Activity from Epidemiological data tab and fill all the data
    Then I click on Residing or working in an area with high risk of transmission of the disease with UNKNOWN option
    And I click on Residing or working in an area with high risk of transmission of the disease with NO option
    And I click on Residing or working in an area with high risk of transmission of the disease with YES option
    Then I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with UNKNOWN option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with NO option
    And I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with YES option
    Then I click on Contacts with source case known with UNKNOWN option
    And I click on Contacts with source case known with NO option
    And I click on Contacts with source case known with YES option
    Then I check if Contacts of Source filed is available
    And I click on save button from Epidemiological Data
    When I am accessing via URL the Epidemiological data tab of the created case
    And I am checking if options in checkbox are displayed correctly
    And I am checking all Exposure data is saved and displayed
    Then I click on discard button from Epidemiological Data Exposure popup
    And I open saved activity from Epidemiological Data
    Then I am checking all Activity data is saved and displayed

  @issue=SORDEV-5523 @env_main
  Scenario: Enter an exposure data in Case Directory
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    When I am accessing via URL the Epidemiological data tab of the created case
    And I click on Exposure details known with NO option
    And I click on Exposure details known with UNKNOWN option
    Then I create a new Exposure for Epidemiological data tab and fill all the data
    And I click on edit Exposure vision button
    And I select from Combobox all options in Type of activity field in Exposure for Epidemiological data tab for Cases
    Then I select a Type of activity Other option in Exposure for Epidemiological data tab in Cases
    And I fill a Type of activity details field in Exposure for Epidemiological data tab in Cases
    Then  I select a Type of activity Gathering option in Exposure for Epidemiological data tab in Cases
    And I select from Combobox all Type of gathering in Exposure for Epidemiological data tab in Cases
    And I select a type of gathering Other option from Combobox in Exposure for Epidemiological data tab in Cases
    And I fill a type of gathering details in Exposure for Epidemiological data tab in Cases
    Then I fill Location form for Type of place by chosen "HOME" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on edit Exposure vision button
    And I select Work option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by chosen "OTHER" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on save button from Epidemiological Data
    And I click on edit Exposure vision button
    And I select Travel option in Type of activity from Combobox in Exposure form
    Then I fill Location form for Type of place by chosen "FACILITY" options in Exposure for Epidemiological data
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on save button from Epidemiological Data
    Then I click on Contacts with source case known with UNKNOWN option
    And I click on Contacts with source case known with NO option
    And I click on Contacts with source case known with YES option
    And I click on save button from Epidemiological Data
    And I check if Contacts of Source filed is available
    And I click on the NEW CONTACT button in in Exposure for Epidemiological data tab in Cases
    And I click on the CHOOSE CASE button in Create new contact form in Exposure for Epidemiological data tab in Cases
    And I search and chose the last case uuid created via API in the CHOOSE CASE Contact window
    And I click on SAVE button in create contact form

  @issue=SORDEV-5523 @env_de
  Scenario: Enter an exposure data in Case Directory for DE version
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data for DE version
    And I check case created from created contact is correctly displayed on Edit Case page for DE
    And I navigate to case person tab
    And I navigate to epidemiological data tab in Edit case page
    And I click on Exposure details known with NEIN option
    And I click on Exposure details known with UNBEKANNT option
    And I click on Exposure details known with JA option
    Then I click on New Entry in Exposure Details Known in Cases directory
    And I tick a Probable infection environmental box in Exposure for Epidemiological data tab in Cases
    And I select from Combobox all options in Type of activity field in Exposure for Epidemiological data tab for Cases for DE version
    Then I select a Type of activity Sonstiges option in Exposure for Epidemiological data tab in Cases
    And I fill a Type of activity details field in Exposure for Epidemiological data tab in Cases
    Then I select a Type of activity Versammlung option in Exposure for Epidemiological data tab in Cases
    And I select from Combobox all Type of gathering in Exposure for Epidemiological data tab in Cases for DE version
    And I select a type of gathering Sonstiges option from Combobox in Exposure for Epidemiological data tab in Cases
    And I fill a type of gathering details in Exposure for Epidemiological data tab in Cases
    And I fill Location form for Type of place field by "Unbekannt" options in Case directory for DE version
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on edit Exposure vision button
    And I select Arbeit option in Type of activity from Combobox in Exposure form
    And I fill Location form for Type of place field by "Sonstiges" options in Case directory for DE version
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on edit Exposure vision button
    And I select Arbeit option in Type of activity from Combobox in Exposure form
    And I fill Location form for Type of place field by "Transportmittel" options in Case directory for DE version
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on edit Exposure vision button
    And I select Arbeit option in Type of activity from Combobox in Exposure form
    And I fill Location form for Type of place field by "Einrichtung" options in Case directory for DE version
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I am checking all Location data in Exposure are saved and displayed
    And I click on save button in Exposure for Epidemiological data tab in Cases
    Then I click on New Entry in Exposure Details Known in Cases directory
    And I tick a Probable infection environmental box in Exposure for Epidemiological data tab in Cases
    Then I select a Type of activity Arbeit option in Exposure for Epidemiological data tab in Cases
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on "NEIN" option to close Exposure as the probable infection environment case Popup
    Then I click on Contacts with source case known with NEIN option for DE
    And I click on Contacts with source case known with UNBEKANNT option for DE
    And I click on Contacts with source case known with JA option for DE
    And I click on save button from Epidemiological Data
    And I check if Contacts of Source filed is available
    And I click on the NEW CONTACT button in in Exposure for Epidemiological data tab in Cases
    And I click on the CHOOSE CASE button in Create new contact form in Exposure for Epidemiological data tab in Cases
    And I search and chose the last case uuid created via UI in the CHOOSE CASE Contact window
    And I click on SAVE button in create contact form



