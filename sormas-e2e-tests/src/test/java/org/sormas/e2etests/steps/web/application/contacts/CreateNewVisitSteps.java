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

import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewVisitPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.CONTACT_CREATED_POPUP;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.FollowUpVisit;
import org.sormas.e2etests.services.FollowUpVisitService;

public class CreateNewVisitSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static FollowUpVisit followUpVisit;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  @Inject
  public CreateNewVisitSteps(
      WebDriverHelpers webDriverHelpers, FollowUpVisitService followUpVisitService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Follow-up visit",
        () -> {
          followUpVisit = followUpVisitService.buildGeneratedFollowUpVisit();

          selectPersonAvailableAndCooperative(followUpVisit.getPersonAvailableAndCooperative());
          fillDateAndTimeVisit(
              followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit(),
              followUpVisitService.buildGeneratedFollowUpVisit().getTimeOfVisit());
          fillVisitRemark(followUpVisit.getVisitRemarks());
          // selectBodyTemperature(followUpVisit.getCurrentBodyTemperature());
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
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CREATED_POPUP);
        });
  }

  public FollowUpVisit collectFollowUpData() {
    return FollowUpVisit.builder()
        .personAvailableAndCooperative(
            webDriverHelpers.getValueFromWebElement(PERSON_AVAILABLE_AND_COOPERATIVE))
        .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
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
    webDriverHelpers.selectFromCombobox(FIRSTSYMPTOM_COMBOBOX, firstSymptom);
  }

  public void selectChillsOrSweats(String chillsOrSweats) {
    webDriverHelpers.clickWebElementByText(CHILLS_OR_SWATS_LABEL, chillsOrSweats);
  }

  public void selectHeadache(String headache) {
    webDriverHelpers.clickWebElementByText(HEADACHE_LABEL, headache);
  }

  public void selectFeelingIll(String feelingIll) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL_LABEL, feelingIll);
  }

  public void selectMusclePain(String musclePain) {
    webDriverHelpers.clickWebElementByText(MUSCLE_PAIN_LABEL, musclePain);
  }

  public void fillDateOfFirstSymptom(LocalDate dateOfFirstSymptom) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfFirstSymptom));
  }

  public void selectFever(String fever) {
    webDriverHelpers.clickWebElementByText(FEVER_LABEL, fever);
  }

  public void selectShivering(String shivering) {
    webDriverHelpers.clickWebElementByText(SHIVERING_LABEL, shivering);
  }

  public void selectOtherClinicalSymptoms(String otherClinicalSymptoms) {
    webDriverHelpers.clickWebElementByText(OTHER_CLINICAL_SYMPTOMS_LABEL, otherClinicalSymptoms);
  }

  public void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
    webDriverHelpers.clickWebElementByText(
        ACUTE_RESPIRATORY_DISTRESS_SYNDROME_LABEL, acuteRespiratoryDistressSyndrome);
  }

  public void selectCough(String cough) {
    webDriverHelpers.clickWebElementByText(COUGH_LABEL, cough);
  }

  public void selectDifficultyBreathing(String difficultyBreathing) {
    webDriverHelpers.clickWebElementByText(DIFFICULTY_BREATHING_LABEL, difficultyBreathing);
  }

  public void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
    webDriverHelpers.clickWebElementByText(OXIGEN_SATURANTION_LABEL, oxygenSaturationLower94);
  }

  public void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
    webDriverHelpers.clickWebElementByText(PNEUMONIA_LABEL, pneumoniaClinicalOrRadiologic);
  }

  public void selectPersonAvailableAndCooperative(String available) {
    webDriverHelpers.clickWebElementByText(PERSON_AVAILABLE_AND_COOPERATIVE, available);
  }

  public void selectRapidBreathing(String rapidBreathing) {
    webDriverHelpers.clickWebElementByText(RAPID_BREATHING_LABEL, rapidBreathing);
  }

  public void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
    webDriverHelpers.clickWebElementByText(
        RESPIRATORY_DISEASE_REQUIRING_VENTILATION_LABEL, respiratoryDiseaseVentilation);
  }

  public void selectRunnyNose(String runnyNose) {
    webDriverHelpers.clickWebElementByText(RUNNY_NOSE_LABEL, runnyNose);
  }

  public void fillDateOfSymptomOnset(LocalDate dateOfSymptomOnset) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
  }

  public void fillVisitRemark(String visitRemark) {
    webDriverHelpers.fillInWebElement(VISIT_REMARKS_INPUT, "visit remark" + LocalTime.now());
  }

  public void selectSoreThroat(String soreThroat) {
    webDriverHelpers.clickWebElementByText(SORE_THROAT_PHARYNGITIS_LABEL, soreThroat);
  }

  public void selectFastHeartRate(String fastHeartRate) {
    webDriverHelpers.clickWebElementByText(FAST_HEART_RATE_LABEL, fastHeartRate);
  }

  public void selectDiarrhea(String diarrhea) {
    webDriverHelpers.clickWebElementByText(DIARRHEA_LABEL, diarrhea);
  }

  public void selectNausea(String nausea) {
    webDriverHelpers.clickWebElementByText(NAUSEA_LABEL, nausea);
  }

  public void selectLossOfSmell(String lossOfSmell) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_SMELL_LABEL, lossOfSmell);
  }

  public void selectLossOfTaste(String lossOfTaste) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_TASTE_LABEL, lossOfTaste);
  }

  public void selectOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.clickWebElementByText(
        OTHER_CLINICAL_SYMPTOMS_LABEL, otherNonHemorrhagicSymptoms);
  }

  public void fillComments(String symptomsComments) {
    webDriverHelpers.fillInWebElement(COMMENTS_INPUT, symptomsComments);
  }

  public void fillDateAndTimeVisit(LocalDate dateOfSymptomOnset, String timeOfVisit) {
    webDriverHelpers.fillInWebElement(
        DATE_AND_TIME_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
    webDriverHelpers.selectFromCombobox(TIME_OF_VISIT_INPUT, timeOfVisit);
  }
}
