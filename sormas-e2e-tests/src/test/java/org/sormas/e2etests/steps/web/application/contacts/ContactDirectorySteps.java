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
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ENTER_BULK_EDIT_MODE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MORE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.INDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.NEW_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OUTDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RISK_AREA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ACTIVE_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.ALLBUTTON_CONTACT;
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
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_FOLLOW_UP_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACT_RESULTS_UUID_LOCATOR;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONVERTED_TO_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.DROPPED_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.FIRST_CONTACT_ID_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.GRID_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.LINE_LISTING;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.MULTIPLE_OPTIONS_SEARCH_INPUT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.RESULTS_GRID_HEADER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.TOTAL_CONTACTS_COUNTER;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.common.DataOperations;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.ContactService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;

public class ContactDirectorySteps implements En {

  protected WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Exposure exposureData;
  public static EpidemiologicalData dataSavedFromCheckbox;
  public static EpidemiologicalData specificCaseData;

  @Inject
  public ContactDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      AssertHelpers assertHelpers,
      ContactService contactService,
      DataOperations dataOperations,
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
        "I click on the NEW CONTACT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CONTACT_BUTTON, FIRST_NAME_OF_CONTACT_PERSON_INPUT));

    When(
        "I click on save Contact button",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
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
          String contactUuid =
              dataOperations.getPartialUuidFromAssociatedLink(
                  apiState.getCreatedContact().getUuid());
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
          webDriverHelpers.waitForPageLoaded();
        });
    When(
        "I click checkbox to choose all Contact results on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALL_RESULTS_CHECKBOX);
          webDriverHelpers.waitForPageLoaded();
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
    When(
        "^I click on Line Listing button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING));

    When(
        "I click on the Epidemiological Data button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EPIDEMIOLOGICAL_DATA_TAB);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
    And(
        "I click on All button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ALLBUTTON_CONTACT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });
    And(
        "I click on Converted to case pending button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CONVERTED_TO_CASE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });
    And(
        "I click on Active contact button in Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTIVE_CONTACT_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
        });
    And(
        "I click on Dropped button on Contact Directory Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DROPPED_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(60);
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
        "I open the last created contact",
        () -> {
          searchAfterContactByMultipleOptions(apiState.getCreatedContact().getUuid());
          openContactFromResultsByUUID(apiState.getCreatedContact().getUuid());
        });

    When(
        "I open the first contact from contacts list",
        () -> webDriverHelpers.clickOnWebElementBySelector(FIRST_CONTACT_ID_BUTTON));

    Then(
        "I check that number of displayed contact results is (\\d+)",
        (Integer number) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(NEW_CONTACT_BUTTON);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(
                          webDriverHelpers.getTextFromPresentWebElement(TOTAL_CONTACTS_COUNTER)),
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
    webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
  }

  private void fillExposure(Exposure exposureData) {
    webDriverHelpers.waitForPageLoaded();
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
