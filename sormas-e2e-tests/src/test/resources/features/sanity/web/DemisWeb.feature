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