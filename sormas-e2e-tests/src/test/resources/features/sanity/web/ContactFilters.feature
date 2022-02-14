@UI @Sanity @Contact @Filters
Feature: Contact filter functionality

  @issue=SORDEV-5692
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

  @issue=SORDEV-5692
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

  @issue=SORDEV-5692
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
    And I check that number of displayed contact results is 0
    And I click on Dropped button on Contact Directory Page
    And I check that number of displayed contact results is 0




