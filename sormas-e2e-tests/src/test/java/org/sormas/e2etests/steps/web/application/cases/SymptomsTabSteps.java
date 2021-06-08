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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pojo.web.Symptoms;
import org.sormas.e2etests.services.SymptomService;
import org.sormas.e2etests.state.ApiState;

public class SymptomsTabSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Symptoms symptoms;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public SymptomsTabSteps(
      WebDriverHelpers webDriverHelpers,
      SymptomService symptomService,
      ApiState apiState,
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Symptoms tab page",
        () -> {
          Symptoms actualSymptoms = collectCasePersonData();
          Truth.assertThat(actualSymptoms).isEqualTo(symptoms);
        });

    When(
        "I am accessing the Symptoms tab using of created case via api",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-ui/#!cases/symptoms/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + caseLinkPath + uuid);
        });

    When(
        "I change all symptoms fields and save",
        () -> {
          symptoms = symptomService.buildEditGeneratedSymptoms();
          selectMaximumBodyTemperatureInCCombobox(symptoms.getMaximumBodyTemperatureInC());
          selectSourceOfBodyTemperature(symptoms.getSourceOfBodyTemperature());
          selectChillsOrSweats(symptoms.getChillsOrSweats());
          selectHeadache(symptoms.getHeadache());
          selectFeelingIll(symptoms.getFeelingIll());
          selectMusclePain(symptoms.getMusclePain());
          selectFever(symptoms.getFever());
          selectShivering(symptoms.getShivering());
          selectAcuteRespiratoryDistressSyndrome(symptoms.getAcuteRespiratoryDistressSyndrome());
          selectOxygenSaturationLower94(symptoms.getOxygenSaturationLower94());
          selectCough(symptoms.getCough());
          selectPneumoniaClinicalOrRadiologic(symptoms.getPneumoniaClinicalOrRadiologic());
          selectDifficultyBreathing(symptoms.getDifficultyBreathing());
          selectRapidBreathing(symptoms.getRapidBreathing());
          selectRespiratoryDiseaseVentilation(symptoms.getRespiratoryDiseaseVentilation());
          selectRunnyNose(symptoms.getRunnyNose());
          selectSoreThroat(symptoms.getSoreThroat());
          selectFastHeartRate(symptoms.getFastHeartRate());
          selectDiarrhea(symptoms.getDiarrhea());
          selectNausea(symptoms.getNausea());
          selectLossOfSmell(symptoms.getLossOfSmell());
          selectLossOfTaste(symptoms.getLossOfTaste());
          selectOtherNonHemorrhagicSymptoms(symptoms.getOtherNonHemorrhagicSymptoms());
          fillOtherNonHemorrhagicSymptoms(symptoms.getSymptomsComments());
          fillSymptomsComments(symptoms.getSymptomsComments());
          selectFistSymptom(symptoms.getFirstSymptom());
          fillDateOfSymptom(symptoms.getDateOfSymptom());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
  }

  public Symptoms collectCasePersonData() {

    return Symptoms.builder()
        .maximumBodyTemperatureInC(
            webDriverHelpers.getValueFromCombobox(MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX))
        .sourceOfBodyTemperature(
            webDriverHelpers.getValueFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX))
        .chillsOrSweats(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_OR_SWEATS_OPTIONS))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE_OPTIONS))
        .feelingIll(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEELING_ILL_OPTIONS))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN_OPTIONS))
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS))
        .shivering(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHIVERING_OPTIONS))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS))
        .oxygenSaturationLower94(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                OXYGEN_SATURATION_LOWER_94_OPTIONS))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_OPTIONS))
        .pneumoniaClinicalOrRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                DIFFICULTY_BREATHING_OPTIONS))
        .rapidBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING_OPTIONS))
        .respiratoryDiseaseVentilation(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                RESPIRATORY_DISEASE_VENTILATION_OPTIONS))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE_OPTIONS))
        .soreThroat(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_OPTIONS))
        .fastHeartRate(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FAST_HEART_RATE_OPTIONS))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA_OPTIONS))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA_OPTIONS))
        .lossOfSmell(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL_OPTIONS))
        .lossOfTaste(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE_OPTIONS))
        .otherNonHemorrhagicSymptoms(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                OTHER_NON_HEMORRHAGIC_SYMPTOMS_OPTIONS))
        .symptomsComments(
            webDriverHelpers.getValueFromWebElement(OTHER_NON_HEMORRHAGIC_SYMPTOMS_INPUT))
        .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
        .dateOfSymptom(getDateOfSymptomOnset())
        .build();
  }

  private LocalDate getDateOfSymptomOnset() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_OF_SYMPTOM_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  public void selectMaximumBodyTemperatureInCCombobox(String maximumBodyTemperatureInCCombobox) {
    webDriverHelpers.selectFromCombobox(
        MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX, maximumBodyTemperatureInCCombobox);
  }

  public void selectSourceOfBodyTemperature(String sourceOfBodyTemperature) {
    webDriverHelpers.selectFromCombobox(
        SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceOfBodyTemperature);
  }

  public void selectChillsOrSweats(String chillsOrSweats) {
    webDriverHelpers.clickWebElementByText(CHILLS_OR_SWEATS_OPTIONS, chillsOrSweats);
  }

  public void selectHeadache(String headache) {
    webDriverHelpers.clickWebElementByText(HEADACHE_OPTIONS, headache);
  }

  public void selectFeelingIll(String feelingIll) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL_OPTIONS, feelingIll);
  }

  public void selectMusclePain(String musclePain) {
    webDriverHelpers.clickWebElementByText(MUSCLE_PAIN_OPTIONS, musclePain);
  }

  public void selectFever(String fever) {
    webDriverHelpers.clickWebElementByText(FEVER_OPTIONS, fever);
  }

  public void selectShivering(String shivering) {
    webDriverHelpers.clickWebElementByText(SHIVERING_OPTIONS, shivering);
  }

  public void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
    webDriverHelpers.clickWebElementByText(
        ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS, acuteRespiratoryDistressSyndrome);
  }

  public void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
    webDriverHelpers.clickWebElementByText(
        OXYGEN_SATURATION_LOWER_94_OPTIONS, oxygenSaturationLower94);
  }

  public void selectCough(String cough) {
    webDriverHelpers.clickWebElementByText(COUGH_OPTIONS, cough);
  }

  public void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
    webDriverHelpers.clickWebElementByText(
        PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS, pneumoniaClinicalOrRadiologic);
  }

  public void selectDifficultyBreathing(String difficultyBreathing) {
    webDriverHelpers.clickWebElementByText(DIFFICULTY_BREATHING_OPTIONS, difficultyBreathing);
  }

  public void selectRapidBreathing(String rapidBreathing) {
    webDriverHelpers.clickWebElementByText(RAPID_BREATHING_OPTIONS, rapidBreathing);
  }

  public void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
    webDriverHelpers.clickWebElementByText(
        RESPIRATORY_DISEASE_VENTILATION_OPTIONS, respiratoryDiseaseVentilation);
  }

  public void selectRunnyNose(String runnyNose) {
    webDriverHelpers.clickWebElementByText(RUNNY_NOSE_OPTIONS, runnyNose);
  }

  public void selectSoreThroat(String soreThroat) {
    webDriverHelpers.clickWebElementByText(SORE_THROAT_OPTIONS, soreThroat);
  }

  public void selectFastHeartRate(String fastHeartRate) {
    webDriverHelpers.clickWebElementByText(FAST_HEART_RATE_OPTIONS, fastHeartRate);
  }

  public void selectDiarrhea(String diarrhea) {
    webDriverHelpers.clickWebElementByText(DIARRHEA_OPTIONS, diarrhea);
  }

  public void selectNausea(String nausea) {
    webDriverHelpers.clickWebElementByText(NAUSEA_OPTIONS, nausea);
  }

  public void selectLossOfSmell(String lossOfSmell) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_SMELL_OPTIONS, lossOfSmell);
  }

  public void selectLossOfTaste(String lossOfTaste) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_TASTE_OPTIONS, lossOfTaste);
  }

  public void selectOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.clickWebElementByText(
        OTHER_NON_HEMORRHAGIC_SYMPTOMS_OPTIONS, otherNonHemorrhagicSymptoms);
  }

  public void fillOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.fillInWebElement(
        OTHER_NON_HEMORRHAGIC_SYMPTOMS_INPUT, otherNonHemorrhagicSymptoms);
  }

  public void fillSymptomsComments(String symptomsComments) {
    webDriverHelpers.fillInWebElement(SYMPTOMS_COMMENTS_INPUT, symptomsComments);
  }

  public void selectFistSymptom(String fistSymptom) {
    webDriverHelpers.selectFromCombobox(FIRST_SYMPTOM_COMBOBOX, fistSymptom);
  }

  public void fillDateOfSymptom(LocalDate dateOfSymptom) {
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_INPUT, DATE_FORMATTER.format(dateOfSymptom));
  }
}
