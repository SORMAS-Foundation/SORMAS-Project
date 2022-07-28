@UI @Sanity @Event @EventImportExport
Feature: Event import and export tests

@tmsLink=SORQA-131 @env_main
Scenario: Basic event export
Given I log in as a Admin User
  Then I click on the Events button from navbar
  And I click on the NEW EVENT button
  Then I create a new event with specific data
  And I click on the Events button from navbar
  Then I search for specific event in event directory
  When I click on the Export Event button
  Then I click on the Basic Event Export button
  And I check if downloaded data generated by basic event export option is correct

  @tmsLink=SORQA-130 @env_main
  Scenario: Detailed event export
    Given I log in as a Admin User
    Then I click on the Events button from navbar
    And I click on the NEW EVENT button
    Then I create a new event with specific data
    And I click on the Events button from navbar
    Then I search for specific event in event directory
    When I click on the Export Event button
    Then I click on the Detailed Event Export button
    And I check if downloaded data generated by detailed event export option is correct

  @tmsLink=SORQA-8047 @env_main
  Scenario: Basic event export with action date check
    Given I log in as a National User
    Then I click on the Events button from navbar
    When I click on the Actions button from Events view switcher
    When I click on the Export Event button
    Then I click on the Basic Event Export button
    Then I check if downloaded data generated by basic event action export option contains actionData

  @tmsLink=SORQA-8047 @env_main
  Scenario: Detailed event export with action date check
    Given I log in as a National User
    Then I click on the Events button from navbar
    When I click on the Actions button from Events view switcher
    When I click on the Export Event button
    Then I click on the Detailed Event Export button
    Then I check if downloaded data generated by detailed event action export option contains actionData

  @tmsLink=SORDEV-9475 @env_de
  Scenario: Test Add reduced vaccination module to imports for Event Participants
    Given API: I create a new event
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Events button from navbar
    Then I open the last created event via api
    And I navigate to EVENT PARTICIPANT from edit event page
    And I click on the Import button from Event Participants directory
    Then I select the "Import_EventParticipants_Vaccinations.csv" CSV file in the file picker
    And I click on the "DATENIMPORT STARTEN" button from the Import Event Participant popup
    Then I click to create new person from the Event Participant Import popup if Pick or create popup appears
    And I check that an import success notification appears in the Import Event Participant popup for DE
    Then I close Import Event Participant form
    And I click on the first row from event participant
    And I check that number of added Vaccinations is 5 on Edit Event Participant Page
    And I click to edit 1 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "08.02.2022"
    And I check that displayed vaccination name is equal to "Sonstiges"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 2 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "07.02.2022"
    And I check that displayed vaccination name is equal to "Unbekannt"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 3 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "06.02.2022"
    And I check that displayed vaccination name is equal to "MRT5500 COVID-19 Impfstoff (Sanofi-GSK)"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 4 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "05.02.2022"
    And I check that displayed vaccination name is equal to "NVX-CoV2373 COVID-19 Impfstoff (Novavax)"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 5 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "04.02.2022"
    And I check that displayed vaccination name is equal to "Ad26.COV2.S (Johnson & Johnson)"
    And I close vaccination form in Edit Event Participant directory
    And I click to navigate to next page in Vaccinations tab
    And I check that number of added Vaccinations is 3 on Edit Event Participant Page
    And I click to edit 1 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "03.02.2022"
    And I check that displayed vaccination name is equal to "Vaxzevria Injektionssuspension COVID-19-Impfstoff (AstraZeneca)"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 2 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "02.02.2022"
    And I check that displayed vaccination name is equal to "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I close vaccination form in Edit Event Participant directory
    And I click to edit 3 vaccination on Edit Event Participant page
    Then I check that displayed vaccination date is equal to "01.02.2022"
    And I check that displayed vaccination name is equal to "Comirnaty (COVID-19-mRNA Impfstoff)"
    And I close vaccination form in Edit Event Participant directory