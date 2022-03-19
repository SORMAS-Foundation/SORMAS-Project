@UI @Sanity @Contact @Filters
Feature: Contact filter functionality

  @issue=SORDEV-5692 @env_main
  Scenario: Check Contact basic filters on Contact directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I apply Contact classification filter to "Unconfirmed contact" on Contact Directory Page
    And I apply Disease of source filter "COVID-19" on Contact Directory Page
    And I apply Classification of source case filter to "Not yet classified" on Contact Directory Page
    And I apply Follow-up status filter to "Under follow-up" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 1
    Then I filter by mocked ContactID on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Id of last api created Contact on Contact Directory Page
    And I apply Contact classification filter to "Confirmed contact" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Contact classification filter to "Unconfirmed contact" on Contact Directory Page
    And I apply Disease of source filter "Cholera" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Disease of source filter "COVID-19" on Contact Directory Page
    And I apply Classification of source case filter to "Suspect case" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Classification of source case filter to "Not yet classified" on Contact Directory Page
    And I apply Follow-up status filter to "Completed follow-up" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Follow-up status filter to "Completed follow-up" on Contact Directory Page

  @issue=SORDEV-5692 @env_main
  Scenario: Check checkbox filters on Contact directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I click SHOW MORE FILTERS button on Contact directory page
    And I check that number of displayed contact results is 1
    And I click "Quarantine ordered verbally?" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Quarantine ordered verbally?" checkbox on Contact directory page
    And I click "Quarantine ordered by official document?" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Quarantine ordered by official document?" checkbox on Contact directory page
    And I click "No quarantine ordered" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I click "No quarantine ordered" checkbox on Contact directory page
    And I click "Help needed in quarantine" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Help needed in quarantine" checkbox on Contact directory page
    And I click "Only high priority contacts" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Only high priority contacts" checkbox on Contact directory page
    And I click "Only contacts with extended quarantine" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Only contacts with extended quarantine" checkbox on Contact directory page
    And I click "Only contacts with reduced quarantine" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Only contacts with reduced quarantine" checkbox on Contact directory page
    And I click "Only contacts from other instances" checkbox on Contact directory page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Only contacts from other instances" checkbox on Contact directory page

  @issue=SORDEV-5692 @env_main
  Scenario: Check aggregation buttons on Contact directory page
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new contact linked to the previous created case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Contacts button from navbar
    Then I apply Id of last api created Contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I click on All button in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I click on Converted to case pending button on Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click on Active contact button in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I click on Dropped button on Contact Directory Page
    And I check that number of displayed contact results is 0

  @issue=SORQA-5911 @env_de
  Scenario: Check Contact basic filters on Contact directory page for DE version
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version
    And I click SAVE button on Edit Contact Page
    Then I check the created data is correctly displayed on Edit Contact page for DE version
    And I click on BESTÄTIGTER KONTAKT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    When I create a new case for contact with specific data for DE
    Then I check case created from created contact is correctly displayed on Edit Case page for DE
    And I click on the Contacts button from navbar
    And I open the last created contact by UI
    And I click on the CHOOSE SOURCE CASE button from CONTACT page
    And I click yes on the DISCARD UNSAVED CHANGES popup from CONTACT page
    And I search for the last case uuid in the CHOOSE SOURCE window for UI
    And I open the first found result in the CHOOSE SOURCE window for DE version
    Then I click SAVE button on Edit Contact Page
    Then I click on the Contacts button from navbar
    And I apply Contact classification filter to "Bestätigter Kontakt" on Contact Directory Page
    And I apply Disease of source filter "COVID-19" on Contact Directory Page
    And I apply Disease variant filter to "B.1.617.1" on Contact Directory Page
    And I apply Classification of source case filter to "0. Nicht klassifiziert" on Contact Directory Page
    And I apply Follow-up status filter to "Nachverfolgung abgebrochen" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I apply Contact classification filter to "Kein Kontakt" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Contact classification filter to "Bestätigter Kontakt" on Contact Directory Page
    And I apply Disease of source filter "COVID-19" on Contact Directory Page
    And I apply Disease variant filter to "B.1.617.3" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Disease variant filter to "B.1.617.1" on Contact Directory Page
    And I apply Classification of source case filter to "D. Labordiagnostisch bei nicht erfüllter Klinik" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Classification of source case filter to "0. Nicht klassifiziert" on Contact Directory Page
    And I apply Follow-up status filter to "Keine Nachverfolgung" on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I apply Follow-up status filter to "Nachverfolgung abgebrochen" on Contact Directory Page

  @issue=SORQA-5911 @env_de
  Scenario: Check checkbox filters on Contact directory page for DE version
    Given I log in with National User
    When I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form for DE version
    And I click SAVE button on Edit Contact Page
    Then I check the created data is correctly displayed on Edit Contact page for DE version
    And I click on BESTÄTIGTER KONTAKT radio button Contact Person tab
    Then I click SAVE button on Edit Contact Page
    And I click Create Case from Contact button
    When I create a new case for contact with specific data for DE
    Then I check case created from created contact is correctly displayed on Edit Case page for DE
    Then I click on the Contacts button from navbar
    And I apply Id of last created Contact on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I click SHOW MORE FILTERS button on Contact directory page
    And I check that number of displayed contact results is 1
    And I click "Quarantäne mündlich verordnet?" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Quarantäne mündlich verordnet?" checkbox on Contact directory page for DE version
    And I click "Quarantäne schriftlich verordnet?" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Quarantäne schriftlich verordnet?" checkbox on Contact directory page for DE version
    And I click "Keine Quarantäne verordnet" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 1
    And I click "Keine Quarantäne verordnet" checkbox on Contact directory page for DE version
    And I click "Maßnahmen zur Gewährleistung der Versorgung" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Maßnahmen zur Gewährleistung der Versorgung" checkbox on Contact directory page for DE version
    And I click "Nur Kontakte mit hoher Priorität" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Nur Kontakte mit hoher Priorität" checkbox on Contact directory page for DE version
    And I click "Nur Kontakte mit verlängerter Quarantäne" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Nur Kontakte mit verlängerter Quarantäne" checkbox on Contact directory page for DE version
    And I click "Nur Kontakte mit verkürzter Quarantäne" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Nur Kontakte mit verkürzter Quarantäne" checkbox on Contact directory page for DE version
    And I click "Nur Kontakte von anderen Instanzen" checkbox on Contact directory page for DE version
    And I click APPLY BUTTON in Contact Directory Page
    And I check that number of displayed contact results is 0
    And I click "Nur Kontakte von anderen Instanzen" checkbox on Contact directory page for DE version