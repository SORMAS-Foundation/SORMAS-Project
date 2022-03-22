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

package org.sormas.e2etests.steps.web.application.entries;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.EPIDEMIOLOGICAL_DATA_TAB;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.ARRIVAL_DATE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.LAST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_CASE_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_PERSON_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_OR_CREATE_PERSON_TITLE_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CASE_PERSON_NAME;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CREATE_CASE_FROM_TRAVEL_ENTRY;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISEASE_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.POINT_OF_ENTRY_CASE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.SAVE_NEW_CASE_FOR_TRAVEL_ENTRY_POPUP;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.TRAVEL_ENTRY_PERSON_TAB;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.entities.services.TravelEntryService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.entities.services.TravelEntryService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class CreateNewTravelEntrySteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
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
  public CreateNewTravelEntrySteps(
      WebDriverHelpers webDriverHelpers,
      TravelEntryService travelEntryService,
      ApiState apiState,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I fill the required fields in a new travel entry form$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          fillFirstName(travelEntry.getFirstName());
          firstName = travelEntry.getFirstName();
          fillLastName(travelEntry.getLastName());
          lastName = travelEntry.getLastName();
          selectSex(travelEntry.getSex());
          sex = travelEntry.getSex();
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillDisease(travelEntry.getDisease());
          disease = travelEntry.getDisease();
          if (travelEntry.getDisease().equals("Andere epidemische Krankheit"))
            fillOtherDisease("Test");

          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I fill the required fields in a new travel entry form for previous created person$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryWithPointOfEntryDetailsDE(entryPoint);
          fillFirstName(firstName);
          fillLastName(lastName);
          selectSex(sex);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillDisease(disease);
          if (travelEntry.getDisease().equals("Andere epidemische Krankheit"))
            fillOtherDisease("Test");

          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I fill the required fields in a new case travel entry form$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I click on Save button from the new travel entry form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I navigate to person tab in Edit travel entry page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_PERSON_TAB);
        });

    When(
        "I check the created data is correctly displayed on Edit travel entry page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry,
              travelEntry,
              List.of(
                  "disease",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "pointOfEntry",
                  "pointOfEntryDetails"));
        });

    When(
        "I check the created data is correctly displayed on Edit case travel entry page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry,
              travelEntry,
              List.of(
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "pointOfEntry",
                  "pointOfEntryDetails"));
        });

    When(
        "I check the created data is correctly displayed on Edit travel entry person page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(2);
          aTravelEntry = collectTravelEntryPersonData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry, travelEntry, List.of("firstName", "lastName", "sex"));
        });

    When(
        "I click on new case button for travel entry",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_FROM_TRAVEL_ENTRY));

    When(
        "I check if data from travel entry for new case is correct",
        () -> {
          newCaseFromTravelEntryData = collectTravelEntryData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              newCaseFromTravelEntryData,
              travelEntry,
              List.of(
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "pointOfEntry",
                  "pointOfEntryDetails"));
          newCaseFromTravelEntryData = collectTravelEntryPersonData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              newCaseFromTravelEntryData, travelEntry, List.of("firstName", "lastName", "sex"));
        });

    When(
        "I save the new case for travel entry",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_NEW_CASE_FOR_TRAVEL_ENTRY_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_NEW_CASE_FOR_TRAVEL_ENTRY_POPUP);
        });

    When(
        "I check if data in case based on travel entry is correct",
        () -> {
          aCase = collectCasePersonDataBasedOnTravelEntryDE();
          softly.assertEquals(
              aCase.getResponsibleRegion(),
              travelEntry.getResponsibleRegion(),
              "Regions are not equal");
          softly.assertEquals(
              aCase.getResponsibleDistrict(),
              travelEntry.getResponsibleDistrict(),
              "Districts are not equal");
          softly.assertEquals(
              aCase.getResponsibleCommunity(),
              travelEntry.getResponsibleCommunity(),
              "Communities are not equal");
          softly.assertEquals(
              aCase.getPointOfEntry(),
              travelEntry.getPointOfEntry() + " (Inaktiv)",
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
        "I check if first and last person name for case in travel entry is correct",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(CASE_PERSON_NAME).toLowerCase(Locale.GERMAN),
              travelEntry.getFirstName().toLowerCase(Locale.GERMAN)
                  + " "
                  + travelEntry.getLastName().toLowerCase(Locale.GERMAN),
              "User name is invalid");
          softly.assertAll();
        });

    When(
        "^I check Pick an existing case in Pick or create person popup in travel entry$",
        () -> webDriverHelpers.clickOnWebElementBySelector(PICK_A_EXISTING_PERSON_LABEL_DE));

    When(
        "^I click confirm button in popup from travel entry$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT));

    When(
        "I choose an existing case while creating case from travel entry",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PICK_A_EXISTING_CASE_LABEL_DE); // wait for popup
          String expectedTitle = "Fall ausw\u00E4hlen oder erstellen";
          String checkPopupTitle =
              webDriverHelpers
                  .getTextFromWebElement(PICK_OR_CREATE_PERSON_TITLE_DE)
                  .toLowerCase(Locale.GERMAN);
          softly.assertEquals(
              checkPopupTitle,
              expectedTitle.toLowerCase(Locale.GERMAN),
              "Wrong popup title for Pick or create a case");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(PICK_A_EXISTING_CASE_LABEL_DE);
        });

    When(
        "^I check if created travel entries are listed in the epidemiological data tab$",
        () -> {
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              By.xpath("//div[text()='" + travelEntry.getPointOfEntryDetails() + "']"));
          webDriverHelpers.isElementDisplayedIn20SecondsOrThrowException(
              By.xpath("//div[text()='Automated test dummy description']"));
        });
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

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(
        CreateNewTravelEntryPage.RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
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

  private TravelEntry collectTravelEntryData() {
    return TravelEntry.builder()
        .disease(webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.DISEASE_COMBOBOX))
        .responsibleRegion(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX))
        .responsibleDistrict(
            webDriverHelpers.getValueFromCombobox(
                EditTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX))
        .responsibleCommunity(
            webDriverHelpers.getValueFromCombobox(
                EditTravelEntryPage.RESPONSIBLE_COMMUNITY_COMBOBOX))
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
}
