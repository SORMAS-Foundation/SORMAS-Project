@UI @Sanity @ShareContacts @SharedData
Feature: Sharing contacts between environments tests

  @tmsLink=SORDEV-13951 @env_s2s_1
  Scenario: S2S - Share a Contact without having a sample
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Then I log in as a S2S
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version with mandatory data with "Baden-Württemberg" as a region and "LK Alb-Donau-Kreis" as a disctrict
    And I click on SAVE new contact button
    And I copy url of current contact
    And I click on share button
    Then I check if popup with "Kontakt kann nicht geteilt werden" title appears
    And I click on okay button
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    Then I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window for DE version
    Then I click SAVE button on Edit Contact Page
    And I click on share button
    And I select organization to share with "s2s_2"
    Then I check if warning information with related to the associated case not being shared appears in share contact popup
    And I click on discard button
    Then I open the Case Contacts tab
    And I navigate to case tab
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to Contacts tab in Edit case page
    And I open the first contact from contacts list
    And I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for contact with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared contact button with copied contact description
    Then I check if a warning pop-up message appears that the Case should be accepted first
    And I click on "accept" shared case button with copied case description
    And I click on "accept" shared contact button with copied contact description
    Then I open last created Case via API on "s2s_2" instance
    And I check that the value selected from Disease combobox is "COVID-19" on Edit Case page

  @tmsLink=SORDEV-13952 @env_s2s_1
  Scenario: S2S - Share a Contact having a sample
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Then I log in as a S2S
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version with mandatory data with "Baden-Württemberg" as a region and "LK Alb-Donau-Kreis" as a disctrict
    And I click on SAVE new contact button
    And I copy url of current contact
    Then I click on New Sample in German
    And I create a new Sample with positive test result for DE version with "Voreingestelltes Labor" as a labor
    And I save the created sample with pathogen test
    And I confirm when a pop-up appears asking user about creating a Case from it in DE
    Then I fill a new case form for DE version with mandatory data forced by positive sample with "Berlin" as a region and "SK Berlin Mitte" as a district
    And I save a new case
    And I collect uuid of the case
    Then I back to contact by url
    And I click on share button
    Then I check if popup with "Kontakt kann nicht geteilt werden" header appears
    And I click on okay button
    Then I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I search for the last case uuid created via Api in the CHOOSE SOURCE Contact window
    And I open the first found result in the CHOOSE SOURCE window for DE version
    Then I click SAVE button on Edit Contact Page
    Then I open the Case Contacts tab
    Then I navigate to case tab
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I open the Case Contacts tab
    And I click on the first Contact ID from Contacts Directory in Contacts in Case
    And I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for contact with random string
    Then I click on share button in s2s share popup
    And I check if popup with error with handover header displays

  @tmsLink=SORDEV-12087 @env_s2s_1 @precon @LanguageRisk
  Scenario: Delete a contact in source system with handing ownership
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
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    Then I navigate to "s2s_1" environment
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I click on the APPLY FILTERS button
    Then I click on the More button on Contact directory page
    And I click Enter Bulk Edit Mode on Contact directory page
    And I click checkbox to choose all Contact results on Contact Directory Page
    And I click on Bulk Actions combobox on Contact Directory Page
    Then I click on Delete button from Bulk Actions Combobox in Contact Directory
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    Then I open the last created contact via API

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a contact in target system with handing ownership
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
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    Then I open the last created contact via API
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_1" environment
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I click on the APPLY FILTERS button
    And I open the first contact from contacts list
    And I check if editable fields are read only for shared contact

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a contact in source system without handing ownership
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
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    And I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I click on the APPLY FILTERS button
    And I open the first contact from contacts list
    And I check if editable fields are read only for shared contact

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a contact in target system without handing ownership
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
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I click on the APPLY FILTERS button
    Then I click on the More button on Contact directory page
    And I click Enter Bulk Edit Mode on Contact directory page
    And I click checkbox to choose all Contact results on Contact Directory Page
    And I click on Bulk Actions combobox on Contact Directory Page
    Then I click on Delete button from Bulk Actions Combobox in Contact Directory
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a contact in source system with handing ownership before acceptance
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given API: I create a new contact with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district linked to last created case
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    And I click on the The Eye Icon located in the Shares Page
    And I check that first shared result has different id then deleted shared contact

  @tmsLink=SOR-4490 @env_s2s_1
  Scenario: Delete a contact in source system with handing ownership and check it in both systems
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
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click to accept potential duplicate in Shares Page
    Then I navigate to "s2s_1" environment
    Then I open the last created contact via API
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I click on the Contacts button from navbar
    Then I open the last created contact via API
    And I check if editable fields are read only for shared contact
    And I check if handover card contains shared with "s2s_2" information
    And I check if handover card contains "Geteilt von: S2S USER" information
    And I check if handover card contains "shared with automated test" information
    Then I navigate to "s2s_2" environment
    Then I open the last created contact via API
    And I check if handover card contains shared with "s2s_1" information
    And I check if handover card contains "Geteilt von: S2S User" information
    And I check if handover card contains "shared with automated test" information

  @tmsLink=SOR-4483 @env_s2s_1
  Scenario: Delete a contact shared in source system but not accepted in target system
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
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I check that accept shared case button with copied case description is visible in Share Directory page
    Then I accept first entity from table in Shares Page
    Then I back to tab number 1
    Then I open the last created contact via API from "s2s_1"
    And I collect uuid of the contact
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for contact with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I check if handover card contains shared with "s2s_2" information
    And I check if handover card contains "Geteilt von: S2S USER" information
    Then I back to tab number 2
    And I refresh current page
    And I check that accept shared contact button with copied contact description is visible in Share Directory page
    Then I back to tab number 1
    Then I click on Delete button from contact
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I back to tab number 2
    Then I click on "accept" shared contact button with copied contact description
    Then I check that entity not found error popup is displayed in Share Directory page