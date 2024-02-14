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

import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.FINAL_LABORATORY_RESULT_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.CreateNewSamplePage.SAVE_EDIT_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.ADDIITONAL_NEW_TEST_RESULT_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.BACK_TO_CASE_DE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.COLLECTED_DATE_TIME_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.COLLECTED_DATE_TIME_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.COMMENT_AREA_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.CQ_CT_VALUE_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DATE_SAMPLE_COLLECTED;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DATE_SAMPLE_RECEIVED;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DATE_SAMPLE_WAS_SENT_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_PATHOGEN_TEST_RESULT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DISCARD_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.EXTERNAL_LAB_TESTING_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.FIELD_SAMPLE_ID_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.FOUR_FOLD_INCREASE_ANTIBODY_TITER;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.INTERNAL_LAB_TESTING_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.LABORATORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.LABORATORY_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.LABORATORY_NAME_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.LAB_SAMPLE_ID_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.NEW_TEST_RESULT_DE;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PATHOGEN_NEW_TEST_RESULT_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PATHOGEN_TEST_RESULT_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.PCR_TEST_SPECIFICATION_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.REASON_FOR_SAMPLING_TESTING_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.REASON_FOR_SAMPLING_TESTING_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.RECEIVED_OPTION_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_DELETION_POPUP_YES_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAVE_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SEE_SAMPLES_FOR_THIS_PERSON_BUTTON_DE;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SPECIFY_TEST_DETAILS_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SPECIMEN_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SPECIMEN_CONDITION_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SPECIMEN_CONDITION_NOT_MANDATORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.TYPING_ID_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.UUID_FIELD;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_EDIT_PURPOSE_OPTIONS;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SEARCH_INPUT;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.messages.MessagesDirectorySteps.convertStringToChosenFormatDate;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Sample;
import org.sormas.e2etests.entities.services.SampleService;
import org.sormas.e2etests.entities.services.api.demis.DemisApiService;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class EditSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter DATE_FORMATTER_DE =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");
  public static LocalDate dateOfSampleCollected;

  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static Sample editedSample;

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public EditSampleSteps(
      WebDriverHelpers webDriverHelpers,
      RunningConfiguration runningConfiguration,
      SampleService sampleService,
      ApiState apiState,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the last created sample via API",
        () -> {
          String LAST_CREATED_SAMPLE_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!samples/data/"
                  + apiState.getCreatedSample().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_SAMPLE_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_FIELD);
        });

    When(
        "I delete the sample",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DELETE_SAMPLE_REASON_POPUP);
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
          if (webDriverHelpers.isElementPresent(SAMPLE_SEARCH_INPUT))
            webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAMPLE_SEARCH_INPUT);
        });

    When(
        "^I click on the new pathogen test from the Edit Sample page for DE version$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_TEST_RESULT_DE);
          webDriverHelpers.scrollToElement(NEW_TEST_RESULT_DE);
          webDriverHelpers.clickOnWebElementBySelector(NEW_TEST_RESULT_DE);
        });

    When(
        "^I click on the new pathogen test from the Edit Sample page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.scrollToElement(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(PATHOGEN_NEW_TEST_RESULT_BUTTON);
        });
    When(
        "^I click on the new additional test from the Edit Sample page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ADDIITONAL_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.scrollToElement(ADDIITONAL_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ADDIITONAL_NEW_TEST_RESULT_BUTTON);
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
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
        });

    When(
        "I set type of sample to {string} on Sample Edit page",
        (String sampleType) -> selectSampleType(sampleType));

    When(
        "I set date sample was collected to yesterday on Sample Edit page",
        () -> fillDateOfCollection(LocalDate.now().minusDays(1)));

    When(
        "I click on Received checkbox in Sample Edit page",
        () -> webDriverHelpers.clickOnWebElementBySelector(RECEIVED_OPTION_BUTTON));

    Then(
        "I check if {string} combobox is available",
        (String option) -> {
          switch (option) {
            case ("Specimen condition"):
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(SPECIMEN_CONDITION_INPUT);
              break;
            case ("Date sample received at lab"):
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_SAMPLE_RECEIVED);
              break;
            case ("Lab sample ID"):
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(LAB_SAMPLE_ID_INPUT);
              break;
          }
        });

    Then(
        "I check if Specimen condition combobox is not mandatory",
        () ->
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                SPECIMEN_CONDITION_NOT_MANDATORY_COMBOBOX));

    When(
        "I click on Save Button in Sample Edit page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_SAMPLE_BUTTON));

    When(
        "I check if sample material has a option {string}",
        (String option) -> webDriverHelpers.selectFromCombobox(SAMPLE_TYPE_COMBOBOX, option));

    When(
        "I set type of sample to {string}",
        (String sampleType) ->
            webDriverHelpers.selectFromCombobox(SAMPLE_TYPE_COMBOBOX, sampleType));

    And(
        "I check if type of sample is set to {string}",
        (String option) -> {
          softly.assertEquals(webDriverHelpers.getValueFromCombobox(SAMPLE_TYPE_COMBOBOX), option);
          softly.assertAll();
        });

    And(
        "I check that all editable fields are enabled for a sample",
        () -> {
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(EXTERNAL_LAB_TESTING_RADIOBUTTON),
              true,
              "External lab testing radiobutton is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(INTERNAL_LAB_TESTING_RADIOBUTTON),
              true,
              "Internal lab testing radiobutton is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DATE_SAMPLE_COLLECTED),
              true,
              "Date sample of collected field is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(COLLECTED_DATE_TIME_COMBOBOX),
              true,
              "Sample Date time field is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(SAMPLE_TYPE_COMBOBOX),
              true,
              "Type of sample combobox is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(REASON_FOR_SAMPLING_TESTING_COMBOBOX),
              true,
              "Reason for sampling testing combobox field is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(FIELD_SAMPLE_ID_INPUT),
              true,
              "Field Sample id input is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(COMMENT_AREA_INPUT),
              true,
              "Comment area input is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(FINAL_LABORATORY_RESULT_COMBOBOX),
              true,
              "Final laboratory result combobox is not editable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DELETE_SAMPLE_BUTTON),
              true,
              "Delete sample button is not enable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISCARD_SAMPLE_BUTTON),
              true,
              "Discard sample button is not enable");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(SAVE_SAMPLE_BUTTON),
              true,
              "Save sample button is not enable");
          softly.assertAll();
        });

    And(
        "I check if editable fields are read only for a sample",
        () -> {
          webDriverHelpers.waitForElementPresent(DATE_SAMPLE_COLLECTED, 2);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DATE_SAMPLE_COLLECTED),
              false,
              "Date sample of collected field is not editable state but it should be!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(COLLECTED_DATE_TIME_INPUT),
              false,
              "Sample Date time field is not editable state but it should be!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(SAMPLE_TYPE_INPUT),
              false,
              "Type of sample combobox is not editable state but it should be!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(REASON_FOR_SAMPLING_TESTING_INPUT),
              false,
              "Reason for sampling testing combobox field is not editable state but it should be!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DATE_SAMPLE_WAS_SENT_INPUT),
              false,
              "Date sample was sent field is not editable state but it should be!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(PATHOGEN_TEST_RESULT_INPUT),
              false,
              "Pathogen test result field is not editable state but it should be!");
          softly.assertAll();
        });

    And(
        "I check if type of sample is not set to {string}",
        (String option) -> {
          softly.assertNotEquals(
              webDriverHelpers.getValueFromCombobox(SAMPLE_TYPE_COMBOBOX), option);
          softly.assertAll();
        });

    When(
        "I click on See samples for this person button",
        () -> webDriverHelpers.clickOnWebElementBySelector(SEE_SAMPLES_FOR_THIS_PERSON_BUTTON_DE));

    When(
        "I set date sample was collected minus (\\d+) days ago on Sample Edit page",
        (Integer days) -> {
          webDriverHelpers.clearAndFillInWebElement(
              DATE_SAMPLE_COLLECTED, DATE_FORMATTER_DE.format(LocalDate.now().minusDays(days)));
        });

    When(
        "I check if date of sample is set for (\\d+) day ago from today on Edit Sample page for DE version",
        (Integer days) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_COLLECTED),
              DATE_FORMATTER_DE.format(LocalDate.now().minusDays(days)),
              "Date is inncorect");
          softly.assertAll();
        });

    When(
        "I collect date of sample from on Edit Sample page for DE version",
        () -> {
          String localDateOfSampleCollected =
              webDriverHelpers.getValueFromWebElement(DATE_SAMPLE_COLLECTED);
          dateOfSampleCollected =
              convertStringToChosenFormatDate(
                  "dd.MM.yyyy", "yyyy-MM-dd", localDateOfSampleCollected);
        });

    Then(
        "^I check that laboratory is set to \"([^\"]*)\" on Edit Sample page$",
        (String labor) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LABORATORY_INPUT),
              labor,
              "Laboratory is incorrect");
          softly.assertAll();
        });

    And(
        "^I check that laboratory details is set to \"([^\"]*)\" on edit Sample page$",
        (String laborDetails) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(LABORATORY_NAME_INPUT),
              laborDetails,
              "Laboratory details are incorrect");
          softly.assertAll();
        });

    Then(
        "I check that lab sample id match {string} specimen id from Demis message on Edit Sample page",
        (String specimen) -> {
          switch (specimen) {
            case "first":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(LAB_SAMPLE_ID_INPUT),
                  DemisApiService.specimenUUID,
                  "Sample id is incorrect");
              softly.assertAll();
              break;
            case "second":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(LAB_SAMPLE_ID_INPUT),
                  DemisApiService.secondSpecimenUUID,
                  "Sample id is incorrect");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I back to the case from Edit Sample page DE$",
        () -> {
          webDriverHelpers.scrollToElement(BACK_TO_CASE_DE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(BACK_TO_CASE_DE_BUTTON);
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
