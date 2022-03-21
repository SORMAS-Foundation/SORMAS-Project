@UI @Sanity @TravelEntries
Feature: Create travel entries

  @issue=SORDEV-8266 @env_de
  Scenario: Upload Travel Entry CSV
    Given I log in as a National User
    And I click on the Entries button from navbar
    When I click on the Import button from Travel Entries directory
    And I select the German travel entry CSV file in the file picker
    And I click on the START DATA IMPORT button from the Import Travel Entries popup
    And I select to create new person from the Import Travel Entries popup
    And I confirm the save Travel Entries Import popup
    Then I check that an import success notification appears in the Import Travel Entries popup

  @issue=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry
    Given I log in as a National User
    And I click on the Entries button from navbar
    And I click on the New Travel Entry button from Travel Entries directory
    When I fill the required fields in a new travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit travel entry page for DE version
    And I navigate to person tab in Edit travel entry page
    And I check the created data is correctly displayed on Edit travel entry person page for DE version

  @issue=SORDEV-8266 @env_de
  Scenario: Create a Travel Entry through case view
    Given I log in as a National User
    And I click on the Cases button from navbar
    And I open last created case
    And I navigate to epidemiological data tab in Edit case page
    And I click on the New Travel Entry button from Edit case page
    When I fill the required fields in a new case travel entry form
    And I click on Save button from the new travel entry form
    Then I check the created data is correctly displayed on Edit case travel entry page for DE version

    @issue=SORDEV-8268 @env_de
    Scenario: Create a case for a travel entry
      Given I log in as a National User
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form
      And I click on Save button from the new travel entry form
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      When I click on new case button for travel entry
      Then I check if data from travel entry for new case is correct
      And I save the new case for travel entry
      Then I check if data in case based on travel entry is correct
      Then I navigate to epidemiological data tab in Edit case page
      And I click on edit travel entry button form case epidemiological tab
      Then I check the created data is correctly displayed on Edit travel entry page for DE version
      And I check if first and last person name for case in travel entry is correct
      And I click on the Entries button from navbar
      And I click on the New Travel Entry button from Travel Entries directory
      When I fill the required fields in a new travel entry form for previous created person
      And I click on Save button from the new travel entry form
      Then I check if pick or create person popup in travel entry is displayed
      Then I check Pick an existing case in Pick or create person popup in travel entry
      And I click confirm button in popup from travel entry
      When I click on new case button for travel entry
      Then I choose an existing case while creating case from travel entry
      And I click confirm button in popup from travel entry
      Then I navigate to epidemiological data tab in Edit case page
      And I check if created travel entries are listed in the epidemiological data tab