@UI @Sanity @DEMIS @DEMISUI
Feature: Demis UI Tests

@env_d2s @LoginKeycloak
Scenario: Create and send laboratory request via Demis
  Given API : Login to DEMIS server
  Then I create and send Laboratory Notification
  And I log in as a National User
  Then I click on the Messages button from navbar
  And I click on fetch messages button
  And I check if first and last name of patient request sent via Demis are correct

  @tmsLink=SORDEV-7491 @env_d2s @LoginKeycloak
    Scenario: Test [DEMIS2SORMAS] Handle New Profile: Enable SORMAS to relate lab messages to each other [2]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a National User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    And I check if first and last name of patient request sent via Demis are correct
    Then I click on the eye icon next for the first fetched message
    And I check if fetched message has UUID field

@tmsLink=SORDEV-8689 @env_d2s @LoginKeycloak
Scenario: Test Handle New Profile: Automatically propose to correct entities related to a reportId [5]
  Given API : Login to DEMIS server
#  #step1
  Then I create and send Laboratory Notification
  And I collect first and last name of the person from Laboratory Notification
  And I create and send Laboratory Notification
  And I collect first and last name of the person from Laboratory Notification
  And I create and send Laboratory Notification
  And I collect first and last name of the person from Laboratory Notification
  And I log in as a National User
  Then I click on the Messages button from navbar
  And I click on fetch messages button
  #step2
  And I click on process button for 3 result in Message Directory page
  And I pick a new person in Pick or create person popup during case creation for DE
  And I choose create new case in Pick or create entry form for DE
  And I check that create new case form with pathogen detection reporting process is displayed for DE
  And I fill only mandatory fields to convert laboratory message into a case for DE
  And I click on save button in the case popup
  And I check that sample correction popup is displayed
  And I click on save sample button
  And I click on save sample button
  And I click on YES button in Update case disease variant popup window
  And I click on the Cases button from navbar
  And I click on the first Case ID from Case Directory
#  And I check that case created from laboratory message contains a sample with pathogen
  And I navigate to case person tab
  And I check that first and last name are equal to data form 1 result in laboratory notification
#  #step3
  When I click on process button for 2 result in Message Directory page
  And I pick a new person in Pick or create person popup during case creation for DE
  And I choose create new case in Pick or create entry form for DE
  And I check that create new case form with pathogen detection reporting process is displayed for DE
  And I check that popup window contains "discard" button
  And I check that popup window contains "save" button
  And I fill only mandatory fields to convert laboratory message into a case for DE
  And I click on save button in the case popup
  And I check that sample correction popup is displayed
  Then I check that popup window contains "cancel" button
  And I check that popup window contains "discard" button
  And I check that popup window contains "save" button
  And I click on "discard" button in sample correction popup
  Then I back to message directory
  When I click on process button for 2 result in Message Directory page
  And I pick a new person in Pick or create person popup during case creation for DE
  And I choose create new case in Pick or create entry form for DE
  And I check that create new case form with pathogen detection reporting process is displayed for DE
  And I fill only mandatory fields to convert laboratory message into a case for DE
  And I click on save button in the case popup
  And I check that sample correction popup is displayed
  And I click on "save" button in sample correction popup
  And I click on YES button in Update case disease variant popup window
  And I verify that status for result 2 is set to processed in Message Directory page
  When I click on process button for 1 result in Message Directory page
  And I pick a new person in Pick or create person popup during case creation for DE
  And I choose create new case in Pick or create entry form for DE
  And I fill only mandatory fields to convert laboratory message into a case for DE
  And I click on save button in the case popup
  And I click on "save and open case" button in sample correction popup
  And I click on YES button in Update case disease variant popup window
  Then I check that I get navigated to the Edit Case page
