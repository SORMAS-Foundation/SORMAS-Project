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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
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
      @Named("ENVIRONMENT_URL") String environmentUrl,
      ApiState apiState,
      AssertHelpers assertHelpers,
      DataOperations dataOperations) {
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
        "I choose random value for Year of birth filter",
        () -> {
          String yearOfBirth = apiState.getLastCreatedPerson().getBirthdateYYYY().toString();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth);
        });

    Then(
        "I choose random value for Month of birth filter",
        () -> {
          String monthOfBirth = apiState.getLastCreatedPerson().getBirthdateMM().toString();
          webDriverHelpers.selectFromCombobox(BIRTH_MONTH_COMBOBOX, monthOfBirth);
        });
    Then(
        "I choose random value for Day of birth filter",
        () -> {
          String dayOfBirth = apiState.getLastCreatedPerson().getBirthdateDD().toString();
          webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, dayOfBirth);
        });

    Then(
        "I choose present condition field from specific range",
        () -> {
          String presentCondition = apiState.getLastCreatedPerson().getPresentCondition();
          String capitalizeWord =
              presentCondition.substring(0, 1).toUpperCase()
                  + presentCondition.substring(1).toLowerCase();
          webDriverHelpers.selectFromCombobox(PRESENT_CONDITION, capitalizeWord);
        });
    Then(
        "I choose random value for Region",
        () -> {
          String regionName =
                  getConvertRegionUuidToName(apiState.getLastCreatedPerson().getAddress().getRegion());
          webDriverHelpers.selectFromCombobox(REGIONS_COMBOBOX, regionName);
        });

    Then(
        "I choose random value for District",
        () -> {
          String districtName =
                  getConvertDistrictUuidToName(apiState.getLastCreatedPerson().getAddress().getDistrict());
          webDriverHelpers.selectFromCombobox(DISTRICTS_COMBOBOX, districtName);
        });

    Then(
        "I choose random value for Community",
        () -> {
          String communityName =
                  getConvertCommunityUuidToName(apiState.getLastCreatedPerson().getAddress().getCommunity());
          webDriverHelpers.selectFromCombobox(COMMUNITY_PERSON_COMBOBOX, communityName);
        });

    Then(
        "I fill a different Community field",
        () -> {
          webDriverHelpers.selectFromCombobox(COMMUNITY_PERSON_COMBOBOX, "Community2");
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
        "I check that number of displayed Person results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    Then(
        "I check that number of displayed Persons results is {int}",
        (Integer number) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                        number.intValue(),
                        "Number of displayed cases is not correct")));

    Then(
        "I change Year of birth filter to {string}",
        (String yearOfBirth) ->
            webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth));

    Then(
        "I change Month of birth filter to {string}",
        (String monthOfBirth) ->
            webDriverHelpers.selectFromCombobox(BIRTH_MONTH_COMBOBOX, monthOfBirth));
    Then(
        "I change Day of birth filter to {string}",
        (String dayOfBirth) -> webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, dayOfBirth));

    Then(
        "I change present condition filter to {string}",
        (String presentCondition) ->
            webDriverHelpers.selectFromCombobox(PRESENT_CONDITION, presentCondition));

    Then(
        "I change REGION filter to {string}",
        (String region) -> webDriverHelpers.selectFromCombobox(REGIONS_COMBOBOX, region));

    Then(
        "I change DISTRICT filter to {string}",
        (String district) -> webDriverHelpers.selectFromCombobox(DISTRICTS_COMBOBOX, district));
    Then(
        "I change Community filter to {string}",
        (String community) ->
            webDriverHelpers.selectFromCombobox(COMMUNITY_PERSON_COMBOBOX, community));

    Then(
        "I search after last created person from API by {string}",
        (String searchCriteria) -> {
          String searchText = "";
          String personUUID =
              dataOperations.getPartialUuidFromAssociatedLink(
                  apiState.getLastCreatedPerson().getUuid());
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

          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
        });

    Then(
        "I change id,full name,email,phone number, by {string}",
        (String searchCriteria) -> {
          String searchText = "";
          String personUUID = "XCBJ2T-XYUGE3-QZK6PD-CWL6KBER";
          switch (searchCriteria) {
            case "uuid":
              searchText = personUUID;
              break;
            case "full name":
              searchText = "Tom Jerry";
              break;
            case "phone number":
              searchText = "(06713) 6606268";
              break;
            case "email":
              searchText = "Tom.Jerry@person.com";
              break;
          }
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, searchText);
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
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(10);
        });
  }
    public String getConvertRegionUuidToName(String regionUuid) {
        for (RegionsValues reg : RegionsValues.values()) {
            if (reg.getUuid() == regionUuid) {
                return reg.getName();
            }
        }
        return null;
    }

    public String getConvertDistrictUuidToName(String districtUuid) {
        for (DistrictsValues dis : DistrictsValues.values()) {
            if (dis.getUuid() == districtUuid) {
                return dis.getName();
            }
        }
        return null;
    }

    public String getConvertCommunityUuidToName(String communityId) {
        for (CommunityValues com : CommunityValues.values()) {
            if (com.getUuid() == communityId) {
                return com.getName();
            }
        }
        return null;
    }
}
