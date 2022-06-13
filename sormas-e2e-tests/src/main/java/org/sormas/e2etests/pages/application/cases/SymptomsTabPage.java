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

public class SymptomsTabPage {
  public static final By CLEAR_ALL_BUTTON = new By.ById("actionClearAll");
  public static final By MAXIMUM_BODY_TEMPERATURE_IN_C_COMBOBOX =
      By.cssSelector("#temperature div");
  public static final By SOURCE_OF_BODY_TEMPERATURE_COMBOBOX =
      By.cssSelector("#temperatureSource div");
  public static final By CHILLS_OR_SWEATS_OPTIONS = By.cssSelector("#chillsSweats label");
  public static final By HEADACHE_OPTIONS = By.cssSelector("#headache label");
  public static final By FEELING_ILL_OPTIONS = By.cssSelector("#feelingIll label");
  public static final By MUSCLE_PAIN_OPTIONS = By.cssSelector("#musclePain label");
  public static final By FEVER_OPTIONS = By.cssSelector("#fever label");
  public static final By SHIVERING_OPTIONS = By.cssSelector("#shivering label");
  public static final By ACUTE_RESPIRATORY_DISTRESS_SYNDROME_OPTIONS =
      By.cssSelector("#acuteRespiratoryDistressSyndrome label");
  public static final By OXYGEN_SATURATION_LOWER_94_OPTIONS =
      By.cssSelector("#oxygenSaturationLower94 label");
  public static final By COUGH_OPTIONS = By.cssSelector("#cough label");
  public static final By PNEUMONIA_CLINICAL_OR_RADIOLOGIC_OPTIONS =
      By.cssSelector("#pneumoniaClinicalOrRadiologic label");
  public static final By DIFFICULTY_BREATHING_OPTIONS =
      By.cssSelector("#difficultyBreathing label");
  public static final By RAPID_BREATHING_OPTIONS = By.cssSelector("#rapidBreathing label");
  public static final By RESPIRATORY_DISEASE_VENTILATION_OPTIONS =
      By.cssSelector("#respiratoryDiseaseVentilation label");
  public static final By RUNNY_NOSE_OPTIONS = By.cssSelector("#runnyNose label");
  public static final By SORE_THROAT_OPTIONS = By.cssSelector("#soreThroat label");
  public static final By SORE_THROAT_YES_OPTION =
      By.xpath("//div[@id='soreThroat']//label[contains(text(), 'Yes')]");
  public static final By FAST_HEART_RATE_OPTIONS = By.cssSelector("#fastHeartRate label");
  public static final By DIARRHEA_OPTIONS = By.cssSelector("#diarrhea label");
  public static final By NAUSEA_OPTIONS = By.cssSelector("#nausea label");
  public static final By LOSS_OF_SMELL_OPTIONS = By.cssSelector("#lossOfSmell label");
  public static final By LOSS_OF_TASTE_OPTIONS = By.cssSelector("#lossOfTaste label");
  public static final By OTHER_NON_HEMORRHAGIC_SYMPTOMS_OPTIONS =
      By.cssSelector("#otherNonHemorrhagicSymptoms label");
  public static final By OTHER_NON_HEMORRHAGIC_SYMPTOMS_INPUT =
      By.cssSelector("#otherNonHemorrhagicSymptomsText");
  public static final By SYMPTOMS_COMMENTS_INPUT = By.cssSelector("#symptomsComments");
  public static final By FIRST_SYMPTOM_COMBOBOX = By.cssSelector("#onsetSymptom div");
  public static final By DATE_OF_SYMPTOM_INPUT = By.cssSelector("#onsetDate input");
  public static final By CASE_TAB = By.cssSelector("#tab-cases-data span");
  public static final By SAVE_BUTTON = By.cssSelector("#commit");
}
