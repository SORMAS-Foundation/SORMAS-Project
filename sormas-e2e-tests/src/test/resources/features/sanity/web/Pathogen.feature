@UI @Sanity @Sample @PathogenTest

Feature: Pathogen Functionalities

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with IgM test type and verify the fields
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And API: I create a new sample
    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    And I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgM test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Four Fold Increase Antibody Titer displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with IgG test type and verify the fields
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And API: I create a new sample
    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgG test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Four Fold Increase Antibody Titer displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with PCR RT PCR test type and verify the fields
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And API: I create a new sample
    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for PCR RT PCR Value Detection test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if PCR RT PCR fields are correctly displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with CQ Value Detection test type and verify the fields
    Given API: I create a new person
    And API: I check that POST call status code is 200
    And API: I create a new case
    And API: I check that POST call status code is 200
    And API: I create a new sample
    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for CQ Value Detection test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if CQ CT Value field is correctly displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with Sequencing test type and verify the fields
    Given API: I create a new person

    And API: I check that POST call status code is 200
    And API: I create a new case

    And API: I check that POST call status code is 200
    And API: I create a new sample

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for Sequencing test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Sequencing or DNA Microarray field is correctly displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with DNA Microarray test type and verify the fields
    Given API: I create a new person

    And API: I check that POST call status code is 200
    And API: I create a new case

    And API: I check that POST call status code is 200
    And API: I create a new sample

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for DNA Microarray test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Sequencing or DNA Microarray field is correctly displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-5492 @env_main
  Scenario: Add a Pathogen test from Samples with Other test type and verify the fields
    Given API: I create a new person

    And API: I check that POST call status code is 200
    And API: I create a new case

    And API: I check that POST call status code is 200
    And API: I create a new sample

    And API: I check that POST call status code is 200
    When I log in as a National User
    And I click on the Sample button from navbar
    And I am opening the last created via API Sample by url navigation
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for Other test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Other field is correctly displayed
    And I delete the Pathogen test

  @tmsLink=SORDEV-8058 @env_main
  Scenario: Unify pathogen test saving logic between cases and contacts
    When API: I create a new person

    And API: I check that POST call status code is 200
    And API: I create a new contact

    And API: I check that POST call status code is 200
    Given I log in as a National User
    Then I open the last created contact via API
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    And I click on edit Sample
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgM test type with positive verified test result
    Then I confirm the Create case from contact with positive test result
    Then I create a new case with specific data for positive pathogen test result
    Then I save a new case
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check if Pathogen test result in Samples is displayed correctly and save

  @tmsLink=SORDEV-8058 @env_main
  Scenario: Unify pathogen test saving logic between cases and event participants
    When API: I create a new person

    And API: I check that POST call status code is 200
    Given API: I create a new event

    And API: I check that POST call status code is 200
    Given I log in as a National User
    When I am accessing the event tab using the created event via api
    Then I add a participant to the event
    Then I check if participant appears in the event participants list
    And I click on the first row from event participant
    And I click on New Sample
    When I collect the sample UUID displayed on create new sample page
    And I create a new Sample with specific data and save
    And I click on edit Sample
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgM test type with positive verified test result
    Then I confirm the Create case from event participant with positive test result
    Then I create a new case with specific data for positive pathogen test result
    Then I save a new case
    And I click on the Sample button from navbar
    And I search for Sample using Sample UUID from the created Sample
    When I open created Sample
    Then I check if Pathogen test result in Samples is displayed correctly and save

    @tmsLink=SORDEV-7092 @env_de
      Scenario: Test Enhance availability of pathogen test information
      Given API: I create a new person

      And API: I check that POST call status code is 200
      And API: I create a new case

      And API: I check that POST call status code is 200
      And API: I create a new sample

      And API: I check that POST call status code is 200
      When I log in as a National User
      And I click on the Sample button from navbar
      And I am opening the last created via API Sample by url navigation
      Then I click on the new pathogen test from the Edit Sample page for DE version
      And I complete all fields from Pathogen test result popup for PCR RT PCR Value Detection test type for DE version and save
      And I check if pathogen test card is correctly displayed for DE version
      Then I navigate to case tab
      And I check if sample card is correctly displayed in case edit tab for DE version
      Then I click on the Sample button from navbar
      And I search after the last created Sample via API
      Then I check if created sample is correctly displayed in samples list