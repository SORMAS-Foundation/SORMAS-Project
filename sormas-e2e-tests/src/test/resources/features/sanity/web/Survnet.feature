@UI @Sanity @env_survnet
Feature: Survnet tests

  @tmsLink=SORQA-963 @precon
  Scenario: Test SurvNet Converter installed correctly
    Given I log in as a Admin User
    When I click on the About button from navbar
    Then I check that the Survnet Converter version is not an unavailable on About directory
    And I check that the Survnet Converter version is correctly displayed on About directory

  @tmsLink=SORQA-1031 @precon
  Scenario: Check user login and language settings of SurvNet User in SORMAS
    Given I log in as a Survnet
    And I check that Surveillance Dashboard header is correctly displayed in German language
    When I click on the User Settings button from navbar
    Then I check that Deutsch language is selected in User Settings

  @tmsLink=SORQA-957
  Scenario: Test send simple Case from SORMAS to "Meldesoftware"
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if "date of report" in SORMAS generated XML file is correct
    And I check if sex in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1006
  Scenario: XML Check of simple Test Case from SORMAS to "Meldesoftware"
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I check the SORMAS generated XML file structure with XSD Schema file
    And I compare the SORMAS generated XML file with the example one
    And I click on the About button from navbar
    And I collect SORMAS VERSION from About page
    And I open SORMAS generated XML file for single case message
    And I check if software info in SORMAS generated XML file is correct
    Then I check if "date of report" in SORMAS generated XML file is correct
    And I check if "change at date" in SORMAS generated XML file is correct
    And I check if "tracked at date" in SORMAS generated XML file is correct
    And I check if "created at date" in SORMAS generated XML file is correct
    And I check if sex in SORMAS generated single XML file is correct
    And I check if external person uuid in SORMAS generated XML file is correct

  @tmsLink=SORQA-1011
  Scenario: Automate "Bulk sending cases from SORMAS to "Meldesoftware"
    Given I log in as a Admin User
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I collect uuid of the case
    And I navigate to case person tab
    And I collect sex of the person from Edit Person page
    And I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and specific sex for Survnet DE
    And I collect uuid of the case
    And I navigate to case person tab
    And I collect sex of the person from Edit Person page
    And I click on the Cases button from navbar
    Then I click on the More button on Case directory page
    And I click Enter Bulk Edit Mode on Case directory page
    When I select 2 last created UI result in grid in Case Directory for Bulk Action
    And I click on Bulk Actions combobox on Case Directory Page
    And I click Send to reporting tool button on Case Directory page
    Then I check if popup send message appeared for "de"
    And I click Leave Bulk Edit Mode on Case directory page
    And I filter with first Case ID
    And I click on the first Case ID from Case Directory
    And I collect case external UUID from Edit Case page
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    And I back to the cases list from edit case
    And I filter with second Case ID
    And I click on the first Case ID from Case Directory
    And I navigate to case person tab
    And I collect person external UUID from Edit Case page
    And I open SORMAS generated XML file for bulk case message
    Then I check if sex for all 2 cases in SORMAS generated bulk XML file is correct
    And I check if external person uuid for all 2 cases in SORMAS generated bult XML file is correct
    And I check if "date of report" for all 2 cases in SORMAS generated bulk XML file is correct

  @tmsLink=SORQA-1028
  Scenario: Symptoms in case when sending from SORMAS to Meldesoftware with YES checkbox option for Survnet DE
  Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I navigate to symptoms tab
    Then I change all symptoms fields to "YES" option field and save for Survnet DE
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    Then I open SORMAS generated XML file for single case message
    And I check if "Fever" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Shivering" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Headache" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Muscle Pain" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Feeling Ill" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Chills Sweats" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Acute Respiratory Distress Syndrome" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Sore Throat" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Cough" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Runny Nose" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Pneumonia Clinical Or Radiologic" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Respiratory Disease Ventilation" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Oxygen Saturation Lower94" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Rapid Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Difficulty Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Fast Heart Rate" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Diarrhea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Nausea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss Of Smell" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss OfTaste" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Other Non Hemorrhagic Symptoms" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Onset Of Disease" SYMPTOM in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1028
  Scenario: Symptoms in case when sending from SORMAS to Meldesoftware with NO checkbox option for Survnet DE
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I navigate to symptoms tab
    Then I change all symptoms fields to "NO" option field and save for Survnet DE
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if "Fever" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Shivering" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Headache" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Muscle Pain" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Feeling Ill" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Chills Sweats" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Acute Respiratory Distress Syndrome" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Sore Throat" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Cough" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Runny Nose" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Pneumonia Clinical Or Radiologic" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Respiratory Disease Ventilation" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Oxygen Saturation Lower94" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Rapid Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Difficulty Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Fast Heart Rate" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Diarrhea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Nausea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss Of Smell" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss OfTaste" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Other Non Hemorrhagic Symptoms" SYMPTOM in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1028
  Scenario: Symptoms in case when sending from SORMAS to Meldesoftware with UNKNOWN checkbox option for Survnet DE
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I navigate to symptoms tab
    Then I change all symptoms fields to "UNKNOWN" option field and save for Survnet DE
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if "Fever" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Shivering" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Headache" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Muscle Pain" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Feeling Ill" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Acute Respiratory Distress Syndrome" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Sore Throat" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Cough" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Runny Nose" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Pneumonia Clinical Or Radiologic" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Respiratory Disease Ventilation" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Rapid Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Difficulty Breathing" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Fast Heart Rate" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Diarrhea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Nausea" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss Of Smell" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Loss OfTaste" SYMPTOM in SORMAS generated single XML file is correct
    And I check if "Other Non Hemorrhagic Symptoms" SYMPTOM in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1027
  Scenario: Calculated age in case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only and birth date for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if age computed field in SORMAS generated XML file is correct

  @tmsLink=SORQA-1030
  Scenario: Check Event type Cluster when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Events button from navbar
    And I create a new cluster event for DE version
    And I add only required data for event participant creation for DE
    And I click on the Event participant tab
    And I add only required data for event participant creation for DE
    And I click on the Event participant tab
    And I back to the Event tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect event external UUID from Edit Event page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for event single message
    And I check if event external UUID in SORMAS generated XML file is correct
    And I check if "created at date" in SORMAS generated XML file is correct
    And I check if "change at date" in SORMAS generated XML file is correct

  @tmsLink=SORQA-1035
  Scenario: Check Vaccination and Vaccination Status of case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I navigate to case tab
    Then I click NEW VACCINATION button for DE
    And I fill new vaccination data in new Vaccination form in Survnet for DE
    And I set the vaccination date 14 days before the date of symptom in displayed vaccination form
    And I click SAVE button in new Vaccination form
    Then I check if Vaccination Status is set to "Geimpft" on Edit Case page
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if case external UUID in SORMAS generated XML file is correct
    And I check if "date of report" in SORMAS generated XML file is correct
    And I check if "vaccination date" in SORMAS generated XML file is correct
    And I check if Vaccine name in SORMAS generated XML file is correct

  @tmsLink=SORQA-1033
  Scenario: Check Re-Infection in case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only with Reinfection option for Survnet DE
    And I navigate to case tab
    Then I set previous infection date 20 days from report date to case in person tab
    And I check all checkboxes with genome sequence in reinfection section in Edit case for DE version
    And I check all checkboxes with Information on the preceding infection in reinfection section in Edit case for DE version
    And I check all checkboxes with More information on the evaluation of reinfection in reinfection section in Edit case for DE version
    And I check all checkboxes with Previous infection completed in reinfection section in Edit case for DE version
    Then I click on save case button
    And I collect values from all checkboxes from reinfection section in Edit case for DE version
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if Reinfection option is set in SORMAS generated single XML file is correct
    Then I check that LabInfoAvailable is change in SORMAS generated single XML file is correct
    Then I check if Reinfection "PREVIOUS COVID INFECTION IS KNOWN" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "CURRENT COVID INFECTION IS KNOWN" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "CURRENT COVID INFECTION DO NOT MATCH" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "PERSON HAS OVERCOME ACUTE RESPIRATORY" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "PERSON HAD AN ASYMPTOMATIC COVID INFECTION" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "COVID GENOM COPY NUMBER" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "INDIVIDUAL TESTED POSITIVE FOR COVID BY PCR" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "PERSON TESTED CONCLUSIVELY NEGATIVE BY PRC" checkbox value in SORMAS generated single XML file is correct
    Then I check if Reinfection "THE LAST POSITIVE PCR DETECTION WAS MORE THAN 3 MONTHS AGO" checkbox value in SORMAS generated single XML file is correct

  @tmsLink=SORQA-1029
  Scenario Outline: Pre-existing disease in case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I select the Pre-existing condition "diabetes" as <option> on Edit Case page
    And I select the Pre-existing condition "immunodeficiencyIncludingHiv" as <option> on Edit Case page
    And I select the Pre-existing condition "chronicLiverDisease" as <option> on Edit Case page
    And I select the Pre-existing condition "malignancyChemotherapy" as <option> on Edit Case page
    And I select the Pre-existing condition "chronicPulmonaryDisease" as <option> on Edit Case page
    And I select the Pre-existing condition "chronicKidneyDisease" as <option> on Edit Case page
    And I select the Pre-existing condition "chronicNeurologicCondition" as <option> on Edit Case page
    And I select the Pre-existing condition "cardiovascularDiseaseIncludingHypertension" as <option> on Edit Case page
    And I click on save button from Edit Case page
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if the Pre-existing condition "diabetes" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "immunodeficiencyIncludingHiv" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "chronicLiverDisease" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "malignancyChemotherapy" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "chronicPulmonaryDisease" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "chronicKidneyDisease" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "chronicNeurologicCondition" has <result> value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "cardiovascularDiseaseIncludingHypertension" has <result> value mapped in SORMAS generated single XML file

    Examples:
      | option      | result       |
      | "Ja"        |  "positive"  |
      | "Nein"      |  "negative"  |
      | "Unbekannt" |  "negative"  |

  @tmsLink=SORQA-1029 @precon
  Scenario: Pre-existing disease in case with 4 diseases when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I select the Pre-existing condition "diabetes" as "ja" on Edit Case page
    And I select the Pre-existing condition "immunodeficiencyIncludingHiv" as "ja" on Edit Case page
    And I select the Pre-existing condition "chronicLiverDisease" as "ja" on Edit Case page
    And I select the Pre-existing condition "malignancyChemotherapy" as "ja" on Edit Case page
    And I click on save button from Edit Case page
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if the Pre-existing condition "diabetes" has "positive" value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "immunodeficiencyIncludingHiv" has "positive" value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "chronicLiverDisease" has "positive" value mapped in SORMAS generated single XML file
    And I check if the Pre-existing condition "malignancyChemotherapy" has "positive" value mapped in SORMAS generated single XML file

  @tmsLink=SORQA-1032
  Scenario Outline: Check "nosokomialen Ausbruch" in case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I click the Resulted from nosocomial outbreak checkbox on Edit Case page for DE
    And I select <setting> from the infection settings on Edit Case page
    And I click on save button from Edit Case page
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if the infection setting <setting> is correctly mapped in SORMAS generated single XML file

    Examples:
    | setting |
    | "Ambulant" |
    | "Stationär" |

  @tmsLink=SORQA-1046
  Scenario: Re-send Case from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I click on Send to reporting tool button on Edit Case page
    Then I check that Reporting tool in Survnet box contain "gesendet am" entry
    And I delete the case for DE
    And I apply "Gelöschte Fälle" to combobox on Case Directory Page
    Then I click on the APPLY FILTERS button
    And I click on the first Case ID from Case Directory
    Then I check that Reporting tool in Survnet box contain "gelöscht am" entry
    Then I click on Restore button from case
    And I apply "Aktive Fälle" to combobox on Case Directory Page
    And I click on the first Case ID from Case Directory
    And I click on Send to reporting tool button on Edit Case page
    Then I click on the Archive case button
    Then I check the end of processing date in the archive popup and select Archive contacts checkbox for DE
    And I click on save case button
    And I click on De-Archive case button
    And I fill De-Archive case popup with test automation reason
    And I validate the existence of "5" Reporting Tools entries in Survnet box

  @tmsLink=SORQA-1051
  Scenario: Check Current Hospitalization of case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific data using created facility for Survnet DE
    And I navigate to Hospitalization tab in Cases
    Then I complete all hospitalization fields for Current Hospitalization and save it for DE
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if Current Hospitalization Was Patient Admitted has correct value mapped in SORMAS generated single XML file
    And I check if Region from Current Hospitalization value in SORMAS generated XML file is correct
    Then I check if "date of visit or admission" for Current Hospitalization in SORMAS generated XML file is correct
    Then I check if "date of discharge or transfer" for Current Hospitalization in SORMAS generated XML file is correct
    And I check reason for Current Hospitalization in SORMAS generated single XML file is correct
    And I check if Stay in the intensive care unit value Current Hospitalization in SORMAS generated XML file is correct
    Then I check if "start of the stay" for Current Hospitalization in SORMAS generated XML file is correct
    Then I check if "end of the stay" for Current Hospitalization in SORMAS generated XML file is correct

  @tmsLink=SORQA-1051
  Scenario: Check Previous Hospitalization of case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    Then I create a new case with specific data using created facility for Survnet DE
    And I navigate to Hospitalization tab in Cases
    Then I add a previous hospitalization and save for DE
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    And I check if Previous Hospitalization Was Patient Admitted has correct value mapped in SORMAS generated single XML file
    And I check if Region from Previous Hospitalization value in SORMAS generated XML file is correct
    And I check if "date of visit or admission" for Previous Hospitalization in SORMAS generated XML file is correct
    And I check if "date of discharge or transfer" for Previous Hospitalization in SORMAS generated XML file is correct
    Then I check reason for Previous Hospitalization in SORMAS generated single XML file is correct
    And I check if Stay in the intensive care unit value for Previous Hospitalization in SORMAS generated XML file is correct
    And I check if "start of the stay" for Previous Hospitalization in SORMAS generated XML file is correct
    And I check if "end of the stay" for Previous Hospitalization in SORMAS generated XML file is correct

  @tmsLink=SORQA-1052
  Scenario: Check Epidemiology data "Expositionsuntersuchung" of case when sending from SORMAS to Meldesoftware
    Given I log in as a Survnet
    When I click on the Cases button from navbar
    And I click on the NEW CASE button
    And I create a new case with mandatory data only for Survnet DE
    And I navigate to epidemiological data tab in Edit case page
    And I click on Exposure details known with JA option
    And I click on New Entry in Exposure Details Known in Cases directory
    And I select Reisen option in Type of activity from Combobox in Exposure form
    And I click on save button in Exposure for Epidemiological data tab in Cases
    And I click on save button from Epidemiological Data
    And I navigate to case tab
    And I click on Send to reporting tool button on Edit Case page
    And I collect case external UUID from Edit Case page
    Then I wait 50 seconds for system reaction
    And I open SORMAS generated XML file for single case message
    Then I check if the exposure settings are correctly mapped in SORMAS generated single XML file