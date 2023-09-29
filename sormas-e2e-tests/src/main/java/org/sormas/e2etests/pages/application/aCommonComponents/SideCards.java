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

package org.sormas.e2etests.pages.application.aCommonComponents;

import org.openqa.selenium.By;

public class SideCards {

  public static final By SHARE_SORMAS_2_SORMAS_BUTTON = By.cssSelector("#sormasToSormasShare");
  public static final By SHARE_ORGANIZATION_POPUP_COMBOBOX =
      By.cssSelector(".popupContent #organization div");
  public static final By HAND_THE_OWNERSHIP_CHECKBOX =
      By.cssSelector(".popupContent #handOverOwnership label");
  public static final By EXCLUDE_PERSONAL_DATA_CHECKBOX =
      By.cssSelector(".popupContent #pseudonymizeData label");
  public static final By SHARE_REPORT_CHECKBOX =
      By.cssSelector(".popupContent #withSurveillanceReports label");
  public static final By ADDED_SAMPLES_IN_SAMPLE_CARD =
      By.xpath("//*[@location='samples']//*[@class='v-slot v-slot-s-list']/div/div/div/div");
  public static final By EDIT_SAMPLE_BUTTON = By.xpath("//div[contains(@id, 'edit-sample')]");
  public static final By EDIT_SURVEILLANCE_REPORT_BUTTON =
      By.xpath("//div[@location='surveillanceReports']//div[contains(@id, 'edit')]");
  public static final By SAMPLES_DISPLAY_ASSOCIATED_LAB_MESSAGES_BUTTON =
      By.xpath("//div[contains(@id, 'see-associated-lab-messages')]");
  public static final By REPORTS_DISPLAY_ASSOCIATED_EXTERNAL_MESSAGES_BUTTON =
      By.xpath("//div[contains(@id, 'see-associated-message')]");
  public static By HANDOVER_SIDE_CARD = By.xpath("//div[@location='sormasToSormas']");
  public static final By SURVEILLANCE_REPORT_NOTIFICATION_DETAILS = By.id("notificationDetails");
  public static final By POPUP_EDIT_REPORT_WINDOW_SAVE_BUTTON = By.cssSelector(".v-window #commit");

  public static By checkTextInHandoverSideComponent(String text) {
    return By.xpath(
        String.format("//div[@location='sormasToSormas']//div[contains(text(),'%s')]", text));
  }

  public static By checkTextInSampleSideComponent(String text) {
    return By.xpath(
        String.format("//div[contains(@location,'samples')]//div[contains(text(), '%s')]", text));
  }

  public static By checkTextInImmunizationSideComponent(String text) {
    return By.xpath(
        String.format(
            "//div[contains(@location,'vaccinations')]//div[contains(text(), '%s')]", text));
  }

  public static By checkTextInReportSideComponent(String text) {
    return By.xpath(
        String.format(
            "//div[text()='Meldevorg\u00E4nge']/../../../../../..//div[text()='%s']", text));
  }

  public static final By LINKED_SHARED_ORGANIZATION_SELECTED_VALUE =
      By.xpath(
          "//div[@location='sormasToSormas']//div[@class='v-slot v-slot-s-list-entry v-slot-s-list-entry-no-border']");
  public static final By SHARE_SORMAS_2_SORMAS_POPUP_BUTTON =
      By.cssSelector(".popupContent #commit");

  public static By getEditSampleButtonByNumber(Integer number) {
    return By.xpath(String.format("(//div[contains(@id, 'edit-sample')])[%x]", number));
  }
}
