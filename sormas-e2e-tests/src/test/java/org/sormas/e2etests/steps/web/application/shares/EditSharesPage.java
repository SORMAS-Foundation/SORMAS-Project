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

package org.sormas.e2etests.steps.web.application.shares;

import org.openqa.selenium.By;

public class EditSharesPage {

  public static final By SHARE_FIRST_EYE_ICON = By.xpath("(//span[@class='v-icon v-icon-eye'])[1]");
  public static final By SHARE_UUID_CASE_TITLE =
      By.cssSelector(".popupContent [class='v-grid-cell v-grid-cell-focused'] a");
  public static final By ACCEPT_BUTTON = By.cssSelector("div #actionAccept");

  public static By getCheckBoxFromShareFormByIndex(int index) {
    return By.xpath(
        String.format("(//*[@class='popupContent']//div[@class='row']//label)[%s]", index));
  }

  public static final By SHARE_OPTION_CHECKBOX =
      By.xpath("//*[@class='popupContent']//input[@type='checkbox']");
  public static final By POPUP_COLUMN_HEADER =
      By.xpath(
          "//*[@class='popupContent']//table//thead//tr/th//div[@class='v-grid-column-header-content v-grid-column-default-header-content']");

  public static By getPopupColumnHeaderByIndex(int index) {
    return By.xpath(
        String.format(
            "(//*[@class='popupContent']//table//thead//tr/th//div[@class='v-grid-column-header-content v-grid-column-default-header-content'])[%s]",
            index));
  }

  public static By WARNING_ACCEPT_CASE_BEFORE_CONTACT_HEADER_DE =
      By.xpath(
          "//*[@class='popupContent']//div[contains(text(), 'Kontakt(e) k\u00F6nnen nicht \u00FCbernommen werden')]");
}
