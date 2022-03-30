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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ALL_RESULTS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.BULK_ACTIONS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_CONNECTION_NUMBER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MEANS_OF_TRANSPORT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_MEANS_OF_TRANSPORT_DETAILS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_SEAT_NUMBER;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FACILITY_ACTIVITY_AS_CASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DESCRIPTION;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ADDITIONAL_INFORMATION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HOUSE_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.INDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.NEW_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_ACTIVITY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OUTDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.POSTAL_CODE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RISK_AREA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.STREET_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ACTIVE_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ALL_BUTTON_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.BULK_ACTIONS_CONTACT_VALUES;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_CASE_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_CLASSIFICATION_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DATA_TAB;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DIRECTORY_DETAILED_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DISEASE_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_DISEASE_VARIANT_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_FOLLOW_UP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONVERTED_TO_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.DROPPED_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.FIRST_CONTACT_ID_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.GRID_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.GRID_RESULTS_COUNTER_CONTACT_DIRECTORY;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.LINE_LISTING;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.MULTIPLE_OPTIONS_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.NEW_ENTRY_EPIDEMIOLOGICAL_DATA;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.PERSON_LIKE_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.RESULTS_GRID_HEADER;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.AREA_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_EMAIL_ADRESS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_FIRST_NAME;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_LAST_NAME;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.CONTACT_PERSON_PHONE_NUMBER;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.EXPOSURE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_DETAILS_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_NAME_AND_DESCRIPTION;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_ACCURACY_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_LATITUDE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.GPS_LONGITUDE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_ACTIVITY_DETAILS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_DETAILS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_PLACE_DETAILS;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.contacts.ContactsLineListingSteps.DATE_FORMATTER_DE;
import static org.sormas.e2etests.steps.web.application.contacts.EditContactSteps.collectedContact;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Contact;
import org.sormas.e2etests.entities.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfGathering;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class ContactDirectorySteps implements En {
  Faker faker = new Faker();

  protected WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Exposure exposureData;
  public static EpidemiologicalData dataSavedFromCheckbox;
  public static EpidemiologicalData specificCaseData;
  public static Contact contact;

  @Inject
  public ContactDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      AssertHelpers assertHelpers,
      ContactService contactService,
      DataOperations dataOperations,
      Faker faker,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I navigate to the last created contact via the url$",
        () -> {
          String LAST_CREATED_CONTACT_URL =
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!contacts/data/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });
    When(
        "I apply Id of last created Contact on Contact Directory Page",
        () -> {
          String contactUuid = collectedContact.getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
        });

    When(
        "I click on the NEW CONTACT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT));

    When(
        "I click on save Contact button",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I open the last created contact",
        () -> {
          searchAfterContactByMultipleOptions(apiState.getCreatedContact().getUuid());
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    When(
        "I click on the DETAILED radiobutton from Contact directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DIRECTORY_DETAILED_RADIOBUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(RESULTS_GRID_HEADER, "Sex")), 20);
          webDriverHelpers.waitUntilANumberOfElementsAreVisibleAndClickable(GRID_HEADERS, 18);
        });

    When(
        "I filter by Contact uuid",
        () -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, contactUuid));
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(uuidLocator);
        });

    When(
        "I apply Id of last api created Contact on Contact Directory Page",
        () -> {
          String contactUuid = apiState.getCreatedContact().getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, contactUuid);
        });

    When(
        "I click on the More button on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MORE_BUTTON);
        });

    And(
        "I click on Link to Event from Bulk Actions combobox on Contact Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS_CONTACT_VALUES));

    When(
        "I click Enter Bulk Edit Mode on Contact directory page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ENTER_BULK_EDIT_MODE);
        });

    When(
        "I click checkbox to choose all Contact results on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
        });

    When(
        "I click on New Entry in Exposure Details Known",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_EPIDEMIOLOGICAL_DATA);
        });

    When(
        "I select all options in Type of activity from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          String[] ListOfTypeOfActivityExposure =
              TypeOfActivityExposure.ListOfTypeOfActivityExposure;
          for (String value : ListOfTypeOfActivityExposure) {
            webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, value);
          }
        });

    When(
        "I select all Type of gathering from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          for (TypeOfGathering value : TypeOfGathering.values()) {
            if (value != TypeOfGathering.valueOf("OTHER")) {
              webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, value.toString());
            }
          }
        });

    When(
        "I check all Type of place from Combobox in Exposure for Epidemiological data tab in Contacts",
        () -> {
          for (TypeOfPlace value : TypeOfPlace.values()) {
            webDriverHelpers.selectFromCombobox(
                TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(value.toString()));
          }
        });

    When(
        "I click on edit Exposure vision button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
        });

    When(
        "I select ([^\"]*) option in Type of activity from Combobox in Exposure form",
        (String typeOfactivity) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, typeOfactivity);
        });

    When(
        "I select ([^\"]*) option in Type of activity from Combobox in Activity as Case form",
        (String typeOfactivity) -> {
          webDriverHelpers.selectFromCombobox(ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX, typeOfactivity);
        });

    When(
        "I fill Location form for Type of place by chosen {string} options in Exposure for Epidemiological data",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputs();
          switch (searchCriteria) {
            case "HOME":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              break;
            case "OTHER":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              webDriverHelpers.fillInWebElement(
                  TYPE_OF_PLACE_DETAILS, exposureData.getTypeOfPlaceDetails());
              break;
            case "FACILITY":
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_PLACE_COMBOBOX, TypeOfPlace.getValueFor(searchCriteria));
              fillLocation(exposureData);
              webDriverHelpers.selectFromCombobox(
                  FACILITY_CATEGORY_COMBOBOX, exposureData.getFacilityCategory());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_TYPE_COMBOBOX, exposureData.getFacilityType());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacility());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              String emailAddress = exposureData.getContactPersonEmail();
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
          }
        });

    When(
        "I fill Location form for Type of place field by {string} options in Case directory for DE version",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
          switch (searchCriteria) {
            case "Unbekannt":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              break;
            case "Sonstiges":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.fillInWebElement(TYPE_OF_PLACE_DETAILS, faker.book().title());
              fillLocationDE(exposureData);
              break;
            case "Transportmittel":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.selectFromCombobox(CASE_MEANS_OF_TRANSPORT, "Sonstiges");
              webDriverHelpers.fillInWebElement(
                  CASE_MEANS_OF_TRANSPORT_DETAILS, faker.book().publisher());
              webDriverHelpers.fillInWebElement(
                  CASE_CONNECTION_NUMBER, String.valueOf(faker.number().numberBetween(1, 200)));
              webDriverHelpers.fillInWebElement(
                  CASE_SEAT_NUMBER, String.valueOf(faker.number().numberBetween(1, 200)));
              fillLocationDE(exposureData);
              break;
            case "Einrichtung":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(
                  FACILITY_CATEGORY_COMBOBOX, exposureData.getFacilityCategory());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_TYPE_COMBOBOX, exposureData.getFacilityType());
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              String emailAddress = exposureData.getContactPersonEmail();
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
          }
        });
    When(
        "I fill Location form for Type of place field by {string} options in Case as Activity directory for DE version",
        (String searchCriteria) -> {
          exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
          String emailAddress = exposureData.getContactPersonEmail();
          switch (searchCriteria) {
            case "Einrichtung":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Krankenhaus");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Gemeinschaftseinrichtung":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              fillLocationDE(exposureData);
              webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, "Schule");
              webDriverHelpers.selectFromCombobox(
                  FACILITY_DETAILS_COMBOBOX, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  FACILITY_NAME_AND_DESCRIPTION, exposureData.getFacilityDetails());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_FIRST_NAME, exposureData.getContactPersonFirstName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_LAST_NAME, exposureData.getContactPersonLastName());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_PHONE_NUMBER, exposureData.getContactPersonPhone());
              webDriverHelpers.fillInWebElement(
                  CONTACT_PERSON_EMAIL_ADRESS,
                  Normalizer.normalize(emailAddress, Normalizer.Form.NFD)
                      .replaceAll("[^\\p{ASCII}]", ""));
              break;
            case "Sonstiges":
              webDriverHelpers.selectFromCombobox(
                  FACILITY_ACTIVITY_AS_CASE_COMBOBOX, searchCriteria);
              webDriverHelpers.fillInWebElement(TYPE_OF_PLACE_DETAILS, faker.book().title());
              exposureData = contactService.buildGeneratedExposureDataContactForRandomInputsDE();
              fillLocationDE(exposureData);
              break;
          }
        });

    When(
        "I click on save button in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });
    When(
        "I select a Type of activity ([^\"]*) option in Exposure for Epidemiological data tab in Contacts",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, option);
        });

    When(
        "I select a type of gathering ([^\"]*) from Combobox in Exposure for Epidemiological data tab in Contacts",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, option);
        });

    When(
        "I fill a type of gathering details in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_GATHERING_DETAILS, faker.chuckNorris().fact());
        });

    When(
        "I fill a Type of activity details field in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_ACTIVITY_DETAILS, faker.book().title());
        });

    When(
        "I click on the Epidemiological Data navbar field",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I click on Bulk Actions combobox on Contact Directory Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(BULK_ACTIONS));
    And(
        "I filter by mocked ContactID on Contact directory page",
        () -> {
          String partialUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
          webDriverHelpers.fillAndSubmitInWebElement(
              CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, partialUuid);
        });
    And(
        "I click SHOW MORE FILTERS button on Contact directory page",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS));
    Then(
        "I apply Disease of source filter {string} on Contact Directory Page",
        (String diseaseFilterOption) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_DISEASE_FILTER_COMBOBOX, DiseasesValues.getCaptionFor(diseaseFilterOption));
        });
    Then(
        "I apply Contact classification filter to {string} on Contact Directory Page",
        (String contactClassification) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_CLASSIFICATION_FILTER_COMBOBOX, contactClassification);
        });
    Then(
        "I apply Disease variant filter to {string} on Contact Directory Page",
        (String diseaseVariant) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_DISEASE_VARIANT_FILTER_COMBOBOX, diseaseVariant);
        });
    And(
        "I apply Classification of source case filter to {string} on Contact Directory Page",
        (String classification) -> {
          webDriverHelpers.selectFromCombobox(
              CONTACT_CASE_CLASSIFICATION_FILTER_COMBOBOX, classification);
        });
    And(
        "I apply Follow-up status filter to {string} on Contact Directory Page",
        (String followUp) -> {
          webDriverHelpers.selectFromCombobox(CONTACT_FOLLOW_UP_FILTER_COMBOBOX, followUp);
        });
    And(
        "I click APPLY BUTTON in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(3); // needed for table refresh
        });

    And(
        "I click {string} checkbox on Contact directory page",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Quarantine ordered verbally?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX);
              break;
            case ("Quarantine ordered by official document?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX);
              break;
            case ("No quarantine ordered"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Help needed in quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Only high priority contacts"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX);
              break;
            case ("Only contacts with extended quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Only contacts with reduced quarantine"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Only contacts from other instances"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
          }
        });

    And(
        "I click {string} checkbox on Contact directory page for DE version",
        (String checkboxDescription) -> {
          switch (checkboxDescription) {
            case ("Quarant\u00E4ne m\u00FCndlich verordnet?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX);
              break;
            case ("Quarant\u00E4ne schriftlich verordnet?"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX);
              break;
            case ("Keine Quarant\u00E4ne verordnet"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Ma\u00DFnahmen zur Gew\u00E4hrleistung der Versorgung"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX);
              break;
            case ("Nur Kontakte mit hoher Priorit\u00E4t"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX);
              break;
            case ("Nur Kontakte mit verl\u00E4ngerter Quarant\u00E4ne"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX);
              break;
            case ("Nur Kontakte mit verk\u00FCrzter Quarant\u00E4ne"):
              webDriverHelpers.clickOnWebElementBySelector(
                  CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX);
              break;
            case ("Nur Kontakte von anderen Instanzen"):
              webDriverHelpers.clickOnWebElementBySelector(CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX);
              break;
          }
        });
    When(
        "^I click on Line Listing button$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(LINE_LISTING);
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING);
        });

    And(
        "I click on All button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_BUTTON_CONTACT);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Converted to case pending button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONVERTED_TO_CASE_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Active contact button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTIVE_CONTACT_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });
    And(
        "I click on Dropped button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DROPPED_BUTTON);
          TimeUnit.SECONDS.sleep(5); // needed for table refresh
        });

    When(
        "I fill all the data in Exposure for Epidemiological data tab in Contacts",
        () -> {
          webDriverHelpers.clickWebElementByText(EXPOSURE_DETAILS_KNOWN_OPTIONS, "YES");
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          exposureData = contactService.buildGeneratedExposureDataForContact();
          fillExposure(exposureData);
        });
    When(
        "I click on the Epidemiological Data button tab in Contact form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on the Contact tab in Contacts",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I am checking all Exposure data is saved and displayed in Contacts",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_OF_EXPOSURE_INPUT);
          Exposure actualExposureData = collectExposureData();
          ComparisonHelper.compareEqualEntities(exposureData, actualExposureData);
        });

    When(
        "I click on Residing or working in an area with high risk of transmission of the disease in Contact with ([^\"]*) option",
        (String option) -> {
          dataSavedFromCheckbox =
              EpidemiologicalData.builder()
                  .residingAreaWithRisk(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission in Contact with ([^\"]*) option",
        (String option) -> {
          dataSavedFromCheckbox =
              dataSavedFromCheckbox.toBuilder()
                  .largeOutbreaksArea(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(
              RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I am checking if options in checkbox for Contact are displayed correctly",
        () -> {
          specificCaseData = collectSpecificData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              specificCaseData,
              dataSavedFromCheckbox,
              List.of("residingAreaWithRisk", "largeOutbreaksArea"));
        });

    When(
        "I search after last created contact via API by UUID and open",
        () -> {
          searchAfterContactByMultipleOptions(apiState.getCreatedContact().getUuid());
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    When(
        "I am checking all Location data in Exposure are saved and displayed",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          Exposure actualLocationData = collectLocationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              actualLocationData,
              exposureData,
              List.of(
                  "continent",
                  "subcontinent",
                  "country",
                  "exposureRegion",
                  "district",
                  "community",
                  "street",
                  "houseNumber",
                  "additionalInformation",
                  "postalCode",
                  "city",
                  "areaType"));
        });

    When(
        "I search after last created contact via API by name and uuid then open",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
          webDriverHelpers.fillInWebElement(
              MULTIPLE_OPTIONS_SEARCH_INPUT, apiState.getCreatedContact().getUuid());
          webDriverHelpers.fillInWebElement(
              PERSON_LIKE_SEARCH_INPUT,
              apiState.getCreatedContact().getPerson().getFirstName()
                  + " "
                  + apiState.getCreatedContact().getPerson().getLastName());
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    When(
        "I open the first contact from contacts list",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_CONTACT_ID_BUTTON));

    Then(
        "I check that number of displayed contact results is (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              GRID_RESULTS_COUNTER_CONTACT_DIRECTORY, 50);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(
                              GRID_RESULTS_COUNTER_CONTACT_DIRECTORY)),
                      number.intValue(),
                      "Number of displayed contacts is not correct"));
        });
  }

  private void searchAfterContactByMultipleOptions(String idPhoneNameEmail) {
    webDriverHelpers.waitUntilElementIsVisibleAndClickable(APPLY_FILTERS_BUTTON);
    webDriverHelpers.fillInWebElement(MULTIPLE_OPTIONS_SEARCH_INPUT, idPhoneNameEmail);
    webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTERS_BUTTON);
  }

  private void openContactFromResultsByUUID(String uuid) {
    By uuidLocator = By.cssSelector(String.format(CONTACT_RESULTS_UUID_LOCATOR, uuid));
    webDriverHelpers.clickOnWebElementBySelector((uuidLocator));
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT, 40);
  }

  private void fillLocation(Exposure exposureData) {
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.selectFromCombobox(EXPOSURE_REGION_COMBOBOX, exposureData.getExposureRegion());
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, exposureData.getDistrict());
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, exposureData.getCommunity());
    webDriverHelpers.fillInWebElement(STREET_INPUT, exposureData.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, exposureData.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_INPUT, exposureData.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, exposureData.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY_INPUT, exposureData.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureData.getAreaType());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureData.getLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE_INPUT, exposureData.getLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY_INPUT, exposureData.getLatLonAccuracy());
  }

  private void fillLocationDE(Exposure exposureData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.selectFromCombobox(EXPOSURE_REGION_COMBOBOX, exposureData.getExposureRegion());
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, exposureData.getDistrict());
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, exposureData.getCommunity());
    webDriverHelpers.fillInWebElement(STREET_INPUT, exposureData.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, exposureData.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_INPUT, exposureData.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, exposureData.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY_INPUT, exposureData.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureData.getAreaType());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureData.getLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE_INPUT, exposureData.getLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY_INPUT, exposureData.getLatLonAccuracy());
  }

  private Exposure collectLocationData() {
    return Exposure.builder()
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .exposureRegion(webDriverHelpers.getValueFromCombobox(EXPOSURE_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromCombobox(AREA_TYPE_COMBOBOX))
        .latitude(webDriverHelpers.getValueFromWebElement(GPS_LATITUDE_INPUT))
        .longitude(webDriverHelpers.getValueFromWebElement(GPS_LONGITUDE_INPUT))
        .latLonAccuracy(webDriverHelpers.getValueFromWebElement(GPS_ACCURACY_INPUT))
        .build();
  }

  private void fillExposure(Exposure exposureData) {
    webDriverHelpers.fillInWebElement(
        START_OF_EXPOSURE_INPUT, formatter.format(exposureData.getStartOfExposure()));
    webDriverHelpers.fillInWebElement(
        END_OF_EXPOSURE_INPUT, formatter.format(exposureData.getEndOfExposure()));
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION_INPUT, exposureData.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY_COMBOBOX, exposureData.getTypeOfActivity().getActivity());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE_COMBOBOX, exposureData.getExposureDetailsRole().getRole());
    webDriverHelpers.clickWebElementByText(
        RISK_AREA_OPTIONS, exposureData.getRiskArea().toString());
    webDriverHelpers.clickWebElementByText(INDOORS_OPTIONS, exposureData.getIndoors().toString());
    webDriverHelpers.clickWebElementByText(OUTDOORS_OPTIONS, exposureData.getOutdoors().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_MASK_OPTIONS, exposureData.getWearingMask().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_PPE_OPTIONS, exposureData.getWearingPpe().toString());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES_OPTIONS, exposureData.getOtherProtectiveMeasures().toString());
    webDriverHelpers.clickWebElementByText(
        SHORT_DISTANCE_OPTIONS, exposureData.getShortDistance().toString());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT_OPTIONS, exposureData.getLongFaceToFaceContact().toString());
    webDriverHelpers.clickWebElementByText(
        PERCUTANEOUS_OPTIONS, exposureData.getPercutaneous().toString());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS_OPTONS, exposureData.getContactToBodyFluids().toString());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES_OPTIONS, exposureData.getHandlingSamples().toString());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_PLACE_COMBOBOX, exposureData.getTypeOfPlace().getUiValue());
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
  }

  private Exposure collectExposureData() {
    return Exposure.builder()
        .startOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE_INPUT), formatter))
        .endOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE_INPUT), formatter))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION_INPUT))
        .typeOfActivity(
            TypeOfActivityExposure.fromString(
                webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX)))
        .exposureDetailsRole(
            ExposureDetailsRole.fromString(
                webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX)))
        .riskArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA_OPTIONS)))
        .indoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS_OPTIONS)))
        .outdoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS_OPTIONS)))
        .wearingMask(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK_OPTIONS)))
        .wearingPpe(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE_OPTIONS)))
        .otherProtectiveMeasures(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    OTHER_PROTECTIVE_MEASURES_OPTIONS)))
        .shortDistance(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE_OPTIONS)))
        .longFaceToFaceContact(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    LONG_FACE_TO_FACE_CONTACT_OPTIONS)))
        .percutaneous(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS_OPTIONS)))
        .contactToBodyFluids(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    CONTACT_TO_BODY_FLUIDS_OPTONS)))
        .handlingSamples(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    HANDLING_SAMPLES_OPTIONS)))
        .typeOfPlace(
            TypeOfPlace.fromString(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX)))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .build();
  }

  private EpidemiologicalData collectSpecificData() {
    return EpidemiologicalData.builder()
        .residingAreaWithRisk(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS)))
        .largeOutbreaksArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS)))
        .build();
  }
}
