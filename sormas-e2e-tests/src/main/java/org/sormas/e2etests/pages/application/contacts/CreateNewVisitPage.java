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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class CreateNewVisitPage {
  public static final By PERSON_AVAILABLE_AND_COOPERATIVE_OPTIONS =
      By.cssSelector("#visitStatus > span > label");
  public static final By DATE_AND_TIME_OF_VISIT_INPUT = By.cssSelector("#visitDateTime_date input");
  public static final By VISIT_REMARKS_INPUT = By.id("visitRemarks");
  public static final By CURRENT_BODY_TEMPERATURE_COMBOBOX = By.cssSelector("#temperature div");
  public static final By SOURCE_BODY_TEMPERATURE_COMBOBOX =
      By.cssSelector("#temperatureSource div");
  public static final By CHILLS_OR_SWATS = By.cssSelector("#chillsSweats > span label");
  public static final By FEELING_ILL = By.cssSelector("#feelingIll > span label");
  public static final By FEVER = By.cssSelector("#fever > span label");
  public static final By HEADACHE = By.cssSelector("#headache > span label");
  public static final By MUSCLE_PAIN = By.cssSelector("#musclePain > span label");
  public static final By SHIVERING = By.cssSelector("#shivering > span label");
  public static final By OXIGEN_SATURANTION =
      By.cssSelector("#oxygenSaturationLower94 > span label");
  public static final By PNEUMONIA = By.cssSelector("#pneumoniaClinicalOrRadiologic > span label");
  public static final By RAPID_BREATHING = By.cssSelector("#rapidBreathing > span label");
  public static final By RESPIRATORY_DISEASE_REQUIRING_VENTILATION =
      By.cssSelector("#respiratoryDiseaseVentilation > span label");
  public static final By RUNNY_NOSE = By.cssSelector("#runnyNose > span label");
  public static final By ACUTE_RESPIRATORY_DISTRESS_SYNDROME =
      By.cssSelector("#acuteRespiratoryDistressSyndrome > span label");
  public static final By COUGH = By.cssSelector("#cough > span label");
  public static final By DIFFICULTY_BREATHING = By.cssSelector("#difficultyBreathing > span label");
  public static final By SORE_THROAT_PHARYNGITIS = By.cssSelector("#soreThroat > span label");
  public static final By FAST_HEART_RATE = By.cssSelector("#fastHeartRate > span label");
  public static final By DIARRHEA = By.cssSelector("#diarrhea > span label");
  public static final By NAUSEA = By.cssSelector("#nausea > span label");
  public static final By LOSS_OF_SMELL = By.cssSelector("#lossOfSmell > span label");
  public static final By LOSS_OF_TASTE = By.cssSelector("#lossOfTaste > span label");
  public static final By OTHER_CLINICAL_SYMPTOMS =
      By.cssSelector("#otherNonHemorrhagicSymptoms > span label");
  public static final By COMMENTS_INPUT = By.id("symptomsComments");
  public static final By FIRST_SYMPTOM_COMBOBOX = By.cssSelector("#onsetSymptom div");
  public static final By DATE_OF_SYMPTOM_ONSET_INPUT = By.cssSelector("#onsetDate input");
  public static final By SAVE_VISIT_BUTTON = By.id("commit");
  public static final By DISCARD_BUTTON = By.id("discard");
}
