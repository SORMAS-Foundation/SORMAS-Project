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

@tmsLink=SORDEV-8689 @env_d2s @LoginKeycloak
Scenario: Test Handle New Profile: Automatically propose to correct entities related to a reportId [5]
  Given API : Login to DEMIS server
  #step1
  Then I create and send Laboratory Notification
  Then I create and send Laboratory Notification
  And I log in as a National User
  Then I click on the Messages button from navbar
  And I click on fetch messages button
  #step2
  And I click on process button for 2 result in Message Directory page
  And I Pick a new person in Pick or create person popup during case creation
  And I choose create new case in Pick or create entry form
  And I check that case correction popup is displayed
  And I fill only mandatory fields for a new case form
  And I click on save button in the case popup
  And I check that sample correction popup is displayed
  And I click on save sample button
  And I click on YES button in Update case disease variant popup window
  Then I check that case from lab message with sample and pathogen test is created
  #step3
  When I click on process button for 1 result in Message Directory page

  Then I check that correction popup contains cancel button
  And I check that correction popup contains discard and continue button
  And I check that correction popup contains save and continue button
  And I check that correction popup contains eye icon to view the lab message
  And I click on the The Eye Icon located in the correction popup in Messages Directory page
  #step4
