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

package org.sormas.e2etests.steps.web.application.continents;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_CONTINENTS_TAB;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.CONTINENTS_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.CONTINENTS_DROPDOWN_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.CONTINENTS_NAME_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.CONTINENTS_TABLE_DATA;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.CONTINENTS_TABLE_ROW;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.ENTER_BULK_EDIT_MODE_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.EXPORT_BUTTON_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.IMPORT_BUTTON_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.IMPORT_DEFAULT_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.NEW_ENTRY_BUTTON_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.RESET_FILTERS_BUTTON_CONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.ContinentsTabPage.SEARCH_FILTER_INPUT_CONTINENTS_CONFIGURATION;

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
import org.sormas.e2etests.pages.application.configuration.ContinentsTabPage;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class ConfigurationContinentsSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private final BaseSteps baseSteps;

  @SneakyThrows
  @Inject
  public ConfigurationContinentsSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;

    When(
        "I navigate to continents tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_CONTINENTS_TAB));

    Then(
        "I check that number of continents is (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          softly.assertEquals(
              Integer.valueOf(
                  Integer.parseInt(
                      webDriverHelpers.getTextFromWebElement(
                          ContinentsTabPage.NUMBER_OF_CONTINENTS))),
              number,
              "Number of continents is not correct!");
          softly.assertAll();
        });

    When(
        "I check that Africa is correctly displayed",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData.toString().contains("EXTERNAL ID=31000005, NAME=Africa"),
              "Africa is not correctly displayed!");
          softly.assertAll();
        });

    When(
        "I check that Africa is correctly displayed in German",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData.toString().contains("EXTERNE ID=31000005, NAME=Afrika"),
              "Africa is not correctly displayed!");
          softly.assertAll();
        });
    When(
        "I check that continent list is correctly displayed",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          List<String> continents =
              List.of(
                  "EXTERNAL ID=31000005, NAME=Africa",
                  "EXTERNAL ID=31000006, NAME=America",
                  "EXTERNAL ID=31000004, NAME=Asia",
                  "EXTERNAL ID=31000007, NAME=Australia (Continent)",
                  "EXTERNAL ID=31000003, NAME=Europe",
                  "EXTERNAL ID=31099999, NAME=Foreign Countries (Unknown)");
          continents.forEach(
              (temp) -> {
                softly.assertTrue(
                    tableRowsData.toString().contains(temp),
                    String.format("%s is not correctly displayed!", temp));
              });
          softly.assertAll();
        });

    When(
        "I check that continent list is correctly displayed in German",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          List<String> continents =
              List.of(
                  "EXTERNE ID=31000005, NAME=Afrika",
                  "EXTERNE ID=31000006, NAME=Amerika",
                  "EXTERNE ID=31000004, NAME=Asien",
                  "EXTERNE ID=31000007, NAME=Australien (Kontinent)",
                  "EXTERNE ID=31000003, NAME=Europa",
                  "EXTERNE ID=31099999, NAME=Ausland (Land unbekannt)");
          continents.forEach(
              (temp) -> {
                softly.assertTrue(
                    tableRowsData.toString().contains(temp),
                    String.format("%s is not correctly displayed!", temp));
              });
          softly.assertAll();
        });

    Then(
        "^I Verify the Presence of the element ([^\"]*) in Continents Configuration page$",
        (String element) -> {
          switch (element) {
            case "Import Button":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  IMPORT_BUTTON_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(IMPORT_BUTTON_CONTINENTS_CONFIGURATION),
                  "The Import Button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Import Default Button":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  IMPORT_DEFAULT_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(IMPORT_DEFAULT_CONTINENTS_CONFIGURATION),
                  "The Import Default Button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Export Button":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  EXPORT_BUTTON_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(EXPORT_BUTTON_CONTINENTS_CONFIGURATION),
                  "The Export Button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "New Entry Button":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  NEW_ENTRY_BUTTON_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(NEW_ENTRY_BUTTON_CONTINENTS_CONFIGURATION),
                  "The New Entry Button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Enter Bulk Edit Mode button":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  ENTER_BULK_EDIT_MODE_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(ENTER_BULK_EDIT_MODE_CONTINENTS_CONFIGURATION),
                  "The Enter Bulk Edit Mode button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Search Input":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  SEARCH_FILTER_INPUT_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(SEARCH_FILTER_INPUT_CONTINENTS_CONFIGURATION),
                  "The Search Input is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Reset Filters":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  RESET_FILTERS_BUTTON_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(RESET_FILTERS_BUTTON_CONTINENTS_CONFIGURATION),
                  "The Reset Filter button is not present in Continents Configuration page");
              softly.assertAll();
              break;
            case "Continents dropdown":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  CONTINENTS_DROPDOWN_CONTINENTS_CONFIGURATION);
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(CONTINENTS_DROPDOWN_CONTINENTS_CONFIGURATION),
                  "The Continents dropdown is not present in Continents Configuration page");
              softly.assertAll();
              break;
            default:
              throw new IllegalArgumentException("No valid options were provided");
          }
        });
    When(
        "I verify the Search and Reset filter functionality in Continents Configuration page",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONTINENTS_NAME_CONTINENTS_CONFIGURATION);
          webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(
              CONTINENTS_NAME_CONTINENTS_CONFIGURATION);
          Integer defaultContinentCount =
              webDriverHelpers.getNumberOfElements(CONTINENTS_NAME_CONTINENTS_CONFIGURATION);
          String continent =
              webDriverHelpers.getTextFromWebElement(CONTINENTS_NAME_CONTINENTS_CONFIGURATION);
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_FILTER_INPUT_CONTINENTS_CONFIGURATION, continent);
          webDriverHelpers.waitUntilNumberOfElementsIsExactly(
              CONTINENTS_NAME_CONTINENTS_CONFIGURATION, 1);
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CONTINENTS_NAME_CONTINENTS_CONFIGURATION, continent);
          webDriverHelpers.clickOnWebElementBySelector(
              RESET_FILTERS_BUTTON_CONTINENTS_CONFIGURATION);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilNumberOfElementsIsExactly(
              CONTINENTS_NAME_CONTINENTS_CONFIGURATION, defaultContinentCount);
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
          List<WebElement> tableData = table.findElements(CONTINENTS_TABLE_DATA);
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTINENTS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(CONTINENTS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONTINENTS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CONTINENTS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(CONTINENTS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(CONTINENTS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
