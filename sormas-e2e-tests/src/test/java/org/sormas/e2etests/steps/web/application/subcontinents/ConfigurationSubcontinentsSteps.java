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

package org.sormas.e2etests.steps.web.application.subcontinents;

import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_SUBCONTINENTS_TAB;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.CONTINENT_NAME_COMBO_BOX_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.ENTER_BULK_EDIT_MODE_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.EXPORT_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.IMPORT_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.IMPORT_DEFAULT_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.NEW_ENTRY_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.RELEVANCE_STATUS_COMBO_BOX_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.RESET_FILTERS_BUTTON_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.SEARCH_INPUT_SUBCONTINENTS_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.SUBCONTINENTS_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.SUBCONTINENTS_TABLE_DATA;
import static org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage.SUBCONTINENTS_TABLE_ROW;

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
import org.sormas.e2etests.pages.application.configuration.SubcontinentsTabPage;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class ConfigurationSubcontinentsSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private final BaseSteps baseSteps;

  @SneakyThrows
  @Inject
  public ConfigurationSubcontinentsSteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, BaseSteps baseSteps, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;

    When(
        "I navigate to subcontinents tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_SUBCONTINENTS_TAB));

    Then(
        "I check that number of subcontinents is at least (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          softly.assertTrue(
              Integer.valueOf(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromWebElement(
                              SubcontinentsTabPage.NUMBER_OF_SUBCONTINENTS)))
                  >= number,
              "Number of subcontinents is not correct!");
          softly.assertAll();
        });

    When(
        "I check that Central Africa is correctly displayed",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData
                  .toString()
                  .contains("CONTINENT NAME=Africa, EXTERNAL ID=31001115, NAME=Central Africa"),
              "Central Africa is not correctly displayed!");
          softly.assertAll();
        });

    When(
        "I check that Central Africa is correctly displayed in German",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          softly.assertTrue(
              tableRowsData
                  .toString()
                  .contains("KONTINENTNAME=Afrika, EXTERNE ID=31001115, NAME=Zentralafrika"),
              "Central Africa is not correctly displayed!");
          softly.assertAll();
        });

    Then(
        "I Verify the page elements are present in Subcontinents Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              IMPORT_BUTTON_SUBCONTINENTS_CONFIGURATION);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(IMPORT_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "Import Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(IMPORT_DEFAULT_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "Import Default Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(EXPORT_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "Export Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(NEW_ENTRY_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "New Entry Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  ENTER_BULK_EDIT_MODE_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "Enter Bulk Edit Mode Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(SEARCH_INPUT_SUBCONTINENTS_CONFIGURATION),
              "Search Input is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  CONTINENT_NAME_COMBO_BOX_SUBCONTINENTS_CONFIGURATION),
              "Continent Name Combo box is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(RESET_FILTERS_BUTTON_SUBCONTINENTS_CONFIGURATION),
              "Reset Filters Button is Not present in Subcontinents Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  RELEVANCE_STATUS_COMBO_BOX_SUBCONTINENTS_CONFIGURATION),
              "Relevance status Combo box is Not present in Subcontinents Configuration");

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
          List<WebElement> tableData = table.findElements(SUBCONTINENTS_TABLE_DATA);
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
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUBCONTINENTS_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(SUBCONTINENTS_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SUBCONTINENTS_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(SUBCONTINENTS_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(SUBCONTINENTS_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(SUBCONTINENTS_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }
}
