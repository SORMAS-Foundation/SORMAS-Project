@UI @Sanity @ShareCases @SharedData
Feature: Sharing cases between environments tests

  @tmsLink=SOR-4489 @env_s2s_1
  Scenario: [S2S] Delete a shared case
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I check if handover card contains "Dieser Fall ist nicht geteilt" information
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup with "shared to be deleted after"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url
    And I check Delete button from case is enabled
    And Total number of read only fields should be 12
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Gelöschte Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I select first created case for person from Cases list
    Then Total number of read only fields should be 13
    And I check if handover card contains shared with "s2s_2" information
    And I check if handover card contains "Geteilt von: S2S USER" information
    And I check if handover card contains "shared to be deleted after" information
    Then I navigate to "s2s_2" environment
    And I click on the Cases button from navbar
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Aktive Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I select first created case for person from Cases list
    Then I check if editable fields are enabled for the case in view
    And I check if handover card contains shared with "s2s_1" information
    And I check if handover card contains "Geteilt von: S2S User" information
    And I check if handover card contains "shared to be deleted after" information

  @tmsLink=SORDEV-13953 @env_s2s_1
  Scenario: S2S - Share a case that was Archived
    Given I log in as a S2S
    When I click on the Cases button from navbar
    Then I click on the NEW CASE button
    And I fill a new case form for DE version with mandatory data with "Berlin" as a region and "SK Berlin Mitte" as a district
    And I save a new case
    And I collect uuid of the case
    Then I click on the Archive case button and confirm popup
    And I click on save button from Edit Case page
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    When I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I open the last created case with collected UUID by url on "s2s_2" instance
    And I check if Archive button changed name to Abschließen
    Then I back to tab number 1
    And I open the last created case with collected UUID by url on "s2s_1" instance
    And I check if Archive button changed name to Wiedereröffnen

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Add sample and immunization in source system before accept s2s case without hand over the ownership
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    And I click to share samples of the case in Share popup
    And I click on share immunizations of the case in Share popup
    Then I click on share button in s2s share popup and wait for share to finish
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version with "Voreingestelltes Labor" as a labor
    And I save the created sample with pathogen test
    And I confirm case with positive test result
    Then I navigate to case tab
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE with "Comirnaty (COVID-19-mRNA Impfstoff)" as a vaccine name
    And I click SAVE button in new Vaccination form
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I open last created Case via API on "s2s_2" instance
    And I refresh current page
    Then I check if Immunization area contains "Comirnaty (COVID-19-mRNA Impfstoff)"
    Then I check if Immunization area contains "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I click on See samples for this person button
    And I check that number of displayed sample results is 2

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Change sample and immunization in target system without hand over the ownership
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I back to tab number 1
    And I click on edit Sample
    Then I set type of sample to "Blut"
    And I click on Save Button in Sample Edit page
    Then I navigate to case tab
    And I click on first vaccination edit button
    And I set vaccine manufacturer to "Valneva"
    Then I click on save button in New Immunization form
    Then I back to tab number 2
    Then I open last created Case via API on "s2s_2" instance
    And I check if sample card has "Es gibt keine Proben für diesen Fall" information
    And I check if Immunization area contains "Es gibt keine Impfungen für diese Person und Krankheit"

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Add sample and immunization in source system after accept s2s case without hand over the ownership
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I open last created Case via API on "s2s_2" instance
    Then I back to tab number 1
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version with "Voreingestelltes Labor" as a labor
    And I save the created sample with pathogen test
    And I confirm case with positive test result
    Then I navigate to case tab
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE with "Comirnaty (COVID-19-mRNA Impfstoff)" as a vaccine name
    And I click SAVE button in new Vaccination form
    Then I back to tab number 2
    And I refresh current page
    Then I check if Immunization area does not contains "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I check if sample card has "Es gibt keine Proben für diesen Fall" information

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Create sample and immuniation after share the case
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 1
    Then I click on the first Case ID from Case Directory
    Then I check if Immunization area contains "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I click on See samples for this person button
    And I check that number of displayed sample results is 1

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Change date of sample and immuniation
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 1
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample
    Then I set date sample was collected minus 4 days ago on Sample Edit page
    And I click on Save Button in Sample Edit page
    Then I navigate to case tab
    And I click on first vaccination edit button
    Then I change the vaccination date for minus 5 day from today
    And I click SAVE button in new Vaccination form
    Then I back to tab number 1
    And I refresh current page
    And I click on view Sample
    And I check if date of sample is set for 4 day ago from today on Edit Sample page for DE version
    Then I navigate to case tab
    And I click on the Edit Vaccination icon on vaccination card on Edit contact page
    Then I check if vaccination date is set for 5 day ago from today on Edit Vaccination page for DE version

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Add sample and immunization in target system
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 1
    Then I click on the first Case ID from Case Directory
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version with "Testlabor DEMIS" as a labor
    And I save the created sample with pathogen test
    And I confirm case with positive test result
    Then I navigate to case tab
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE with "Comirnaty (COVID-19-mRNA Impfstoff)" as a vaccine name
    And I click SAVE button in new Vaccination form
    Then I back to tab number 1
    And I refresh current page
    Then I check if Immunization area contains "Comirnaty (COVID-19-mRNA Impfstoff)"
    Then I check if Immunization area contains "COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"
    And I click on See samples for this person button
    And I check that number of displayed sample results is 2

  @tmsLink=SORDEV-12095 @env_s2s_1
  Scenario: [S2S] Sample and Immunization - Change sample and immunization in target system
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    Then I click on save button in the case popup
    And I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form for DE
    And I click SAVE button in new Vaccination form
    And I open the last created Case via API
    When I click on New Sample in German
    And I create a new Sample with positive test result for DE version
    And I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup
    And I save the created sample with pathogen test
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I check that number of displayed cases results is 1
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample
    Then I set type of sample to "Blut"
    And I click on Save Button in Sample Edit page
    Then I navigate to case tab
    And I click on first vaccination edit button
    And I set vaccine manufacturer to "Valneva"
    Then I click on save button in New Immunization form
    Then I back to tab number 1
    And I open the last created Case via API
    And I click on first vaccination edit button
    Then I check vaccine manufacturer is set to "Valneva"
    Then I click on save button in New Immunization form
    And I click on edit Sample
    Then I check if type of sample is set to "Blut"

  @tmsLink=SORDEV-12449 @env_s2s_1
  Scenario: S2S_added sample after sharing a case/contact does not get shared [2]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I accept first entity from table in Shares Page
    Then I back to tab number 1
    When I open the Case Contacts tab
    And I click on the first Contact ID from Contacts Directory in Contacts in Case
    And I click on New Sample in German
    And I create a new Sample with only required fields for DE version
    And I click on save sample button
    And I click on share button
    And I select organization to share with "s2s_2"
    And I click to share samples of the case in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I click on New Sample in German
    And I create a new Sample with only required fields for DE version
    And I click on save sample button
    Then I back to tab number 2
    And I click on the Shares button from navbar
    And I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    Then I check that the number of added samples on the Edit case page is 2
    Then I back to tab number 1
    Then I check that the number of added samples on the Edit case page is 2

  @tmsLink=SORDEV-12449 @env_s2s_1
  Scenario: S2S_added sample after sharing a case/contact does not get shared [1]
    Given I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific person name and "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    And I click on save button in the case popup
    And I click on New Sample in German
    And I create a new Sample with only required fields for DE version
    And I click on save sample button
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to share samples of the case in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I click on New Sample in German
    And I create a new Sample with only required fields for DE version
    And I click on save sample button
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    Then I check that the number of added samples on the Edit case page is 2

  @tmsLink=SORDEV-12094 @env_s2s_1
  Scenario: [S2S] Mergen with hand over the ownership - merge for source system
    Given I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific person name and "Hessen" region and "LK Fulda" district for DE version
    Then I click on save button in the case popup
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific person name and "Hessen" region and "LK Fulda" district for DE version
    Then I click on save button in the case popup
    Then I back to tab number 1
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I back to tab number 2
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I click on Okay button in Potential duplicate popup
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    Then I click on Merge button for source system from received case
    And I confirm merge duplicated case
    Then I check if popup with merge message in german appears

  @tmsLink=SORDEV-12094 @env_s2s_1
  Scenario: [S2S] Mergen with hand over the ownership - merge for target system
    Given I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific person name and "Hessen" region and "LK Fulda" district for DE version
    Then I click on save button in the case popup
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific person name and "Hessen" region and "LK Fulda" district for DE version
    Then I click on save button in the case popup
    Then I back to tab number 1
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I back to tab number 2
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I click on Okay button in Potential duplicate popup
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    Then I click on Merge button for target system from received case
    And I confirm merge duplicated case
    Then I check if popup with merge message in german appears

  @tmsLink=SORDEV-12094 @env_s2s_1
  Scenario: [S2S] Mergen without hand over the ownership
    Given I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with mandatory data with person name and "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    Then I click on save button in the case popup
    Then I navigate to "s2s_2" environment in new driver tab
    And I log in as a S2S
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then  I create a new case with mandatory data with person name and "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district for DE version
    Then I click on save button in the case popup
    Then I back to tab number 1
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I back to tab number 2
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I click on Okay button in Potential duplicate popup
    And I click on the Cases button from navbar
    And I click on the More button on Case directory page
    Then I click on Merge Duplicates on Case directory page
    And I click to CONFIRM FILTERS on Merge Duplicate Cases page
    Then I click on Merge button for source system from received case
    And I confirm merge duplicated case
    Then I check if popup with merge duplicated case appears
    And I click on cancel button in merge duplicated cases popup
    Then I click on Merge button for target system from received case
    And I confirm merge duplicated case
    Then I check if popup with merge message in german appears

  @tmsLink=SORDEV-12445 @env_d2s
  Scenario: S2S_Processed lab messages should be transferred [2]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a S2S
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I collect message data from searched record in Messages directory
    And I click on process button for 1 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    Then I click on share button
    And I select organization to share with "s2s_3"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_3" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    Then I filter by last created person via DEMIS API in Case Directory
    And I apply "Zur Ansicht" to ownership combobox on Case Directory Page
    Then I click on the first Case ID from Case Directory
    And I check if edit sample button is unavailable
    Then I back to tab number 1
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample

  @tmsLink=SORDEV-11838 @env_s2s_1
  Scenario: [S2S] Test Avoiding simultaneous work of two health departments - preventing sharing twice to the same target system as long as the target system has not yet accepted or rejected for case with hand over the ownership [1]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I check if share warning is displayed
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_3"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup
    And I check if popup with error with handover header displays
    And I open the last created Case via API
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description

  @tmsLink=SORDEV-11838 @env_s2s_1
  Scenario: [S2S] Test Avoiding simultaneous work of two health departments - preventing sharing twice to the same target system as long as the target system has not yet accepted or rejected for case without hand over the ownership [2]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    And I click to share samples of the case in Share popup
    And I click to share reports of the case in Share popup
    Then I click on share button in s2s share popup and wait for share to finish
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I check if share warning is displayed
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_3"
    And I fill comment in share popup with "shared with automated test"
    And I click to share samples of the case in Share popup
    And I click to share reports of the case in Share popup
    Then I click on share button in s2s share popup
    And I open the last created Case via API
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I back to tab number 1
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    And I click to hand over the ownership in Share popup
    Then I click on share button in s2s share popup and wait for share to finish

  @tmsLink=SORDEV-11838 @env_s2s_1
  Scenario: [S2S] Test Avoiding simultaneous work of two health departments - preventing sharing twice to the same target system as long as the target system has not yet accepted or rejected for contact with hand over the ownership [3]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    And I click to hand over the ownership in Share popup
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to Contacts tab in Edit case page
    And I open the first contact from contacts list
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I check if share warning is displayed
    And I open the last created Case via API
    Then I navigate to Contacts tab in Edit case page
    And I open the first contact from contacts list
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for contact with random string
    And I click to hand over the ownership in Share popup
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared contact button with copied contact description

  @tmsLink=SORDEV-11838 @env_s2s_1
  Scenario: [S2S] Test Avoiding simultaneous work of two health departments - preventing sharing twice to the same target system as long as the target system has not yet accepted or rejected for contact without hand over the ownership [4]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    And I click to share samples of the case in Share popup
    And I click to share reports of the case in Share popup
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to Contacts tab in Edit case page
    And I open the first contact from contacts list
    Then I click on share button
    And I click to share samples of the case in Share popup
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I check if share warning is displayed
    And I open the last created Case via API
    Then I navigate to Contacts tab in Edit case page
    And I open the first contact from contacts list
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for contact with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared contact button with copied contact description
    Then I back to tab number 1
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for contact with random string
    And I click to hand over the ownership in Share popup
    Then I click on share button in s2s share popup and wait for share to finish

  @tmsLink=SORDEV-12447 @env_s2s_1
  Scenario: [S2S] S2S_deactivate share parameter 'share associated contacts' (for cases) [1]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I check that share associated contacts checkbox is not visible in Share form for DE
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on the The Eye Icon located in the Shares Page
    And I check that "KONTAKT-ID" column header is not visible in Share request details window for DE

  @tmsLink=SORDEV-12447 @env_s2s_1
  Scenario: [S2S] S2S_deactivate share parameter 'share associated contacts' (for cases) [2]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Then API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    And I open the last created Case via API
    When I open the Case Contacts tab
    Then I click on new contact button from Case Contacts tab
    And I create a new basic contact to from Cases Contacts tab for DE
    And I open the last created Case via API
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I accept first entity from table in Shares Page
    Then I back to tab number 1
    When I open the Case Contacts tab
    And I click on the first Contact ID from Contacts Directory in Contacts in Case
    And I click on share button
    And I check that share associated contacts checkbox is not visible in Share form for DE
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    And I back to tab number 2
    And I click on the Shares button from navbar
    And I click on the The Eye Icon located in the Shares Page
    And I check that "FALL-ID" column header is not visible in Share request details window for DE

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [1]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "reject" shared case button with copied case description
    Then I fill comment field in Reject share request popup and click confirm
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url
    And I check if reject share case button in Edit Case is unavailable

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [2]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I back to tab number 1
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    And I check if popup with error with handover displays

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [3]
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "reject" shared case button with copied case description
    Then I fill comment field in Reject share request popup and click confirm
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I click on the Shares button from navbar
    And I click on "reject" shared case button with copied case description

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [4]
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
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url without check if uuid is enabled
    And I check if share button is unavailable

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [5]
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
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I back to tab number 1
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I back to tab number 2
    And I click on "accept" shared case button with copied case description
    And I check if popup with revoke error with handover displays

  @tmsLink=SORDEV-12081 @env_s2s_1
  Scenario: Accept Reject Special Cases [6]
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
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I back to tab number 1
    And I click on revoke share button
    Then I click on Ja button in Revoke case popup
    Then I back to tab number 2
    And I click on "reject" shared case button with copied case description
    Then I fill comment field in Reject share request popup and click confirm
    And I check if popup with error with handover displays

  @tmsLink=SORDEV-12445 @env_d2s
  Scenario: S2S_Processed lab messages should be transferred [1]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a S2S
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I collect message data from searched record in Messages directory
    And I click on process button for 1 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    Then I click on share button
    And I click to hand over the ownership in Share popup
    And I select organization to share with "s2s_3"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_3" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the Cases button from navbar
    Then I filter by last created person via DEMIS API in Case Directory
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample
    Then I back to tab number 1
    And I click on the Cases button from navbar
    And I apply "Zur Ansicht" to ownership combobox on Case Directory Page
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I check if edit sample button is unavailable

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a case in source system with handing ownership
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
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Alle aktiven und archivierten Fälle" to combobox on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click checkbox to choose all Case results
    And I click on Bulk Actions combobox on Case Directory Page
    Then I click on Delete button from Bulk Actions Combobox in Case Directory
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    And I click on the Cases button from navbar
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Alle aktiven und archivierten Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I select first created case for person from Cases list
    Then I check if editable fields are enabled for the case in view

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a case in target system with handing ownership
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
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the Cases button from navbar
    And I select first created case for person from Cases list
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_1" environment
    And I click on the Cases button from navbar
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Alle aktiven und archivierten Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I select first created case for person from Cases list
    Then I check if editable fields are enabled for the case in view

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a case in source system without handing ownership
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment
    And I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    And I click on the Cases button from navbar
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Alle aktiven und archivierten Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I select first created case for person from Cases list
    Then I check if editable fields are enabled for the case in view

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a case in target system without handing ownership
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
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
    And I click on the Cases button from navbar
    And I filter by CaseID on Case directory page
    And I apply "Alle" to ownership combobox on Case Directory Page
    And I apply "Alle aktiven und archivierten Fälle" to combobox on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    And I click SHOW MORE FILTERS button on Case directory page
    And I click checkbox to choose all Case results
    And I click on Bulk Actions combobox on Case Directory Page
    Then I click on Delete button from Bulk Actions Combobox in Case Directory
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_1" environment
    Then I navigate to the last created case via the url

  @tmsLink=SORDEV-12087 @env_s2s_1
  Scenario: Delete a case in source system with handing ownership before acceptance
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup with "shared with automated test"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    Then I navigate to "s2s_2" environment
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on the The Eye Icon located in the Shares Page
    Then I check that first shared result has different id then deleted shared case

  @tmsLink=SORQA-478 @env_s2s_1
  Scenario: Test send case to another instance using S2S connection
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
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
    Then I click on the The Eye Icon located in the Shares Page
    And I check if received case id is equal with sent

  @tmsLink=SORQA-981 @env_s2s_1
  Scenario: S2S - Delete a case that was shared but not yet accepted
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    And API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I check that accept shared case button with copied case description is visible in Share Directory page
    Then I back to tab number 1
    And I click on Delete button from case
    And I set Reason for deletion as "Löschen auf Anforderung der betroffenen Person nach DSGVO"
    And I click on Yes option in Confirm deletion popup
    And I apply "Zum Besitz" to ownership combobox on Case Directory Page
    And I apply "Gelöschte Fälle" to combobox on Case Directory Page
    And I click APPLY BUTTON in Case Directory Page
    And I select first created case for person from Cases list
    Then I check if editable fields are enabled for the case in view
    And Total number of read only fields should be 21
    When I back to tab number 2
    And I click on "accept" shared case button with copied case description
    Then I check if Share request not found popup message appeared for DE
    And I click on okay button

  @tmsLink=SORQA-1063 @env_s2s_1
  Scenario: S2S - Share a Case type POINT OF ENTRY
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    And API: I create a new case Point Of Entry type with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    Then I collect uuid of the case
    And I refer case from Point Of Entry with Place of Stay EINRICHTUNG
    And I click on save button from Edit Case page
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the Cases button from navbar
    And I select first created case for person from Cases list
    Then I check that Point Of Entry and Place Of Stay EINRICHTUNG information is correctly display on Edit case page

  @tmsLink=SORQA-1061 @env_s2s_1
  Scenario: S2S - Actions on Shares directory
    Given API: I create a new person with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district
    And API: I check that POST call status code is 200
    And API: I create a new case with "Baden-Württemberg" region and "LK Alb-Donau-Kreis" district and "General Hospital" facility
    And API: I check that POST call status code is 200
    Given I log in as a S2S
    Then I navigate to the last created case via the url
    And I collect uuid of the case
    Then I click on share button
    And I select organization to share with "s2s_2"
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_2" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I pick INCOMING tab on Share Directory page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    Then I check that message popup about Request in not yet accepted is appear
    And I close share request details window
    Then I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    Then I click on the Shares button from navbar
    And I click the header UUID of column
    Then I check that error not appear
    Then I select German Status Dropdown to Accepted
    And I check that Accepted status value is corresponding with entities
    Then I select German Status Dropdown to Pending
    And I check that Pending status value is corresponding with entities
    And I pick OUTGOING tab on Share Directory page
    Then I select German Status Dropdown to Accepted
    And I check that Accepted status value is corresponding with entities
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And I click on the Shares button from navbar
    And I pick OUTGOING tab on Share Directory page
    Then I select German Status Dropdown to Pending
    And I check that Pending status value is corresponding with entities
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And I click on the Shares button from navbar
    And I pick OUTGOING tab on Share Directory page
    Then I select German Status Dropdown to Rejected
    And I check that Rejected status value is corresponding with entities
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And I click on the Shares button from navbar
    And I pick OUTGOING tab on Share Directory page
    Then I select German Status Dropdown to Revoked
    And I check that Revoked status value is corresponding with entities
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case

  @tmsLink=HSP-6268 @env_s2s_2
  Scenario: S2S - Share a case which was sent to Survnet
    Given I log in as a S2S
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with specific data using created facility for Survnet DE
    And I collect uuid of the case
    And I click on Send to reporting tool button on Edit Case page
    Then I click on share button
    And I select organization to share with "s2s_1"
    And I click to hand over the ownership in Share popup
    And I fill comment in share popup with "shared to be deleted after"
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_1" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the Cases button from navbar
    And I open the last created case with collected UUID by url on "s2s_1" instance
    Then I check if editable fields are enabled for the case in view
    And I check if handover card not contains "Status: Ausstehend" shared information
    And I check if handover card not contains "Kommentar: shared to be deleted after" shared information
    When I back to tab number 1
    And I refresh current page
    And Total number of read only fields should be 10
    Then Total number of read only fields in Survnet details section should be 3

  @tmsLink=HSP=6265 @env_d2s @LoginKeycloak
  Scenario: S2S - Share a Case created from processed Lab message with: -"Exclude personal data" -"Share reports"
  Given API : Login to DEMIS server
   Then I create and send Laboratory Notification
    And I log in as a S2S
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person and a new case from received message
    Then I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on edit surveillance report
    Then I collect data from surveillance report
    And I fill comment in surveillance report notification details with random string
    And I click on Save popup button
    Then I click on share button
    And I select organization to share with "s2s_1"
    Then I click to exclude personal data in Share popup
    And I click to share report data in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_1" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    Then I accept first entity from table in Shares Page
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And Total number of read only fields should be 9
    Then I check that the case has no samples on side card for DE
    And I check that that surveillance report has no connected with lab message
    And I click on view surveillance report
    And Total number of read only fields should be 13
    Then I check that data present in target are match to data from source in surveillance report

  @tmsLink=HSP=6343 @env_d2s @LoginKeycloak
    Scenario: S2S - Share a Case created from processed Lab message/Physician Report with option "Share reports"
    #Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a S2S
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person and a new case from received message
    And I verify that status for result 1 is set to processed in Message Directory page
    And I click on the eye icon next for the first fetched message
    And I collect message uuid
    And I close HTML message
    Then I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I collect uuid of the case
    And I click on edit surveillance report
    Then I collect data from surveillance report
    And I click on Discard popup button
    And I click on share button
    And I select organization to share with "s2s_1"
    And I click to share reports of the case in Share popup
    And I fill comment in share popup for case with random string
    Then I click on share button in s2s share popup and wait for share to finish
    Then I navigate to "s2s_1" environment in new driver tab
    Given I log in as a S2S
    And I click on the Shares button from navbar
    And I click on "accept" shared case button with copied case description
    And I click on the The Eye Icon located in the Shares Page
    And I click on the shortened case/contact ID to open the case
    And Total number of read only fields should be 9
    Then I check that the case has no samples on side card for DE
    And I check that that surveillance report has no connected with lab message
    And I click on view surveillance report
    And Total number of read only fields should be 13
    Then I check that data present in target are match to data from source in surveillance report
    And I click on the Messages button from navbar
    And I filter messages by collected uuid
    And I check that number of displayed messages results is 0
    Then I back to tab number 1
    And I click on the Messages button from navbar
    And I filter messages by collected uuid
    And I verify that status for result 1 is set to processed in Message Directory page