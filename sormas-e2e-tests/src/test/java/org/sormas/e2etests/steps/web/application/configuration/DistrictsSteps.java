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

import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_ARCHIVING_YES_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_DISTRICTS_TAB;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.ARCHIVE_DISTRICT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.CONFIRM_ARCHIVING_DISTRICT_TEXT;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.CREATE_NEW_ENTRY_DISTRICTS_EPID_CODE_INPUT;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.CREATE_NEW_ENTRY_DISTRICTS_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.CREATE_NEW_ENTRY_DISTRICTS_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.DISTRICTS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.EDIT_DISTRICT_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.RESET_FILTERS_DISTRICTS_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.SAVE_NEW_ENTRY_DISTRICTS;
import static org.sormas.e2etests.pages.application.configuration.DistrictsTabPage.SEARCH_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.ARRIVAL_DATE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.LAST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISEASE_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.INFO_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.POINT_OF_ENTRY_CASE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PERSON_FILTER_INPUT;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.Districts;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.entities.services.DistrictsService;
import org.sormas.e2etests.entities.services.TravelEntryService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.testng.asserts.SoftAssert;

public class DistrictsSteps implements En {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  protected Districts districts;
  public static TravelEntry travelEntry;
  public static TravelEntry aTravelEntry;
  public static TravelEntry newCaseFromTravelEntryData;
  public static Case aCase;
  String firstName;
  String lastName;
  String sex;
  String disease;
  String entryPoint = "Test entry point";

  @Inject
  public DistrictsSteps(
      WebDriverHelpers webDriverHelpers,
      DistrictsService districtsService,
      TravelEntryService travelEntryService,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on Districts button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_DISTRICTS_TAB));

    When(
        "I click on New Entry button in Districts tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(DISTRICTS_NEW_ENTRY_BUTTON));

    When(
        "I fill new district with specific data for DE version",
        () -> {
          districts = districtsService.buildSpecificDistrict();
          fillDistrictName(districts.getDistrictName());
          fillEpidCode(districts.getEpidCode());
          selectCommunityRegion(districts.getRegion());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_NEW_ENTRY_DISTRICTS);
        });

    When(
        "I create new travel entry with created district for DE version",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          fillFirstName(travelEntry.getFirstName());
          firstName = travelEntry.getFirstName();
          fillLastName(travelEntry.getLastName());
          lastName = travelEntry.getLastName();
          selectSex(travelEntry.getSex());
          sex = travelEntry.getSex();
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(districts.getRegion());
          selectResponsibleDistrict(districts.getDistrictName());
          fillDisease(travelEntry.getDisease());
          disease = travelEntry.getDisease();
          if (travelEntry.getDisease().equals("Andere epidemische Krankheit"))
            fillOtherDisease("Test");
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "I check if data with created district in case based on travel entry is correct",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(INFO_BUTTON);
          aCase = collectCasePersonDataBasedOnTravelEntryDE();
          softly.assertEquals(
              aCase.getResponsibleRegion(), districts.getRegion(), "Regions are not equal");
          softly.assertEquals(
              aCase.getResponsibleDistrict(),
              districts.getDistrictName(),
              "Districts are not equal");
          softly.assertEquals(
              aCase.getPointOfEntry(),
              travelEntry.getPointOfEntry(),
              "Point of entries are not equal");
          softly.assertEquals(
              aCase.getFirstName().toLowerCase(Locale.GERMAN),
              travelEntry.getFirstName().toLowerCase(Locale.GERMAN),
              "First names are not equal");
          softly.assertEquals(
              aCase.getLastName().toLowerCase(Locale.GERMAN),
              travelEntry.getLastName().toLowerCase(Locale.GERMAN),
              "Last names are not equal");
          softly.assertAll();
        });

    When(
        "I check the created data is correctly displayed on Edit travel entry page with specific district for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(
              4); // workaround because of problem with "element is not attached to the page
          // document"
          aTravelEntry = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry, travelEntry, List.of("disease", "pointOfEntry", "pointOfEntryDetails"));
        });

    When(
        "I filter by last created district",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_DISTRICT_INPUT, districts.getDistrictName());
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(RESET_FILTERS_DISTRICTS_BUTTON);
        });
    When(
        "I click on edit button for filtered district",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_DISTRICT_BUTTON));

    When(
        "I archive chosen district",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CREATE_NEW_ENTRY_DISTRICTS_NAME_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_DISTRICT_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_ARCHIVING_DISTRICT_TEXT);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ARCHIVING_YES_BUTTON);
        });

    When(
        "I filter by Person ID on Travel Entry directory page with specific district",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_FILTER_INPUT, newCaseFromTravelEntryData.getUuid());
        });

    When(
        "I check if data from travel entry for new case is correct with specific district",
        () -> {
          newCaseFromTravelEntryData = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              newCaseFromTravelEntryData,
              travelEntry,
              List.of("responsibleRegion", "pointOfEntry", "pointOfEntryDetails"));
          newCaseFromTravelEntryData = collectTravelEntryPersonData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              newCaseFromTravelEntryData, travelEntry, List.of("firstName", "lastName", "sex"));
        });

    When(
        "I check if archived district is marked as a inactive",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(
                  EditTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX),
              aTravelEntry.getResponsibleDistrict() + " (Inaktiv)",
              "Responsible districts are not equal");
        });

    When(
        "I check if archived district is unavailable",
        () -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  DISTRICT_COMBOBOX, districts.getDistrictName()));
          softly.assertAll();
        });
  }

  public void fillDistrictName(String communityName) {
    webDriverHelpers.fillInWebElement(CREATE_NEW_ENTRY_DISTRICTS_NAME_INPUT, communityName);
  }

  public void fillEpidCode(String epidCode) {
    webDriverHelpers.fillInWebElement(CREATE_NEW_ENTRY_DISTRICTS_EPID_CODE_INPUT, epidCode);
  }

  private void selectCommunityRegion(String region) {
    webDriverHelpers.selectFromCombobox(CREATE_NEW_ENTRY_DISTRICTS_REGION_COMBOBOX, region);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_OF_CONTACT_PERSON_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_OF_CONTACT_PERSON_INPUT, lastName);
  }

  private void fillDateOfArrival(LocalDate dateOfArrival, Locale locale) {
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(
          ARRIVAL_DATE, DATE_FORMATTER_DE.format(dateOfArrival));
    else webDriverHelpers.clearAndFillInWebElement(ARRIVAL_DATE, formatter.format(dateOfArrival));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(CreateNewTravelEntryPage.SEX_COMBOBOX, sex);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(CreateNewTravelEntryPage.DISEASE_COMBOBOX, disease);
  }

  private void fillOtherDisease(String otherDisease) {
    webDriverHelpers.fillInWebElement(DISEASE_NAME_INPUT, otherDisease);
  }

  private void fillPointOfEntry(String pointOfEntry) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.POINT_OF_ENTRY_COMBOBOX, pointOfEntry);
  }

  private void fillPointOfEntryDetails(String pointOfEntryDetails) {
    webDriverHelpers.fillInWebElement(
        CreateNewTravelEntryPage.POINT_OF_ENTRY_DETAILS_INPUT, pointOfEntryDetails);
  }

  private Case getUserInformationDE() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    return Case.builder().firstName(userInfos[0]).lastName(userInfos[1]).build();
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
  }

  private Case collectCasePersonDataBasedOnTravelEntryDE() {
    Case userInfo = getUserInformationDE();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .pointOfEntry(webDriverHelpers.getValueFromWebElement(POINT_OF_ENTRY_CASE))
        .build();
  }

  private TravelEntry collectTravelEntryData() {
    return TravelEntry.builder()
        .responsibleRegion(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(
            webDriverHelpers.getValueFromCombobox(
                EditTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX))
        .disease(webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX))
        .pointOfEntry(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.POINT_OF_ENTRY_COMBOBOX))
        .pointOfEntryDetails(
            webDriverHelpers.getValueFromWebElement(
                EditTravelEntryPage.POINT_OF_ENTRY_DETAILS_INPUT))
        .build();
  }

  private TravelEntry collectTravelEntryPersonData() {
    return TravelEntry.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
        .sex(webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.SEX_COMBOBOX))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }
}
