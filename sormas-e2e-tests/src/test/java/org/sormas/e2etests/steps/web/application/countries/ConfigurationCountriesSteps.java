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
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.COUNTRIES_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.COUNTRIES_TABLE_DATA;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.COUNTRIES_TABLE_ROW;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.COUNTRY_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.NUMBER_OF_COUNTRIES;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.SEARCH_COUNTRY;
import static org.sormas.e2etests.pages.application.configuration.CountriesTabPage.SUBCONTINENT_TABLE_VALUE;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class ConfigurationCountriesSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private final BaseSteps baseSteps;

  @SneakyThrows
  @Inject
  public ConfigurationCountriesSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;

    When(
        "I navigate to countries tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_COUNTRIES_TAB));

    When(
        "I fill search filter with {string} country name on Country Configuration Page",
        (String country) -> webDriverHelpers.fillInWebElement(SEARCH_COUNTRY, country));

    When(
        "I check the {string} name for the country on Country Configuration Page",
        (String expectedSubcontinentName) -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              COUNTRY_GRID_RESULTS_ROWS, expectedSubcontinentName);
          String subcontinentName =
              webDriverHelpers.getTextFromWebElement(SUBCONTINENT_TABLE_VALUE);
          softly.assertEquals(
              subcontinentName,
              expectedSubcontinentName,
              "The subcontinent name is different then expected");
          softly.assertAll();
        });
    Then(
        "I check that number of countries is at least (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          softly.assertTrue(
              Integer.valueOf(
                      Integer.parseInt(webDriverHelpers.getTextFromWebElement(NUMBER_OF_COUNTRIES)))
                  >= number,
              "Number of countries is not correct!");
          softly.assertAll();
        });

    When(
        "I check that Albania is correctly displayed",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData
                  .toString()
                  .contains(
                      "ISO CODE=ALB, SUBCONTINENT=Southeast Europe, EXTERNAL ID=21000125, UNO CODE=8, NAME=Albania"),
              "Albania is not correctly displayed!");
          softly.assertAll();
        });

    When(
        "I check that Albania is correctly displayed in German",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData
                  .toString()
                  .contains(
                      "ISO CODE=ALB, SUBKONTINENT=S\u00fcdosteuropa, EXTERNE ID=21000125, UNO CODE=8, NAME=Albanien"),
              "Albania is not correctly displayed!");
          softly.assertAll();
        });
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    headers.remove("EDIT");
    headers.remove("BEARBEITEN");
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(COUNTRIES_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COUNTRIES_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(COUNTRIES_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(COUNTRIES_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(COUNTRIES_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(COUNTRIES_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(COUNTRIES_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
