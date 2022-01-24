/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static org.sormas.e2etests.pages.application.persons.EditPersonPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.getByPersonUuid;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import javax.inject.Named;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Person;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;

public class PersonDirectorySteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  protected Person createdPerson;

  @Inject
  public PersonDirectorySteps(
      WebDriverHelpers webDriverHelpers, @Named("ENVIRONMENT_URL") String environmentUrl, ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    //TODO refactor all BDD methods naming to be more explicit regarding where data comes from

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

    When(
        "I navigate to the last created Person page via URL",
        () -> {
          String createdPersonUUID = EditContactPersonSteps.fullyDetailedPerson.getUuid();
          String LAST_CREATED_PERSON_PAGE_URL =
              environmentUrl + "/sormas-webdriver/#!persons/data/" + createdPersonUUID;
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

      Then(
              "I search after last created person from API by {string}",
              (String searchCriteria) -> {
                  String searchText ="";
                  String personUUID = apiState.getLastCreatedPerson().getUuid();
                  switch (searchCriteria){
                      case "uuid" : searchText = personUUID;
                          break;
                      case "full name" : searchText = apiState.getLastCreatedPerson().getLastName() + " " + apiState.getLastCreatedPerson().getFirstName();
                          break;
                          //etc
                  }
                  webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
                  webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON);
                  webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);

                  webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
                  webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
                  By uuidLocator =
                          By.cssSelector(String.format(PERSON_RESULTS_UUID_LOCATOR, personUUID));
                  webDriverHelpers.isElementVisibleWithTimeout(uuidLocator, 150);
                  webDriverHelpers.clickOnWebElementBySelector(uuidLocator);
                  webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
                  webDriverHelpers.isElementVisibleWithTimeout(UUID_INPUT, 20);
              });

      //TODO Michal, due to specific logic to filter only for this situation, create a method with a suggestive name, where you apply filters based on generated POJO data.
      //We can't create multiple methods to reuse them in this case, but please keep in mind to create generic locators
      //The above method will need to be finished by you (the switch to cover all search criteria)
  }
}
