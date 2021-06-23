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

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class FollowUpTabPage {

  public static final By NEW_VISIT_BUTTON = By.cssSelector("[id='visitNewVisit']");
  // Create new visit pop-up mappings
  public static final By PERSONS_AVAILABLE_OPTIONS =
      By.cssSelector("[id='visitStatus'] [class='v-radiobutton v-select-option']");
  public static final By AVAILABLE_AND_COOPERATIVE =
      By.cssSelector("[id='visitStatus'] span:last-child");
  public static final By DATE_OF_VISIT_INPUT = By.cssSelector("[id='visitDateTime_date'] input");
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
  public static final By SET_CLEARED_TO_NO_BUTTON = By.cssSelector("[id='symptomsSetClearedToNo']");
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
  public static final By SYMPTOMS_COMMENTS_INPUT = By.cssSelector("[id='symptomsComments']");
  public static final By FIRST_SYMPTOM_COMBOBOX = By.cssSelector("[id='onsetSymptom'] div");
  public static final By FIRST_SYMPTOM_INPUT = By.cssSelector("[id='onsetSymptom'] input");
  public static final By DATE_OF_ONSET_INPUT = By.cssSelector("[id='onsetDate'] input");
  public static final By SAVE_VISIT_BUTTON = By.cssSelector("[id='commit']");
  public static final By EDIT_VISIT_BUTTON = By.cssSelector("table span");
}
