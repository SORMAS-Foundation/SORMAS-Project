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

import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.*;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.*;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Sample;
import org.sormas.e2etests.entities.services.SampleService;
import org.sormas.e2etests.enums.PathogenTestResults;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class CreateNewSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample sample;
  public static Sample sampleTestResult;
  public static String sampleId;
  private final WebDriverHelpers webDriverHelpers;
  private final Faker faker;

  @Inject
  public CreateNewSampleSteps(
      WebDriverHelpers webDriverHelpers,
      SampleService sampleService,
      Faker faker,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

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
        "^I save the created sample",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
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
          fillReportDate(sampleTestResult.getReportDate());
          selectTypeOfTest(sampleTestResult.getTypeOfTest());
          selectTestedDisease(sampleTestResult.getTestedDisease());
          selectPathogenLaboratory(sampleTestResult.getLaboratory());
          selectTestResult(sampleTestResult.getSampleTestResults());
          fillDateOfResult(sampleTestResult.getDateOfResult());
          fillTimeOfResult(sampleTestResult.getTimeOfResult());
          selectResultVerifiedByLabSupervisor(
              sampleTestResult.getResultVerifiedByLabSupervisor(),
              RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          fillTestResultsComment(sampleTestResult.getTestResultsComment());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
        });

    When(
        "I fill all fields from Pathogen test for COVID-19 disease result popup and save",
        () -> {
          sampleTestResult = sampleService.buildGeneratedSampleTestResultForCovid();
          fillReportDate(sampleTestResult.getReportDate());
          selectTypeOfTest(sampleTestResult.getTypeOfTest());
          selectTestedDisease(sampleTestResult.getTestedDisease());
          selectPathogenLaboratory(sampleTestResult.getLaboratory());
          selectTestResult(sampleTestResult.getSampleTestResults());
          fillDateOfResult(sampleTestResult.getDateOfResult());
          fillTimeOfResult(sampleTestResult.getTimeOfResult());
          selectResultVerifiedByLabSupervisor(
              sampleTestResult.getResultVerifiedByLabSupervisor(),
              RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
          fillTestResultsComment(sampleTestResult.getTestResultsComment());
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
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(EDIT_PATHOGEN_TEST_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              sampleTestResult,
              actualSampleTestResult,
              List.of(
                  "laboratory",
                  "sampleTestResults",
                  "reportDate",
                  "typeOfTest",
                  "testedDisease",
                  "dateOfResult",
                  "timeOfResult",
                  "resultVerifiedByLabSupervisor",
                  "testResultsComment"));
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
        "^I check that the created Pathogen is correctly displayed",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TEST_RESULTS_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsData();
          ComparisonHelper.compareEqualEntities(sampleTestResult, actualSampleTestResult);
        });

    When(
        "I confirm the Create case from contact with positive test result",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(CREATE_CASE_POSITIVE_TEST_RESULT_LABEL);
          String expectedText = "Create case from contact with positive test result?";
          softly.assertEquals(displayedText, expectedText);
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON);
        });

    When(
        "I confirm the Create case from event participant with positive test result",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_BUTTON);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(CREATE_CASE_POSITIVE_TEST_RESULT_LABEL);
          String expectedText = "Create case from event participant with positive test result?";
          softly.assertEquals(displayedText, expectedText);
          softly.assertAll();
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

  private void fillReportDate(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_TEST_REPORT, DATE_FORMATTER.format(dateOfCollection));
  }

  private void selectTypeOfTest(String typeOfTest) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_TEST_COMBOBOX, typeOfTest);
  }

  private void selectTestedDisease(String typeOfDisease) {
    webDriverHelpers.selectFromCombobox(TESTED_DISEASE_COMBOBOX, typeOfDisease);
  }

  private void fillDateOfResult(LocalDate dateOfCollection) {
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

  private LocalDate getReportDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_TEST_REPORT), DATE_FORMATTER);
  }

  private String getTypeOfTest() {
    return webDriverHelpers.getValueFromWebElement(TYPE_OF_TEST_INPUT);
  }

  private String getTestedDisease() {
    return webDriverHelpers.getValueFromWebElement(TESTED_DISEASE_INPUT);
  }

  private LocalDate getDateOfResult() {
    return LocalDate.parse(webDriverHelpers.getValueFromWebElement(DATE_OF_RESULT), DATE_FORMATTER);
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

  private Sample collectPathogenTestResultsData() {
    return Sample.builder()
        .sampleTestResults(getPathogenPopupTestResult())
        .reportDate(getReportDate())
        .typeOfTest(getTypeOfTest())
        .testedDisease(getTestedDisease())
        .dateOfResult(getDateOfResult())
        .timeOfResult(getTimeOfResult())
        .laboratory(getPathogenPopupLaboratory())
        .resultVerifiedByLabSupervisor(getResultVerifiedByLabSupervisor())
        .testResultsComment(getTestResultComment())
        .build();
  }

  private Sample simplePathogenBuilderResult(String testType) {
    SampleService sampleService = new SampleService(faker);
    sampleTestResult = sampleService.buildPathogenTestResultType(testType);
    fillReportDate(sampleTestResult.getReportDate());
    selectTypeOfTest(sampleTestResult.getTypeOfTest());
    selectTestedDisease(sampleTestResult.getTestedDisease());
    selectPathogenLaboratory(sampleTestResult.getLaboratory());
    selectTestResult(sampleTestResult.getSampleTestResults());
    fillDateOfResult(sampleTestResult.getDateOfResult());
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
    fillReportDate(sampleTestResult.getReportDate());
    selectTypeOfTest(sampleTestResult.getTypeOfTest());
    selectTestedDisease(sampleTestResult.getTestedDisease());
    selectPathogenLaboratory(sampleTestResult.getLaboratory());
    selectLaboratoryNamePopup(sampleTestResult.getLaboratoryName());
    selectTestResult(sampleTestResult.getSampleTestResults());
    fillDateOfResult(sampleTestResult.getDateOfResult());
    fillTimeOfResult(sampleTestResult.getTimeOfResult());
    selectResultVerifiedByLabSupervisor(
        sampleTestResult.getResultVerifiedByLabSupervisor(),
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_OPTIONS);
    fillTestResultsComment(sampleTestResult.getTestResultsComment());
    return sampleTestResult;
  }
}
