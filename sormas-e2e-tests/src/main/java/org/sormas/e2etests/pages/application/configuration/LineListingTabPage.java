/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class LineListingTabPage {
  public static final By DISEASE_COMBO_BOX_LINE_LISTING_CONFIGURATION =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By ENABLE_LINE_LISTING_FOR_DISEASE_BUTTON =
      By.id("lineListingEnableForDisease");
  public static final By DISEASE_LABEL_LINE_LISTING =
      By.cssSelector("[class*='v-label v-widget rounded-corners v-label-rounded-corners']");
  public static final By DISEASE_BUTTONS_LINE_LISTING =
      By.cssSelector("[class*='v-widget borderless v-button-borderless']");
  public static final By EDIT_LINE_LISTING_BUTTON = By.id("lineListingEdit");
  public static final By DISABLE_ALL_LINE_LISTING = By.id("lineListingDisableAll");
  public static final By CONFIRM_DISABLE_LINE_LISTING_MODAL = By.id("actionConfirm");
  public static final By NOTIFICATION_LINE_LISTING_CONFIGURATION =
      By.cssSelector(".v-Notification-description");
}
