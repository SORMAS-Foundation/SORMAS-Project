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

public class EditCasePage {
  public static final By REPORT_DATE_INPUT = By.cssSelector("#reportDate input");
  public static final By RESPONSIBLE_REGION_INPUT = By.cssSelector("#region input");
  public static final By RESPONSIBLE_DISTRICT_INPUT = By.cssSelector("#district input");
  public static final By RESPONSIBLE_COMMUNITY_INPUT = By.cssSelector("#community input");
  public static final By PLACE_OF_STAY_SELECTED_VALUE =
      By.cssSelector("#facilityOrHome input[checked] + label");
  public static final By PLACE_DESCRIPTION_INPUT = By.cssSelector("#healthFacilityDetails");
  public static final By EXTERNAL_ID_INPUT = By.id("externalID");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By USER_INFORMATION =
      By.cssSelector(".v-slot-view-header .v-slot-primary div");
  public static final By CASE_SAVED_MESSAGE = By.xpath("//*[contains(text(),'Case saved')]");
  public static final By CASE_PERSON_TAB = By.cssSelector("div#tab-cases-person");
  public static final By NEW_TASK_BUTTON = By.cssSelector("div#taskNewTask");
  public static final By EDIT_TASK_BUTTON = By.cssSelector("div[id*='edit-task']");
  public static final By NEW_SAMPLE_BUTTON = By.cssSelector("[id='New sample']");
  public static final By EDIT_SAMPLE_BUTTON =
      By.cssSelector(
          "[location='samples'] [class='v-button v-widget link v-button-link compact v-button-compact']");
}
