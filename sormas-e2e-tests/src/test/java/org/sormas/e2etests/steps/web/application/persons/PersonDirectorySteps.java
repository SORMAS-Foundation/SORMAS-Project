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

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.PresentCondition;
import org.sormas.e2etests.enums.RegionsValues;
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
      EnvironmentManager environmentManager,
      Faker faker) {
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
        "I choose random value for Year of birth filter in Persons for the last created person by API",
        () -> {
          String yearOfBirth = apiState.getLastCreatedPerson().getBirthdateYYYY().toString();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth);
        });

    Then(
        "I choose random value for Month of birth filter in Persons for the last created person by API",
        () -> {
          String monthOfBirth = apiState.getLastCreatedPerson().getBirthdateMM().toString();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_MONTH_COMBOBOX, monthOfBirth);
        });

    Then(
        "I choose random value for Day of birth filter in Persons for the last created person by API",
        () -> {
          String dayOfBirth = apiState.getLastCreatedPerson().getBirthdateDD().toString();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, dayOfBirth);
        });

    Then(
        "I fill Persons UUID for the last created person by API",
        () -> {
          String personUUID =
              dataOperations.getPartialUuidFromAssociatedLink(
                  apiState.getLastCreatedPerson().getUuid());
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, personUUID);
        });

    Then(
        "I choose present condition field from specific range for the last created person by API",
        () -> {
          String presentCondition = apiState.getLastCreatedPerson().getPresentCondition();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              PRESENT_CONDITION, PresentCondition.getValueFor(presentCondition));
        });

    Then(
        "I choose random value of Region in Persons for the last created person by API",
        () -> {
          String regionName = apiState.getLastCreatedPerson().getAddress().getRegion();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              REGIONS_COMBOBOX, RegionsValues.getValueFor(regionName));
        });

    Then(
        "I choose random value of District in Persons for the last created person by API",
        () -> {
          String districtName = apiState.getLastCreatedPerson().getAddress().getDistrict();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              DISTRICTS_COMBOBOX, DistrictsValues.getValueFor(districtName));
        });

    Then(
        "I choose random value of Community in Persons for the last created person by API",
        () -> {
          String communityName = apiState.getLastCreatedPerson().getAddress().getCommunity();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              COMMUNITY_PERSON_COMBOBOX, CommunityValues.getValueFor(communityName));
        });

    Then(
        "I check that number of displayed Person results is {int}",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoaded();
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                      number.intValue(),
                      "Number of displayed persons is not correct"));
        });

    Then(
        "I change Year of birth filter by random value for Person",
        () -> {
          Integer yearOfBirth = faker.number().numberBetween(1900, 2022);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth.toString());
        });

    Then(
        "I change Month of birth filter  by random value for Person",
        () -> {
          Integer monthOfBirth = faker.number().numberBetween(1, 12);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_MONTH_COMBOBOX, monthOfBirth.toString());
        });

    Then(
        "I change Day of birth filter by random value for Person",
        () -> {
          Integer dayOfBirth = faker.number().numberBetween(1, 29);
          webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, dayOfBirth.toString());
        });

    Then(
        "I change present condition filter to random for Person",
        () -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(
              PRESENT_CONDITION, PresentCondition.getRandomPresentCondition());
        });

    Then(
        "I change REGION filter to {string} for Person",
        (String region) -> {
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(REGIONS_COMBOBOX, region);
        });

    Then(
        "I change DISTRICT filter to {string} for Person",
        (String district) -> webDriverHelpers.selectFromCombobox(DISTRICTS_COMBOBOX, district));

    Then(
        "I change Community filter to {string} for Person",
        (String community) ->
            webDriverHelpers.selectFromCombobox(COMMUNITY_PERSON_COMBOBOX, community));

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
        "I click on the RESET FILTERS button for Person",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              RESET_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });

    Then(
        "I search after last created person from API by {string} in Person directory",
        (String searchCriteria) -> {
          String searchText = "";
          String personUUID = apiState.getLastCreatedPerson().getUuid();
          switch (searchCriteria) {
            case "uuid":
              searchText = personUUID;
              break;
            case "full name":
              searchText =
                  apiState.getLastCreatedPerson().getLastName()
                      + " "
                      + apiState.getLastCreatedPerson().getFirstName();
              break;
            case "phone number":
              searchText = apiState.getLastCreatedPerson().getPhone();
              break;
            case "email":
              searchText = apiState.getLastCreatedPerson().getEmailAddress();
              break;
          }
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(150);
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          By uuidLocator = By.cssSelector(String.format(PERSON_RESULTS_UUID_LOCATOR, personUUID));
          webDriverHelpers.isElementVisibleWithTimeout(uuidLocator, 150);
          webDriverHelpers.clickOnWebElementBySelector(uuidLocator);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
          webDriverHelpers.isElementVisibleWithTimeout(UUID_INPUT, 20);
        });

    Then(
        "I search after last created person from API by factor {string} in Person directory",
        (String searchCriteria) -> {
          String searchText = "";
          String personUUID = apiState.getLastCreatedPerson().getUuid();
          switch (searchCriteria) {
            case "uuid":
              searchText = dataOperations.getPartialUuidFromAssociatedLink((personUUID));
              break;
            case "full name":
              searchText =
                  apiState.getLastCreatedPerson().getLastName()
                      + " "
                      + apiState.getLastCreatedPerson().getFirstName();
              break;
            case "phone number":
              searchText = apiState.getLastCreatedPerson().getPhone();
              break;
            case "email":
              searchText = apiState.getLastCreatedPerson().getEmailAddress();
              break;
          }

          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
        });

    Then(
        "I change {string} information data field for Person",
        (String searchCriteria) -> {
          String searchText = "";
          String personUUID =
              dataOperations.getPartialUuidFromAssociatedLink(UUID.randomUUID().toString());
          switch (searchCriteria) {
            case "uuid":
              searchText = personUUID;
              break;
            case "full name":
              searchText = faker.name().fullName();
              break;
            case "phone number":
              searchText = faker.phoneNumber().phoneNumber();
              break;
            case "email":
              searchText = faker.name().fullName() + "@PERSON.com";
              break;
          }
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
        });
  }
}
