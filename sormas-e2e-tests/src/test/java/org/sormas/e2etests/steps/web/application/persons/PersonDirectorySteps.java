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

package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;
import org.testng.Assert;

public class PersonDirectorySteps implements En {
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public PersonDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      AssertHelpers assertHelpers,
      DataOperations dataOperations,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    // TODO refactor all BDD methods naming to be more explicit regarding where data comes from

    /** Avoid using this method until Person's performance is fixed */
    Then(
        "I open the last created person",
        () -> {
          String createdPersonUUID = EditContactPersonSteps.fullyDetailedPerson.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);

          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, createdPersonUUID);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          By uuidLocator =
              By.cssSelector(String.format(PERSON_RESULTS_UUID_LOCATOR, createdPersonUUID));
          webDriverHelpers.isElementVisibleWithTimeout(uuidLocator, 150);
          webDriverHelpers.clickOnWebElementBySelector(uuidLocator);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
          webDriverHelpers.isElementVisibleWithTimeout(UUID_INPUT, 20);
        });

    Then(
        "I check the result for UID for second person in grid PERSON ID column",
        () -> {
          webDriverHelpers.waitUntilAListOfElementsHasText(
              CASE_GRID_RESULTS_ROWS, apiState.getLastCreatedPerson().getUuid());
          assertHelpers.assertWithPoll20Second(
              () ->
                  Truth.assertWithMessage(
                          apiState.getLastCreatedPerson().getUuid()
                              + " value is not displayed in grid Disease column")
                      .that(apiState.getLastCreatedPerson().getUuid())
                      .isEqualTo(
                          String.valueOf(
                              webDriverHelpers.getTextFromPresentWebElement(
                                  CASE_PERSON_ID_COLUMN_HEADERS))));
        });

    Then(
        "I check that number of displayed Persons results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    When(
        "I navigate to the last created Person page via URL",
        () -> {
          String createdPersonUUID = EditContactPersonSteps.fullyDetailedPerson.getUuid();
          String LAST_CREATED_PERSON_PAGE_URL =
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!persons/data/"
                  + createdPersonUUID;
          webDriverHelpers.accessWebSite(LAST_CREATED_PERSON_PAGE_URL);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 50);
        });

    When(
        "I search for specific person in person directory",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEARCH_PERSON_BY_FREE_TEXT, 30);
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_PERSON_BY_FREE_TEXT, personUuid);
        });

    When(
        "I click on specific person in person directory",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.clickOnWebElementBySelector(getByPersonUuid(personUuid));
        });

    When(
        "I filter for persons who are alive",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.selectFromCombobox(PRESENT_CONDITION_FILTER_COMBOBOX, "Alive");
        });

    When(
        "I click on first person in person directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(By.cssSelector("[role='gridcell'] a"));
        });

    When(
        "I apply on the APPLY FILTERS button",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              APPLY_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });

    When(
        "I click on the RESET FILTERS button",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON, 30);
          TimeUnit.SECONDS.sleep(10);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });
  }
}
