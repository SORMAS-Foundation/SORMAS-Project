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

public class CaseImportExportPage {

  public static final By CASE_EXPORT_BUTTON = By.id("export");
  public static final By CUSTOM_CASE_EXPORT_BUTTON = By.id("exportCaseCustom");
  public static final By NEW_EXPORT_CONFIGURATION_BUTTON = By.id("exportNewExportConfiguration");
  public static final By CONFIGURATION_NAME_INPUT =
      By.xpath("//*[@class='v-widget v-has-caption v-caption-on-top']//input");
  public static final By EXPORT_CONFIGURATION_DATA_DISEASE_CHECKBOX =
      By.xpath("//label[text()='Disease']");
  public static final By EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX =
      By.xpath("//label[text()='First name']");
  public static final By EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX =
      By.xpath("//label[text()='Last name']");
  public static final By NEW_EXPORT_CONFIGURATION_SAVE_BUTTON = By.id("actionSave");
  public static final By CUSTOM_CASE_EXPORT_DOWNLOAD_BUTTON =
      By.xpath("//div[@class='popupContent']//div[@class='v-slot v-slot-primary']");
  public static final By CUSTOM_CASE_DELETE_BUTTON =
      By.xpath(
          "//div[@class='popupContent']//div[@class='v-horizontallayout v-layout v-horizontal v-widget']//div[5]");
}
