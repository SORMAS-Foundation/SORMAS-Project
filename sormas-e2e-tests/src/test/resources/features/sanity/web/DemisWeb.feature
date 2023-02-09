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

  @tmsLink=SORDEV-9470 @env_d2s @LoginKeycloak
    Scenario Outline: Test [DEMIS2SORMAS] Automatically prefill PathogenTest.testType [1]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification with Loinc code <DEMIS_VALUE>
    And I log in as a National User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person and a new case from received message
    Then I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample
    Then I click on edit pathogen button
    And I check if Demis Value is mapped to "<SORMAS_PATHOGEN_TEST>" and "<SORMAS_PCRTestSpecification>"

    Examples:
    | DEMIS_VALUE        | SORMAS_PATHOGEN_TEST                                    | SORMAS_PCRTestSpecification                 |
    | 95209-3            | Antigen-Nachweistest                                    |                                             |
    | 94307-6            | Nukleinsäure-Nachweis (z.B. PCR)                        |                                             |
    | 94763-0            | Kultur                                                  |                                             |
    | 94558-4            | Antigen Nachweistest (Schnelltest)                      |                                             |
    | 96752-1            | Nukleinsäure-Nachweis (z.B. PCR)                        | Nachweis variantenspezifischer Mutation(en) |
    | 96757-0            | Nukleinsäure-Nachweis (z.B. PCR)                        | Nachweis der Mutation N501Y                 |
    | 94764-8            | Gesamtgenomsequenzierung                                |                                             |
    | 96756-2            | Sonstiges                                               |                                             |
    | 94562-6            | IgA Serum Antikörper                                    |                                             |
    | 94661-6            | Antikörpernachweis                                      |                                             |
    | 94505-5            | IgG Serum Antikörper                                    |                                             |
    | 94506-3            | IgM Serum Antikörper                                    |                                             |
    | 95410-7            | Antikörper-Neutralisationstest                          |                                             |
    | 97097-0            | Antigen-Nachweistest                                    |                                             |

  @tmsLink=SORDEV-8689 @env_d2s @LoginKeycloak
  Scenario: Test Handle New Profile: Process entities related to the same reportId
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I collect first and last name of the person from Laboratory Notification
    And I create and send Laboratory Notification
    And I collect first and last name of the person from Laboratory Notification
    And I create and send Laboratory Notification
    And I collect first and last name of the person from Laboratory Notification
    And I log in as a National User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    And I click on process button for 3 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on YES button in Update case disease variant popup window
    And I click on the Cases button from navbar
    And I click on the first Case ID from Case Directory
    And I check that case created from laboratory message contains a sample with one test
    And I navigate to case person tab
    And I check that first and last name are equal to data form 1 result in laboratory notification
    And I click on the Messages button from navbar
    When I click on process button for 2 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on "discard" button in new sample form with pathogen detection reporting process
    Then I back to message directory
    When I click on process button for 2 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on "save" button in new sample form with pathogen detection reporting process
    And I click on "save" button in new sample form with pathogen detection reporting process
    And I click on YES button in Update case disease variant popup window
    And I verify that status for result 2 is set to processed in Message Directory page
    When I click on process button for 1 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I click on "save and open case" button in new sample form with pathogen detection reporting process
    And I click on YES button in Update case disease variant popup window
    Then I check that I get navigated to the Edit Case page
    And I check that case created from laboratory message contains a sample with one test
    And I navigate to case person tab
    And I check that first and last name are equal to data form 3 result in laboratory notification

  @tmsLink=SORDEV-8689 @env_d2s @LoginKeycloak
  Scenario: Test delete option in Lab Messages
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    Then I create and send Laboratory Notification
    When I log in as a National User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    Then I click on the eye icon next for the first fetched message
    And I collect message uuid
    Then I click Delete button in Message form
    And I confirm message deletion
    And I filter last deleted message
    And I check that number of displayed messages results is 0
    And I click on reset filters button from Message Directory
    And I click on process button for 1 result in Message Directory page
    Then I create a new person and a new case from received message
    Then I click on the eye icon next for the first fetched message
    And I check that the Delete button is not available