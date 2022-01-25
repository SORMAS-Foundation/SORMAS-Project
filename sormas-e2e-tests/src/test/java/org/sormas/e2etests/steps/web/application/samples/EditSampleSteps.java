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

import static org.sormas.e2etests.pages.application.samples.EditSamplePage.*;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.pojo.web.Sample;
import org.sormas.e2etests.services.SampleService;
import org.sormas.e2etests.state.ApiState;

public class EditSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample editedSample;

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public EditSampleSteps(
      WebDriverHelpers webDriverHelpers,
      @Named("ENVIRONMENT_URL") String environmentUrl,
      SampleService sampleService,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the last created sample via API",
        () -> {
          String LAST_CREATED_SAMPLE_URL =
              environmentUrl
                  + "/sormas-webdriver/#!samples/data/"
                  + apiState.getCreatedSample().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_SAMPLE_URL);
        });

    When(
        "I delete the sample",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAMPLE_DELETION_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAMPLE_SEARCH_INPUT);
        });

    When(
        "I click on the new pathogen test from the Edit Sample page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.scrollToElement(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(PATHOGEN_NEW_TEST_RESULT_BUTTON);
        });

    When(
        "^I change all Sample fields and save$",
        () -> {
          editedSample = sampleService.buildEditSample();
          selectPurposeOfSample(editedSample.getPurposeOfTheSample(), SAMPLE_EDIT_PURPOSE_OPTIONS);
          fillDateOfCollection(editedSample.getDateOfCollection());
          fillTimeOfCollection(editedSample.getTimeOfCollection());
          selectSampleType(editedSample.getSampleType());
          selectReasonForSample(editedSample.getReasonForSample());
          fillSampleID(editedSample.getSampleID());
          selectLaboratory(editedSample.getLaboratory());
          selectLaboratoryName(editedSample.getLaboratoryName());
          fillReceivedDate(editedSample.getReceivedDate());
          selectSpecimenCondition(editedSample.getSpecimenCondition());
          fillLabSampleId(editedSample.getLabSampleId());
          fillCommentsOnSample(editedSample.getCommentsOnSample());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_SAMPLE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });

    When(
        "^I check the edited Sample is correctly displayed on Edit Sample page",
        () -> {
          final Sample actualSample = collectSampleData();
          ComparisonHelper.compareEqualEntities(editedSample, actualSample);
        });

    When(
        "I check that if Four Fold Increase Antibody Titer displayed",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              FOUR_FOLD_INCREASE_ANTIBODY_TITER);
        });

    When(
        "I check that if CQ CT Value field is correctly displayed",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(CQ_CT_VALUE_INPUT);
        });

    When(
        "I check that if Sequencing or DNA Microarray field is correctly displayed",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(TYPING_ID_INPUT);
        });

    When(
        "I check that if PCR RT PCR fields are correctly displayed",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(CQ_CT_VALUE_INPUT);
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              PCR_TEST_SPECIFICATION_COMBOBOX);
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              SPECIFY_TEST_DETAILS_INPUT);
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(TYPING_ID_INPUT);
        });

    When(
        "I check that if Other field is correctly displayed",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              SPECIFY_TEST_DETAILS_INPUT);
        });

    When(
        "I delete the Pathogen test",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_PATHOGEN_TEST_RESULT);
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
        });
  }

  private void selectPurposeOfSample(String samplePurpose, By element) {
    webDriverHelpers.clickWebElementByText(element, samplePurpose);
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

  private void selectSpecimenCondition(String specimenCondition) {
    webDriverHelpers.selectFromCombobox(SPECIMEN_CONDITION_COMBOBOX, specimenCondition);
  }

  private void fillReceivedDate(LocalDate receivedDate) {
    webDriverHelpers.clearAndFillInWebElement(
        DATE_SAMPLE_RECEIVED, DATE_FORMATTER.format(receivedDate));
  }

  private void selectLaboratoryName(String laboratoryName) {
    webDriverHelpers.clearAndFillInWebElement(LABORATORY_NAME_INPUT, laboratoryName);
  }

  private void fillLabSampleId(long labSampleId) {
    webDriverHelpers.clearAndFillInWebElement(LAB_SAMPLE_ID_INPUT, String.valueOf(labSampleId));
  }

  private void fillCommentsOnSample(String commentsOnSample) {
    webDriverHelpers.clearAndFillInWebElement(COMMENT_AREA_INPUT, commentsOnSample);
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

  private String getLabSampleID() {
    return webDriverHelpers.getValueFromWebElement(LAB_SAMPLE_ID_INPUT);
  }

  private String getCommentsOnSample() {
    return webDriverHelpers.getValueFromWebElement(COMMENT_AREA_INPUT);
  }

  private String getSpecimenCondition() {
    return webDriverHelpers.getValueFromWebElement(SPECIMEN_CONDITION_INPUT);
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
}
