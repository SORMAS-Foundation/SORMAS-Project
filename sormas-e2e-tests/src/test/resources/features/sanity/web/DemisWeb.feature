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




