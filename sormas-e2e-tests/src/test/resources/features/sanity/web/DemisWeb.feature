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

  @tmsLink=SORDEV-8689 @env_d2s @LoginKeycloak @precon @LanguageRisk
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
    And I filter by the name of the 1 most recently created person in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I click on the first Case ID from Case Directory
    And I check that case created from laboratory message contains a sample with one test
    And I navigate to case person tab
    And I check that first and last name are equal to data form 1 result in laboratory notification
    And I click on the Messages button from navbar
    And I click on the RESET FILTERS button for Messages
    And I filter by the name of the 2 most recently created person in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on "discard" button in new sample form with pathogen detection reporting process
    Then I back to message directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on "save" button in new sample form with pathogen detection reporting process
    And I click on "save" button in new sample form with pathogen detection reporting process
    And I verify that status for result 1 is set to processed in Message Directory page
    And I click on the RESET FILTERS button for Messages
    And I filter by the name of the 3 most recently created person in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    And I click on "save and open case" button in new sample form with pathogen detection reporting process
    Then I check that I get navigated to the Edit Case page
    And I check that case created from laboratory message contains a sample with one test
    And I navigate to case person tab
    And I check that first and last name are equal to data form 3 result in laboratory notification

  @tmsLink=SORDEV-8862 @env_d2s @LoginKeycloak
  Scenario: Test [DEMIS2SORMAS] Add filters and columns to the lab message directory
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a National User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I collect message data from searched record in Messages directory
    Then I click on the RESET FILTERS button for Messages
    And I search created message by UUID
    Then I check if searched message has correct UUID
    And I check that number of displayed messages results is 1
    Then I click on the RESET FILTERS button for Messages
    And I search created message by laboratory name
    Then I check if searched message has correct laboratory name
    Then I click on the RESET FILTERS button for Messages
    And I search created message by laboratory postal code
    And I check if searched message has correct laboratory postal code
    Then I click on the RESET FILTERS button for Messages
    And I search created message by date and time
    Then I check if searched message has correct date and time
    Then I click on the RESET FILTERS button for Messages
    And I search created message by birthday date
    Then I check if searched message has correct birthday date

  @tmsLink=SORDEV-5588 @env_d2s @LoginKeycloak @testIt
  Scenario: Test delete option in Lab Messages
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I collect first and last name of the person from Laboratory Notification
    And I create and send Laboratory Notification
    And I collect first and last name of the person from Laboratory Notification
    When I log in as a National User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by the name of the 1 most recently created person in Messages Directory
    Then I click on the eye icon next for the first fetched message
    And I collect message uuid
    Then I click Delete button in Message form
    And I confirm message deletion
    And I refresh current page
    And I check that number of displayed messages results is 0
    And I click on reset filters button from Message Directory
    And I filter by the name of the 2 most recently created person in Messages Directory
    And I click on process button for 1 result in Message Directory page
    Then I create a new person and a new case from received message
    Then I click on the eye icon next for the first fetched message
    And I check that the Delete button is not available

  @tmsLink=SORDEV-5590 @env_d2s @LoginKeycloak
  Scenario: [DEMIS Interface] Introduce option to reject lab messages
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    When I log in as a National User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    Then I click on the eye icon next for the first fetched message
    And I collect message uuid
    And I click on the Mark as unclear button
    And I confirm popup window
    And I filter messages by "Unclear" in Message Directory
    And I filter messages by collected uuid
    And I check that number of displayed messages results for Unklar is 1
    And I click on reset filters button from Message Directory
    Then I click on the eye icon next for the first fetched message
    And I click on the Mark as a forwarded button
    And I confirm popup window
    Then I filter messages by "Forwarded" in Message Directory
    And I filter messages by collected uuid
    And I check that number of displayed messages results for Weitergeleitet is 1
    Then I click on reset filters button from Message Directory

  @tmsLink=SORDEV-5189 @env_d2s @LoginKeycloak
  Scenario: Test improvement of the mapping/prefilling the new sample forms when processing a DEMIS LabMessage
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification with patient's phone and email
    When I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    Then I click on process button for 1 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I check if "email address" is prefilled in New case form while processing a DEMIS LabMessage
    And I check if "phone number" is prefilled in New case form while processing a DEMIS LabMessage
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I check if "specimen condition" is prefilled in New sample form while processing a DEMIS LabMessage
    And I check if "specimen condition" is set to "Ausreichend"
    And I check if "tested disease" is prefilled in New sample form while processing a DEMIS LabMessage
    And I check if "tested disease" is set to "COVID-19"
    And I check if "test result" is prefilled in New sample form while processing a DEMIS LabMessage
    And I check if "test result verified" is prefilled in New sample form while processing a DEMIS LabMessage
    And I check if "test result verified" is set to "JA"

  @tmsLink=SORDEV-6171 @env_d2s @LoginKeycloak
  Scenario: Test [DEMIS2SORMAS] Prefill SampleMaterial
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I collect message data from searched record in Messages directory
    And I click on process button for 1 result in Message Directory page
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check if sample material has a option "Nasen-Abstrich"
    And I check if sample material has a option "Oropharynx-Aspirat"
    And I check if sample material has a option "Oropharynx-Aspirat"
    And I check if sample material has a option "Nasopharynx-Abstrich"
    And I check if sample material has a option "Pleuralflüssigkeitsprobe"

  @tmsLink=SORDEV-5629 @env_d2s @LoginKeycloak
  Scenario: [4841] [Sormas@DEMIS] Add columns in Lab Massage Directory [0.5]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I collect message data from searched record in Messages directory
    Then I check if "laboratory name" in received message is set to "Testlabor"
    And I check if "laboratory postal code" in received message is set to "12347"
    And I check if postal code for test instance in received message is set correctly

  @tmsLink=SORDEV-8810 @env_d2s @LoginKeycloak
  Scenario: [DEMIS2SORMAS] Test messages directory status quick filters
    Given I log in as a Admin User
    When I click on the Messages button from navbar
    Then I check that "Alle" quick filter button is selected in Message directory page
    When I click on "Unverarbeitet" quick filter above the messages in Message directory page
    Then I check that "Unverarbeitet" quick filter button is selected in Message directory page
    And I check that the Status column is filtered by "Unverarbeitet" on Message directory page
    When I click on "Verarbeitet" quick filter above the messages in Message directory page
    Then I check that "Verarbeitet" quick filter button is selected in Message directory page
    And I check that the Status column is filtered by "Verarbeitet" on Message directory page
    When I click on "Unklar" quick filter above the messages in Message directory page
    Then I check that "Unklar" quick filter button is selected in Message directory page
    And I check that the Status column is filtered by "Unklar" on Message directory page
    When I click on "Weitergeleitet" quick filter above the messages in Message directory page
    Then I check that "Weitergeleitet" quick filter button is selected in Message directory page
    And I check that the Status column is filtered by "Weitergeleitet" on Message directory page

    @tmsLink=SORDEV-9104 @env_d2s @LoginKeycloak
    Scenario: Test [DEMIS2SORMAS] Add new userflow for lab message processing [1]
      Given API : Login to DEMIS server
      Then I create and send Laboratory Notification
      And I log in as a Admin User
      Then I click on the Messages button from navbar
      And I click on fetch messages button
      Then I filter by last created person via API in Messages Directory
      And I click on Verarbeiten button in Messages Directory
      Then I create a new person and a new case from received message
      Then I click on the Cases button from navbar
      And I search the case by last created person via Demis message
      Then I click on the first Case ID from Case Directory

  @tmsLink=SORDEV-9104 @env_d2s @LoginKeycloak
  Scenario: Test [DEMIS2SORMAS] Add new userflow for lab message processing [2]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person from received message
    And I create a new contact form received message
    Then I check if contact tab was opened after create new contact from message

  @tmsLink=SORDEV-9104 @env_d2s @LoginKeycloak
  Scenario: Test [DEMIS2SORMAS] Add new userflow for lab message processing [3]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person from received message
    And I create a new event participant form received message
    Then I check if event participant tab was opened after create new contact from message

  @tmsLink=SORDEV-13372 @env_d2s @LoginKeycloak
  Scenario: [DEMIS2SORMAS] Adjust surveillance reports to also represent laboratory reports
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person and a new case from received message
    Then I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I check if report side component in Edit Case has "Labor"
    And I check if report side component in Edit Case has "Testlabor DEMIS"
    And I check if report side component in Edit Case has today date

  @tmsLink=SORDEV-6170 @env_d2s @LoginKeycloak
  Scenario: Test [Lab Message] Make person first- and lastName editable when processing a lab message for case
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I check if while creating new case from demis message there is a possibility to edit first and last name

  @tmsLink=SORDEV-6170 @env_d2s @LoginKeycloak
  Scenario: Test [Lab Message] Make person first- and lastName editable when processing a lab message for contact
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I check if while creating new contact from demis message there is a possibility to edit first and last name

  @tmsLink=SORDEV-6170 @env_d2s @LoginKeycloak
  Scenario: Test [Lab Message] Make person first- and lastName editable when processing a lab message for event participant
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I check if while creating new event participant from demis message there is a possibility to edit first and last name

  @tmsLink=SORQA-959 @env_d2s @LoginKeycloak
  Scenario: Test [Lab Message] Demis - Process a Lab message that has no mapped ID for Facility in Sormas
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with other facility name "Other Laboratory" and facility ID "928170"
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    Then I verify that labor is prefilled with "Andere Einrichtung" in New sample form while processing a DEMIS LabMessage
    And I verify that labor description is prefilled with "Other Laboratory" in New sample form while processing a DEMIS LabMessage
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on edit Sample
    Then I check that laboratory is set to "Andere Einrichtung" on Edit Sample page
    And I check that laboratory details is set to "Other Laboratory" on edit Sample page
    When I navigate to case tab
    Then I check if report side component in Edit Case has today date
    When I click on edit Report on Edit Case page
    Then I check that Reporter Facility in Edit report form is set to "Andere Einrichtung (Inaktiv)"
    And I check that Reporter Facility Details in Edit report form is set to "Other Laboratory"

  @tmsLink=SORQA-960 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Lab message that has mapped 2 existing laboratory IDs from Sormas
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with two different facilities
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I check that laboratory is set to "" on Edit Sample page
    And I select "Testlabor DEMIS" as a Laboratory in New sample form while processing a DEMIS LabMessage
    And I select "Andere Einrichtung" as a Laboratory for pathogen in New sample form while processing a DEMIS LabMessage
    And I click on save sample button
    And I click on save sample button
#    Then I check that new sample form with pathogen detection reporting process is displayed
#    And I select "Testlabor DEMIS" as a Laboratory in New sample form while processing a DEMIS LabMessage
#    And I click on save sample button
#    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on Display associated lab messages button from Samples side component
    And I check if external message window appears and close it
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    When I click on edit Report on Edit Case page
    Then I check that Reporter Facility in Edit report form is set to ""
