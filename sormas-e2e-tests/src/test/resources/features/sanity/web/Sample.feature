@UI @Sanity @Sample
Feature: Sample Functionalities

  @env_main
  Scenario: Edit a new case Sample
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check the created Sample is correctly displayed on Edit Sample page
    When I change all Sample fields and save
    Then I check the edited Sample is correctly displayed on Edit Sample page

  @issue=SORDEV-5471 @env_main
  Scenario: Edit a new contact Sample
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I collect the contact person UUID displayed on Edit contact page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check the created Sample is correctly displayed on Edit Sample page
    When I change all Sample fields and save
    Then I check the edited Sample is correctly displayed on Edit Sample page

  @issue=SORDEV-5471 @env_main
  Scenario: Edit a new contact Sample with alternate purpose
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I collect the contact person UUID displayed on Edit contact page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with alternate purpose
    And I save the created sample
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check the alternate Sample is correctly displayed on Edit Sample page

  @issue=SORDEV-5471 @env_main
  Scenario: Edit a new event participant Sample
    Given I log in with National User
    And I click on the Events button from navbar
    And I click on the NEW EVENT button
    And I create a new event with specific data
    And I click on the Events button from navbar
    And I search for specific event in event directory
    And I click on the searched event
    And I collect the UUID displayed on Edit event page
    And I add a participant to the event
    And I check if participant appears in the event participants list
    And I click on the created event participant from the list
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check the created Sample is correctly displayed on Edit Sample page
    When I change all Sample fields and save
    Then I check the edited Sample is correctly displayed on Edit Sample page

  @env_main
  Scenario: Add a Pathogen test from Samples and verify the fields
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in with National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    And I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup and save
    Then I check that the created Pathogen is correctly displayed

  @env_main
  Scenario: Delete created sample
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    When I click on the Sample button from navbar
    Then I open the last created sample via API
    Then I delete the sample
    Then I search after the last created Sample via API
    And I check that number of displayed sample results is 0

  @issue=SORDEV-10052 @env_main
  Scenario: Basic export sample
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Sample button from navbar
    Then I fill full name of last created via API Person into Sample Directory
    And I click on apply filters button from Sample Directory
    And I click Export button in Sample Directory
    And I click on Basic Export button in Sample Directory
    And I check if downloaded data generated by basic export option is correct
    Then I delete exported file from Sample Directory

  @issue=SORDEV-10053 @env_main
  Scenario: Detailed export sample
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    When I click on the Sample button from navbar
    Then I fill full name of last created via API Person into Sample Directory
    And I click on apply filters button from Sample Directory
    And I click Export button in Sample Directory
    And I click on Detailed Export button in Sample Directory
    And I check if downloaded data generated by detailed export option is correct
    Then I delete exported file from Sample Directory