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

package org.sormas.e2etests.steps.web.application.contacts;

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
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.*;
import static org.sormas.e2etests.pages.application.contacts.CreateNewVisitPage.*;
import static org.sormas.e2etests.pages.application.contacts.FollowUpVisitsTabPage.CONTACTS_LIST_BUTTON;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.FollowUpVisit;
import org.sormas.e2etests.entities.services.FollowUpVisitService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

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
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new Follow-up visit",
        () -> {
          followUpVisit = followUpVisitService.buildGeneratedFollowUpVisit();
          selectPersonAvailableAndCooperative(followUpVisit.getPersonAvailableAndCooperative());
          fillDateAndTimeVisit(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());
          fillVisitRemark(followUpVisit.getVisitRemarks());
          selectBodyTemperature(followUpVisit.getCurrentBodyTemperature());
          selectSourceOfBodyTemperature(followUpVisit.getSourceOfBodyTemperature());
          selectChillsOrSweats(followUpVisit.getChillsOrSweats());
          selectFever(followUpVisit.getFever());
          selectHeadache(followUpVisit.getHeadache());
          selectMusclePain(followUpVisit.getMusclePain());
          selectAcuteRespiratoryDistressSyndrome(
              followUpVisit.getAcuteRespiratoryDistressSyndrome());
          selectCough(followUpVisit.getCough());
          selectDifficultyBreathing(followUpVisit.getDifficultyBreathing());
          selectPneumoniaClinicalOrRadiologic(followUpVisit.getPneumoniaClinicalRadiologic());
          selectRapidBreathing(followUpVisit.getRapidBreathing());
          selectRunnyNose(followUpVisit.getRunnyNose());
          selectSoreThroat(followUpVisit.getSoreThroatPharyngitis());
          selectDiarrhea(followUpVisit.getDiarrhea());
          selectNausea(followUpVisit.getNausea());
          selectLossOfSmell(followUpVisit.getNewLossOfSmell());
          selectLossOfTaste(followUpVisit.getNewLossOfTaste());
          selectAbnormalLungXrayFindings(followUpVisit.getAbnormalLungXrayFindings());
          selectFatigueWeakness(followUpVisit.getFatigueWeakness());
          selectJointPain(followUpVisit.getJointPain());
          selectCoughWithHeamoptysis(followUpVisit.getCoughWithHeamoptysis());
          selectCoughWithSputum(followUpVisit.getCoughWithSputum());
          selectFluidInLungCavityXray(followUpVisit.getFluidInLungCavityXray());
          selectFluidInLungCavityAuscultation(followUpVisit.getFluidInLungCavityAuscultation());
          selectInDrawingOfChestWall(followUpVisit.getInDrawingOfChestWall());
          selectAbdominalPain(followUpVisit.getAbdominalPain());
          selectVomiting(followUpVisit.getVomiting());
          selectSkinUlcers(followUpVisit.getSkinUlcers());
          selectUnexplainedBleeding(followUpVisit.getUnexplainedBleeding());
          selectComa(followUpVisit.getComa());
          selectLymphadenopathy(followUpVisit.getLymphadenopathy());
          selectInabilityToWalk(followUpVisit.getInabilityToWalk());
          selectSkinRash(followUpVisit.getSkinRash());
          selectConfusedDisoriented(followUpVisit.getConfusedDisoriented());
          selectSeizures(followUpVisit.getSeizures());
          selectOtherComplications(followUpVisit.getOtherComplications());
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
          ComparisonHelper.compareEqualEntities(followUpVisit, actualFollowUpVisit);
        });

    And(
        "I click on discard button from follow up view",
        () -> webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON));

    And(
        "^I change Follow-up visit fields and save$",
        () -> {
          followUpEditVisit = followUpVisitService.buildEditFollowUpVisit();
          selectPersonAvailableAndCooperative(followUpEditVisit.getPersonAvailableAndCooperative());
          fillDateAndTimeVisit(followUpVisitService.buildEditFollowUpVisit().getDateOfVisit());
          fillVisitRemark(followUpEditVisit.getVisitRemarks());
        });

    Then(
        "^I check all changes from follow up are correctly displayed$",
        () -> {
          final FollowUpVisit editedFollowUpVisit = collectEditedFollowUpData();
          ComparisonHelper.compareEqualEntities(followUpEditVisit, editedFollowUpVisit);
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
          webDriverHelpers.clickOnWebElementBySelector(FOLLOW_UP_VISITS_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(FROM_INPUT);
        });

    Then(
        "^I am validating the From and To dates displayed$",
        () -> {
          String uuid = apiState.getCreatedContact().getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          webDriverHelpers.clearAndFillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, uuid);
          fillDateFrom(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());
          fillDateTo(followUpVisitService.buildGeneratedFollowUpVisit().getDateOfVisit());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(3); // wait for filter reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CONTACT_GRID_RESULTS_ROWS);
          Assert.assertEquals(
              webDriverHelpers.getNumberOfElements(CONTACT_GRID_RESULTS_ROWS),
              1,
              "Contact grid results row is not correct");
        });
  }

  @SneakyThrows
  private FollowUpVisit collectFollowUpData() {
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
        .fever(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(FEVER))
        .headache(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HEADACHE))
        .musclePain(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(MUSCLE_PAIN))
        .acuteRespiratoryDistressSyndrome(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME))
        .cough(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(COUGH))
        .difficultyBreathing(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIFFICULTY_BREATHING))
        .pneumoniaClinicalRadiologic(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PNEUMONIA))
        .rapidBreathing(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RAPID_BREATHING))
        .runnyNose(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RUNNY_NOSE))
        .soreThroatPharyngitis(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SORE_THROAT_PHARYNGITIS))
        .diarrhea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(DIARRHEA))
        .nausea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(NAUSEA))
        .newLossOfSmell(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_SMELL))
        .newLossOfTaste(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LOSS_OF_TASTE))
        .otherClinicalSymptoms(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OTHER_CLINICAL_SYMPTOMS))
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
        .comments(webDriverHelpers.getValueFromWebElement(COMMENTS_INPUT))
        .firstSymptom(webDriverHelpers.getValueFromCombobox(FIRST_SYMPTOM_COMBOBOX))
        .dateOfSymptomOnset(parsedDateOfSymptomOnset)
        .build();
  }

  private FollowUpVisit collectEditedFollowUpData() {
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

  private void selectSourceOfBodyTemperature(String sourceOfBodyTemperature) {
    webDriverHelpers.selectFromCombobox(SOURCE_BODY_TEMPERATURE_COMBOBOX, sourceOfBodyTemperature);
  }

  private void selectBodyTemperature(String bodyTemperature) {
    webDriverHelpers.selectFromCombobox(CURRENT_BODY_TEMPERATURE_COMBOBOX, bodyTemperature);
  }

  private void selectFirstSymptom(String firstSymptom) {
    webDriverHelpers.selectFromCombobox(FIRST_SYMPTOM_COMBOBOX, firstSymptom);
  }

  private void selectChillsOrSweats(String chillsOrSweats) {
    webDriverHelpers.clickWebElementByText(CHILLS_OR_SWATS, chillsOrSweats);
  }

  private void selectHeadache(String headache) {
    webDriverHelpers.clickWebElementByText(HEADACHE, headache);
  }

  private void selectFeelingIll(String feelingIll) {
    webDriverHelpers.clickWebElementByText(FEELING_ILL, feelingIll);
  }

  private void selectMusclePain(String musclePain) {
    webDriverHelpers.clickWebElementByText(MUSCLE_PAIN, musclePain);
  }

  private void fillDateOfFirstSymptom(LocalDate dateOfFirstSymptom) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfFirstSymptom));
  }

  private void selectFever(String fever) {
    webDriverHelpers.clickWebElementByText(FEVER, fever);
  }

  private void selectShivering(String shivering) {
    webDriverHelpers.clickWebElementByText(SHIVERING, shivering);
  }

  private void selectOtherClinicalSymptoms(String otherClinicalSymptoms) {
    webDriverHelpers.clickWebElementByText(OTHER_CLINICAL_SYMPTOMS, otherClinicalSymptoms);
  }

  private void selectAcuteRespiratoryDistressSyndrome(String acuteRespiratoryDistressSyndrome) {
    webDriverHelpers.clickWebElementByText(
        ACUTE_RESPIRATORY_DISTRESS_SYNDROME, acuteRespiratoryDistressSyndrome);
  }

  private void selectCough(String cough) {
    webDriverHelpers.clickWebElementByText(COUGH, cough);
  }

  private void selectDifficultyBreathing(String difficultyBreathing) {
    webDriverHelpers.clickWebElementByText(DIFFICULTY_BREATHING, difficultyBreathing);
  }

  private void selectOxygenSaturationLower94(String oxygenSaturationLower94) {
    webDriverHelpers.clickWebElementByText(OXIGEN_SATURANTION, oxygenSaturationLower94);
  }

  private void selectPneumoniaClinicalOrRadiologic(String pneumoniaClinicalOrRadiologic) {
    webDriverHelpers.clickWebElementByText(PNEUMONIA, pneumoniaClinicalOrRadiologic);
  }

  private void selectPersonAvailableAndCooperative(String available) {
    webDriverHelpers.clickWebElementByText(PERSON_AVAILABLE_AND_COOPERATIVE_OPTIONS, available);
  }

  private void selectRapidBreathing(String rapidBreathing) {
    webDriverHelpers.clickWebElementByText(RAPID_BREATHING, rapidBreathing);
  }

  private void selectRespiratoryDiseaseVentilation(String respiratoryDiseaseVentilation) {
    webDriverHelpers.clickWebElementByText(
        RESPIRATORY_DISEASE_REQUIRING_VENTILATION, respiratoryDiseaseVentilation);
  }

  private void selectRunnyNose(String runnyNose) {
    webDriverHelpers.clickWebElementByText(RUNNY_NOSE, runnyNose);
  }

  private void fillDateOfSymptomOnset(LocalDate dateOfSymptomOnset) {
    webDriverHelpers.fillInWebElement(
        DATE_OF_SYMPTOM_ONSET_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
  }

  private void fillDateFrom(LocalDate from) {
    BaseSteps.driver.findElement(FROM_INPUT).clear();
    BaseSteps.driver.findElement(FROM_INPUT).sendKeys(DATE_FORMATTER.format(from));
  }

  private void fillDateTo(LocalDate to) {
    BaseSteps.driver.findElement(FROM_INPUT).clear();
    BaseSteps.driver.findElement(FROM_INPUT).sendKeys(DATE_FORMATTER.format(to));
  }

  private void fillVisitRemark(String visitRemark) {
    webDriverHelpers.clearAndFillInWebElement(VISIT_REMARKS_INPUT, visitRemark);
  }

  private void selectSoreThroat(String soreThroat) {
    webDriverHelpers.clickWebElementByText(SORE_THROAT_PHARYNGITIS, soreThroat);
  }

  private void selectFastHeartRate(String fastHeartRate) {
    webDriverHelpers.clickWebElementByText(FAST_HEART_RATE, fastHeartRate);
  }

  private void selectDiarrhea(String diarrhea) {
    webDriverHelpers.clickWebElementByText(DIARRHEA, diarrhea);
  }

  private void selectNausea(String nausea) {
    webDriverHelpers.clickWebElementByText(NAUSEA, nausea);
  }

  private void selectLossOfSmell(String lossOfSmell) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_SMELL, lossOfSmell);
  }

  private void selectLossOfTaste(String lossOfTaste) {
    webDriverHelpers.clickWebElementByText(LOSS_OF_TASTE, lossOfTaste);
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

  private void selectOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
    webDriverHelpers.clickWebElementByText(OTHER_CLINICAL_SYMPTOMS, otherNonHemorrhagicSymptoms);
  }

  private void fillComments(String symptomsComments) {
    webDriverHelpers.fillInWebElement(COMMENTS_INPUT, symptomsComments);
  }

  private void fillDateAndTimeVisit(LocalDate dateOfSymptomOnset) {
    webDriverHelpers.fillInWebElement(
        DATE_AND_TIME_OF_VISIT_INPUT, DATE_FORMATTER.format(dateOfSymptomOnset));
  }
}
