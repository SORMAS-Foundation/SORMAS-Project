@UI @Sanity @DEMIS @DEMISUI @ignore
Feature: Demis UI Tests

@env_d2s @LoginKeycloak
Scenario: Create and send laboratory request via Demis
  Given API : Login to DEMIS server
  Then I create and send Laboratory Notification
  And I log in as a National User
  Then I click on the Messages button from navbar
  And I click on fetch messages button
  And I filter by last created person via API in Messages Directory
  And I check if first and last name of patient request sent via Demis are correct

  @tmsLink=SORDEV-7491 @env_d2s @LoginKeycloak
    Scenario: Test [DEMIS2SORMAS] Handle New Profile: Enable SORMAS to relate lab messages to each other [2]
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I log in as a National User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
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

  @tmsLink=SORDEV-5588 @env_d2s @LoginKeycloak
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
    And I filter by last created person via API in Messages Directory
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
    Then I check that Reporter Facility in Edit report form is set to "Andere Einrichtung"
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
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on Display associated lab messages button from Samples side component
    And I check if external message window appears and close it
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    When I click on edit Report on Edit Case page
    Then I check that Reporter Facility in Edit report form is set to ""

  @tmsLink=SORQA-905 @env_d2s @LoginKeycloak
  Scenario: Demis - Map no disease variant on the case if lab message contains more than 1 positive pathogen test
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with two positive pathogens
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I check if disease variant field for first record displays "" in Message Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I check if disease variant field is "" in New case form while processing a DEMIS LabMessage
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I verify that disease variant for "first" pathogen is prefilled with "B.1.1.7 - 501Y.V1 (Alpha)" in New Sample form while processing a DEMIS LabMessage
    And I verify that disease variant for "second" pathogen is prefilled with "B.1.1.28.1 - P.1 - 501Y.V3 (Gamma)" in New Sample form while processing a DEMIS LabMessage
    And I fill laboratory name with "Quick laboratory" in New Sample form while processing a DEMIS LabMessage
    And I fill "first" pathogen laboratory name with "Quick laboratory" in New Sample form while processing a DEMIS LabMessage
    And I fill "second" pathogen laboratory name with "Quick laboratory" in New Sample form while processing a DEMIS LabMessage
    And I click on save sample button
    And I click on save sample button
    And I click on save sample button
    And I click on YES button in Update case disease variant popup window
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I check that the value selected from Disease variant combobox is "B.1.1.7 - 501Y.V1 (Alpha)" on Edit Case page

  @tmsLink=SORQA-904 @env_d2s @LoginKeycloak
  Scenario: Demis - Map disease variant on the case while processing the lab message
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with one positive pathogen
    And I log in as a Admin User
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    When I click the header of column 8
    Then I check that error not appear
    And I check that an upwards arrow appears in the header of column 8
    When I click the header of column 4
    And I check that an upwards arrow appears in the header of column 4
    When I click the header of column 4
    And I check that a downwards arrow appears in the header of column 4
    Then I filter by last created person via API in Messages Directory
    And I check if disease variant field for first record displays "B.1.1.7 - 501Y.V1 (Alpha)" in Message Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I check if disease variant field is "B.1.1.7 - 501Y.V1 (Alpha)" in New case form while processing a DEMIS LabMessage
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I verify that disease variant for "first" pathogen is prefilled with "B.1.1.7 - 501Y.V1 (Alpha)" in New Sample form while processing a DEMIS LabMessage
    And I fill laboratory name with "Synevox" in New Sample form while processing a DEMIS LabMessage
    And I fill "first" pathogen laboratory name with "Synevox" in New Sample form while processing a DEMIS LabMessage
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I check that the value selected from Disease variant combobox is "B.1.1.7 - 501Y.V1 (Alpha)" on Edit Case page

  @tmsLink=SORQA-979 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Lab message that has multiple samples
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with two samples
    And I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    When I click on save button in the case popup
    Then I check that multiple samples window pops up
    And I confirm multiple samples window
    And I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on save sample button
    And I click on YES button in Update case disease variant popup window
    And I pick a new sample in Pick or create sample popup during processing case
    When I check that new sample form with pathogen detection reporting process is displayed
    And I click on save sample button
    And I click on save sample button
    And I click on YES button in Update case disease variant popup window
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I check that the number of added samples on the Edit case page is 2
    And I click on edit sample icon of the 1 displayed sample on Edit Case page
    And I check that lab sample id match "first" specimen id from Demis message on Edit Sample page
    And I validate the existence of "2" pathogen tests
    And I back to the case from Edit Sample page DE
    When I click on edit sample icon of the 2 displayed sample on Edit Case page
    Then I check that lab sample id match "second" specimen id from Demis message on Edit Sample page
    And I validate the existence of "1" pathogen tests
    And I back to the case from Edit Sample page DE
    When I click on edit Report on Edit Case page
    And I click on discard button
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    Then I click on the Messages button from navbar
    And I filter by last created person via API in Messages Directory
    And I verify that status for result 1 is set to processed in Message Directory page

  @tmsLink=SORQA-958 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Lab message that has mapped 1 existing laboratory ID from Sormas
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with one existing facility
    And I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I verify that labor is prefilled with "Testlabor DEMIS" in New sample form while processing a DEMIS LabMessage
    When I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    And I click on the first Case ID from Case Directory
    And I click on Display associated lab messages button from Samples side component
    Then I check if external message window appears and close it
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    When I click on edit Report on Edit Case page
    Then I check that Reporter Facility in Edit report form is set to "Testlabor DEMIS (Inaktiv)"
    And I click on discard button
    And I click on the Messages button from navbar
    And I filter by last created person via API in Messages Directory
    And I verify that status for result 1 is set to processed in Message Directory page
    And I click on the eye icon next for the first fetched message
    And I check if external message window appears and close it

  @tmsLink=SORQA-980 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Lab message that has multiple pathogen test in a sample
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification with multiple pathogen in one sample
    And I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I choose create new case in Pick or create entry form for DE
    And I check that create new case form with pathogen detection reporting process is displayed for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on save button in the case popup
    Then I check that new sample form with pathogen detection reporting process is displayed
    And I fill laboratory name with "Testing laboratory" in New Sample form while processing a DEMIS LabMessage
    And I verify that test type for "first" pathogen is prefilled with "Nukleinsäure-Nachweis (z.B. PCR)" in New Sample form while processing a DEMIS LabMessage
    And I verify that test type for "second" pathogen is prefilled with "Gesamtgenomsequenzierung" in New Sample form while processing a DEMIS LabMessage
    And I fill "first" pathogen laboratory name with "Testing laboratory pathogen 1" in New Sample form while processing a DEMIS LabMessage
    And I fill "second" pathogen laboratory name with "Testing laboratory pathogen 2" in New Sample form while processing a DEMIS LabMessage
    And I click on save sample button
    And I click on save sample button
    And I click on save sample button
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    And I click on edit sample icon of the 1 displayed sample on Edit Case page
    And I check that lab sample id match "first" specimen id from Demis message on Edit Sample page
    And I validate the existence of "2" pathogen tests
    And I back to the case from Edit Sample page DE
    And I check if report side component in Edit Case has today date
    When I click on edit Report on Edit Case page
    And I click on discard button
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    Then I click on the Messages button from navbar
    And I filter by last created person via API in Messages Directory
    And I verify that status for result 1 is set to processed in Message Directory page

  @tmsLink=SORQA-1024 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Physician Report[1]
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification for physician report
    And I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I collect shortened message uuid from Message Directory page
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on "save" button in new physician report form while processing a message
    And I click next button while processing a "hospitalization" in DEMIS LabMessage
    And I click next button while processing a "clinical measurement" in DEMIS LabMessage
    And I click next button while processing a "exposure investigation" in DEMIS LabMessage
    And I click on "save" button in new physician report form while processing a message
    And I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    Then I check if there is no displayed sample result on Edit case page
    And I click on Display associated external messages button from Reports side component
    And I check if external message window appears and close it
    Then I click on the Messages button from navbar
    And I filter by last created person via API in Messages Directory
    And I verify that status for result 1 is set to processed in Message Directory page
    And I select "Arztmeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I click on "Verarbeitet" quick filter above the messages in Message directory page
    And I check that all displayed messages have "Arztmeldung" in grid Message Directory Type column
    And I click on the eye icon next for the first fetched message
    Then I check if there are any buttons from processed message in HTML message file
    And I close HTML message
    And I download "processed" message from Message Directory page
    And I verify if lab message file is downloaded correctly

  @tmsLink=SORQA-1024 @env_d2s @LoginKeycloak
  Scenario: Demis - Process a Physician Report[2]
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification for physician report
    And I log in as a S2S
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I collect shortened message uuid from Message Directory page
    And I select "Arztmeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I click on "Unverarbeitet" quick filter above the messages in Message directory page
    And I check that all displayed messages have "Arztmeldung" in grid Message Directory Type column
    And I assign the Assignee to the message on Message Directory page
    And I check that "Ad MIN" is assigned to the message on Message Directory page
    And I click on the eye icon next for the first fetched message
    Then I check if there are all needed buttons in HTML message file
    And I close HTML message
    And I download "unprocessed" message from Message Directory page
    And I verify if lab message file is downloaded correctly

  @tmsLink=SORQA-1060 @env_d2s @LoginKeycloak
  Scenario: Demis - Actions on Messages directory[1]
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification for physician report
    And I log in as a Admin User
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I collect shortened message uuid from Message Directory page
    And I click on Verarbeiten button in Messages Directory
    And I pick a new person in Pick or create person popup during case creation for DE
    And I fill only mandatory fields to convert laboratory message into a case for DE
    And I click on "save" button in new physician report form while processing a message
    And I click next button while processing a "hospitalization" in DEMIS LabMessage
    And I click next button while processing a "clinical measurement" in DEMIS LabMessage
    And I click next button while processing a "exposure investigation" in DEMIS LabMessage
    And I click on "save" button in new physician report form while processing a message
    Then I click on "Verarbeitet" quick filter above the messages in Message directory page
    And I check that "Verarbeitet" quick filter button is selected in Message directory page
    And I click on the eye icon next for the first fetched message
    Then I check if there are any buttons from processed message in HTML message file
    And I close HTML message
    And I download "processed" message from Message Directory page
    And I verify if lab message file is downloaded correctly
    And I assign the Assignee to the message on Message Directory page
    And I check that "Ad MIN" is assigned to the message on Message Directory page
    And I click on the RESET FILTERS button for Messages
    Then I click on "Weitergeleitet" quick filter above the messages in Message directory page
    And I check that status of the messages correspond to selected tab value "Weitergeleitet" in grid Message Directory Type column
    And I click the header UUID of column
    Then I check that error not appear
    Then I click on "Unklar" quick filter above the messages in Message directory page
    And I check that status of the messages correspond to selected tab value "Unklar" in grid Message Directory Type column
    And I click the header UUID of column
    Then I check that error not appear
    Then I click on "Verarbeitet" quick filter above the messages in Message directory page
    And I check that status of the messages correspond to selected tab value "Verarbeitet" in grid Message Directory Type column
    And I click the header UUID of column
    Then I check that error not appear
    Then I click on Enter Bulk Edit Mode from Message Directory
    And I select first 3 results in grid in Message Directory
    And I click on Bulk Actions combobox in Message Directory
    Then I click on Delete button from Bulk Actions Combobox in Message Directory
    And I check if popup message for deleting is "Only unprocessed messages can be deleted" in Message Directory for DE
    And I click on Leave Bulk Edit Mode from Message Directory
    Then I click on "Alle" quick filter above the messages in Message directory page
    And I select "Arztmeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I check that all displayed messages have "Arztmeldung" in grid Message Directory Type column
    And I select "Labormeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I check that all displayed messages have "Labormeldung" in grid Message Directory Type column

  @tmsLink=SORQA-1060 @env_d2s @LoginKeycloak
  Scenario: Demis - Actions on Messages directory[2]
    Given API : Login to DEMIS server
    When I create and send Laboratory Notification for physician report
    And I log in as a S2S
    And I click on the Messages button from navbar
    And I click on fetch messages button
    And I filter by last created person via API in Messages Directory
    And I collect shortened message uuid from Message Directory page
    Then I click on "Unverarbeitet" quick filter above the messages in Message directory page
    And I check that "Unverarbeitet" quick filter button is selected in Message directory page
    And I click on the eye icon next for the first fetched message
    Then I check if there are all needed buttons in HTML message file
    And I close HTML message
    And I download "unprocessed" message from Message Directory page
    And I verify if lab message file is downloaded correctly
    And I assign the Assignee to the message on Message Directory page
    And I check that "Ad MIN" is assigned to the message on Message Directory page
    And I click on the RESET FILTERS button for Messages
    Then I click on "Unverarbeitet" quick filter above the messages in Message directory page
    And I check that status of the messages correspond to selected tab value "Unverarbeitet" in grid Message Directory Type column
    And I click the header UUID of column
    Then I check that error not appear
    Then I click on Enter Bulk Edit Mode from Message Directory
    And I select first 3 results in grid in Message Directory
    And I click on Bulk Actions combobox in Message Directory
    Then I click on Delete button from Bulk Actions Combobox in Message Directory
    And I click yes on the CONFIRM REMOVAL popup from Message Directory page
    And I check if popup message for deleting is "All selected eligible messages have been deleted" in Message Directory for DE
    And I click on Leave Bulk Edit Mode from Message Directory
    Then I click on "Alle" quick filter above the messages in Message directory page
    And I select "Arztmeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I check that all displayed messages have "Arztmeldung" in grid Message Directory Type column
    And I select "Labormeldung" type of message in Message Directory page
    And I click on the APPLY FILTERS button
    And I check that all displayed messages have "Labormeldung" in grid Message Directory Type column

  @tmsLink=HSP-6177 @env_d2s @LoginKeycloak
  Scenario: Check Laboratory messages of case when sending from SORMAS to Meldesoftware
    Given API : Login to DEMIS server
    Then I create and send Laboratory Notification
    And I navigate to "survnet" environment
    And I log in as a Survnet
    Then I click on the Messages button from navbar
    And I click on fetch messages button
    Then I filter by last created person via API in Messages Directory
    And I click on Verarbeiten button in Messages Directory
    Then I create a new person and a new case from received message
    And I click on the eye icon next for the first fetched message
    And I collect message uuid
    And I close HTML message
    Then I click on the Cases button from navbar
    And I search the case by last created person via Demis message
    Then I click on the first Case ID from Case Directory
    Then I collect "DiagnosedAt" Date from Sample side card for DE
    And I click on edit Sample
    Then I collect date of sample from on Edit Sample page for DE version
    And I click on edit pathogen test
    Then I collect date of Report from Pathogen test result sample
    And I collect via Demis checkbox value Pathogen test result sample
    Then I click on save button in Edit pathogen test result
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if "diagnose at date" in SORMAS generated XML file is correct
    And I check if "date of sample collected" in SORMAS generated XML file is correct
    And I check if "date of report for pathogen at date" in SORMAS generated XML file is correct
    Then I check if Notification Via DEMIS checkbox value has correctly mapped in SORMAS generated singleXmlFile XML file
