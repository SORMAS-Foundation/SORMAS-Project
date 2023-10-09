/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.steps.web.application.samples;

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAMPLES_CARD_DATE_AND_TIME_OF_RESULT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAMPLES_CARD_DATE_OF_COLLECTED_SAMPLE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAMPLES_CARD_LABORATORY;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAMPLES_CARD_NUMBER_OF_TESTS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAMPLES_CARD_TEST_TYPE;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_TABLE_DATA;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.EVENT_ACTIONS_TABLE_ROW;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.ADD_PATHOGEN_TEST;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.ADD_PATHOGEN_TEST_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.ANTIGEN_DETECTION_TEST_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.CELLS_IN_URINE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.CELLS_IN_URINE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.COLLECTED_DATE_TIME_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.COLLECTED_DATE_TIME_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.COMMENT_AREA_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.CONJ_BILIRUBIN_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.CREATININE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_AND_TIME_OF_RESULTS;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_AND_TIME_OF_RESULTS_INPUT_FIELD;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_OF_RESULT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_SAMPLE_COLLECTED;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_SAMPLE_RECEIVED;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_SAMPLE_RECEIVED_INPUT_FIELD;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_SAMPLE_SEND_INPUT_FIELD;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.DATE_TEST_REPORT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.FIELD_SAMPLE_ID_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.FINAL_LABORATORY_RESULT_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.GENERIC_ERROR_POPUP;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.HAEMOGLOBIN_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.HAEMOGLOBIN_IN_URINE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.HAEMOGLOBIN_IN_URINE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.HCO3_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.ISOLATION_TEST_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.LABORATORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.LABORATORY_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.LABORATORY_NAME_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.LABORATORY_NAME_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.LAB_SAMPLE_ID_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.NEW_TEST_RESULTS_BUTTON_FOR_PATHOGEN_TESTS;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.OTHER_TESTS_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.OXYGEN_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PAO2_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_CARD_DATE_OF_RESULT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_CARD_DISEASE;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_CARD_TEST_RESULT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_CARD_TEST_RESULT_COMMENT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_CARD_TYPE_OF_TEST;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_LABORATORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_LABORATORY_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_TEST_RESULT_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PATHOGEN_TEST_RESULT_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PCO2_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PCR_RTP_PCR_TEST_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PH_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PLATELETS_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.POTASSIUM_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PROTEIN_IN_URINE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PROTEIN_IN_URINE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.PROTHROMBIN_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.REASON_FOR_SAMPLING_TESTING_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.REASON_FOR_SAMPLING_TESTING_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.RECEIVED_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.REQUEST_PATHOGEN_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_PURPOSE_OPTIONS;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_TEST_RESULT_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAMPLE_UUID;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAVE_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAVE_SAMPLE_WITH_PATHOGEN_TEST_BUTTON;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SGOT_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SGPT_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SPECIMEN_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SPECIMEN_CONDITION_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TESTED_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TESTED_DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TEST_RESULTS_COMMENT_AREA_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TIME_OF_RESULT_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TIME_OF_RESULT_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TOTAL_BILIRUBIN_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TYPE_OF_TEST_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.TYPE_OF_TEST_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.UPDATE_CASE_DISEASE_VARIANT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.UREA_INPUT;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.VIA_DEMIS_CHECKBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.WBC_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.EDIT_PATHOGEN_TEST;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PCR_TEST_SPECIFICATION_COMBOBOX_DIV;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.TESTED_DISEASE_VARIANT;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.CREATE_CASE_POSITIVE_TEST_RESULT_LABEL;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.EDIT_ADDITIONAL_TEST_RESULTS_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.EDIT_PATHOGEN_TEST_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.EDIT_TEST_RESULTS_BUTTON;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.RESULT_VERIFIED_BY_LAB_SUPERVISOR_EDIT_OPTIONS;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_EDIT_PURPOSE_OPTIONS;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_RECEIVED_CHECKBOX;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SHIPPED_CHECKBOX;
import static org.sormas.e2etests.steps.web.application.messages.MessagesDirectorySteps.convertStringToChosenFormatDate;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Sample;
import org.sormas.e2etests.entities.pojo.web.SampleAdditionalTest;
import org.sormas.e2etests.entities.services.SampleAdditionalTestService;
import org.sormas.e2etests.entities.services.SampleService;
import org.sormas.e2etests.enums.PathogenTestResults;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class CreateNewSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample sample;
  public static Sample sampleTestResult;
  public static SampleAdditionalTest additionalTestResult;
  public static String sampleId;
  private final WebDriverHelpers webDriverHelpers;
  private final Faker faker;
  private final BaseSteps baseSteps;
  public static LocalDate sampleCollectionDateForFollowUpDate;
  public static LocalDate reportDate;
  public static String CheckboxViaDemisValue;

  @Inject
  public CreateNewSampleSteps(
      WebDriverHelpers webDriverHelpers,
      SampleService sampleService,
      SampleAdditionalTestService sampleAdditionalTestService,
      Faker faker,
      SoftAssert softly,
      BaseSteps baseSteps,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;

    When(
        "^I create a new Sample with specific data and save$",
        () -> {
          sample = sampleService.buildGeneratedSample();
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          selectLaboratory(sample.getLaboratory());
          selectLaboratoryName(sample.getLaboratoryName());
          selectReceivedOptionButton(sample.getReceived());
          fillReceivedDate(sample.getReceivedDate());
          selectSpecimenCondition(sample.getSpecimenCondition());
          fillLabSampleId(sample.getLabSampleId());
          fillCommentsOnSample(sample.getCommentsOnSample());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
        });

    When(
        "^I create a new Sample with alternate purpose$",
        () -> {
          sample = sampleService.buildAlternateSample();
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          fillCommentsOnSample(sample.getCommentsOnSample());
        });

    When(
        "^I create a new Sample with positive test result for DE version$",
        () -> {
          sample = sampleService.buildGeneratedPositiveSampleDE();
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollectionDE(sample.getDateOfCollection());
          selectSampleType(sample.getSampleType());
          webDriverHelpers.clickOnWebElementBySelector(ADD_PATHOGEN_TEST_BUTTON);
          selectTestedDisease(sample.getTestedDisease());
          selectTestResult(sample.getSampleTestResults());
          selectLaboratory(sample.getLaboratory());
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
        });
    When(
        "^I create a new Sample with only required fields for DE version$",
        () -> {
          sample = sampleService.buildOnlyRequiredSampleFieldsDE();
          fillDateOfCollectionDE(sample.getDateOfCollection());
          selectSampleType(sample.getSampleType());
          selectLaboratory(sample.getLaboratory());
        });
    And(
        "I set date of sample collection to {int} day ago in Sample form",
        (Integer days) -> {
          sampleCollectionDateForFollowUpDate = LocalDate.now().minusDays(days);
          fillDateOfCollectionDE(LocalDate.now().minusDays(days));
        });

    When(
        "I check if value {string} is unavailable in Type of Sample combobox on Create new Sample page",
        (String sampleMaterial) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  SAMPLE_TYPE_COMBOBOX, sampleMaterial));
          softly.assertAll();
        });

    When(
        "I set Final Laboratory Result to {string} on Create new Sample page",
        (String value) -> {
          webDriverHelpers.selectFromCombobox(FINAL_LABORATORY_RESULT_COMBOBOX, value);
        });

    When(
        "^I create a new Sample with positive test result with ([^\"]*) as disease$",
        (String diseaseType) -> {
          sample = sampleService.buildAlternateSampleWithSelectableDisease(diseaseType);
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          fillCommentsOnSample(sample.getCommentsOnSample());
          webDriverHelpers.clickOnWebElementBySelector(ADD_PATHOGEN_TEST);
          selectTestedDisease(sample.getTestedDisease());
          selectTypeOfTest(sample.getTypeOfTest());
          selectTestResult(sample.getSampleTestResults());
          fillDateOfResult(sample.getDateOfResult(), Locale.ENGLISH);
          fillTimeOfResult(sample.getTimeOfResult());
          selectLaboratory(sample.getLaboratory());
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          selectTestResult(sample.getTestResults());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_WITH_PATHOGEN_TEST_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "^I create a new pathogen test result with ([^\"]*) as disease$",
        (String diseaseType) -> {
          sample = sampleService.buildAlternateSampleWithSelectableDisease(diseaseType);
          selectTypeOfTest(sample.getTypeOfTest());
          selectTestedDisease(sample.getTestedDisease());
          selectTestResult(sample.getSampleTestResults());
          selectLaboratory(sample.getLaboratory());
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          selectTestResult(sample.getTestResults());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I create a new pathogen test result with {string} as disease and {string} as a test type",
        (String diseaseType, String testType) -> {
          sample = sampleService.buildPathogenTestResultTypeVerified(diseaseType, testType);
          selectTypeOfTest(sample.getTypeOfTest());
          selectTestedDisease(sample.getTestedDisease());
          selectTestResult(sample.getSampleTestResults());
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
        });

    When(
        "I create new sample with pathogen test with {string} as disease and {string} as type of test",
        (String diseaseType, String typeOfTest) -> {
          sample =
              sampleService.buildGeneratedSampleWithTestResultForSelectedDiseaseAndTestType(
                  diseaseType, typeOfTest);
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          selectSampleType(sample.getSampleType());
          webDriverHelpers.clickOnWebElementBySelector(ADD_PATHOGEN_TEST);
          selectTestedDisease(sample.getTestedDisease());
          selectTypeOfTest(sample.getTypeOfTest());
          selectTestResult(sample.getSampleTestResults());
          fillDateOfResult(sample.getDateOfResult(), Locale.ENGLISH);
          selectLaboratory(sample.getLaboratory());
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
        });

    When(
        "I set PCR RT PCR Test specification to {string} option",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(PCR_TEST_SPECIFICATION_COMBOBOX_DIV, option);
        });

    When(
        "I set Tested disease variant as {string}",
        (String variant) -> {
          webDriverHelpers.selectFromCombobox(TESTED_DISEASE_VARIANT, variant);
        });

    When(
        "^I validate date and time is present on sample card$",
        () -> {
          String dateAndTimeVisible =
              webDriverHelpers.getTextFromWebElement(DATE_AND_TIME_OF_RESULTS);
          LocalDate date = sample.getDateOfResult();
          LocalTime time = sample.getTimeOfResult();
          DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("M/d/yyyy");
          DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern("hh:mm a");
          softly.assertEquals(
              "Date and time of result: "
                  + dateFormater.format(date)
                  + " "
                  + timeFormater.format(time),
              dateAndTimeVisible,
              "Date or time is not equal");
          softly.assertAll();
        });

    When(
        "^I click on new test result for pathogen tests",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TEST_RESULTS_BUTTON_FOR_PATHOGEN_TESTS);
        });

    When(
        "I validate the existence of {string} pathogen tests",
        (String number) -> {
          int numberInt = Integer.parseInt(number);
          TimeUnit.SECONDS.sleep(2);
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(EDIT_PATHOGEN_TEST),
              numberInt,
              "Number of pathogen tests is not correct");
          softly.assertAll();
        });

    When(
        "I click on edit pathogen test",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_PATHOGEN_TEST);
          TimeUnit.SECONDS.sleep(5);
        });

    When(
        "I select the German words for Antigen Detection Test as Type of Test in the Create New Sample popup",
        () -> {
          selectTypeOfTest("Antigen-Nachweistest");
        });

    When(
        "I select the German words for Rapid Antigen Detection Test as Type of Test in the Create New Sample popup",
        () -> {
          selectTypeOfTest("Antigen Nachweistest (Schnelltest)");
        });

    When(
        "I select the German words for Isolation as Type of Test in the Create New Sample popup",
        () -> {
          selectTypeOfTest("Isolation");
        });

    When(
        "I select the German words for PCR RT-PCR as Type of Test in the Create New Sample popup",
        () -> {
          selectTypeOfTest("Nukleins\u00E4ure-Nachweis (z.B. PCR)");
        });

    When(
        "^I create a new Sample with for COVID alternative purpose$",
        () -> {
          sample = sampleService.buildGeneratedSample();
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectReasonForSample(sample.getReasonForSample());
          fillSampleID(sample.getSampleID());
          selectLaboratory(sample.getLaboratory());
          selectLaboratoryName(sample.getLaboratoryName());
          webDriverHelpers.clickOnWebElementBySelector(REQUEST_PATHOGEN_OPTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ANTIGEN_DETECTION_TEST_OPTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ISOLATION_TEST_OPTION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(PCR_RTP_PCR_TEST_OPTION_BUTTON);
          webDriverHelpers.selectFromCombobox(
              FINAL_LABORATORY_RESULT_COMBOBOX, PathogenTestResults.POSITIVE.getPathogenResults());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I fill all fields from Pathogen test for COVID-19 disease result popup and save",
        () -> {
          sampleTestResult = sampleService.buildGeneratedSampleTestResultForCovid();
          //    fillReportDate(sampleTestResult.getReportDate(), Locale.ENGLISH);
          selectTypeOfTest(sampleTestResult.getTypeOfTest());
          selectTestedDisease(sampleTestResult.getTestedDisease());
          selectPathogenLaboratory(sampleTestResult.getLaboratory());
          selectTestResult(sampleTestResult.getSampleTestResults());
          fillDateOfResult(sampleTestResult.getDateOfResult(), Locale.ENGLISH);
          fillTimeOfResult(sampleTestResult.getTimeOfResult());
          selectResultVerifiedByLabSupervisor(
              sampleTestResult.getResultVerifiedByLabSupervisor(),
              RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          fillTestResultsComment(sampleTestResult.getTestResultsComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I create a new Sample with positive test result for DE version with {string} as a labor",
        (String option) -> {
          sample = sampleService.buildGeneratedPositiveSampleDE();
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollectionDE(sample.getDateOfCollection());
          selectSampleType(sample.getSampleType());
          webDriverHelpers.clickOnWebElementBySelector(ADD_PATHOGEN_TEST_BUTTON);
          selectTestedDisease(sample.getTestedDisease());
          selectTestResult(sample.getSampleTestResults());
          selectTypeOfTest("Kultur");
          selectLaboratory(option);
          selectResultVerifiedByLabSupervisor(
              sample.getResultVerifiedByLabSupervisor(), RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
        });

    When(
        "^I save the created sample",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_SAMPLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "^I save the created sample with pathogen test",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_WITH_PATHOGEN_TEST_BUTTON);
        });

    When(
        "^I check the created Sample is correctly displayed on Edit Sample page",
        () -> {
          final Sample actualSample = collectSampleData();
          ComparisonHelper.compareEqualEntities(sample, actualSample);
        });

    When(
        "^I check the alternate Sample is correctly displayed on Edit Sample page",
        () -> {
          final Sample actualSample = collectAlternateSampleData();
          ComparisonHelper.compareEqualEntities(sample, actualSample);
        });

    When(
        "^I complete all fields from Pathogen test result popup and save$",
        () -> {
          sampleTestResult = sampleService.buildPathogenTestResult();
          //    fillReportDate(sampleTestResult.getReportDate(), Locale.ENGLISH);
          selectTypeOfTest(sampleTestResult.getTypeOfTest());
          selectTestedDisease(sampleTestResult.getTestedDisease());
          selectPathogenLaboratory(sampleTestResult.getLaboratory());
          selectTestResult(sampleTestResult.getSampleTestResults());
          fillDateOfResult(sampleTestResult.getDateOfResult(), Locale.ENGLISH);
          fillTimeOfResult(sampleTestResult.getTimeOfResult());
          selectResultVerifiedByLabSupervisor(
              sampleTestResult.getResultVerifiedByLabSupervisor(),
              RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          fillTestResultsComment(sampleTestResult.getTestResultsComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });
    When(
        "^I complete all fields from Additional test result popup and save$",
        () -> {
          additionalTestResult = sampleAdditionalTestService.buildSampleAdditionalTestResult();
          fillDateOfResult(additionalTestResult.getDateOfResult(), Locale.ENGLISH);
          fillTimeOfResult(additionalTestResult.getTimeOfResult());
          selectHaemoglobinInUrine(additionalTestResult.getHaemoglobinInUrine());
          selectProteinInUrine(additionalTestResult.getProteinInUrine());
          selectCellsInUrine(additionalTestResult.getRedBloodCellsInUrine());
          fillPh(additionalTestResult.getPh());
          fillPCO2(additionalTestResult.getPCO2());
          fillPAO2(additionalTestResult.getPAO2());
          fillHCO3(additionalTestResult.getHCO3());
          fillOxygenTherapy(additionalTestResult.getOxygen());
          fillSgpt(additionalTestResult.getSgpt());
          fillTotalBilirubin(additionalTestResult.getTotalBilirubin());
          fillSgot(additionalTestResult.getSgot());
          fillConjBilirubin(additionalTestResult.getConjBilirubin());
          fillCretinine(additionalTestResult.getCreatine());
          fillWbc(additionalTestResult.getWbc());
          fillPotassium(additionalTestResult.getPotassium());
          fillPlatelets(additionalTestResult.getPlatelets());
          fillUrea(additionalTestResult.getUrea());
          fillProthrombin(additionalTestResult.getProthrombin());
          fillHaemoglobin(additionalTestResult.getHaemoglobin());
          fillOtherTests(additionalTestResult.getOtherResults());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "^I complete all fields from Pathogen test result popup for IgM test type and save$",
        () -> {
          simplePathogenBuilderResult("IgM serum antibody");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I check if Pathogen test result in Samples is displayed correctly and save",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_PATHOGEN_TEST_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              sampleTestResult,
              actualSampleTestResult,
              List.of(
                  "laboratory",
                  "sampleTestResults",
                  "typeOfTest",
                  "testedDisease",
                  "dateOfResult",
                  "timeOfResult",
                  "resultVerifiedByLabSupervisor",
                  "testResultsComment"));
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for IgM test type for DE version and save",
        () -> {
          buildPathogenTestDE("IgM Serum Antik\u00F6rper");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for IgM test type with positive verified test result$",
        () -> {
          simplePathogenBuilderVerifiedResult("IgG serum antibody");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "^I complete all fields from Pathogen test result popup for IgG test type and save$",
        () -> {
          simplePathogenBuilderResult("IgG serum antibody");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for PCR RT PCR Value Detection test type and save",
        () -> {
          simplePathogenBuilderResult("PCR / RT-PCR");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for PCR RT PCR Value Detection test type for DE version and save",
        () -> {
          buildPathogenTestDE("Nukleins\u00E4ure-Nachweis (z.B. PCR)");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for CQ Value Detection test type and save",
        () -> {
          simplePathogenBuilderResult("CQ Value Detection");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for Sequencing test type and save",
        () -> {
          simplePathogenBuilderResult("Sequencing");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for DNA Microarray test type and save",
        () -> {
          simplePathogenBuilderResult("DNA Microarray");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I complete all fields from Pathogen test result popup for Other test type and save",
        () -> {
          simplePathogenBuilderResult("Other");
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I collect the sample UUID displayed on create new sample page",
        () -> sampleId = collectSampleUuid());

    When(
        "^I check that the created Pathogen is correctly displayed$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TEST_RESULTS_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              sampleTestResult,
              actualSampleTestResult,
              List.of(
                  "typeOfTest",
                  "testedDisease",
                  "laboratory",
                  "sampleTestResults",
                  "dateOfResult",
                  "timeOfResult",
                  "resultVerified",
                  "testResultsComment"));
        });

    When(
        "^I check if sample card is correctly displayed in case edit tab for DE version$",
        () -> {
          final Sample actualSampleTestResult = collectCaseCardSampleDataDE();
          ComparisonHelper.compareEqualFieldsOfEntities(
              sampleTestResult,
              actualSampleTestResult,
              List.of("typeOfTest", "dateOfResult", "timeOfResult"));
          softly.assertEquals(
              apiState.getCreatedSample().getLab().getCaption(),
              actualSampleTestResult.getLaboratory(),
              "Labs are not equal");
          softly.assertEquals(getNumberOfTests(), "1", "Number of tests are not equal");
          softly.assertAll();
        });

    When(
        "^I check if pathogen test card is correctly displayed for DE version$",
        () -> {
          final Sample actualSampleTestResult = collectPathogenCardDataDE();
          ComparisonHelper.compareEqualFieldsOfEntities(
              sampleTestResult,
              actualSampleTestResult,
              List.of("typeOfTest", "dateOfResult", "commentsOnSample", "sampleTestResults"));
        });

    When(
        "I check if created sample is correctly displayed in samples list",
        () -> {
          Map<String, String> detailedSampleTableRow = getTableRowsData().get(0);
          softly.assertEquals(
              detailedSampleTableRow.get(SampleTableColumnsHeaders.LABORATORY.toStringDE()),
              apiState.getCreatedSample().getLab().getCaption(),
              "Laboratories are not equal");
          softly.assertEquals(
              detailedSampleTableRow.get(SampleTableColumnsHeaders.DISEASE.toStringDE()),
              sampleTestResult.getTestedDisease(),
              "Diseases are not equal");
          softly.assertEquals(
              detailedSampleTableRow.get(SampleTableColumnsHeaders.ASSOCIATED_CASE.toStringDE()),
              apiState.getCreatedCase().getPerson().getFirstName()
                  + " "
                  + apiState.getCreatedCase().getPerson().getLastName().toUpperCase()
                  + " ("
                  + apiState.getCreatedCase().getUuid().substring(0, 6).toUpperCase()
                  + ")",
              "Persons data are not equal");
          softly.assertEquals(
              detailedSampleTableRow.get(
                  SampleTableColumnsHeaders.LATEST_PATHOGEN_TEST.toStringDE()),
              sampleTestResult.getTypeOfTest(),
              "Type of tests are not equal");
          softly.assertEquals(
              detailedSampleTableRow.get(SampleTableColumnsHeaders.NUMBER_OF_TESTS.toStringDE()),
              "1",
              "Number of tests are not equal");
          softly.assertAll();
        });

    When(
        "^I check that the created Additional test is correctly displayed$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_ADDITIONAL_TEST_RESULTS_BUTTON);
          final SampleAdditionalTest actualAdditionalTestResult =
              collectAdditionalTestResultsData();
          ComparisonHelper.compareEqualEntities(additionalTestResult, actualAdditionalTestResult);
        });

    When(
        "^I check that the created Pathogen is correctly displayed for DE version$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TEST_RESULTS_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsDataDE();
          ComparisonHelper.compareEqualEntities(sampleTestResult, actualSampleTestResult);
        });

    When(
        "I confirm the Create case from contact with positive test result",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    When(
        "I confirm to create case for selected disease",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    When(
        "I confirm case with positive test result",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    When(
        "I confirm the Create case from event participant with positive test result",
        () -> {
          TimeUnit.SECONDS.sleep(5); // weak performance, wait for popup
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(CREATE_CASE_POSITIVE_TEST_RESULT_LABEL);
          String expectedText = "Create case from event participant with positive test result?";
          softly.assertEquals(
              displayedText,
              expectedText,
              "Case creation confirmation popup message is not correct");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    When(
        "I check if default disease value for new Pathogen test is set for ([^\"]*)",
        (String disease) -> {
          String testedDisease = webDriverHelpers.getValueFromCombobox(TESTED_DISEASE_COMBOBOX);
          softly.assertEquals(disease, testedDisease, "Diseases are not equal");
          softly.assertAll();
        });

    When(
        "I set Test Disease as ([^\"]*) in new pathogen result",
        (String disease) -> webDriverHelpers.selectFromCombobox(TESTED_DISEASE_COMBOBOX, disease));

    When(
        "I check if Type of test in new pathogen results has no ([^\"]*) option",
        (String typeOfTest) -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(TYPE_OF_TEST_COMBOBOX, typeOfTest),
              "Type of test is incorrect");
          softly.assertAll();
        });

    And(
        "I select Sent dispatched checkbox in new sample page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_SHIPPED_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_SHIPPED_CHECKBOX);
        });

    And(
        "I select Received checkbox in new sample page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_RECEIVED_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_RECEIVED_CHECKBOX);
        });

    Then(
        "I check is Sent dispatched Date and Received Date fields required",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DATE_SAMPLE_SEND_INPUT_FIELD);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DATE_SAMPLE_RECEIVED_INPUT_FIELD);
        });

    And(
        "I click Add Pathogen test in Sample creation page",
        () -> webDriverHelpers.clickOnWebElementBySelector(ADD_PATHOGEN_TEST_BUTTON));

    And(
        "I check DATE AND TIME OF RESULT field",
        () -> {
          TimeUnit.SECONDS.sleep(10);
          webDriverHelpers.getWebElement(DATE_AND_TIME_OF_RESULTS_INPUT_FIELD);
        });

    And(
        "I click on save sample button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    Then(
        "I check error popup message in German",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(GENERIC_ERROR_POPUP);
          List<String> popupExpected =
              List.of(
                  "Bitte überprüfen Sie die Eingabedaten",
                  "Labor",
                  "Art des Tests",
                  "Ergebnis verifiziert von Laborleitung");
          webDriverHelpers.checkIsPopupContainsList(GENERIC_ERROR_POPUP, popupExpected);
        });

    When(
        "I check if error popup contains {string}",
        (String str) -> {
          webDriverHelpers.checkWebElementContainsText(GENERIC_ERROR_POPUP, str);
        });

    When(
        "I collect date of Report from Pathogen test result sample",
        () -> {
          String LocalDateOfReport = webDriverHelpers.getValueFromWebElement(DATE_TEST_REPORT);
          reportDate =
              convertStringToChosenFormatDate("dd.MM.yyyy", "yyyy-MM-dd", LocalDateOfReport);
        });

    When(
        "I collect via Demis checkbox value Pathogen test result sample",
        () -> {
          String localCheckboxViaDemis =
              webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(VIA_DEMIS_CHECKBOX);
          if (localCheckboxViaDemis.equals("Via DEMIS")) CheckboxViaDemisValue = "true";
        });

    When(
        "I click on save button in Edit pathogen test result",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I confirm update case result",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON));

    When(
        "I check if Update case disease variant popup is available",
        () -> webDriverHelpers.isElementVisibleWithTimeout(UPDATE_CASE_DISEASE_VARIANT, 10));

    When(
        "I create sample with {string} as a Laboratory",
        (String labor) -> {
          sample = sampleService.buildSampleWithParametrizedLaboratory(labor);
          selectPurposeOfSample(sample.getPurposeOfTheSample(), SAMPLE_PURPOSE_OPTIONS);
          fillDateOfCollection(sample.getDateOfCollection());
          fillTimeOfCollection(sample.getTimeOfCollection());
          selectSampleType(sample.getSampleType());
          selectLaboratory(sample.getLaboratory());
        });

    When(
        "I click on edit pathogen button",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_PATHOGEN_TEST_BUTTON));

    And(
        "^I click on yes in Confirm case popup window$",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for popup to load
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });
  }

  private void selectPurposeOfSample(String samplePurpose, By element) {
    webDriverHelpers.clickWebElementByText(element, samplePurpose);
  }

  private String collectSampleUuid() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_UUID);
  }

  private void fillDateOfCollection(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_COLLECTED, DATE_FORMATTER.format(dateOfCollection));
  }

  private void fillDateOfCollectionDE(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_COLLECTED, DATE_FORMATTER_DE.format(dateOfCollection));
  }

  private void fillTimeOfCollection(LocalTime timeOfCollection) {
    webDriverHelpers.selectFromCombobox(
        COLLECTED_DATE_TIME_COMBOBOX, TIME_FORMATTER.format(timeOfCollection));
  }

  private void selectSampleType(String sampleType) {
    webDriverHelpers.selectFromCombobox(SAMPLE_TYPE_COMBOBOX, sampleType);
  }

  private void selectReasonForSample(String reasonForSample) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_SAMPLING_TESTING_COMBOBOX, reasonForSample);
  }

  private void fillSampleID(long sampleID) {
    webDriverHelpers.clearAndFillInWebElement(FIELD_SAMPLE_ID_INPUT, String.valueOf(sampleID));
  }

  private void selectLaboratory(String laboratory) {
    webDriverHelpers.selectFromCombobox(LABORATORY_COMBOBOX, laboratory);
  }

  private void selectPathogenLaboratory(String laboratory) {
    webDriverHelpers.selectFromCombobox(PATHOGEN_LABORATORY_COMBOBOX, laboratory);
  }

  private void selectTestResult(String testResult) {
    webDriverHelpers.selectFromCombobox(PATHOGEN_TEST_RESULT_COMBOBOX, testResult);
  }

  private void selectSpecimenCondition(String specimenCondition) {
    webDriverHelpers.selectFromCombobox(SPECIMEN_CONDITION_COMBOBOX, specimenCondition);
  }

  private void fillReceivedDate(LocalDate receivedDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_RECEIVED, DATE_FORMATTER.format(receivedDate));
  }

  public void selectLaboratoryName(String laboratoryName) {
    webDriverHelpers.clearAndFillInWebElement(LABORATORY_NAME_INPUT, laboratoryName);
  }

  private void selectReceivedOptionButton(String received) {
    webDriverHelpers.clickWebElementByText(RECEIVED_OPTION_BUTTON, received);
  }

  private void fillLabSampleId(long labSampleId) {
    webDriverHelpers.clearAndFillInWebElement(LAB_SAMPLE_ID_INPUT, String.valueOf(labSampleId));
  }

  private void fillCommentsOnSample(String commentsOnSample) {
    webDriverHelpers.clearAndFillInWebElement(COMMENT_AREA_INPUT, commentsOnSample);
  }

  private void selectSampleTestResultButton() {
    webDriverHelpers.clickOnWebElementBySelector(SAMPLE_TEST_RESULT_BUTTON);
  }

  private void fillReportDate(LocalDate dateOfCollection, Locale locale) {
    //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
    //    if (locale.equals(Locale.GERMAN))
    //      webDriverHelpers.clearAndFillInWebElement(
    //          DATE_TEST_REPORT, formatter.format(dateOfCollection));
    //    else
    //      webDriverHelpers.clearAndFillInWebElement(
    //          DATE_TEST_REPORT, DATE_FORMATTER.format(dateOfCollection));
  }

  private void selectTypeOfTest(String typeOfTest) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_TEST_COMBOBOX, typeOfTest);
  }

  private void selectTestedDisease(String typeOfDisease) {
    webDriverHelpers.selectFromCombobox(TESTED_DISEASE_COMBOBOX, typeOfDisease);
  }

  private void fillDateOfResult(LocalDate dateOfCollection, Locale locale) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(DATE_OF_RESULT, formatter.format(dateOfCollection));
    else
      webDriverHelpers.clearAndFillInWebElement(
          DATE_OF_RESULT, DATE_FORMATTER.format(dateOfCollection));
  }

  private void fillTimeOfResult(LocalTime timeOfCollection) {
    webDriverHelpers.selectFromCombobox(
        TIME_OF_RESULT_COMBOBOX, TIME_FORMATTER.format(timeOfCollection));
  }

  private void selectResultVerifiedByLabSupervisor(String resultVerified, By element) {
    webDriverHelpers.clickWebElementByText(element, resultVerified);
  }

  private void fillTestResultsComment(String testResultsComment) {
    webDriverHelpers.clearAndFillInWebElement(TEST_RESULTS_COMMENT_AREA_INPUT, testResultsComment);
  }

  private void selectHaemoglobinInUrine(String haemoglobinInUrine) {
    webDriverHelpers.selectFromCombobox(HAEMOGLOBIN_IN_URINE_COMBOBOX, haemoglobinInUrine);
  }

  private void selectProteinInUrine(String proteinInUrine) {
    webDriverHelpers.selectFromCombobox(PROTEIN_IN_URINE_COMBOBOX, proteinInUrine);
  }

  private void selectCellsInUrine(String cellsInUrine) {
    webDriverHelpers.selectFromCombobox(CELLS_IN_URINE_COMBOBOX, cellsInUrine);
  }

  private void fillPh(String ph) {
    webDriverHelpers.clearAndFillInWebElement(PH_INPUT, ph);
  }

  private void fillPCO2(String pCO2) {
    webDriverHelpers.clearAndFillInWebElement(PCO2_INPUT, pCO2);
  }

  private void fillPAO2(String pAO2) {
    webDriverHelpers.clearAndFillInWebElement(PAO2_INPUT, pAO2);
  }

  private void fillHCO3(String hCO3) {
    webDriverHelpers.clearAndFillInWebElement(HCO3_INPUT, hCO3);
  }

  private void fillOxygenTherapy(String oxygenTherapy) {
    webDriverHelpers.clearAndFillInWebElement(OXYGEN_INPUT, oxygenTherapy);
  }

  private void fillSgpt(String sgpt) {
    webDriverHelpers.clearAndFillInWebElement(SGPT_INPUT, sgpt);
  }

  private void fillTotalBilirubin(String totalBilirubin) {
    webDriverHelpers.clearAndFillInWebElement(TOTAL_BILIRUBIN_INPUT, totalBilirubin);
  }

  private void fillSgot(String sgot) {
    webDriverHelpers.clearAndFillInWebElement(SGOT_INPUT, sgot);
  }

  private void fillConjBilirubin(String conjBilirubin) {
    webDriverHelpers.clearAndFillInWebElement(CONJ_BILIRUBIN_INPUT, conjBilirubin);
  }

  private void fillCretinine(String creatinine) {
    webDriverHelpers.clearAndFillInWebElement(CREATININE_INPUT, creatinine);
  }

  private void fillWbc(String wbc) {
    webDriverHelpers.clearAndFillInWebElement(WBC_INPUT, wbc);
  }

  private void fillPotassium(String potassium) {
    webDriverHelpers.clearAndFillInWebElement(POTASSIUM_INPUT, potassium);
  }

  private void fillPlatelets(String platelets) {
    webDriverHelpers.clearAndFillInWebElement(PLATELETS_INPUT, platelets);
  }

  private void fillUrea(String urea) {
    webDriverHelpers.clearAndFillInWebElement(UREA_INPUT, urea);
  }

  private void fillProthrombin(String prothrombin) {
    webDriverHelpers.clearAndFillInWebElement(PROTHROMBIN_INPUT, prothrombin);
  }

  private void fillHaemoglobin(String haemoglobin) {
    webDriverHelpers.clearAndFillInWebElement(HAEMOGLOBIN_INPUT, haemoglobin);
  }

  private void fillOtherTests(String otherTests) {
    webDriverHelpers.clearAndFillInWebElement(OTHER_TESTS_INPUT, otherTests);
  }

  private Sample collectSampleData() {
    return Sample.builder()
        .purposeOfTheSample(getPurposeOfSample())
        .dateOfCollection(getDateOfCollection())
        .timeOfCollection(getTimeOfCollection())
        .sampleType(getSampleType())
        .reasonForSample(getReasonForSample())
        .sampleID(Long.parseLong(getSampleID()))
        .laboratory(getLaboratory())
        .laboratoryName(getLaboratoryName())
        .labSampleId(Long.parseLong(getLabSampleID()))
        .received(getReceivedOption())
        .receivedDate(getReceivedDate())
        .specimenCondition(getSpecimenCondition())
        .commentsOnSample(getCommentsOnSample())
        .build();
  }

  private Sample collectAlternateSampleData() {
    return Sample.builder()
        .purposeOfTheSample(getPurposeOfSample())
        .dateOfCollection(getDateOfCollection())
        .timeOfCollection(getTimeOfCollection())
        .sampleType(getSampleType())
        .reasonForSample(getReasonForSample())
        .sampleID(Long.parseLong(getSampleID()))
        .commentsOnSample(getCommentsOnSample())
        .build();
  }

  private String getPurposeOfSample() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SAMPLE_EDIT_PURPOSE_OPTIONS);
  }

  private LocalDate getDateOfCollection() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_COLLECTED), DATE_FORMATTER);
  }

  private LocalTime getTimeOfCollection() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(COLLECTED_DATE_TIME_INPUT), TIME_FORMATTER);
  }

  private String getSampleType() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_TYPE_INPUT);
  }

  private String getReasonForSample() {
    return webDriverHelpers.getValueFromWebElement(REASON_FOR_SAMPLING_TESTING_INPUT);
  }

  private String getSampleID() {
    return webDriverHelpers.getValueFromWebElement(FIELD_SAMPLE_ID_INPUT);
  }

  private String getLaboratory() {
    return webDriverHelpers.getValueFromWebElement(LABORATORY_INPUT);
  }

  private String getPathogenPopupLaboratory() {
    return webDriverHelpers.getValueFromWebElement(PATHOGEN_LABORATORY_INPUT);
  }

  private String getPathogenPopupTestResult() {
    return webDriverHelpers.getValueFromWebElement(PATHOGEN_TEST_RESULT_INPUT);
  }

  private String getLaboratoryName() {
    return webDriverHelpers.getValueFromWebElement(LABORATORY_NAME_INPUT);
  }

  private String getReceivedOption() {
    return webDriverHelpers.getTextFromWebElement(RECEIVED_OPTION_BUTTON);
  }

  private LocalDate getReceivedDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_RECEIVED), DATE_FORMATTER);
  }

  private String getSpecimenCondition() {
    return webDriverHelpers.getValueFromWebElement(SPECIMEN_CONDITION_INPUT);
  }

  private String getLabSampleID() {
    return webDriverHelpers.getValueFromWebElement(LAB_SAMPLE_ID_INPUT);
  }

  private String getCommentsOnSample() {
    return webDriverHelpers.getValueFromWebElement(COMMENT_AREA_INPUT);
  }

  private String getSampleTestResult() {
    return webDriverHelpers.getTextFromWebElement(SAMPLE_TEST_RESULT_BUTTON);
  }

  private void selectLaboratoryNamePopup(String laboratoryName) {
    webDriverHelpers.clearAndFillInWebElement(LABORATORY_NAME_POPUP_INPUT, laboratoryName);
  }

  private LocalDate getReportDate(Locale locale) {
    if (locale.equals(Locale.GERMAN))
      return LocalDate.parse(
          webDriverHelpers.getValueFromWebElement(DATE_TEST_REPORT), DATE_FORMATTER_DE);
    else
      return LocalDate.parse(
          webDriverHelpers.getValueFromWebElement(DATE_TEST_REPORT), DATE_FORMATTER);
  }

  private String getTypeOfTest() {
    return webDriverHelpers.getValueFromWebElement(TYPE_OF_TEST_INPUT);
  }

  private String getTestedDisease() {
    return webDriverHelpers.getValueFromWebElement(TESTED_DISEASE_INPUT);
  }

  private LocalDate getDateOfResult(Locale locale) {
    if (locale.equals(Locale.GERMAN))
      return LocalDate.parse(
          webDriverHelpers.getValueFromWebElement(DATE_OF_RESULT), DATE_FORMATTER_DE);
    else
      return LocalDate.parse(
          webDriverHelpers.getValueFromWebElement(DATE_OF_RESULT), DATE_FORMATTER);
  }

  private LocalTime getTimeOfResult() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(TIME_OF_RESULT_INPUT), TIME_FORMATTER);
  }

  private String getTestResultComment() {
    return webDriverHelpers.getValueFromWebElement(TEST_RESULTS_COMMENT_AREA_INPUT);
  }

  private String getResultVerifiedByLabSupervisor() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_EDIT_OPTIONS);
  }

  private String getHaemoglobinInUrine() {
    return webDriverHelpers.getValueFromWebElement(HAEMOGLOBIN_IN_URINE_INPUT);
  }

  private String getProteinInUrine() {
    return webDriverHelpers.getValueFromWebElement(PROTEIN_IN_URINE_INPUT);
  }

  private String getCellsInUrine() {
    return webDriverHelpers.getValueFromWebElement(CELLS_IN_URINE_INPUT);
  }

  private String getPh() {
    return webDriverHelpers.getValueFromWebElement(PH_INPUT);
  }

  private String getPCO2() {
    return webDriverHelpers.getValueFromWebElement(PCO2_INPUT);
  }

  private String getPAO2() {
    return webDriverHelpers.getValueFromWebElement(PAO2_INPUT);
  }

  private String getHC03() {
    return webDriverHelpers.getValueFromWebElement(HCO3_INPUT);
  }

  private String getOxygen() {
    return webDriverHelpers.getValueFromWebElement(OXYGEN_INPUT);
  }

  private String getSgpt() {
    return webDriverHelpers.getValueFromWebElement(SGPT_INPUT);
  }

  private String getTotalBilirubin() {
    return webDriverHelpers.getValueFromWebElement(TOTAL_BILIRUBIN_INPUT);
  }

  private String getSgot() {
    return webDriverHelpers.getValueFromWebElement(SGOT_INPUT);
  }

  private String getConjBilirubin() {
    return webDriverHelpers.getValueFromWebElement(CONJ_BILIRUBIN_INPUT);
  }

  private String getCreatine() {
    return webDriverHelpers.getValueFromWebElement(CREATININE_INPUT);
  }

  private String getWbc() {
    return webDriverHelpers.getValueFromWebElement(WBC_INPUT);
  }

  private String getPotassium() {
    return webDriverHelpers.getValueFromWebElement(POTASSIUM_INPUT);
  }

  private String getPlatelets() {
    return webDriverHelpers.getValueFromWebElement(PLATELETS_INPUT);
  }

  private String getUrea() {
    return webDriverHelpers.getValueFromWebElement(UREA_INPUT);
  }

  private String getProthrombin() {
    return webDriverHelpers.getValueFromWebElement(PROTHROMBIN_INPUT);
  }

  private String getHaemoglobin() {
    return webDriverHelpers.getValueFromWebElement(HAEMOGLOBIN_INPUT);
  }

  private String getOtherResults() {
    return webDriverHelpers.getValueFromWebElement(OTHER_TESTS_INPUT);
  }

  private String getTypeOfTestCard() {
    return webDriverHelpers.getTextFromWebElement(PATHOGEN_CARD_TYPE_OF_TEST);
  }

  private String getTestResultsCommentsCard() {
    return webDriverHelpers.getTextFromWebElement(PATHOGEN_CARD_TEST_RESULT_COMMENT);
  }

  private String getTestedDiseaseCard() {
    return webDriverHelpers.getTextFromWebElement(PATHOGEN_CARD_DISEASE);
  }

  private LocalDate getDateOfResultCardDE() {
    String[] spl =
        webDriverHelpers.getTextFromWebElement(PATHOGEN_CARD_DATE_OF_RESULT).split("\\s+");
    LocalDate data = LocalDate.parse(spl[0], DATE_FORMATTER_DE);
    return data;
  }

  private String getSampleTestResultCard() {
    return webDriverHelpers.getTextFromWebElement(PATHOGEN_CARD_TEST_RESULT);
  }

  private LocalDate getDateOfCollectionCaseCardDE() {
    String[] spl =
        webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_DATE_OF_COLLECTED_SAMPLE).split("\\s+");
    LocalDate data = LocalDate.parse(spl[1], DATE_FORMATTER_DE);
    return data;
  }

  private String getLaboratoryCaseCard() {
    return webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_LABORATORY);
  }

  private String getNumberOfTests() {
    String spl[] =
        webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_NUMBER_OF_TESTS).split("\\s+");
    return spl[3];
  }

  private LocalDate getDateOfResultCaseCardDE() {
    String spl[] =
        webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_DATE_AND_TIME_OF_RESULT).split("\\s+");
    return LocalDate.parse(spl[5], DATE_FORMATTER_DE);
  }

  private LocalTime getTimeOfResultCaseCardDE() {
    String spl[] =
        webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_DATE_AND_TIME_OF_RESULT).split("\\s+");
    return LocalTime.parse(spl[6], DateTimeFormatter.ofPattern("HH:mm").localizedBy(Locale.GERMAN));
  }

  private String getTypeOfTestCaseCard() {
    return webDriverHelpers.getTextFromWebElement(SAMPLES_CARD_TEST_TYPE);
  }

  private Sample collectPathogenTestResultsData() {
    return Sample.builder()
        .sampleTestResults(getPathogenPopupTestResult())
        .typeOfTest(getTypeOfTest())
        .testedDisease(getTestedDisease())
        .dateOfResult(getDateOfResult(Locale.ENGLISH))
        .timeOfResult(getTimeOfResult())
        .laboratory(getPathogenPopupLaboratory())
        .resultVerifiedByLabSupervisor(getResultVerifiedByLabSupervisor())
        .testResultsComment(getTestResultComment())
        .build();
  }

  private Sample collectPathogenCardDataDE() {
    return Sample.builder()
        .typeOfTest(getTypeOfTestCard())
        .testResultsComment(getTestResultsCommentsCard())
        .testedDisease(getTestedDiseaseCard())
        .dateOfResult(getDateOfResultCardDE())
        .sampleTestResults(getSampleTestResultCard())
        .build();
  }

  private Sample collectCaseCardSampleDataDE() {
    return Sample.builder()
        .dateOfCollection(getDateOfCollectionCaseCardDE())
        .laboratory(getLaboratoryCaseCard())
        .dateOfResult(getDateOfResultCaseCardDE())
        .timeOfResult(getTimeOfResultCaseCardDE())
        .typeOfTest(getTypeOfTestCaseCard())
        .build();
  }

  private SampleAdditionalTest collectAdditionalTestResultsData() {
    return SampleAdditionalTest.builder()
        .dateOfResult(getDateOfResult(Locale.ENGLISH))
        .timeOfResult(getTimeOfResult())
        .haemoglobinInUrine(getHaemoglobinInUrine())
        .proteinInUrine(getProteinInUrine())
        .redBloodCellsInUrine(getCellsInUrine())
        .ph(getPh())
        .pCO2(getPCO2())
        .pAO2(getPAO2())
        .hCO3(getHC03())
        .oxygen(getOxygen())
        .sgpt(getSgpt())
        .totalBilirubin(getTotalBilirubin())
        .sgot(getSgot())
        .conjBilirubin(getConjBilirubin())
        .creatine(getCreatine())
        .wbc(getWbc())
        .potassium(getPotassium())
        .platelets(getPlatelets())
        .urea(getUrea())
        .prothrombin(getProthrombin())
        .haemoglobin(getHaemoglobin())
        .otherResults(getOtherResults())
        .build();
  }

  private Sample collectPathogenTestResultsDataDE() {
    return Sample.builder()
        .sampleTestResults(getPathogenPopupTestResult())
        .reportDate(getReportDate(Locale.GERMAN))
        .typeOfTest(getTypeOfTest())
        .testedDisease(getTestedDisease())
        .dateOfResult(getDateOfResult(Locale.GERMAN))
        .timeOfResult(getTimeOfResult())
        .laboratory(getPathogenPopupLaboratory())
        .laboratoryName(getLaboratoryName())
        .resultVerifiedByLabSupervisor(getResultVerifiedByLabSupervisor())
        .testResultsComment(getTestResultComment())
        .build();
  }

  private Sample simplePathogenBuilderResult(String testType) {
    SampleService sampleService = new SampleService(faker);
    sampleTestResult = sampleService.buildPathogenTestResultType(testType);
    // fillReportDate(sampleTestResult.getReportDate(), Locale.ENGLISH);
    selectTypeOfTest(sampleTestResult.getTypeOfTest());
    selectTestedDisease(sampleTestResult.getTestedDisease());
    selectPathogenLaboratory(sampleTestResult.getLaboratory());
    selectTestResult(sampleTestResult.getSampleTestResults());
    fillDateOfResult(sampleTestResult.getDateOfResult(), Locale.ENGLISH);
    fillTimeOfResult(sampleTestResult.getTimeOfResult());
    selectResultVerifiedByLabSupervisor(
        sampleTestResult.getResultVerifiedByLabSupervisor(),
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
    fillTestResultsComment(sampleTestResult.getTestResultsComment());
    return sampleTestResult;
  }

  private Sample simplePathogenBuilderVerifiedResult(String testType) {
    SampleService sampleService = new SampleService(faker);
    sampleTestResult = sampleService.buildPathogenTestResultTypeVerified(testType);
    // fillReportDate(sampleTestResult.getReportDate(), Locale.ENGLISH);
    selectTypeOfTest(sampleTestResult.getTypeOfTest());
    selectTestedDisease(sampleTestResult.getTestedDisease());
    selectPathogenLaboratory(sampleTestResult.getLaboratory());
    selectLaboratoryNamePopup(sampleTestResult.getLaboratoryName());
    selectTestResult(sampleTestResult.getSampleTestResults());
    fillDateOfResult(sampleTestResult.getDateOfResult(), Locale.ENGLISH);
    fillTimeOfResult(sampleTestResult.getTimeOfResult());
    selectResultVerifiedByLabSupervisor(
        sampleTestResult.getResultVerifiedByLabSupervisor(),
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
    fillTestResultsComment(sampleTestResult.getTestResultsComment());
    return sampleTestResult;
  }

  private Sample buildPathogenTestDE(String testType) {
    SampleService sampleService = new SampleService(faker);
    sampleTestResult = sampleService.buildPathogenTestUnverifiedDE(testType);
    fillReportDate(sampleTestResult.getReportDate(), Locale.GERMAN);
    selectTypeOfTest(sampleTestResult.getTypeOfTest());
    selectTestedDisease(sampleTestResult.getTestedDisease());
    selectPathogenLaboratory(sampleTestResult.getLaboratory());
    selectLaboratoryNamePopup(sampleTestResult.getLaboratoryName());
    selectTestResult(sampleTestResult.getSampleTestResults());
    fillDateOfResult(sampleTestResult.getDateOfResult(), Locale.GERMAN);
    fillTimeOfResult(sampleTestResult.getTimeOfResult());
    selectResultVerifiedByLabSupervisor(
        sampleTestResult.getResultVerifiedByLabSupervisor(),
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
    fillTestResultsComment(sampleTestResult.getTestResultsComment());
    return sampleTestResult;
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(EVENT_ACTIONS_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(EVENT_ACTIONS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(EVENT_ACTIONS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(EVENT_ACTIONS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(EVENT_ACTIONS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
