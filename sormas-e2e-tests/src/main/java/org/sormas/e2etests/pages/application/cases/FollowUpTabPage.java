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

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class FollowUpTabPage {

  public static final By NEW_VISIT_BUTTON = By.id("visitNewVisit");
  public static final By PERSONS_AVAILABLE_OPTIONS =
      By.cssSelector("[id='visitStatus'] [class='v-radiobutton v-select-option']");
  public static final By AVAILABLE_AND_COOPERATIVE =
      By.cssSelector("[id='visitStatus'] span:last-child");
  public static final By PERSON_AVAILABLE_AND_COOPERATIVE = By.cssSelector("#visitStatus label");
  public static final By DATE_OF_VISIT_INPUT = By.cssSelector("[id='visitDateTime_date'] input");
  public static final By TIME_OF_VISIT_INPUT =
      By.cssSelector("[id='visitDateTime_time'] [class='v-filterselect-button']");
  public static final By VISIT_REMARKS = By.cssSelector("[id='visitRemarks']");
  public static final By CURRENT_BODY_TEMPERATURE_COMBOBOX =
      By.cssSelector("[id='temperature'] div");
  public static final By CURRENT_BODY_TEMPERATURE_INPUT =
      By.cssSelector("[id='temperature'] input");
  public static final By SOURCE_OF_BODY_TEMPERATURE_COMBOBOX =
      By.cssSelector("[id='temperatureSource'] div");
  public static final By SOURCE_OF_BODY_TEMPERATURE_INPUT =
      By.cssSelector("[id='temperatureSource'] input");
  public static final By CLEAR_ALL = By.cssSelector("[id='actionClearAll']");
  public static final By ACTION_CONFIRM = By.cssSelector("[id='actionConfirm']");
  public static final By SET_CLEARED_TO_NO_BUTTON = By.cssSelector("[id='symptomsSetClearedToNo']");
  public static final By OPTION_FOR_SET_BUTTONS =
      By.cssSelector(
          "[class='v-button v-widget link v-button-link caption-overflow-label v-button-caption-overflow-label']");
  public static final By CHILLS_SWEATS_OPTIONS =
      By.cssSelector("[id='chillsSweats'] [class='v-checkbox v-select-option']");
  public static final By CHILLS_SWEATS_YES_BUTTON =
      By.xpath("//div[@id='chillsSweats']//label[contains(text(), 'Yes')]");
  public static final By FEELING_ILL_OPTIONS =
      By.cssSelector("[id='feelingIll'] [class='v-checkbox v-select-option']");
  public static final By FEELING_ILL_YES_BUTTON =
      By.xpath("//div[@id='feelingIll']//label[contains(text(), 'Yes')]");
  public static final By FEVER_OPTIONS =
      By.cssSelector("[id='fever'] [class='v-checkbox v-select-option']");
  public static final By FEVER_YES_BUTTON =
      By.xpath("//div[@id='fever']//label[contains(text(), 'Yes')]");
  public static final By HEADACHE_OPTIONS =
      By.cssSelector("[id='headache'] [class='v-checkbox v-select-option']");
  public static final By MUSCLE_PAIN_OPTIONS =
      By.cssSelector("[id='musclePain'] [class='v-checkbox v-select-option']");
  public static final By SHIVERING_OPTIONS =
      By.cssSelector("[id='shivering'] [class='v-checkbox v-select-option']");
  public static final By ABNORMAL_LUNG_XRAY_FINDINGS_OPTIONS =
      By.cssSelector("[id='abnormalLungXrayFindings'] [class='v-checkbox v-select-option']");
  public static final By FATIGUE_WEAKNESS_OPTIONS =
      By.cssSelector("[id='fatigueWeakness'] [class='v-checkbox v-select-option']");
  public static final By JOINT_PAIN_OPTIONS =
      By.cssSelector("[id='jointPain'] [class='v-checkbox v-select-option']");
  public static final By COUGH_WITH_HEAMOPTYSIS_OPTIONS =
      By.cssSelector("[id='coughWithHeamoptysis'] [class='v-checkbox v-select-option']");
  public static final By COUGH_WITH_SPUTUM_OPTIONS =
      By.cssSelector("[id='coughWithSputum'] [class='v-checkbox v-select-option']");
  public static final By FLUID_IN_LUNG_CAVITY_XRAY_OPTIONS =
      By.cssSelector("[id='fluidInLungCavityXray'] [class='v-checkbox v-select-option']");
  public static final By FLUID_IN_LUNG_CAVITY_AUSCULTATION_OPTIONS =
      By.cssSelector("[id='fluidInLungCavityAuscultation'] [class='v-checkbox v-select-option']");
  public static final By INDRAWING_OF_CHEST_WALL_OPTIONS =
      By.cssSelector("[id='inDrawingOfChestWall'] [class='v-checkbox v-select-option']");
  public static final By ABDOMINAL_PAIN_OPTIONS =
      By.cssSelector("[id='abdominalPain'] [class='v-checkbox v-select-option']");
  public static final By VOMITING_OPTIONS =
      By.cssSelector("[id='vomiting'] [class='v-checkbox v-select-option']");
  public static final By SKIN_ULCERS_OPTIONS =
      By.cssSelector("[id='skinUlcers'] [class='v-checkbox v-select-option']");
  public static final By UNEXPLAINED_BLEEDING_OPTIONS =
      By.cssSelector("[id='unexplainedBleeding'] [class='v-checkbox v-select-option']");
  public static final By COMA_OPTIONS =
      By.cssSelector("[id='coma'] [class='v-checkbox v-select-option']");
  public static final By LYMPHADENOPATHY_OPTIONS =
      By.cssSelector("[id='lymphadenopathy'] [class='v-checkbox v-select-option']");
  public static final By INABILITY_TO_WALK_OPTIONS =
      By.cssSelector("[id='inabilityToWalk'] [class='v-checkbox v-select-option']");
  public static final By SKIN_RASH_OPTIONS =
      By.cssSelector("[id='skinRash'] [class='v-checkbox v-select-option']");
  public static final By CONFUSED_DISORIENTED_OPTIONS =
      By.cssSelector("[id='confusedDisoriented'] [class='v-checkbox v-select-option']");
  public static final By SEIZURES_OPTIONS =
      By.cssSelector("[id='seizures'] [class='v-checkbox v-select-option']");
  public static final By OTHER_COMPLICATIONS_OPTIONS =
      By.cssSelector("[id='otherComplications'] [class='v-checkbox v-select-option']");
  public static final By ACUTE_RESPIRATORY_OPTIONS =
      By.cssSelector(
          "[id='acuteRespiratoryDistressSyndrome'] [class='v-checkbox v-select-option']");
  public static final By COUGH_OPTIONS =
      By.cssSelector("[id='cough'] [class='v-checkbox v-select-option']");
  public static final By DIFFICULTY_BREATHING_OPTIONS =
      By.cssSelector("[id='difficultyBreathing'] [class='v-checkbox v-select-option']");
  public static final By OXYGEN_SATURATION_OPTIONS =
      By.cssSelector("[id='oxygenSaturationLower94'] [class='v-checkbox v-select-option']");
  public static final By PNEUMONIA_OPTIONS =
      By.cssSelector("[id='pneumoniaClinicalOrRadiologic'] [class='v-checkbox v-select-option']");
  public static final By RAPID_BREATHING_OPTIONS =
      By.cssSelector("[id='rapidBreathing'] [class='v-checkbox v-select-option']");
  public static final By RESPIRATORY_DISEASE_OPTIONS =
      By.cssSelector("[id='respiratoryDiseaseVentilation'] [class='v-checkbox v-select-option']");
  public static final By RUNNY_NOSE_OPTIONS =
      By.cssSelector("[id='runnyNose'] [class='v-checkbox v-select-option']");
  public static final By SORE_THROAT_OPTIONS =
      By.cssSelector("[id='soreThroat'] [class='v-checkbox v-select-option']");
  public static final By FAST_HEART_OPTIONS =
      By.cssSelector("[id='fastHeartRate'] [class='v-checkbox v-select-option']");
  public static final By DIARRHEA_OPTIONS =
      By.cssSelector("[id='diarrhea'] [class='v-checkbox v-select-option']");
  public static final By NAUSEA_OPTIONS =
      By.cssSelector("[id='nausea'] [class='v-checkbox v-select-option']");
  public static final By LOSS_OF_SMELL_OPTIONS =
      By.cssSelector("[id='lossOfSmell'] [class='v-checkbox v-select-option']");
  public static final By LOSS_OF_TASTE_OPTIONS =
      By.cssSelector("[id='lossOfTaste'] [class='v-checkbox v-select-option']");
  public static final By OTHER_OPTIONS =
      By.cssSelector("[id='otherNonHemorrhagicSymptoms'] [class='v-checkbox v-select-option']");
  public static final By SYMPTOMS_COMMENTS_INPUT = By.cssSelector("[id='symptomsComments']");
  public static final By FIRST_SYMPTOM_COMBOBOX = By.cssSelector("[id='onsetSymptom'] div");
  public static final By FIRST_SYMPTOM_INPUT = By.cssSelector("[id='onsetSymptom'] input");
  public static final By DATE_OF_ONSET_INPUT = By.cssSelector("[id='onsetDate'] input");
  public static final By SAVE_BUTTON = By.cssSelector("[id='commit']");
  public static final By EDIT_VISIT_BUTTON = By.cssSelector(".v-icon.v-icon-edit");
  public static final By SPECIFY_OTHER_SYMPTOMS =
      By.cssSelector("[id='otherNonHemorrhagicSymptomsText']");
  public static final By CONTACT_PERSONS_PHONE_NUMBER =
      By.xpath("//div[@class='v-link v-widget v-has-width']//span");
  public static final By EXPORT_FOLLOW_UP_BUTTON = By.id("export");
}
