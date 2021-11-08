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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewVisitPage.*;
import static org.sormas.e2etests.pages.application.contacts.FollowUpVisitsTabPage.CONTACTS_LIST_BUTTON;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.FollowUpVisit;
import org.sormas.e2etests.services.FollowUpVisitService;
import org.sormas.e2etests.state.ApiState;

public class CreateNewVisitSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static FollowUpVisit followUpVisit;
  public static FollowUpVisit followUpEditVisit;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public CreateNewVisitSteps(
      WebDriverHelpers webDriverHelpers,
      FollowUpVisitService followUpVisitService,
      ApiState apiState,
      final SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Follow-up visit",
        () -> {
          followUpVisit = followUpVisitService.buildGeneratedFollowUpVisit();
          webDriverHelpers.waitForPageLoaded();
          selectPersonAvailableAndCooperative(followUpVisit.getPersonAvailableAndCooperative());
          fillDateAndTimeVisit(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());
          fillVisitRemark(followUpVisit.getVisitRemarks());
          selectBodyTemperature(followUpVisit.getCurrentBodyTemperature());
          selectSourceOfBodyTemperature(followUpVisit.getSourceOfBodyTemperature());
          selectChillsOrSweats(followUpVisit.getChillsOrSweats());
          selectFeelingIll(followUpVisit.getFeelingIll());
          selectFever(followUpVisit.getFever());
          selectHeadache(followUpVisit.getHeadache());
          selectMusclePain(followUpVisit.getMusclePain());
          selectShivering(followUpVisit.getShivering());
          selectAcuteRespiratoryDistressSyndrome(
              followUpVisit.getAcuteRespiratoryDistressSyndrome());
          selectCough(followUpVisit.getCough());
          selectDifficultyBreathing(followUpVisit.getDifficultyBreathing());
          selectOxygenSaturationLower94(followUpVisit.getOxygenSaturation94());
          selectPneumoniaClinicalOrRadiologic(followUpVisit.getPneumoniaClinicalRadiologic());
          selectRapidBreathing(followUpVisit.getRapidBreathing());
          selectRespiratoryDiseaseVentilation(
              followUpVisit.getRespiratoryDiseaseRequiringVentilation());
          selectRunnyNose(followUpVisit.getRunnyNose());
          selectSoreThroat(followUpVisit.getSoreThroatPharyngitis());
          selectFastHeartRate(followUpVisit.getFastHeartRate());
          selectDiarrhea(followUpVisit.getDiarrhea());
          selectNausea(followUpVisit.getNausea());
          selectLossOfSmell(followUpVisit.getNewLossOfSmell());
          selectLossOfTaste(followUpVisit.getNewLossOfTaste());
          selectOtherClinicalSymptoms(followUpVisit.getOtherClinicalSymptoms());
          fillComments(followUpVisit.getComments());
          selectFirstSymptom(followUpVisit.getFirstSymptom());
          fillDateOfFirstSymptom(followUpVisit.getDateOfSymptomOnset());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_VISIT_BUTTON);
        });

    Then(
        "^I validate recently created follow up is correctly displayed$",
        () -> {
          final FollowUpVisit actualFollowUpVisit = collectFollowUpData();
          Truth.assertThat(followUpVisit).isEqualTo(actualFollowUpVisit);
        });

    And(
        "I click on discard button from follow up view",
        () -> webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON));

    And(
        "^I change Follow-up visit fields and save$",
        () -> {
          followUpEditVisit = followUpVisitService.buildEditFollowUpVisit();
          webDriverHelpers.waitForPageLoaded();
          selectPersonAvailableAndCooperative(followUpEditVisit.getPersonAvailableAndCooperative());
          fillDateAndTimeVisit(followUpVisitService.buildEditFollowUpVisit().getDateOfVisit());
          fillVisitRemark(followUpEditVisit.getVisitRemarks());
        });

    Then(
        "^I check all changes from follow up are correctly displayed$",
        () -> {
          final FollowUpVisit editedFollowUpVisit = collectEditedFollowUpData();
          Truth.assertThat(followUpEditVisit).isEqualTo(editedFollowUpVisit);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_VISIT_BUTTON);
        });

    And(
        "^I am accessing the contacts from New Visit$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACTS_LIST_BUTTON);
        });

    And(
        "^I open Follow up Visits tab from contact directory$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_VISITS_BUTTON);
        });

    Then(
        "^I am validating the From and To dates displayed$",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          String uuid = apiState.getCreatedContact().getUuid();
          webDriverHelpers.clearAndFillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, uuid);
          fillDateFrom(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());
          fillDateTo(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());

          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CONTACT_GRID_RESULTS_ROWS);
          softly
              .assertThat(webDriverHelpers.getNumberOfElements(CONTACT_GRID_RESULTS_ROWS))
              .isEqualTo(1);
          softly.assertAll();
        });
  }

  @SneakyThrows
  public FollowUpVisit collectFollowUpData() {
    TimeUnit.SECONDS.sleep(2); // time needed to refresh the page
    String dateOfVisit = webDriverHelpers.getValueFromWebElement(DATE_AND_TIME_OF_VISIT_INPUT);
    LocalDate parsedDateOfVisit = LocalDate.parse(dateOfVisit, DATE_FORMATTER);
    String dateOfSymptomOnset =
        webDriverHelpers.getValueFromWebElement(DATE_OF_SYMPTOM_ONSET_INPUT);
    LocalDate parsedDateOfSymptomOnset = LocalDate.parse(dateOfSymptomOnset, DATE_FORMATTER);

    return FollowUpVisit.builder()
        .personAvailableAndCooperative(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                PERSON_AVAILABLE_AND_COOPERATIVE_OPTIONS))
        .dateOfVisit(parsedDateOfVisit)
        .timeOfVisit(followUpVisit.getTimeOfVisit())
        .visitRemarks(webDriverHelpers.getValueFromWebElement(VISIT_REMARKS_INPUT))
        .currentBodyTemperature(
            webDriverHelpers
                .getValueFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX)
                .substring(0, 2))
        .sourceOfBodyTemperature(
            webDriverHelpers.getValueFromCombobox(SOURCE_BODY_TEMPERATURE_COMBOBOX))
        .chillsOrSweats(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_OR_SWATS))
        .feelingIll(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEELING_ILL))
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN))
        .shivering(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHIVERING))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIFFICULTY_BREATHING))
        .oxygenSaturation94(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OXIGEN_SATURANTION))
        .pneumoniaClinicalRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PNEUMONIA))
        .rapidBreathing(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING))
        .respiratoryDiseaseRequiringVentilation(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                RESPIRATORY_DISEASE_REQUIRING_VENTILATION))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE))
        .soreThroatPharyngitis(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_PHARYNGITIS))
        .fastHeartRate(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FAST_HEART_RATE))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA))
        .newLossOfSmell(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL))
        .newLossOfTaste(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE))
        .otherClinicalSymptoms(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OTHER_CLINICAL_SYMPTOMS))
        .comments(webDriverHelpers.getValueFromWebElement(COMMENTS_INPUT))
        .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
        .dateOfSymptomOnset(parsedDateOfSymptomOnset)
        .build();
  }

  public FollowUpVisit collectEditedFollowUpData() {
    String dateOfVisit = webDriverHelpers.getValueFromWebElement(DATE_AND_TIME_OF_VISIT_INPUT);
    LocalDate parsedDateOfVisit = LocalDate.parse(dateOfVisit, DATE_FORMATTER);
    String dateOfSymptomOnset =
        webDriverHelpers.getValueFromWebElement(DATE_OF_SYMPTOM_ONSET_INPUT);
    return FollowUpVisit.builder()
        .personAvailableAndCooperative(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                PERSON_AVAILABLE_AND_COOPERATIVE_OPTIONS))
        .dateOfVisit(parsedDateOfVisit)
        .timeOfVisit(followUpEditVisit.getTimeOfVisit())
        .visitRemarks(webDriverHelpers.getValueFromWebElement(VISIT_REMARKS_INPUT))
        .build();
  }

  private LocalDate getDateOfVisit() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_AND_TIME_OF_VISIT_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  public void selectSourceOfBodyTemperature(String sourceOfBodyTemperature) {
    webDriverHelpers.selectFromCombobox(SOURCE_BODY_TEMPERATURE_COMBOBOX, sourceOfBodyTemperature);
  }

  public void selectBodyTemperature(String bodyTemperature) {
    webDriverHelpers.selectFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX, bodyTemperature);
  }

  public void selectFirstSymptom(String firstSymptom) {
    webDriverHelpers.selectFromCombobox(FIRST_SYMPTOM_COMBOBOX, firstSymptom);
  }

  public void selectChillsOrSweats(String chillsOrSweats) {
    webDriverHelpers.clickWebElementByText(CHILLS_OR_SWATS, chillsOrSweats);
  }

  public void selectHeadache(String headache) {
    webDriverHelpers.clickWebElementByText(HEADACHE, headache);
  }

  public void selectFeelingIll(String feelingIll) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL, feelingIll);
  }

  public void selectMusclePain(String musclePain) {
    webDriverHelpers.clickWebElementByText(MUSCLE_PAIN, musclePain);
  }

  public void fillDateOfFirstSymptom(LocalDate dateOfFirstSymptom) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfFirstSymptom));
  }

  public void selectFever(String fever) {
    webDriverHelpers.clickWebElementByText(FEVER, fever);
  }

  public void selectShivering(String shivering) {
    webDriverHelpers.clickWebElementByText(SHIVERING, shivering);
  }

  public void selectOtherClinicalSymptoms(String otherClinicalSymptoms) {
    webDriverHelpers.clickWebElementByText(OTHER_CLINICAL_SYMPTOMS, otherClinicalSymptoms);
  }

  public void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
    webDriverHelpers.clickWebElementByText(
        ACUTE_RESPIRATORY_DISTRESS_SYNDROME, acuteRespiratoryDistressSyndrome);
  }

  public void selectCough(String cough) {
    webDriverHelpers.clickWebElementByText(COUGH, cough);
  }

  public void selectDifficultyBreathing(String difficultyBreathing) {
    webDriverHelpers.clickWebElementByText(DIFFICULTY_BREATHING, difficultyBreathing);
  }

  public void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
    webDriverHelpers.clickWebElementByText(OXIGEN_SATURANTION, oxygenSaturationLower94);
  }

  public void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
    webDriverHelpers.clickWebElementByText(PNEUMONIA, pneumoniaClinicalOrRadiologic);
  }

  public void selectPersonAvailableAndCooperative(String available) {
    webDriverHelpers.clickWebElementByText(PERSON_AVAILABLE_AND_COOPERATIVE_OPTIONS, available);
  }

  public void selectRapidBreathing(String rapidBreathing) {
    webDriverHelpers.clickWebElementByText(RAPID_BREATHING, rapidBreathing);
  }

  public void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
    webDriverHelpers.clickWebElementByText(
        RESPIRATORY_DISEASE_REQUIRING_VENTILATION, respiratoryDiseaseVentilation);
  }

  public void selectRunnyNose(String runnyNose) {
    webDriverHelpers.clickWebElementByText(RUNNY_NOSE, runnyNose);
  }

  public void fillDateOfSymptomOnset(LocalDate dateOfSymptomOnset) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
  }

  public void fillDateFrom(LocalDate from) {
    webDriverHelpers.clearAndFillInWebElement(FROM_INPUT, DATE_FORMATTER.format(from));
  }

  public void fillDateTo(LocalDate to) {
    webDriverHelpers.clearAndFillInWebElement(TO_INPUT, DATE_FORMATTER.format(to));
  }

  public void fillVisitRemark(String visitRemark) {
    webDriverHelpers.clearAndFillInWebElement(VISIT_REMARKS_INPUT, visitRemark);
  }

  public void selectSoreThroat(String soreThroat) {
    webDriverHelpers.clickWebElementByText(SORE_THROAT_PHARYNGITIS, soreThroat);
  }

  public void selectFastHeartRate(String fastHeartRate) {
    webDriverHelpers.clickWebElementByText(FAST_HEART_RATE, fastHeartRate);
  }

  public void selectDiarrhea(String diarrhea) {
    webDriverHelpers.clickWebElementByText(DIARRHEA, diarrhea);
  }

  public void selectNausea(String nausea) {
    webDriverHelpers.clickWebElementByText(NAUSEA, nausea);
  }

  public void selectLossOfSmell(String lossOfSmell) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_SMELL, lossOfSmell);
  }

  public void selectLossOfTaste(String lossOfTaste) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_TASTE, lossOfTaste);
  }

  public void selectOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.clickWebElementByText(OTHER_CLINICAL_SYMPTOMS, otherNonHemorrhagicSymptoms);
  }

  public void fillComments(String symptomsComments) {
    webDriverHelpers.fillInWebElement(COMMENTS_INPUT, symptomsComments);
  }

  public void fillDateAndTimeVisit(LocalDate dateOfSymptomOnset) {
    webDriverHelpers.fillInWebElement(
        DATE_AND_TIME_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
  }
}
