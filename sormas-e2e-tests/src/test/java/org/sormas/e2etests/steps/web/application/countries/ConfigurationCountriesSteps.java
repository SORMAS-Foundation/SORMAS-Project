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

package org.sormas.e2etests.steps.web.application.countries;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_COUNTRIES_TAB;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.SEARCH_COUNTRY;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.SUBCONTINENT_TABLE_VALUE;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class ConfigurationCountriesSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;

  @SneakyThrows
  @Inject
  public ConfigurationCountriesSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

    When(
        "I navigate to countries tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_COUNTRIES_TAB));

    When(
        "I fill search filter with {string} country name on Country Configuration Page",
        (String country) -> webDriverHelpers.fillInWebElement(SEARCH_COUNTRY, country));
    When(
        "I check the subcontinent name for Germany on Country Configuration Page",
        () -> {
          TimeUnit.SECONDS.sleep(4); // waiting for page loaded
          String subcontinentName =
              webDriverHelpers.getTextFromWebElement(SUBCONTINENT_TABLE_VALUE);
          String expectedSubcontinentName = "Central Europe";
          softly.assertEquals(
              subcontinentName,
              expectedSubcontinentName,
              "The subcontinent name is different then expected");
          softly.assertAll();
        });
  }
}