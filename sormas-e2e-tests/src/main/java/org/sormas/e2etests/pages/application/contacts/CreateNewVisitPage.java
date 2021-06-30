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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class CreateNewVisitPage {
  public static final By PERSON_AVAILABLE_AND_COOPERATIVE =
      By.cssSelector("#visitStatus > span > label");
  public static final By DATE_AND_TIME_OF_VISIT_INPUT = By.cssSelector("#visitDateTime_date input");
  public static final By TIME_OF_VISIT_INPUT = By.cssSelector("#visitDateTime_time div");
  public static final By VISIT_REMARKS_INPUT = By.id("visitRemarks");
  public static final By CURRENT_BODY_TEMPERATURE_COMBOBOX = By.cssSelector("#temperature div");
  public static final By SOURCE_BODY_TEMPERATURE_COMBOBOX =
      By.cssSelector("#temperatureSource div");
  public static final By CHILLS_OR_SWATS_LABEL = By.cssSelector("#chillsSweats > span label");
  public static final By FEELING_ILL_LABEL = By.cssSelector("#feelingIll > span label");
  public static final By FEVER_LABEL = By.cssSelector("#fever > span label");
  public static final By HEADACHE_LABEL = By.cssSelector("#headache > span label");
  public static final By MUSCLE_PAIN_LABEL = By.cssSelector("#musclePain > span label");
  public static final By SHIVERING_LABEL = By.cssSelector("#shivering > span label");
  public static final By OXIGEN_SATURANTION_LABEL =
      By.cssSelector("#oxygenSaturationLower94 > span label");
  public static final By PNEUMONIA_LABEL =
      By.cssSelector("#pneumoniaClinicalOrRadiologic > span label");
  public static final By RAPID_BREATHING_LABEL = By.cssSelector("#rapidBreathing > span label");
  public static final By RESPIRATORY_DISEASE_REQUIRING_VENTILATION_LABEL =
      By.cssSelector("#respiratoryDiseaseVentilation > span label");
  public static final By RUNNY_NOSE_LABEL = By.cssSelector("#runnyNose > span label");
  public static final By ACUTE_RESPIRATORY_DISTRESS_SYNDROME_LABEL =
      By.cssSelector("#acuteRespiratoryDistressSyndrome > span label");
  public static final By COUGH_LABEL = By.cssSelector("#cough > span label");
  public static final By DIFFICULTY_BREATHING_LABEL =
      By.cssSelector("#difficultyBreathing > span label");
  public static final By SORE_THROAT_PHARYNGITIS_LABEL = By.cssSelector("#soreThroat > span label");
  public static final By FAST_HEART_RATE_LABEL = By.cssSelector("#fastHeartRate > span label");
  public static final By DIARRHEA_LABEL = By.cssSelector("#diarrhea > span label");
  public static final By NAUSEA_LABEL = By.cssSelector("#nausea > span label");
  public static final By LOSS_OF_SMELL_LABEL = By.cssSelector("#lossOfSmell > span label");
  public static final By LOSS_OF_TASTE_LABEL = By.cssSelector("#lossOfTaste > span label");
  public static final By OTHER_CLINICAL_SYMPTOMS_LABEL =
      By.cssSelector("#otherNonHemorrhagicSymptoms > span label");
  public static final By SPECIFIC_OTHER_SYMPTOMS_INPUT = By.id("otherNonHemorrhagicSymptomsText");
  public static final By COMMENTS_INPUT = By.id("symptomsComments");
  public static final By FIRSTSYMPTOM_COMBOBOX = By.cssSelector("#onsetSymptom div");
  public static final By DATE_OF_SYMPTOM_ONSET_INPUT = By.cssSelector("#onsetDate input");
  public static final By SAVE_VISIT_BUTTON = By.id("commit");
  public static final By DISCARD_BUTTON = By.id("discard");
}
