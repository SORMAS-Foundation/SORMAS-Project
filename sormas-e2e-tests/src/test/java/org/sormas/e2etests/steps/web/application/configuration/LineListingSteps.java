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

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_LINE_LISTING_TAB;
import static org.sormas.e2etests.pages.application.configuration.LineListingTabPage.DISEASE_COMBO_BOX_LINE_LISTING_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.LineListingTabPage.ENABLE_LINE_LISTING_FOR_DISEASE_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class LineListingSteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public LineListingSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on Line Listing button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_LINE_LISTING_TAB));

    Then(
        "I Verify the page elements are present in Line Listing Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              ENABLE_LINE_LISTING_FOR_DISEASE_BUTTON);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(ENABLE_LINE_LISTING_FOR_DISEASE_BUTTON),
              "Enable Line Listing for Disease Button is not present in the Line Listing Configuration Page");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DISEASE_COMBO_BOX_LINE_LISTING_CONFIGURATION),
              "Disease combo-box is not present in the Line Listing Configuration Page");
          softly.assertAll();
        });
  }
}
