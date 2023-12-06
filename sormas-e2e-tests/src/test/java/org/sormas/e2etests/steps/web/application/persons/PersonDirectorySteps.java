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

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.CASE_OF_DEATH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePersonPage.DATE_OF_DEATH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ALL_BUTTON_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ALL_BUTTON_CONTACT_DE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.entries.CreateNewTravelEntrySteps.TravelEntryUuid;
import static org.sormas.e2etests.steps.web.application.entries.CreateNewTravelEntrySteps.aCase;
import static org.sormas.e2etests.steps.web.application.persons.EditPersonSteps.personUuid;

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.web.Person;
import org.sormas.e2etests.entities.services.PersonService;
import org.sormas.e2etests.enums.PresentCondition;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.pages.application.contacts.EditContactPage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.sormas.e2etests.steps.web.application.contacts.EditContactPersonSteps;
import org.sormas.e2etests.steps.web.application.contacts.EditContactSteps;
import org.sormas.e2etests.steps.web.application.entries.CreateNewTravelEntrySteps;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;
import org.sormas.e2etests.steps.web.application.immunizations.CreateNewImmunizationSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class PersonDirectorySteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  protected Person createdPerson;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Person personSharedForAllEntities;
  private static String copiedPersonUUID;

  @Inject
  public PersonDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      DataOperations dataOperations,
      Faker faker,
      AssertHelpers assertHelpers,
      RunningConfiguration runningConfiguration,
      SoftAssert softly,
      PersonService personService,
      RestAssuredClient restAssuredClient) {
    this.webDriverHelpers = webDriverHelpers;
    personSharedForAllEntities = personService.buildGeneratedPerson();
    EnvironmentManager manager = new EnvironmentManager(restAssuredClient);

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
        "I open the last created person linked with Case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          aCase = EditCaseSteps.aCase;
          String PersonFullName = aCase.getFirstName() + " " + aCase.getLastName();
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, PersonFullName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_RESULTS_UUID_LOCATOR_FROM_GRID);
          webDriverHelpers.doubleClickOnWebElementBySelector(PERSON_RESULTS_UUID_LOCATOR_FROM_GRID);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });

    Then(
        "I filter the last created person linked with Case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          aCase = EditCaseSteps.aCase;
          String PersonFullName = aCase.getFirstName() + " " + aCase.getLastName();
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, PersonFullName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    Then(
        "I filter by shared person data across all entities",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          String PersonFullName =
              personSharedForAllEntities.getFirstName()
                  + " "
                  + personSharedForAllEntities.getLastName();
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, PersonFullName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    Then(
        "I filter the last created person linked with Event Participant",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT, EditEventSteps.person.getUuid());
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    Then(
        "I filter the last created person linked with Contact",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          String PersonFullName =
              EditContactSteps.collectedContact.getFirstName()
                  + " "
                  + EditContactSteps.collectedContact.getLastName();
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, PersonFullName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    Then(
        "I filter the last created person linked with Travel Entry",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MULTIPLE_OPTIONS_SEARCH_INPUT);
          String PersonFullName =
              CreateNewTravelEntrySteps.aTravelEntry.getFirstName()
                  + " "
                  + CreateNewTravelEntrySteps.aTravelEntry.getLastName();
          TimeUnit.SECONDS.sleep(5); // waiting for event table grid reloaded
          webDriverHelpers.fillAndSubmitInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, PersonFullName);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
        });
    When(
        "^I open the last created Person via API",
        () -> {
          String LAST_CREATED_PERSON_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!persons/data/"
                  + apiState.getLastCreatedPerson().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_PERSON_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "^I check that EDIT TRAVEL ENTRY button appears on Edit Person page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getByTravelEntryPersonUuid(TravelEntryUuid.getUuid()));
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
    Then(
        "I click on Travel Entry aggregation button in Person Directory for DE specific",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_AGGREGATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
        });

    Then(
        "I click on Case aggregation button in Person Directory for DE specific",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_AGGREGATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
        });
    Then(
        "I click on Contact aggregation button in Person Directory for DE specific",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_AGGREGATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
        });
    Then(
        "I click on Events aggregation button in Person Directory for DE specific",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_AGGREGATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
        });
    Then(
        "I click on All aggregation button in Person Directory for DE specific",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_AGGREGATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
        });
    Then(
        "I fill Year of birth filter in Persons with the year of the last created person via API",
        () -> {
          String yearOfBirth = apiState.getLastCreatedPerson().getBirthdateYYYY().toString();
          webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth);
        });

    Then(
        "I fill Month of birth filter in Persons with the month of the last created person via API",
        () -> {
          String month =
              new DateFormatSymbols()
                  .getMonths()[apiState.getLastCreatedPerson().getBirthdateMM() - 1];
          webDriverHelpers.selectFromCombobox(
              BIRTH_MONTH_COMBOBOX,
              month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase());
        });

    Then(
        "I fill Day of birth filter in Persons with the day of birth of the last created person via API",
        () -> {
          String dayOfBirth = apiState.getLastCreatedPerson().getBirthdateDD().toString();
          webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, dayOfBirth);
        });

    Then(
        "I fill UUID of the last created person via API",
        () -> {
          String personUUID =
              dataOperations.getPartialUuidFromAssociatedLink(
                  apiState.getLastCreatedPerson().getUuid());
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, personUUID);
        });
    Then(
        "I fill UUID of the collected person from last created Travel Entry",
        () -> {
          String personUUID =
              dataOperations.getPartialUuidFromAssociatedLink(
                  CreateNewTravelEntrySteps.collectTravelEntryPersonUuid);
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, personUUID);
        });

    Then(
        "I select present condition field with condition of the last created person via API",
        () -> {
          String personCondition = apiState.getLastCreatedPerson().getPresentCondition();
          webDriverHelpers.selectFromCombobox(
              PRESENT_CONDITION, PresentCondition.getValueFor(personCondition));
        });

    Then(
        "I choose random value of Region in Persons for the last created person by API",
        () -> {
          String regionName = apiState.getLastCreatedPerson().getAddress().getRegion();
          webDriverHelpers.selectFromCombobox(REGIONS_COMBOBOX, manager.getRegionName(regionName));
        });

    Then(
        "I choose random value of District in Persons for the last created person by API",
        () -> {
          String districtName = apiState.getLastCreatedPerson().getAddress().getDistrict();
          webDriverHelpers.selectFromCombobox(
              DISTRICTS_COMBOBOX, manager.getDistrictName(districtName));
        });

    Then(
        "I choose random value of Community in Persons for the last created person by API",
        () -> {
          String communityName = apiState.getLastCreatedPerson().getAddress().getCommunity();
          webDriverHelpers.selectFromCombobox(
              COMMUNITY_PERSON_COMBOBOX, manager.getCommunityName(communityName));
        });
    When(
        "I filter by Person full name from Immunization on Person Directory Page",
        () -> {
          TimeUnit.SECONDS.sleep(3); // waiting for grid refresh
          webDriverHelpers.fillAndSubmitInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT,
              CreateNewImmunizationSteps.immunization.getFirstName()
                  + " "
                  + CreateNewImmunizationSteps.immunization.getLastName());
        });
    When(
        "I click Immunization aggregation button on Person Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_AGGREGATION_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });

    Then(
        "I check that number of displayed Person results is {int}",
        (Integer number) -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getNumberOfElements(CASE_GRID_RESULTS_ROWS),
                      number.intValue(),
                      "Number of displayed persons is not correct"));
        });

    Then(
        "I fill Year of birth filter in Persons with wrong value for last created Person via API",
        () -> {
          Integer yearOfBirth = apiState.getLastCreatedPerson().getBirthdateYYYY() + 1;
          webDriverHelpers.selectFromCombobox(BIRTH_YEAR_COMBOBOX, yearOfBirth.toString());
        });

    Then(
        "I fill Month of birth filter in Persons with wrong value for last created Person via API",
        () -> {
          Integer monthOfBirth = apiState.getLastCreatedPerson().getBirthdateMM();
          Integer differentMonthOfBirth =
              (monthOfBirth.intValue() == 12) ? monthOfBirth - 1 : monthOfBirth + 1;
          String month = new DateFormatSymbols().getMonths()[differentMonthOfBirth - 1];
          webDriverHelpers.selectFromCombobox(BIRTH_MONTH_COMBOBOX, month);
        });

    Then(
        "I fill Day of birth filter in Persons with wrong value for last created Person via API",
        () -> {
          Integer dayOfBirth = apiState.getLastCreatedPerson().getBirthdateDD();
          Integer differentDayOfBirth =
              (dayOfBirth.intValue() >= 30) ? dayOfBirth - 1 : dayOfBirth + 1;
          webDriverHelpers.selectFromCombobox(BIRTH_DAY_COMBOBOX, differentDayOfBirth.toString());
        });

    Then(
        "I change present condition filter to other than condition of last created via API Person",
        () -> {
          String conditionOfLastCreatedApiPerson =
              apiState.getLastCreatedPerson().getPresentCondition();
          webDriverHelpers.selectFromCombobox(
              PRESENT_CONDITION,
              PresentCondition.getRandomConditionDifferentThan(conditionOfLastCreatedApiPerson));
        });

    Then(
        "I change REGION filter to {string} for Person",
        (String region) -> {
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
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!persons/data/"
                  + createdPersonUUID;
          webDriverHelpers.accessWebSite(LAST_CREATED_PERSON_PAGE_URL);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 50);
        });
    When(
        "I navigate to the last created via api Person page via URL",
        () -> {
          String personLinkPath = "/sormas-ui/#!persons/data/";
          String uuid = apiState.getLastCreatedPerson().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale) + personLinkPath + uuid);
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
        "I open the last new created person by UI in person directory",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEARCH_PERSON_BY_FREE_TEXT, 30);
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_PERSON_BY_FREE_TEXT, personUuid);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_RESULT_IN_PERSON_DIRECTORY_TABLE);
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
        "I check if Date of dead for specified case is correct",
        () -> {
          String date = webDriverHelpers.getValueFromWebElement(DATE_OF_DEATH_INPUT);
          LocalDate deadDate = LocalDate.now().minusDays(1);
          softly.assertEquals(DATE_FORMATTER.format(deadDate), date, "Death date is not equal");
          softly.assertAll();
        });

    When(
        "I check if Cause of death is ([^\"]*)",
        (String causeOfDeath) -> {
          String deathCause = webDriverHelpers.getValueFromCombobox(CASE_OF_DEATH_COMBOBOX);
          softly.assertEquals(deathCause, causeOfDeath, "Cause of death is not equal");
          softly.assertAll();
        });

    When(
        "I click on first person in person directory",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(PERSON_FIRST_RECORD_IN_TABLE);
          webDriverHelpers.clickOnWebElementBySelector(PERSON_FIRST_RECORD_IN_TABLE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the APPLY FILTERS button",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              APPLY_FILTERS_BUTTON, 30);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
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
          String personUUID = dataOperations.getPartialUuidFromAssociatedLink(generateShortUUID());
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

    When(
        "I copy uuid of current person",
        () -> {
          webDriverHelpers.scrollToElement(EditContactPage.UUID_INPUT);
          copiedPersonUUID = webDriverHelpers.getValueFromWebElement(EditContactPage.UUID_INPUT);
        });

    When(
        "I search by copied uuid of the person in Person Directory",
        () -> {
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, copiedPersonUUID);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON_CONTACT);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
        });

    And(
        "^I search by copied uuid of the person in Person Directory for DE$",
        () -> {
          webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, copiedPersonUUID);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON_CONTACT_DE);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(120);
        });
  }
}
