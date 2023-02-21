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

package org.sormas.e2etests.steps.web.application.configuration;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_POINTS_OF_ENTRY_TAB;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_ACTIVE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_COUNTRY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_DISTRICT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_ENTER_BULK_EDIT_MODE_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_EXPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_REGION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_RELEVANCE_STATUS_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.configuration.PointsOfEntryTabPage.POINT_OF_ENTRY_TYPE_FILTER_COMBOBOX;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class PointsOfEntrySteps implements En {
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public PointsOfEntrySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on Points Of Entry button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_POINTS_OF_ENTRY_TAB));

    Then(
        "I Verify the page elements are present in Points Of Entry Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              POE_ENTER_BULK_EDIT_MODE_BUTTON);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_IMPORT_BUTTON),
              "Import Button is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_EXPORT_BUTTON),
              "Export Button is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_NEW_ENTRY_BUTTON),
              "New Entry Button is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_ENTER_BULK_EDIT_MODE_BUTTON),
              "Enter Bulk Edit Mode Button is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_SEARCH_INPUT),
              "Search Input is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_COUNTRY_FILTER_COMBOBOX),
              "Country Combo box is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_REGION_FILTER_COMBOBOX),
              "Region Combo box is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_DISTRICT_FILTER_COMBOBOX),
              "District Combo box is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POINT_OF_ENTRY_TYPE_FILTER_COMBOBOX),
              "Points of Entry Type Combo box is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_ACTIVE_FILTER_COMBOBOX),
              "Points of Entry Active? Combo box is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_RESET_FILTERS_BUTTON),
              "Reset Filters Button is Not present in Points of Entry Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(POE_RELEVANCE_STATUS_COMBOBOX),
              "Relevance status Combo box is Not present in Points of Entry Configuration");
          softly.assertAll();
        });
  }
}
