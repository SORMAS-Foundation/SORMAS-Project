@UI @Sanity @SharePersons @SharedData
Feature: Sharing persons between environments tests

  @tmsLink=SORDEV-12088 @env_s2s_1
  Scenario: [S2S] Simultaneous Work on Person [1]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given API: I create a new contact with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district linked to last created case
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    And I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    When I back to tab number 1
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    And I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    When I back to tab number 2
    And I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    And I click on the Contacts button from navbar
    And I open the first contact from contacts list
    Then I open Contact Person tab
    And I change first name of person from Edit person page
    When I back to tab number 2
    Then I navigate to "s2s_2" environment
    And I open the last created contact via API
    Then I open Contact Person tab
    And I check if first name of person has been changed

  @tmsLink=SORDEV-12088 @env_s2s_1
  Scenario: [S2S] Simultaneous Work on Person [2]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    And I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the Cases button from navbar
    And I filter Cases by collected case uuid
    And I click on the first Case ID from Case Directory
    And I navigate to case person tab
    And I change first name of person from Edit person page
    When I back to tab number 1
    Then I navigate to "s2s_1" environment
    And I open the last created case via API and check if Edit case page is read only
    And I navigate to case person tab
    And I check if first name of person has been changed

  @tmsLink=SORDEV-12088 @env_s2s_1
  Scenario: [S2S] Simultaneous Work on Person [3]
    Given I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case data for duplicates merge with for one person data for DE
    And I save a new case
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    And I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And I check if editable fields are read only for shared case/contact
    And I navigate to case person tab
    And I check if editable fields are read only for person case/contact tab
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case data for duplicates merge with for one person data for DE
    And I click on SAVE new case button and choose same person in duplicate detection
    And I choose same case in duplicate detection and save for DE
    And I check if editable fields are read only for shared case/contact
    And I navigate to case person tab
    And I check if editable fields are read only for person case/contact tab
    And I copy uuid of current person
    And I click on the Persons button from navbar
    And I search by copied uuid of the person in Person Directory for DE
    And I click on first person in person directory
    Then I check if editable fields are read only for person
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I fill new case data for duplicates merge with for one person data for DE
    And I click on SAVE new case button and choose same person in duplicate detection
    And I choose create new case in Pick or create entry form for DE
    And I navigate to case person tab
    And I change first name of person from Edit person page
    And I click on the Persons button from navbar
    And I search by copied uuid of the person in Person Directory for DE
    And I click on first person in person directory
    And I check if first name of person has been changed
    When I back to tab number 1
    And I navigate to case person tab
    Then I check if first name of person from case has not been changed

  @tmsLink=SORDEV-12088 @env_s2s_1
  Scenario: [S2S] Simultaneous Work on Person [4]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    And I click on save Contact button
    And I collect contact UUID displayed on Edit Contact Page
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window for DE version
    Then I click SAVE button on Edit Contact Page
    Then I navigate to the last created case via the url
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    And I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    When I back to tab number 1
    And I navigate to Contacts tab in Edit case page
    And I open a contact using the collected contact UUID
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    When I back to tab number 2
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And I check if editable fields are read only for shared case/contact
    And I open Contact Person tab
    And I check if editable fields are read only for person case/contact tab
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    And I click on SAVE new case button and choose same person in duplicate detection
    And I choose same contact in duplicate detection and save for DE
    And I check if editable fields are read only for shared case/contact
    And I open Contact Person tab
    And I check if editable fields are read only for person case/contact tab
    And I copy uuid of current person
    And I click on the Persons button from navbar
    And I search by copied uuid of the person in Person Directory for DE
    And I click on first person in person directory
    Then I check if editable fields are read only for person
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form with same person data with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    And I click on SAVE new case button and choose same person in duplicate detection
    And I choose create new contact in Pick or create entry form for DE
    And I open Contact Person tab
    And I change first name of person from Edit person page
    And I click on the Persons button from navbar
    And I search by copied uuid of the person in Person Directory for DE
    And I click on first person in person directory
    And I check if first name of person has been changed
    When I back to tab number 1
    And I open Contact Person tab
    And I check if first name of person from contact has not been changed