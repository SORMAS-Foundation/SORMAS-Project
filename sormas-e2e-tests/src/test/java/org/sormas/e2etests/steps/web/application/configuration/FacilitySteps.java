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

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_FACILITIES_TAB;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITIES_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class FacilitySteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public FacilitySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on Facilities button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_FACILITIES_TAB));

    When(
        "I click on New Entry button in Facilities tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(FACILITIES_NEW_ENTRY_BUTTON));

    When(
        "I set name, region and district in Facilities tab in Configuration",
        () -> {
          String timestamp = String.valueOf(System.currentTimeMillis());
          fillFacilityNameAndDescription("Facility" + timestamp);
          selectRegion(RegionsValues.VoreingestellteBundeslander.getName());
          selectDistrict(DistrictsValues.VoreingestellterLandkreis.getName());
        });

    When(
        "I set Facility Category to {string} and Facility Type to {string} in Facilities tab in Configuration",
        (String facilityCategory, String facilityType) -> {
          selectFacilityCategory(facilityCategory);
          selectFacilityType(facilityType);
        });
    When(
        "I click on Save Button in new Facility form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacilityCategory(String facility) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facility);
  }

  private void fillFacilityNameAndDescription(String facilityDescription) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_INPUT, facilityDescription);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }
}
