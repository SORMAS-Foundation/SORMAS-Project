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

package org.sormas.e2etests.steps.web.application.reports;

import cucumber.api.java8.En;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.reports.WeeklyReportsPage;

@Slf4j
public class WeeklyReportsSteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public WeeklyReportsSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I choose {string} as year for weekly reports",
        (String year) -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(WeeklyReportsPage.YEAR_FILTER, year);
        });

    When(
        "I choose {string} as epi week for weekly reports",
        (String epiWeek) -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(WeeklyReportsPage.EPI_WEEK_FILTER, epiWeek);
        });

    When(
        "I click on the last epi week button for weekly reports",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickOnWebElementBySelector(WeeklyReportsPage.LAST_EPI_WEEK_BUTTON);
        });

    Then(
        "I check that all filter components for weekly reports are shown",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(WeeklyReportsPage.YEAR_FILTER);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(WeeklyReportsPage.YEAR_FILTER, 1);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(WeeklyReportsPage.YEAR_FILTER);

          webDriverHelpers.waitUntilIdentifiedElementIsPresent(WeeklyReportsPage.EPI_WEEK_FILTER);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(WeeklyReportsPage.EPI_WEEK_FILTER, 1);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(WeeklyReportsPage.EPI_WEEK_FILTER);

          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              WeeklyReportsPage.LAST_EPI_WEEK_BUTTON);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(
              WeeklyReportsPage.LAST_EPI_WEEK_BUTTON, 1);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              WeeklyReportsPage.LAST_EPI_WEEK_BUTTON);
        });

    Then(
        "^I check that info icon for weekly reports is shown$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(WeeklyReportsPage.INFO_ICON);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(WeeklyReportsPage.INFO_ICON, 1);
        });

    Then(
        "^I check that grid for weekly reports is shown$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(WeeklyReportsPage.GRID);
          webDriverHelpers.waitUntilAListOfElementsIsPresent(WeeklyReportsPage.GRID, 1);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(WeeklyReportsPage.GRID);
        });

    Then(
        "^I check that header names of grid for weekly reports are shown$",
        () -> {
          List<String> expectedHeaderNames =
              Arrays.asList(
                  "Officers",
                  "Informants",
                  "Region",
                  "Number of officers",
                  "Number of officers reports",
                  "Percentage",
                  "Number of officer zero reports",
                  "Number of informants",
                  "Number of informant reports",
                  "Number of informant zero reports");

          checkThatHeaderNamesExistInGrid(expectedHeaderNames);
        });
  }

  private void checkThatHeaderNamesExistInGrid(List<String> expectedHeaderNames) {
    for (String expectedHeaderName : expectedHeaderNames) {
      webDriverHelpers.waitUntilElementIsVisibleAndClickable(
          By.xpath(String.format(WeeklyReportsPage.GRID_HEADER, expectedHeaderName)));
    }
  }
}
