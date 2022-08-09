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

  @tmsLink=SORDEV-5471 @env_main
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

  @tmsLink=SORDEV-5471 @env_main
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

  @tmsLink=SORDEV-5471 @env_main
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

  @tmsLink=SORDEV-10052 @env_main
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

  @tmsLink=SORDEV-10053 @env_main
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


  @env_main @tmsLink=SORDEV-5493
  Scenario: Add a Additional test from Samples and verify the fields
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    And API: I create a new sample
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in as a Admin User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new additional test from the Edit Sample page
    And I complete all fields from Additional test result popup and save
    And I check that the created Additional test is correctly displayed

    @env_main @#8556
  Scenario: Add two positive Pathogen Test Result of different diseases to a Sample of a Contact
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in with National User
    Then I navigate to the last created contact via the url
    Then I click on New Sample
    Then I collect the sample UUID displayed on create new sample page
    Then I create a new Sample with positive test result with COVID-19 as disease
    Then I confirm the Create case from contact with positive test result
    Then I create a new case with specific data for positive pathogen test result
    Then I save the new case
    Then I navigate to the last created contact via the url
    Then I click on edit Sample
    Then I click on new test result for pathogen tests
    Then I create a new pathogen test result with Anthrax as disease
    Then I confirm the Create case from contact with positive test result
    Then I create a new case with specific data for positive pathogen test result
    Then I save the new case
    Then I navigate to the last created contact via the url
    Then I validate only one sample is created with two pathogen tests
    Then I click on edit Sample
    Then I validate the existence of two pathogen tests

  @env_main @#8560
  Scenario: Display date and time for pathogen test result on sample card
    Given API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    When I log in with National User
    Then I navigate to the last created contact via the url
    Then I click on New Sample
    Then I collect the sample UUID displayed on create new sample page
    Then I create a new Sample with positive test result with COVID-19 as disease
    Then I confirm creating a new case
    Then I navigate to the last created contact via the url
    Then I validate date and time is present on sample card

  @tmsLink=SORDEV-5669 @env_main
    Scenario: Add variant specific Nucleic acid detection methods while creating sample
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    And I create new sample with pathogen test with "COVID-19" as disease and "PCR / RT-PCR" as type of test
    Then I set PCR RT PCR Test specification to "Variant specific" option
    Then I set PCR RT PCR Test specification to "N501Y mutation detection" option
    And I save the created sample

  @tmsLink=SORDEV-5669 @env_main
  Scenario: Add variant specific Nucleic acid detection methods after creating sample
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    Then I click on edit Sample
    Then I click on the new pathogen test from the Edit Sample page
    And I create a new pathogen test result with "COVID-19" as disease and "PCR / RT-PCR" as a test type
    Then I set PCR RT PCR Test specification to "Variant specific" option
    Then I set PCR RT PCR Test specification to "N501Y mutation detection" option
    And I save the created sample

  @tmsLink=SORDEV-5669 @env_main
  Scenario: Add variant specific Nucleic acid detection methods
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create new case with COVID-19 and variant "B.1.1.529.5 - BA.5 (Omicron)"
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    Then I click on edit Sample
    Then I click on the new pathogen test from the Edit Sample page
    And I create a new pathogen test result with "COVID-19" as disease and "PCR / RT-PCR" as a test type
    Then I set Tested disease variant as "B.1.617.3"
    And I save the created sample
    And I confirm update case result
    Then I check if Update case disease variant popup is available

  @tmsLink=SORDEV-7427 @env_de
  Scenario: Test Make date fields in sample creation mask and information non-compulsory
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new case
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in with National User
    And I click on the Cases button from navbar
    And I open the last created Case via API
    Then I click on New Sample in German
    And I select Sent dispatched checkbox in new sample page
    And I select Received checkbox in new sample page
    Then I check is Sent dispatched Date and Received Date fields required
    And I click Add Pathogen test in Sample creation page
    And I check DATE AND TIME OF RESULT field
    And I click on save sample button
    Then I check error popup message in German

  @tmsLink=SORDEV-6849 @env_main
  Scenario: Test Lab officers should have full access to entities whose sample was assigned to the lab officers lab
    Given I log in as a Admin User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create new case with COVID-19 and variant "B.1.1.529.5 - BA.5 (Omicron)"
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    Then I create sample with "Voreingestelltes Labor" as a Laboratory
    And I save the created sample
    And I click on logout button from navbar
    Given I log in as a Laboratory Officer
    Then I check if "Cases" tab is available
    And I check if "Contacts" tab is available
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I navigate to case tab
    Then I navigate to case person tab
    And I check if first and last person name for case person tab is correct
    Then I navigate to Contacts tab in Edit case page
    And I click on new contact button from Case Contacts tab
    Then I click on discard button from new task
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I set type of sample to "Sera" on Sample Edit page
    Then I set date sample was collected to yesterday on Sample Edit page

  @tmsLink=SORDEV-10588 @env_main
  Scenario: Test "Specimen condition" should not be mandatory for sample added to case
    Given I log in with National User
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    When I create a new case with specific data
    And I collect the case person UUID displayed on Edit case page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    Then I create sample with "Voreingestelltes Labor" as a Laboratory
    And I save the created sample
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I click on Received checkbox in Sample Edit page
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I click on Save Button in Sample Edit page
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I check if Specimen condition combobox is not mandatory
    And I click on Save Button in Sample Edit page

  @tmsLink=SORDEV-10588 @env_main
  Scenario: Test "Specimen condition" should not be mandatory for sample added to contact
    Given I log in with National User
    And I click on the Contacts button from navbar
    And I click on the NEW CONTACT button
    And I fill a new contact form
    And I click on SAVE new contact button
    And I collect the contact person UUID displayed on Edit contact page
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    Then I create sample with "Voreingestelltes Labor" as a Laboratory
    And I save the created sample
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I click on Received checkbox in Sample Edit page
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I click on Save Button in Sample Edit page
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I check if Specimen condition combobox is not mandatory
    And I click on Save Button in Sample Edit page

  @tmsLink=SORDEV-10588 @env_main
  Scenario: Test "Specimen condition" should not be mandatory for sample added to event
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
    Then I create sample with "Voreingestelltes Labor" as a Laboratory
    And I save the created sample
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I click on Received checkbox in Sample Edit page
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I click on Save Button in Sample Edit page
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I check if Specimen condition combobox is not mandatory
    And I click on Save Button in Sample Edit page

  @tmsLink=SORDEV-10588 @env_main
  Scenario: Test "Specimen condition" should not be mandatory for sample added to case import
    Given I log in as a Admin User
    Then I click on the Cases button from navbar
    And I click on the Import button from Case directory
    And I click on the detailed button from import Case tab
    Then I check is possible to set Value Separator to Semicolon
    Then I select the "Import_specimen_condition.csv" CSV file in the file picker
    And I click on the "START DATA IMPORT" button from the Import Detailed Case popup
    Then I check if csv file for detailed case is imported successfully
    And I search case by user by name "Testung Release"
    Then I click on the first Case ID from Case Directory
    When I click on edit Sample
    Then I check if "Lab sample ID" combobox is available
    And I check if "Date sample received at lab" combobox is available
    And I check if "Specimen condition" combobox is available
    And I check if Specimen condition combobox is not mandatory
    And I click on Save Button in Sample Edit page