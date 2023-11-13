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

package org.sormas.e2etests.steps.web.application.configuration;

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FACILITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FACILITY_NAME_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_FACILITIES_TAB;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.ACTION_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.ARCHIVE_FACILITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.COMMUNITY_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.COUNTRY_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.DISTRICT_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.EDIT_FIRST_FACILITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.ENTER_BULK_EDIT_MODE_BUTTON_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.EXPORT_FACILITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITIES_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITIES_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CATEGORY_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CONTACT_PERSON_EMAIL_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CONTACT_PERSON_FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CONTACT_PERSON_LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_CONTACT_PERSON_PHONE_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_EXPOSURE_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_GRID_RESULTS_ROWS;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.FACILITY_TYPE_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.NEW_CASE_FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.REGION_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.RELEVANCE_STATUS_COMBOBOX_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.RESET_FILTERS_BUTTON_FACILITIES_CONFIGURATION;
import static org.sormas.e2etests.pages.application.configuration.FacilitiesTabPage.SEARCH_FACILITY;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.ACTIVITY_AS_CASE_FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.ACTIVITY_AS_CASE_TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.EXPOSURE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.FACILITY_DETAILS_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.EVENT_DISTRICT;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.EVENT_REGION;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.TITLE_INPUT;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.events.EditEventPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.events.EditEventPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.APPLY_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.NEW_EVENT_BUTTON;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.RESET_FILTER;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.SEARCH_EVENT_BY_FREE_TEXT_INPUT;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CONTACT_PERSON_EMAIL_CASE_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CONTACT_PERSON_FIRST_NAME_CASE_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CONTACT_PERSON_LAST_NAME_CASE_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.FACILITY_CONTACT_PERSON_PHONE_CASE_PERSON_INPUT;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.Event;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.entities.services.EventService;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class FacilitySteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  protected static Case caze;
  protected static Event newEvent;
  String aFacilityCategory;
  String aFacilityType;

  @Inject
  public FacilitySteps(
      WebDriverHelpers webDriverHelpers,
      SoftAssert softly,
      Faker faker,
      CaseService caseService,
      EventService eventService,
      AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

    String firstNameFaker = faker.name().firstName();
    String lastNameFaker = faker.name().lastName();
    String emailFaker = faker.internet().emailAddress();
    String phoneNumberFaker = faker.phoneNumber().cellPhone();
    String facilityName = "Facility" + String.valueOf(System.currentTimeMillis());

    When(
        "I click on Facilities button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_FACILITIES_TAB));

    When(
        "I click on New Entry button in Facilities tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(FACILITIES_NEW_ENTRY_BUTTON));

    When(
        "I set name, region and district in Facilities tab in Configuration",
        () -> {
          fillFacilityNameAndDescription(facilityName);
          selectRegion(RegionsValues.VoreingestellteBundeslander.getName());
          selectDistrict(DistrictsValues.VoreingestellterLandkreis.getName());
        });

    When(
        "I set Facility Category to {string} and Facility Type to {string} in Facilities tab in Configuration",
        (String facilityCategory, String facilityType) -> {
          aFacilityCategory = facilityCategory;
          aFacilityType = facilityType;
          selectFacilityCategory(facilityCategory);
          selectFacilityType(facilityType);
        });
    When(
        "I click on Save Button in new Facility form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I set Facility Contact person first and last name with email address and phone number",
        () -> {
          webDriverHelpers.fillInWebElement(
              FACILITY_CONTACT_PERSON_FIRST_NAME_INPUT, firstNameFaker);
          webDriverHelpers.fillInWebElement(FACILITY_CONTACT_PERSON_LAST_NAME_INPUT, lastNameFaker);
          webDriverHelpers.fillInWebElement(FACILITY_CONTACT_PERSON_EMAIL_INPUT, emailFaker);
          webDriverHelpers.fillInWebElement(FACILITY_CONTACT_PERSON_PHONE_INPUT, phoneNumberFaker);
        });

    When(
        "I create a new case with specific data using created facility",
        () -> {
          caze =
              caseService.buildGeneratedCaseWithCreatedFacilityDE(
                  aFacilityCategory, aFacilityType, facilityName);
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectPlaceOfStay(caze.getPlaceOfStay());
          selectFacilityCategory(caze.getFacilityCategory());
          selectNewCaseFacilityType(caze.getFacilityType());
          selectFacility(caze.getFacility());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          selectSex(caze.getSex());
        });

    When(
        "^I create a new event with specific data for DE version with created facility$",
        () -> {
          newEvent =
              eventService.buildGeneratedEventWithCreatedFacilityDE(
                  aFacilityCategory, aFacilityType, facilityName);
          fillDateOfReportEvent(newEvent.getReportDate(), Locale.GERMAN);
          fillTitle(newEvent.getTitle());
          selectResponsibleRegionEvent(newEvent.getRegion());
          selectResponsibleDistrictEvent(newEvent.getDistrict());
          selectTypeOfPlace(newEvent.getEventLocation());
          selectFacilityCategory(newEvent.getFacilityCategory());
          selectFacilityTypeEvent(newEvent.getFacilityType());
          selectFacilityEvent(newEvent.getFacility());
          newEvent =
              newEvent.toBuilder()
                  .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
                  .build();
        });

    When(
        "I set all needed data in Exposure popup to check if created facility is connected",
        () -> {
          webDriverHelpers.selectFromCombobox(
              EXPOSURE_REGION_COMBOBOX, caze.getResponsibleRegion());
          webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, caze.getResponsibleDistrict());
          webDriverHelpers.selectFromCombobox(
              FACILITY_CATEGORY_COMBOBOX, caze.getFacilityCategory());
          webDriverHelpers.selectFromCombobox(
              FACILITY_EXPOSURE_TYPE_COMBOBOX, caze.getFacilityType());
          webDriverHelpers.selectFromCombobox(FACILITY_DETAILS_COMBOBOX, caze.getFacility());
          TimeUnit.SECONDS.sleep(1);
        });

    When(
        "I check if data for created facility is automatically imported to the correct fields",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(FACILITY_CONTACT_PERSON_FIRST_NAME_INPUT),
              firstNameFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(FACILITY_CONTACT_PERSON_LAST_NAME_INPUT),
              lastNameFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(FACILITY_CONTACT_PERSON_EMAIL_INPUT),
              emailFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(FACILITY_CONTACT_PERSON_PHONE_INPUT),
              phoneNumberFaker);
          softly.assertAll();
        });

    When(
        "I set all needed data in Activity as Case popup to check if created facility is connected",
        () -> {
          webDriverHelpers.selectFromCombobox(
              EXPOSURE_REGION_COMBOBOX, caze.getResponsibleRegion());
          webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, caze.getResponsibleDistrict());
          webDriverHelpers.selectFromCombobox(
              ACTIVITY_AS_CASE_TYPE_OF_ACTIVITY_COMBOBOX, "Untergebracht in");
          webDriverHelpers.selectFromCombobox(
              ACTIVITY_AS_CASE_FACILITY_TYPE_COMBOBOX, "Einrichtung");
          webDriverHelpers.selectFromCombobox(
              FACILITY_EXPOSURE_TYPE_COMBOBOX, caze.getFacilityType());
          webDriverHelpers.selectFromCombobox(FACILITY_DETAILS_COMBOBOX, facilityName);
        });

    When(
        "I set facility name to created facility",
        () -> {
          webDriverHelpers.selectFromCombobox(FACILITY_NAME_COMBOBOX, facilityName);
        });

    When(
        "I check if data for created facility is automatically imported to the correct fields in Case Person tab",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(
                  FACILITY_CONTACT_PERSON_FIRST_NAME_CASE_PERSON_INPUT),
              firstNameFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(
                  FACILITY_CONTACT_PERSON_LAST_NAME_CASE_PERSON_INPUT),
              lastNameFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(
                  FACILITY_CONTACT_PERSON_EMAIL_CASE_PERSON_INPUT),
              emailFaker);
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(
                  FACILITY_CONTACT_PERSON_PHONE_CASE_PERSON_INPUT),
              phoneNumberFaker);
          softly.assertAll();
        });

    When(
        "^I search for specific event with created facility in event directory$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(NEW_EVENT_BUTTON, 35);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER);
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
          final String eventUuid = newEvent.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEARCH_EVENT_BY_FREE_TEXT_INPUT, 20);
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_EVENT_BY_FREE_TEXT_INPUT, eventUuid);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "I click on the searched event with created facility",
        () -> {
          final String eventUuid = newEvent.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              getByEventUuid(eventUuid));
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID_INPUT);
        });

    When(
        "I search last created facility",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_FACILITY, facilityName);
          TimeUnit.SECONDS.sleep(1); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I click on edit button for the last searched facility",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EDIT_FIRST_FACILITY_BUTTON);
        });

    When(
        "I archive facility",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_FACILITY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_BUTTON);
        });

    Then(
        "I Verify the page elements are present in Facilities Configuration Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              FACILITY_CATEGORY_COMBOBOX_FACILITIES_CONFIGURATION);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FACILITIES_IMPORT_BUTTON),
              "Import Button is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(EXPORT_FACILITY_BUTTON),
              "Export Button is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FACILITIES_NEW_ENTRY_BUTTON),
              "New Entry Button is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  ENTER_BULK_EDIT_MODE_BUTTON_FACILITIES_CONFIGURATION),
              "Enter Bulk Edit Mode Button is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(SEARCH_FACILITY),
              "Search Input is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  FACILITY_CATEGORY_COMBOBOX_FACILITIES_CONFIGURATION),
              "Facility Category Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(FACILITY_TYPE_COMBOBOX_FACILITIES_CONFIGURATION),
              "Facility Type Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(COUNTRY_COMBOBOX_FACILITIES_CONFIGURATION),
              "Country Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(REGION_COMBOBOX_FACILITIES_CONFIGURATION),
              "Region Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(DISTRICT_COMBOBOX_FACILITIES_CONFIGURATION),
              "District Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(COMMUNITY_COMBOBOX_FACILITIES_CONFIGURATION),
              "Community Combo box is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(RESET_FILTERS_BUTTON_FACILITIES_CONFIGURATION),
              "Reset Filters Button is Not present in Facilities Configuration");
          softly.assertTrue(
              webDriverHelpers.isElementPresent(RELEVANCE_STATUS_COMBOBOX_FACILITIES_CONFIGURATION),
              "Relevance status Combo box is Not present in Facilities Configuration");
          softly.assertAll();
        });

    When(
        "I filter facility by {string}",
        (String facilName) -> {
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_FACILITY, facilName);
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check that number of displayed Facilities results is {int}",
        (Integer nr) -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getNumberOfElements(FACILITY_GRID_RESULTS_ROWS),
                      nr.intValue(),
                      "Number of displayed facilities is not correct"));
        });
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectNewCaseFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(NEW_CASE_FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacilityTypeEvent(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_EXPOSURE_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacilityCategory(String facility) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facility);
  }

  private void fillFacilityNameAndDescription(String facilityDescription) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_INPUT, facilityDescription);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleRegionEvent(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(EVENT_REGION, selectResponsibleRegion);
  }

  private void selectResponsibleDistrictEvent(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(EVENT_DISTRICT, responsibleDistrict);
  }

  private void selectFacility(String selectFacility) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, selectFacility);
  }

  private void selectFacilityEvent(String selectFacility) {
    webDriverHelpers.selectFromCombobox(FACILITY_NAME_COMBOBOX, selectFacility);
  }

  private void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.clickWebElementByText(PLACE_OF_STAY, placeOfStay);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void selectTypeOfPlace(String typeOfPlace) {
    webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, typeOfPlace);
  }

  private void fillTitle(String title) {
    webDriverHelpers.fillInWebElement(TITLE_INPUT, title);
  }

  private void fillDateOfReportEvent(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(REPORT_DATE_INPUT, formatter.format(date));
  }
}
