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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.NO;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.UNKNOWN;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.ABDOMINAL_PAIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.ABNORMAL_LUNG_XRAY_FINDINGS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.COMA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.CONFUSED_DISORIENTED_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.COUGH_WITH_HEAMOPTYSIS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.COUGH_WITH_SPUTUM_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.FATIGUE_WEAKNESS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.FLUID_IN_LUNG_CAVITY_AUSCULTATION_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.FLUID_IN_LUNG_CAVITY_XRAY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.INABILITY_TO_WALK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.INDRAWING_OF_CHEST_WALL_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.JOINT_PAIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.LYMPHADENOPATHY_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.OTHER_COMPLICATIONS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.SEIZURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.SKIN_RASH_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.SKIN_ULCERS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.UNEXPLAINED_BLEEDING_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.VOMITING_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.SymptomsTabPage.*;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Symptoms;
import org.sormas.e2etests.entities.services.SymptomService;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
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
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;
    String firstSymptom = "Sore throat/pharyngitis";
    String firstSymptomDE = "Halsentz\u00FCndung/Pharyngitis";

    When(
        "I check the created data is correctly displayed on Symptoms tab page",
        () -> {
          Symptoms actualSymptoms = collectSymptomsData();
          ComparisonHelper.compareEqualEntities(actualSymptoms, symptoms);
        });

    When(
        "I check the created data that describes Clinical signs and Symptoms are correctly displayed for No or UNKNOWN option in Symptoms tab page",
        () -> {
          Symptoms actualSymptoms = collectSymptomsDataForNoOption();
          ComparisonHelper.compareEqualEntities(actualSymptoms, symptoms);
        });

    When(
        "I am accessing the Symptoms tab using of created case via api",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String caseLinkPath = "/sormas-webdriver/#!cases/symptoms/";
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + caseLinkPath + uuid);
        });

    When(
        "I change Other symptoms to {string} option",
        (String option) -> {
          selectOtherNonHemorrhagicSymptoms(option);
        });

    When(
        "^I click on save case button in Symptoms tab$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I change all symptoms fields and save",
        () -> {
          symptoms = symptomService.buildEditGeneratedSymptoms();
          selectMaximumBodyTemperatureInCCombobox(symptoms.getMaximumBodyTemperatureInC());
          selectSourceOfBodyTemperature(symptoms.getSourceOfBodyTemperature());
          selectChillsOrSweats(symptoms.getChillsOrSweats());
          selectHeadache(symptoms.getHeadache());
          selectMusclePain(symptoms.getMusclePain());
          selectFever(symptoms.getFever());
          selectAcuteRespiratoryDistressSyndrome(symptoms.getAcuteRespiratoryDistressSyndrome());
          selectCough(symptoms.getCough());
          selectPneumoniaClinicalOrRadiologic(symptoms.getPneumoniaClinicalOrRadiologic());
          selectDifficultyBreathing(symptoms.getDifficultyBreathing());
          selectRapidBreathing(symptoms.getRapidBreathing());
          selectRunnyNose(symptoms.getRunnyNose());
          selectSoreThroat(symptoms.getSoreThroat());
          selectDiarrhea(symptoms.getDiarrhea());
          selectNausea(symptoms.getNausea());
          selectLossOfSmell(symptoms.getLossOfSmell());
          selectLossOfTaste(symptoms.getLossOfTaste());
          selectOtherNonHemorrhagicSymptoms(symptoms.getOtherNonHemorrhagicSymptoms());
          fillOtherNonHemorrhagicSymptoms(symptoms.getSymptomsComments());
          fillSymptomsComments(symptoms.getSymptomsComments());
          selectFistSymptom(symptoms.getFirstSymptom());
          selectAbnormalLungXrayFindings(symptoms.getAbnormalLungXrayFindings());
          selectFatigueWeakness(symptoms.getFatigueWeakness());
          selectJointPain(symptoms.getJointPain());
          selectCoughWithHeamoptysis(symptoms.getCoughWithHeamoptysis());
          selectCoughWithSputum(symptoms.getCoughWithSputum());
          selectFluidInLungCavityXray(symptoms.getFluidInLungCavityXray());
          selectFluidInLungCavityAuscultation(symptoms.getFluidInLungCavityAuscultation());
          selectInDrawingOfChestWall(symptoms.getInDrawingOfChestWall());
          selectAbdominalPain(symptoms.getAbdominalPain());
          selectVomiting(symptoms.getVomiting());
          selectSkinUlcers(symptoms.getSkinUlcers());
          selectUnexplainedBleeding(symptoms.getUnexplainedBleeding());
          selectComa(symptoms.getComa());
          selectLymphadenopathy(symptoms.getLymphadenopathy());
          selectInabilityToWalk(symptoms.getInabilityToWalk());
          selectSkinRash(symptoms.getSkinRash());
          selectConfusedDisoriented(symptoms.getConfusedDisoriented());
          selectSeizures(symptoms.getSeizures());
          fillDateOfSymptom(symptoms.getDateOfSymptom());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I change all symptoms fields to {string} option field and save",
        (String option) -> {
          switch (option) {
            case "NO":
              symptoms = symptomService.buildEditGeneratedSymptomsWithNoOptions();
              FillSymptomsDataForNoUnknown(symptoms);
              selectOtherNonHemorrhagicSymptoms(NO.toString());
              fillSymptomsComments(symptoms.getSymptomsComments());
              webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
              break;
            case "NO_AND_OTHER_SYMPTOMS_TO_YES":
              symptoms = symptomService.buildEditGeneratedSymptomsWithNoOptions();
              FillSymptomsDataForNoUnknown(symptoms);
              selectOtherNonHemorrhagicSymptoms(YES.toString());
              fillOtherNonHemorrhagicSymptoms(symptoms.getSymptomsComments());
              fillSymptomsComments(symptoms.getSymptomsComments());
              selectFistSymptom("Other clinical symptoms");
              fillDateOfSymptom(LocalDate.now().minusDays(2));
              webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
              break;
            case "UNKNOWN":
              symptoms = symptomService.buildEditGeneratedSymptomsWithUnknownOptions();
              FillSymptomsDataForNoUnknown(symptoms);
              selectOtherNonHemorrhagicSymptoms(UNKNOWN.toString());
              fillSymptomsComments(symptoms.getSymptomsComments());
              webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
              break;
            case "YES":
              symptoms = symptomService.buildEditGeneratedSymptoms();
              FillSymptomsData(symptoms);
              fillOtherNonHemorrhagicSymptoms(symptoms.getSymptomsComments());
              fillSymptomsComments(symptoms.getSymptomsComments());
              selectFistSymptom(symptoms.getFirstSymptom());
              fillDateOfSymptom(symptoms.getDateOfSymptom());
              webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
              break;
          }
        });

    // TODO refactor this to be provide the checkbox and select any option not only yes
    And(
        "I check Yes Option for Soar Throat on Symptoms tab page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SORE_THROAT_YES_OPTION_DE));

    And(
        "I select sore throat option",
        () -> {
          webDriverHelpers.scrollToElement(FIRST_SYMPTOM_COMBOBOX);
          selectFistSymptom(firstSymptomDE);
          // TODO refactor this to allow selecting any option from this view
        });

    Then(
        "I click on Case tab from Symptoms tab directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_TAB);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
        });

    And(
        "I click on Clear all button From Symptoms tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CLEAR_ALL_BUTTON);
        });
  }

  private void FillSymptomsData(Symptoms symptoms) {
    selectMaximumBodyTemperatureInCCombobox(symptoms.getMaximumBodyTemperatureInC());
    selectSourceOfBodyTemperature(symptoms.getSourceOfBodyTemperature());
    selectChillsOrSweats(symptoms.getChillsOrSweats());
    selectHeadache(symptoms.getHeadache());
    selectMusclePain(symptoms.getMusclePain());
    selectFever(symptoms.getFever());
    selectAcuteRespiratoryDistressSyndrome(symptoms.getAcuteRespiratoryDistressSyndrome());
    selectCough(symptoms.getCough());
    selectPneumoniaClinicalOrRadiologic(symptoms.getPneumoniaClinicalOrRadiologic());
    selectDifficultyBreathing(symptoms.getDifficultyBreathing());
    selectRapidBreathing(symptoms.getRapidBreathing());
    selectRunnyNose(symptoms.getRunnyNose());
    selectSoreThroat(symptoms.getSoreThroat());
    selectDiarrhea(symptoms.getDiarrhea());
    selectNausea(symptoms.getNausea());
    selectLossOfSmell(symptoms.getLossOfSmell());
    selectLossOfTaste(symptoms.getLossOfTaste());
    selectOtherNonHemorrhagicSymptoms(symptoms.getOtherNonHemorrhagicSymptoms());
    selectAbnormalLungXrayFindings(symptoms.getAbnormalLungXrayFindings());
    selectFatigueWeakness(symptoms.getFatigueWeakness());
    selectJointPain(symptoms.getJointPain());
    selectCoughWithHeamoptysis(symptoms.getCoughWithHeamoptysis());
    selectCoughWithSputum(symptoms.getCoughWithSputum());
    selectFluidInLungCavityXray(symptoms.getFluidInLungCavityXray());
    selectFluidInLungCavityAuscultation(symptoms.getFluidInLungCavityAuscultation());
    selectInDrawingOfChestWall(symptoms.getInDrawingOfChestWall());
    selectAbdominalPain(symptoms.getAbdominalPain());
    selectVomiting(symptoms.getVomiting());
    selectSkinUlcers(symptoms.getSkinUlcers());
    selectUnexplainedBleeding(symptoms.getUnexplainedBleeding());
    selectComa(symptoms.getComa());
    selectLymphadenopathy(symptoms.getLymphadenopathy());
    selectInabilityToWalk(symptoms.getInabilityToWalk());
    selectSkinRash(symptoms.getSkinRash());
    selectConfusedDisoriented(symptoms.getConfusedDisoriented());
    selectSeizures(symptoms.getSeizures());
    selectFistSymptom(symptoms.getFirstSymptom());
  }

  private void FillSymptomsDataForNoUnknown(Symptoms symptoms) {
    selectMaximumBodyTemperatureInCCombobox(symptoms.getMaximumBodyTemperatureInC());
    selectSourceOfBodyTemperature(symptoms.getSourceOfBodyTemperature());
    selectChillsOrSweats(symptoms.getChillsOrSweats());
    selectHeadache(symptoms.getHeadache());
    selectMusclePain(symptoms.getMusclePain());
    selectFever(symptoms.getFever());
    selectAcuteRespiratoryDistressSyndrome(symptoms.getAcuteRespiratoryDistressSyndrome());
    selectCough(symptoms.getCough());
    selectPneumoniaClinicalOrRadiologic(symptoms.getPneumoniaClinicalOrRadiologic());
    selectDifficultyBreathing(symptoms.getDifficultyBreathing());
    selectRapidBreathing(symptoms.getRapidBreathing());
    selectRunnyNose(symptoms.getRunnyNose());
    selectSoreThroat(symptoms.getSoreThroat());
    selectDiarrhea(symptoms.getDiarrhea());
    selectNausea(symptoms.getNausea());
    selectLossOfSmell(symptoms.getLossOfSmell());
    selectLossOfTaste(symptoms.getLossOfTaste());
    selectAbnormalLungXrayFindings(symptoms.getAbnormalLungXrayFindings());
    selectFatigueWeakness(symptoms.getFatigueWeakness());
    selectJointPain(symptoms.getJointPain());
    selectCoughWithHeamoptysis(symptoms.getCoughWithHeamoptysis());
    selectCoughWithSputum(symptoms.getCoughWithSputum());
    selectFluidInLungCavityXray(symptoms.getFluidInLungCavityXray());
    selectFluidInLungCavityAuscultation(symptoms.getFluidInLungCavityAuscultation());
    selectInDrawingOfChestWall(symptoms.getInDrawingOfChestWall());
    selectAbdominalPain(symptoms.getAbdominalPain());
    selectVomiting(symptoms.getVomiting());
    selectSkinUlcers(symptoms.getSkinUlcers());
    selectUnexplainedBleeding(symptoms.getUnexplainedBleeding());
    selectComa(symptoms.getComa());
    selectLymphadenopathy(symptoms.getLymphadenopathy());
    selectInabilityToWalk(symptoms.getInabilityToWalk());
    selectSkinRash(symptoms.getSkinRash());
    selectConfusedDisoriented(symptoms.getConfusedDisoriented());
    selectSeizures(symptoms.getSeizures());
    selectOtherComplications(symptoms.getOtherComplications());
  }

  private Symptoms collectSymptomsData() {

    return Symptoms.builder()
        .maximumBodyTemperatureInC(
            webDriverHelpers
                .getValueFromCombobox(MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX)
                .substring(0, 4))
        .sourceOfBodyTemperature(
            webDriverHelpers.getValueFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX))
        .chillsOrSweats(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_OR_SWEATS_OPTIONS))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE_OPTIONS))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN_OPTIONS))
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_OPTIONS))
        .pneumoniaClinicalOrRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                DIFFICULTY_BREATHING_OPTIONS))
        .rapidBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING_OPTIONS))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE_OPTIONS))
        .soreThroat(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_OPTIONS))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA_OPTIONS))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA_OPTIONS))
        .lossOfSmell(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL_OPTIONS))
        .lossOfTaste(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE_OPTIONS))
        .otherNonHemorrhagicSymptoms(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                OTHER_NON_HEMORRHAGIC_SYMPTOMS_OPTIONS))
        .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
        .symptomsComments(
            webDriverHelpers.getValueFromWebElement(OTHER_NON_HEMORRHAGIC_SYMPTOMS_INPUT))
        .abnormalLungXrayFindings(
            (webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ABNORMAL_LUNG_XRAY_FINDINGS_OPTIONS)))
        .fatigueWeakness(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FATIGUE_WEAKNESS_OPTIONS))
        .jointPain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(JOINT_PAIN_OPTIONS))
        .coughWithHeamoptysis(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                COUGH_WITH_HEAMOPTYSIS_OPTIONS))
        .coughWithSputum(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_WITH_SPUTUM_OPTIONS))
        .fluidInLungCavityXray(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                FLUID_IN_LUNG_CAVITY_XRAY_OPTIONS))
        .fluidInLungCavityAuscultation(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                FLUID_IN_LUNG_CAVITY_AUSCULTATION_OPTIONS))
        .inDrawingOfChestWall(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INDRAWING_OF_CHEST_WALL_OPTIONS))
        .abdominalPain(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ABDOMINAL_PAIN_OPTIONS))
        .vomiting(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(VOMITING_OPTIONS))
        .skinUlcers(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SKIN_ULCERS_OPTIONS))
        .unexplainedBleeding(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                UNEXPLAINED_BLEEDING_OPTIONS))
        .coma(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COMA_OPTIONS))
        .lymphadenopathy(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LYMPHADENOPATHY_OPTIONS))
        .inabilityToWalk(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INABILITY_TO_WALK_OPTIONS))
        .skinRash(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SKIN_RASH_OPTIONS))
        .confusedDisoriented(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                CONFUSED_DISORIENTED_OPTIONS))
        .seizures(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEIZURES_OPTIONS))
        .dateOfSymptom(getDateOfSymptomOnset())
        .build();
  }

  private Symptoms collectSymptomsDataForNoOption() {

    return Symptoms.builder()
        .maximumBodyTemperatureInC(
            webDriverHelpers
                .getValueFromCombobox(MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX)
                .substring(0, 4))
        .sourceOfBodyTemperature(
            webDriverHelpers.getValueFromCombobox(SOURCE_OF_BODY_TEMPERATURE_COMBOBOX))
        .chillsOrSweats(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CHILLS_OR_SWEATS_OPTIONS))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE_OPTIONS))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN_OPTIONS))
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER_OPTIONS))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_OPTIONS))
        .pneumoniaClinicalOrRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                DIFFICULTY_BREATHING_OPTIONS))
        .rapidBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING_OPTIONS))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE_OPTIONS))
        .soreThroat(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_OPTIONS))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA_OPTIONS))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA_OPTIONS))
        .lossOfSmell(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL_OPTIONS))
        .lossOfTaste(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE_OPTIONS))
        .abnormalLungXrayFindings(
            (webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ABNORMAL_LUNG_XRAY_FINDINGS_OPTIONS)))
        .fatigueWeakness(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FATIGUE_WEAKNESS_OPTIONS))
        .jointPain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(JOINT_PAIN_OPTIONS))
        .coughWithHeamoptysis(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                COUGH_WITH_HEAMOPTYSIS_OPTIONS))
        .coughWithSputum(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH_WITH_SPUTUM_OPTIONS))
        .fluidInLungCavityXray(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                FLUID_IN_LUNG_CAVITY_XRAY_OPTIONS))
        .fluidInLungCavityAuscultation(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                FLUID_IN_LUNG_CAVITY_AUSCULTATION_OPTIONS))
        .inDrawingOfChestWall(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                INDRAWING_OF_CHEST_WALL_OPTIONS))
        .abdominalPain(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ABDOMINAL_PAIN_OPTIONS))
        .vomiting(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(VOMITING_OPTIONS))
        .skinUlcers(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SKIN_ULCERS_OPTIONS))
        .unexplainedBleeding(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                UNEXPLAINED_BLEEDING_OPTIONS))
        .coma(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COMA_OPTIONS))
        .lymphadenopathy(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LYMPHADENOPATHY_OPTIONS))
        .inabilityToWalk(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INABILITY_TO_WALK_OPTIONS))
        .skinRash(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SKIN_RASH_OPTIONS))
        .confusedDisoriented(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                CONFUSED_DISORIENTED_OPTIONS))
        .seizures(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SEIZURES_OPTIONS))
        .otherComplications(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OTHER_COMPLICATIONS_OPTIONS))
        .symptomsComments(webDriverHelpers.getValueFromWebElement(SYMPTOMS_COMMENTS_INPUT))
        .build();
  }

  private LocalDate getDateOfSymptomOnset() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_OF_SYMPTOM_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private void selectMaximumBodyTemperatureInCCombobox(String maximumBodyTemperatureInCCombobox) {
    webDriverHelpers.selectFromCombobox(
        MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX, maximumBodyTemperatureInCCombobox);
  }

  private void selectSourceOfBodyTemperature(String sourceOfBodyTemperature) {
    webDriverHelpers.selectFromCombobox(
        SOURCE_OF_BODY_TEMPERATURE_COMBOBOX, sourceOfBodyTemperature);
  }

  private void selectChillsOrSweats(String chillsOrSweats) {
    webDriverHelpers.clickWebElementByText(CHILLS_OR_SWEATS_OPTIONS, chillsOrSweats);
  }

  private void selectHeadache(String headache) {
    webDriverHelpers.clickWebElementByText(HEADACHE_OPTIONS, headache);
  }

  private void selectFeelingIll(String feelingIll) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL_OPTIONS, feelingIll);
  }

  private void selectMusclePain(String musclePain) {
    webDriverHelpers.clickWebElementByText(MUSCLE_PAIN_OPTIONS, musclePain);
  }

  private void selectFever(String fever) {
    webDriverHelpers.clickWebElementByText(FEVER_OPTIONS, fever);
  }

  private void selectShivering(String shivering) {
    webDriverHelpers.clickWebElementByText(SHIVERING_OPTIONS, shivering);
  }

  private void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
    webDriverHelpers.clickWebElementByText(
        ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS, acuteRespiratoryDistressSyndrome);
  }

  private void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
    webDriverHelpers.clickWebElementByText(
        OXYGEN_SATURATION_LOWER_94_OPTIONS, oxygenSaturationLower94);
  }

  private void selectCough(String cough) {
    webDriverHelpers.clickWebElementByText(COUGH_OPTIONS, cough);
  }

  private void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
    webDriverHelpers.clickWebElementByText(
        PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS, pneumoniaClinicalOrRadiologic);
  }

  private void selectDifficultyBreathing(String difficultyBreathing) {
    webDriverHelpers.clickWebElementByText(DIFFICULTY_BREATHING_OPTIONS, difficultyBreathing);
  }

  private void selectRapidBreathing(String rapidBreathing) {
    webDriverHelpers.clickWebElementByText(RAPID_BREATHING_OPTIONS, rapidBreathing);
  }

  private void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
    webDriverHelpers.clickWebElementByText(
        RESPIRATORY_DISEASE_VENTILATION_OPTIONS, respiratoryDiseaseVentilation);
  }

  private void selectRunnyNose(String runnyNose) {
    webDriverHelpers.clickWebElementByText(RUNNY_NOSE_OPTIONS, runnyNose);
  }

  private void selectSoreThroat(String soreThroat) {
    webDriverHelpers.clickWebElementByText(SORE_THROAT_OPTIONS, soreThroat);
  }

  private void selectFastHeartRate(String fastHeartRate) {
    webDriverHelpers.clickWebElementByText(FAST_HEART_RATE_OPTIONS, fastHeartRate);
  }

  private void selectDiarrhea(String diarrhea) {
    webDriverHelpers.clickWebElementByText(DIARRHEA_OPTIONS, diarrhea);
  }

  private void selectNausea(String nausea) {
    webDriverHelpers.clickWebElementByText(NAUSEA_OPTIONS, nausea);
  }

  private void selectLossOfSmell(String lossOfSmell) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_SMELL_OPTIONS, lossOfSmell);
  }

  private void selectLossOfTaste(String lossOfTaste) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_TASTE_OPTIONS, lossOfTaste);
  }

  private void selectOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.clickWebElementByText(
        OTHER_NON_HEMORRHAGIC_SYMPTOMS_OPTIONS, otherNonHemorrhagicSymptoms);
  }

  private void fillOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.fillInWebElement(
        OTHER_NON_HEMORRHAGIC_SYMPTOMS_INPUT, otherNonHemorrhagicSymptoms);
  }

  private void fillSymptomsComments(String symptomsComments) {
    webDriverHelpers.fillInWebElement(SYMPTOMS_COMMENTS_INPUT, symptomsComments);
  }

  private void selectFistSymptom(String fistSymptom) {
    webDriverHelpers.selectFromCombobox(FIRST_SYMPTOM_COMBOBOX, fistSymptom);
  }

  private void fillDateOfSymptom(LocalDate dateOfSymptom) {
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_INPUT, DATE_FORMATTER.format(dateOfSymptom));
  }

  private void selectAbnormalLungXrayFindings(String abnormalLungXrayFindings) {
    webDriverHelpers.clickWebElementByText(
        ABNORMAL_LUNG_XRAY_FINDINGS_OPTIONS, abnormalLungXrayFindings);
  }

  private void selectFatigueWeakness(String fatigueWeakness) {
    webDriverHelpers.clickWebElementByText(FATIGUE_WEAKNESS_OPTIONS, fatigueWeakness);
  }

  private void selectJointPain(String jointPain) {
    webDriverHelpers.clickWebElementByText(JOINT_PAIN_OPTIONS, jointPain);
  }

  private void selectCoughWithHeamoptysis(String coughWithHeamoptysis) {
    webDriverHelpers.clickWebElementByText(COUGH_WITH_HEAMOPTYSIS_OPTIONS, coughWithHeamoptysis);
  }

  private void selectCoughWithSputum(String coughWithSputum) {
    webDriverHelpers.clickWebElementByText(COUGH_WITH_SPUTUM_OPTIONS, coughWithSputum);
  }

  private void selectFluidInLungCavityXray(String fluidInLungCavityXray) {
    webDriverHelpers.clickWebElementByText(
        FLUID_IN_LUNG_CAVITY_XRAY_OPTIONS, fluidInLungCavityXray);
  }

  private void selectFluidInLungCavityAuscultation(String fluidInLungCavityAuscultation) {
    webDriverHelpers.clickWebElementByText(
        FLUID_IN_LUNG_CAVITY_AUSCULTATION_OPTIONS, fluidInLungCavityAuscultation);
  }

  private void selectInDrawingOfChestWall(String inDrawingOfChestWall) {
    webDriverHelpers.clickWebElementByText(INDRAWING_OF_CHEST_WALL_OPTIONS, inDrawingOfChestWall);
  }

  private void selectAbdominalPain(String abdominalPain) {
    webDriverHelpers.clickWebElementByText(ABDOMINAL_PAIN_OPTIONS, abdominalPain);
  }

  private void selectSkinUlcers(String skinUlcers) {
    webDriverHelpers.clickWebElementByText(SKIN_ULCERS_OPTIONS, skinUlcers);
  }

  private void selectVomiting(String vomiting) {
    webDriverHelpers.clickWebElementByText(VOMITING_OPTIONS, vomiting);
  }

  private void selectUnexplainedBleeding(String unexplainedBleeding) {
    webDriverHelpers.clickWebElementByText(UNEXPLAINED_BLEEDING_OPTIONS, unexplainedBleeding);
  }

  private void selectComa(String coma) {
    webDriverHelpers.clickWebElementByText(COMA_OPTIONS, coma);
  }

  private void selectLymphadenopathy(String lymphadenopathy) {
    webDriverHelpers.clickWebElementByText(LYMPHADENOPATHY_OPTIONS, lymphadenopathy);
  }

  private void selectInabilityToWalk(String inabilityToWalk) {
    webDriverHelpers.clickWebElementByText(INABILITY_TO_WALK_OPTIONS, inabilityToWalk);
  }

  private void selectSkinRash(String skinRash) {
    webDriverHelpers.clickWebElementByText(SKIN_RASH_OPTIONS, skinRash);
  }

  private void selectConfusedDisoriented(String confusedDisoriented) {
    webDriverHelpers.clickWebElementByText(CONFUSED_DISORIENTED_OPTIONS, confusedDisoriented);
  }

  private void selectSeizures(String seizures) {
    webDriverHelpers.clickWebElementByText(SEIZURES_OPTIONS, seizures);
  }

  private void selectOtherComplications(String otherComplications) {
    webDriverHelpers.clickWebElementByText(OTHER_COMPLICATIONS_OPTIONS, otherComplications);
  }
}
