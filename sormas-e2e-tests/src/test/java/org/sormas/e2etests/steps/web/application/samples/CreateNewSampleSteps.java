/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Sample;
import org.sormas.e2etests.services.SampleService;

public class CreateNewSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample sample;
  public static Sample sampleTestResult;
  public static String sampleId;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewSampleSteps(WebDriverHelpers webDriverHelpers, SampleService sampleService) {
    this.webDriverHelpers = webDriverHelpers;

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
        "^I check the created Sample is correctly displayed on Edit Sample page",
        () -> {
          final Sample actualSample = collectSampleData();
          Truth.assertThat(sample).isEqualTo(actualSample);
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
        "I collect the sample UUID displayed on create new sample page",
        () -> sampleId = collectSampleUuid());

    When(
        "^I check that the created Pathogen is correctly displayed",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_TEST_RESULTS_BUTTON);
          final Sample actualSampleTestResult = collectPathogenTestResultsData();
          Truth.assertThat(sampleTestResult).isEqualTo(actualSampleTestResult);
        });
  }

  public void selectPurposeOfSample(String samplePurpose, By element) {
    webDriverHelpers.clickWebElementByText(element, samplePurpose);
  }

  public String collectSampleUuid() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_UUID);
  }

  public void fillDateOfCollection(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_COLLECTED, DATE_FORMATTER.format(dateOfCollection));
  }

  public void fillTimeOfCollection(LocalTime timeOfCollection) {
    webDriverHelpers.selectFromCombobox(
        COLLECTED_DATE_TIME_COMBOBOX, TIME_FORMATTER.format(timeOfCollection));
  }

  public void selectSampleType(String sampleType) {
    webDriverHelpers.selectFromCombobox(SAMPLE_TYPE_COMBOBOX, sampleType);
  }

  public void selectReasonForSample(String reasonForSample) {
    webDriverHelpers.selectFromCombobox(REASON_FOR_SAMPLING_TESTING_COMBOBOX, reasonForSample);
  }

  public void fillSampleID(long sampleID) {
    webDriverHelpers.clearAndFillInWebElement(FIELD_SAMPLE_ID_INPUT, String.valueOf(sampleID));
  }

  public void selectLaboratory(String laboratory) {
    webDriverHelpers.selectFromCombobox(LABORATORY_COMBOBOX, laboratory);
  }

  public void selectPathogenLaboratory(String laboratory) {
    webDriverHelpers.selectFromCombobox(PATHOGEN_LABORATORY_COMBOBOX, laboratory);
  }

  public void selectTestResult(String testResult) {
    webDriverHelpers.selectFromCombobox(PATHOGEN_TEST_RESULT_COMBOBOX, testResult);
  }

  public void selectSpecimenCondition(String specimenCondition) {
    webDriverHelpers.selectFromCombobox(SPECIMEN_CONDITION_COMBOBOX, specimenCondition);
  }

  public void fillReceivedDate(LocalDate receivedDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_RECEIVED, DATE_FORMATTER.format(receivedDate));
  }

  public void selectLaboratoryName(String laboratoryName) {
    webDriverHelpers.clearAndFillInWebElement(LABORATORY_NAME_INPUT, laboratoryName);
  }

  public void selectReceivedOptionButton(String received) {
    webDriverHelpers.clickWebElementByText(RECEIVED_OPTION_BUTTON, received);
  }

  public void fillLabSampleId(long labSampleId) {
    webDriverHelpers.clearAndFillInWebElement(LAB_SAMPLE_ID_INPUT, String.valueOf(labSampleId));
  }

  public void fillCommentsOnSample(String commentsOnSample) {
    webDriverHelpers.clearAndFillInWebElement(COMMENT_AREA_INPUT, commentsOnSample);
  }

  public void selectSampleTestResultButton() {
    webDriverHelpers.clickOnWebElementBySelector(SAMPLE_TEST_RESULT_BUTTON);
  }

  public void fillReportDate(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_TEST_REPORT, DATE_FORMATTER.format(dateOfCollection));
  }

  public void selectTypeOfTest(String typeOfTest) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_TEST_COMBOBOX, typeOfTest);
  }

  public void selectTestedDisease(String typeOfDisease) {
    webDriverHelpers.selectFromCombobox(TESTED_DISEASE_COMBOBOX, typeOfDisease);
  }

  public void fillDateOfResult(LocalDate dateOfCollection) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_OF_RESULT, DATE_FORMATTER.format(dateOfCollection));
  }

  public void fillTimeOfResult(LocalTime timeOfCollection) {
    webDriverHelpers.selectFromCombobox(
        TIME_OF_RESULT_COMBOBOX, TIME_FORMATTER.format(timeOfCollection));
  }

  public void selectResultVerifiedByLabSupervisor(String resultVerified, By element) {
    webDriverHelpers.clickWebElementByText(element, resultVerified);
  }

  public void fillTestResultsComment(String testResultsComment) {
    webDriverHelpers.clearAndFillInWebElement(TEST_RESULTS_COMMENT_AREA_INPUT, testResultsComment);
  }

  public Sample collectSampleData() {
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

  public String getPurposeOfSample() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SAMPLE_EDIT_PURPOSE_OPTIONS);
  }

  public LocalDate getDateOfCollection() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_COLLECTED), DATE_FORMATTER);
  }

  public LocalTime getTimeOfCollection() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(COLLECTED_DATE_TIME_INPUT), TIME_FORMATTER);
  }

  public String getSampleType() {
    return webDriverHelpers.getValueFromWebElement(SAMPLE_TYPE_INPUT);
  }

  public String getReasonForSample() {
    return webDriverHelpers.getValueFromWebElement(REASON_FOR_SAMPLING_TESTING_INPUT);
  }

  public String getSampleID() {
    return webDriverHelpers.getValueFromWebElement(FIELD_SAMPLE_ID_INPUT);
  }

  public String getLaboratory() {
    return webDriverHelpers.getValueFromWebElement(LABORATORY_INPUT);
  }

  public String getPathogenPopupLaboratory() {
    return webDriverHelpers.getValueFromWebElement(PATHOGEN_LABORATORY_INPUT);
  }

  public String getPathogenPopupTestResult() {
    return webDriverHelpers.getValueFromWebElement(PATHOGEN_TEST_RESULT_INPUT);
  }

  public String getLaboratoryName() {
    return webDriverHelpers.getValueFromWebElement(LABORATORY_NAME_INPUT);
  }

  public String getReceivedOption() {
    return webDriverHelpers.getTextFromWebElement(RECEIVED_OPTION_BUTTON);
  }

  public LocalDate getReceivedDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_RECEIVED), DATE_FORMATTER);
  }

  public String getSpecimenCondition() {
    return webDriverHelpers.getValueFromWebElement(SPECIMEN_CONDITION_INPUT);
  }

  public String getLabSampleID() {
    return webDriverHelpers.getValueFromWebElement(LAB_SAMPLE_ID_INPUT);
  }

  public String getCommentsOnSample() {
    return webDriverHelpers.getValueFromWebElement(COMMENT_AREA_INPUT);
  }

  public String getSampleTestResult() {
    return webDriverHelpers.getTextFromWebElement(SAMPLE_TEST_RESULT_BUTTON);
  }

  public LocalDate getReportDate() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(DATE_TEST_REPORT), DATE_FORMATTER);
  }

  public String getTypeOfTest() {
    return webDriverHelpers.getValueFromWebElement(TYPE_OF_TEST_INPUT);
  }

  public String getTestedDisease() {
    return webDriverHelpers.getValueFromWebElement(TESTED_DISEASE_INPUT);
  }

  public LocalDate getDateOfResult() {
    return LocalDate.parse(webDriverHelpers.getValueFromWebElement(DATE_OF_RESULT), DATE_FORMATTER);
  }

  public LocalTime getTimeOfResult() {
    return LocalTime.parse(
        webDriverHelpers.getValueFromWebElement(TIME_OF_RESULT_INPUT), TIME_FORMATTER);
  }

  public String getTestResultComment() {
    return webDriverHelpers.getValueFromWebElement(TEST_RESULTS_COMMENT_AREA_INPUT);
  }

  public String getResultVerifiedByLabSupervisor() {
    return webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
        RESULT_VERIFIED_BY_LAB_SUPERVISOR_EDIT_OPTIONS);
  }

  public Sample collectPathogenTestResultsData() {
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
}
