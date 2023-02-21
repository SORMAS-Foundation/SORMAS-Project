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

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_OUTBREAKS_TAB;
import static org.sormas.e2etests.pages.application.configuration.OutbreaksTabPage.DISCARD_OUTBREAK_CONFIGURATION_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.OutbreaksTabPage.DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.OutbreaksTabPage.REGION_DISEASE_MATRIX_OUTBREAKS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.OutbreaksTabPage.SAVE_OUTBREAK_CONFIGURATION_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.OutbreaksTabPage.SET_OUTBREAK_RADIO_BUTTONS;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class OutbreaksSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  private final BaseSteps baseSteps;

  @Inject
  public OutbreaksSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I click on the Outbreaks Tab in the Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              CONFIGURATION_OUTBREAKS_TAB);
          webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_OUTBREAKS_TAB);
        });

    Then(
        "I Verify the presence of all Diseases in Outbreaks Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION);
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "AFP");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "ANTHRAX");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "COVID-19");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "CHOLERA");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "CRS");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "DENGUE");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "EVD");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "GUINEA WORM");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "RABIES");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "NEW FLU");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "LASSA");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "MEASLES");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "MONKEYPOX");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "UNDEFINED");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "OTHER");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "PLAGUE");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "POLIO");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "VHF");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "YELLOW FEVER");
        });

    Then(
        "I Verify the presence of all Regions in Outbreaks Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION);
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Bayern");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Berlin");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Brandenburg");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Bremen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Hamburg");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Hessen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Mecklenburg-Vorpommern");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Niedersachsen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Nordrhein-Westfalen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Region1");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Region2");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Rheinland-Pfalz");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Saarland");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Sachsen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Sachsen-Anhalt");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Schleswig-Holstein");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION, "Th\u00FCringen");
          webDriverHelpers.verifyListContainsText(
              DISEASE_GRID_CELL_HEADERS_OUTBREAKS_CONFIGURATION,
              "Voreingestellte Bundesl\u00E4nder");
        });

    When(
        "I Verify the presence of Matrix in Outbreaks Configuration Page",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(REGION_DISEASE_MATRIX_OUTBREAKS_CONFIGURATION),
              "Error: Matrix is not present in Outbreaks Configuration page ");
          softly.assertAll();
        });

    When(
        "I click on one of the Outbreaks Matrix element in Outbreaks Configuration Page",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              REGION_DISEASE_MATRIX_OUTBREAKS_CONFIGURATION);
          webDriverHelpers.clickOnWebElementBySelectorAndIndex(
              REGION_DISEASE_MATRIX_OUTBREAKS_CONFIGURATION, 1);
        });

    Then(
        "I verify the Disease-Region popup elements are displayed in Outbreaks Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SET_OUTBREAK_RADIO_BUTTONS);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(SET_OUTBREAK_RADIO_BUTTONS),
              "Error: Radio buttons are not present in the Disease-region popup ");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(SAVE_OUTBREAK_CONFIGURATION_BUTTON),
              "Error: Save button is not present in the Disease-region popup ");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DISCARD_OUTBREAK_CONFIGURATION_BUTTON),
              "Error: Discard button is not present in the Disease-region popup ");
          softly.assertAll();
        });

    Then(
        "^I Click the ([^\"]*) button in Outbreaks Configuration Page$",
        (String button) -> {
          switch (button) {
            case "Save":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  SAVE_OUTBREAK_CONFIGURATION_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(SAVE_OUTBREAK_CONFIGURATION_BUTTON);
              break;
            case "Discard":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  DISCARD_OUTBREAK_CONFIGURATION_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(DISCARD_OUTBREAK_CONFIGURATION_BUTTON);
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });
  }
}
