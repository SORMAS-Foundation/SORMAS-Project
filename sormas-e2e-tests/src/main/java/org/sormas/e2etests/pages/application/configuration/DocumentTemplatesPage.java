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

package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class DocumentTemplatesPage {
  public static final By UPLOAD_CASE_TEMPLATE_BUTTON =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div[1]/div/div[1]/div/div/div[3]/div");
  public static final By UPLOAD_CONTACT_TEMPLATE_BUTTON =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div[3]/div/div[1]/div/div/div[3]/div");
  public static final By UPLOAD_EVENT_PARTICIPANT_TEMPLATE_BUTTON =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div[5]/div/div[1]/div/div/div[3]/div");
  public static final By UPLOAD_TRAVEL_ENTRY_TEMPLATE_BUTTON =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div[7]/div/div[1]/div/div/div[3]/div");
  public static final By UPLOAD_EVENT_HANDOUT_TEMPLATE_BUTTON =
      By.xpath(
          "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div[9]/div/div[1]/div/div/div[3]/div");
  public static final By UPLOAD_TEMPLATE_POPUP_BUTTON =
      By.cssSelector(".popupContent [class='v-button']");
  public static final By FILE_PICKER = By.cssSelector(".popupContent [class='gwt-FileUpload']");
  public static final By TEMPLATE_OVERWRITE_CONFIRM_BUTTON = By.cssSelector("div#actionConfirm");
  public static final By UPLOAD_SUCCESS_POPUP = By.cssSelector("div#actionOkay");
}
