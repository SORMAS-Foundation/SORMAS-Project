@UI @Sanity @Sample @PathogenTest
Feature: Sample Functionalities

  Scenario: Edit a new Sample
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

  Scenario: Add a Pathogen test from Samples with IgM test type and verify the fields
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
    And I am accessing the created sample via api
    And I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgM test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Four Fold Increase Antibody Titer displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with IgG test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for IgG test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Four Fold Increase Antibody Titer displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with PCR RT PCR test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for PCR RT PCR Value Detection test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if PCR RT PCR fields are correctly displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with CQ Value Detection test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for CQ Value Detection test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if CQ CT Value field is correctly displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with Sequencing test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for Sequencing test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Sequencing or DNA Microarray field is correctly displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with DNA Microarray test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for DNA Microarray test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Sequencing or DNA Microarray field is correctly displayed
    And I delete the Pathogen test

  Scenario: Add a Pathogen test from Samples with Other test type and verify the fields
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
    And I am accessing the created sample via api
    Then I click on the new pathogen test from the Edit Sample page
    And I complete all fields from Pathogen test result popup for Other test type and save
    Then I check that the created Pathogen is correctly displayed
    And I check that if Other field is correctly displayed
    And I delete the Pathogen test

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