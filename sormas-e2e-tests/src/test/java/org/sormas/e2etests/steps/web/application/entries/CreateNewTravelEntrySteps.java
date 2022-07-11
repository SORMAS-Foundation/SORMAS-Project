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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CLOSE_FORM_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DIFFERENT_POINT_OF_ENTRY_JURISDICTION;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_SEARCH_LOCATOR_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_DISTRICT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_REGION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.QUARANTINE_ORDER_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.ARRIVAL_DATE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DATE_OF_ARRIVAL_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DATE_OF_ARRIVAL_POPUP_CLOSE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DIFFERENT_POINT_OF_ENTRY_CHECKBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DISEASE_COMBOBOX_DISABLED;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.DISEASE_VARIANT_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_TRAVEL_ENTRY_ID_BUTTON;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.FIRST_UUID_TABLE_TRAVEL_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.INPUT_DATA_ERROR_POPUP;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.LAST_NAME_OF_CONTACT_PERSON_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.OPEN_CASE_OF_THIS_TRAVEL_ENTRY_BUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_CASE_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_PERSON_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_OR_CREATE_PERSON_TITLE_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.POINT_OF_ENTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.POINT_OF_ENTRY_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.POINT_OF_ENTRY_REGION_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.REPORT_DATE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CASE_PERSON_NAME;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CREATE_CASE_FROM_TRAVEL_ENTRY;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CREATE_DOCUMENT_BUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.CREATE_DOCUMENT_POPUP_BUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DELETE_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISCARD_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISEASE_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.EDIT_TASK_DE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.GENERATED_DOCUMENT_NAME_DE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.INFO_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.NEW_TASK_DE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.PERSON_ID_LABEL;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.POINT_OF_ENTRY_CASE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.SAVE_EDIT_TRAVEL_PAGE;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.SAVE_NEW_CASE_FOR_TRAVEL_ENTRY_POPUP;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.SAVE_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.TASK_STATUS_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.TRAVEL_ENTRY_PERSON_TAB;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.TRAVEL_ENTRY_TAB;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PERSON_FILTER_INPUT;
import static org.sormas.e2etests.steps.web.application.entries.TravelEntryDirectorySteps.userDirPath;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.entities.services.TravelEntryService;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.CreateNewCasePage;
import org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.sormas.e2etests.state.ApiState;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class CreateNewTravelEntrySteps implements En {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private final WebDriverHelpers webDriverHelpers;
  public static TravelEntry travelEntry;
  public static TravelEntry aTravelEntry;
  public static TravelEntry TravelEntryUuid;
  public static TravelEntry newCaseFromTravelEntryData;
  public static Case aCase;
  public static String collectTravelEntryPersonUuid;
  public static LocalDate previousWeekDate;
  String firstName;
  String lastName;
  String sex;
  String disease;
  String entryPoint = "Test entry point";
  protected static TravelEntry travelEntryWithSamePersonData;
  List<TravelEntry> TravelEntryUuidList = new ArrayList<>();

  @Inject
  public CreateNewTravelEntrySteps(
      WebDriverHelpers webDriverHelpers,
      TravelEntryService travelEntryService,
      ApiState apiState,
      Faker faker,
      SoftAssert softly,
      AssertHelpers assertHelpers) {
    this.webDriverHelpers = webDriverHelpers;
    Random r = new Random();
    char c = (char) (r.nextInt(26) + 'a');
    String firstNameforDuplicatedPerson = faker.name().firstName() + c;
    String lastNameforDuplicatedPerson = faker.name().lastName() + c;
    String sexforDuplicatedPerson = GenderValues.getRandomGenderDE();
    travelEntryWithSamePersonData =
        travelEntryService.buildGeneratedEntryWithParametrizedPersonDataDE(
            firstNameforDuplicatedPerson, lastNameforDuplicatedPerson, sexforDuplicatedPerson);

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
        "^I fill the required fields in a new travel entry form without disease and person data$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });
    When(
        "^I fill the required fields in a new travel entry form with same person data$",
        () -> {
          fillFirstName(travelEntryWithSamePersonData.getFirstName());
          fillLastName(travelEntryWithSamePersonData.getLastName());
          selectSex(travelEntryWithSamePersonData.getSex());
          fillDateOfArrival(travelEntryWithSamePersonData.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntryWithSamePersonData.getResponsibleRegion());
          selectResponsibleDistrict(travelEntryWithSamePersonData.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntryWithSamePersonData.getResponsibleCommunity());
          fillDisease(travelEntryWithSamePersonData.getDisease());
          if (travelEntryWithSamePersonData.getDisease().equals("Andere epidemische Krankheit"))
            fillOtherDisease("Test");

          fillPointOfEntry(travelEntryWithSamePersonData.getPointOfEntry());
          fillPointOfEntryDetails(travelEntryWithSamePersonData.getPointOfEntryDetails());
        });

    When(
        "^I change a Report Date for previous week date$",
        () -> {
          previousWeekDate = travelEntry.getReportDate().minusDays(7);
          fillReportDate(previousWeekDate, Locale.GERMAN);
        });

    When(
        "I check that ([^\"]*) is not visible in New Travel Entry popup",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "First Name":
              selector = FIRST_NAME_OF_CONTACT_PERSON_INPUT;
              break;
            case "Last Name":
              selector = LAST_NAME_OF_CONTACT_PERSON_INPUT;
              break;
            case "Sex":
              selector = SEX_COMBOBOX;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertFalse(elementVisible, option + " is visible!");
          softly.assertAll();
        });
    When(
        "I check that disease in New Travel Entry popup is disabled",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          Boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(DISEASE_COMBOBOX_DISABLED);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertTrue(elementVisible, "Disease combobox is enabled!");
          softly.assertAll();
        });
    When(
        "I check that disease in New Travel Entry popup is enabled",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          Boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(DISEASE_COMBOBOX_DISABLED);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertFalse(elementVisible, "Disease combobox is disabled!");
          softly.assertAll();
        });
    When(
        "^I open last created Travel Entry",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(FIRST_TRAVEL_ENTRY_ID_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(FIRST_TRAVEL_ENTRY_ID_BUTTON);
        });

    When(
        "^I fill the required fields in a new travel entry form without personal data$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
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
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    When(
        "^I fill the required fields for new case in existing travel entry form$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryDE();
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectResponsibleCommunity(travelEntry.getResponsibleCommunity());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
        });

    When(
        "^I click on the person search button in create new travel entry form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });
    When(
        "^I change a Date of Arrival for wrong date from next day",
        () -> {
          fillDateOfArrival(travelEntry.getDateOfArrival().plusDays(1), Locale.GERMAN);
        });

    When(
        "^I change a Date of Arrival for correct date",
        () -> {
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
        });

    When(
        "I check that word Date of arrival is appropriate translated to German language",
        () -> {
          String expectedWordToTranslateInGerman = "EINREISEDATUM";
          String wordGettingFromLabel =
              webDriverHelpers.getTextFromWebElement(DATE_OF_ARRIVAL_LABEL_DE);
          softly.assertEquals(
              wordGettingFromLabel,
              expectedWordToTranslateInGerman,
              "The translation is not proper");
          softly.assertAll();
        });

    When(
        "I check the information about Dates for imported travel entry on Edit Travel entry page",
        () -> {
          String reportDate = webDriverHelpers.getValueFromWebElement(REPORT_DATE);
          String arrivalDate = webDriverHelpers.getValueFromWebElement(ARRIVAL_DATE);

          softly.assertEquals(reportDate, "03.10.2021");
          softly.assertEquals(arrivalDate, "02.10.2021");
          softly.assertAll();
        });

    When(
        "^I check that Date of Arrival validation popup is appear",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DATE_OF_ARRIVAL_POPUP_CLOSE);
          webDriverHelpers.clickOnWebElementBySelector(DATE_OF_ARRIVAL_POPUP_CLOSE);
        });

    When(
        "^I click on Save button from the new travel entry form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I click on Save button from the edit travel entry form",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_EDIT_TRAVEL_PAGE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_TRAVEL_PAGE);
        });

    When(
        "^I navigate to person tab in Edit travel entry page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_PERSON_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PERSON_ID_LABEL);
        });
    When(
        "^I navigate to Edit travel entry page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(TRAVEL_ENTRY_TAB);
        });
    When(
        "I collect the Travel Entry person UUID displayed on Travel Entry Person page",
        () -> collectTravelEntryPersonUuid = collectTravelEntryPersonUuid());

    When(
        "I check the created data is correctly displayed on Edit travel entry page for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(3); // because 'element is not attached to the page document' issue
          webDriverHelpers.waitForPageLoaded();
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
        "I click NEW TASK in Edit Travel Entry page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_DE);
        });
    When(
        "I check if new task is displayed in Task tab on Edit Travel Entry page",
        () -> {
          boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(EDIT_TASK_DE);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertTrue(elementVisible, "Task is not visible!");
          softly.assertAll();
        });
    When(
        "I check that ([^\"]*) option is visible in Edit Task form on Edit Travel Entry page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "Discard":
              selector = DISCARD_TASK_BUTTON;
              break;
            case "Save":
              selector = SAVE_TASK_BUTTON;
              break;
            case "Delete":
              selector = DELETE_TASK_BUTTON;
              break;
            case "Task status":
              selector = TASK_STATUS_RADIOBUTTON;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          softly.assertTrue(elementVisible, option + " is not visible!");
          softly.assertAll();
        });
    When(
        "I click on Discard button in Task form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_TASK_BUTTON);
        });

    And(
        "I click on Create Document button from Bulk Actions combobox on Edit Travel Entry Page",
        () -> webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_BUTTON_DE));
    And(
        "I click on checkbox to upload generated document to entities in Create Document form in Travel Entry directory",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(UPLOAD_DOCUMENT_TO_ENTITIES_CHECKBOX_DE));
    When(
        "I select {string} Create Document form in Travel Entry directory",
        (String name) -> {
          webDriverHelpers.selectFromCombobox(QUARANTINE_ORDER_COMBOBOX, name);
        });
    And(
        "I click on Create button in Create Document form in Travel Entry directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_DOCUMENT_POPUP_BUTTON_DE);
        });
    And(
        "I click on close button in Create Document Order form",
        () -> webDriverHelpers.clickOnWebElementBySelector(CLOSE_FORM_BUTTON));

    When(
        "I check if downloaded file is correct for {string} in Edit Travel Entry directory",
        (String name) -> {
          String uuid = aTravelEntry.getUuid();
          Path path =
              Paths.get(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertTrue(
                      Files.exists(path),
                      "Quarantine order document was not downloaded. Path used for check: "
                          + path.toAbsolutePath()));
        });
    When(
        "I check if generated document based on {string} appeared in Documents tab in Edit Travel Entry directory",
        (String name) -> {
          String uuid = aTravelEntry.getUuid();
          String path = uuid.substring(0, 6).toUpperCase() + "-" + name;
          assertHelpers.assertWithPoll(
              () ->
                  Assert.assertEquals(
                      path, webDriverHelpers.getTextFromWebElement(GENERATED_DOCUMENT_NAME_DE)),
              120);
        });
    When(
        "I delete downloaded file created from {string} Document Template for Travel Entry",
        (String name) -> {
          String uuid = aTravelEntry.getUuid();
          File toDelete =
              new File(
                  userDirPath + "/downloads/" + uuid.substring(0, 6).toUpperCase() + "-" + name);
          toDelete.deleteOnExit();
        });
    When(
        "I collect travel UUID from travel entry",
        () -> {
          TravelEntryUuid = collectTravelEntryUuid();
          TravelEntryUuidList.add(TravelEntryUuid);
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
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(INFO_BUTTON);
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
        "I check that Point of Entry and Point of Entry details are generated automatically by system and appear on Edit Travel Entry page",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.POINT_OF_ENTRY_COMBOBOX),
              "Anderer Einreiseort",
              "Point of Entry is not set properly");
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(
                  EditTravelEntryPage.POINT_OF_ENTRY_DETAILS_INPUT),
              "[System] Automatisch bef\u00FCllter Einreiseort",
              "Point of Entry Details are not set properly");
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

    When(
        "I click on Open case of this travel entry on Travel entry tab for DE version",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(OPEN_CASE_OF_THIS_TRAVEL_ENTRY_BUTTON_DE));

    When(
        "I search for ([^\"]*) created travel entry by UUID for person in Travel Entries Directory",
        (String option) -> {
          String uuid;
          if (option.equals("first")) uuid = TravelEntryUuidList.get(0).getUuid();
          else uuid = TravelEntryUuidList.get(1).getUuid();
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.fillAndSubmitInWebElement(PERSON_FILTER_INPUT, uuid);
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if ([^\"]*) Travel Entry UUID is available in Travel Entries Directory List",
        (String option) -> {
          String collectedUUID =
              webDriverHelpers.getTextFromWebElement(FIRST_UUID_TABLE_TRAVEL_ENTRIES);
          if (option.equals("first"))
            softly.assertEquals(
                collectedUUID,
                TravelEntryUuidList.get(0).getUuid().substring(0, 6),
                "UUIDs are not equal");
          else if (option.equals("second"))
            softly.assertEquals(
                collectedUUID,
                TravelEntryUuidList.get(1).getUuid().substring(0, 6),
                "UUIDs are not equal");
          else softly.fail("There is no valid uuid");
          softly.assertAll();
        });

    And(
        "^I check if Different Point Of Entry Jurisdiction checkbox appears in New Travel Entry popup$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              DIFFERENT_POINT_OF_ENTRY_JURISDICTION);
        });

    When(
        "^I create new travel entry with Different Point Of Entry Jurisdiction for DE$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryWithDifferentPointOfEntryDE();
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          selectPointOfEntryRegion(travelEntry.getPointOfEntryRegion());
          selectPointOfEntryDistrict(travelEntry.getPointOfEntryDistrict());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
          fillFirstName(travelEntry.getFirstName());
          firstName = travelEntry.getFirstName();
          fillLastName(travelEntry.getLastName());
          lastName = travelEntry.getLastName();
          selectSex(travelEntry.getSex());
          sex = travelEntry.getSex();
          fillDisease(travelEntry.getDisease());
          disease = travelEntry.getDisease();
        });

    And(
        "^I check the created Different Point Of Entry data is correctly displayed on Edit travel entry page for DE$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          aTravelEntry = collectTravelEntryDifferentPointOfEntryJurisdictionData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              aTravelEntry, travelEntry, List.of("pointOfEntryRegion", "pointOfEntryDistrict"));
        });

    And(
        "^I convert the Travel Entry into a case$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_CASE_FROM_TRAVEL_ENTRY);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CreateNewCasePage.DATE_OF_REPORT_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    Then(
        "^I check that all required fields except person fields are mandatory in the new travel entry form DE specific$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INPUT_DATA_ERROR_POPUP);
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Einreisedatum");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Bundesland");
          webDriverHelpers.checkWebElementContainsText(
              INPUT_DATA_ERROR_POPUP, "Landkreis/Kreisfreie Stadt");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Einreiseort");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Meldedatum");
          webDriverHelpers.checkWebElementContainsText(
              INPUT_DATA_ERROR_POPUP, "Zust\u00E4ndige/r Landkreis/Kreisfreie Stadt");
          webDriverHelpers.checkWebElementContainsText(
              INPUT_DATA_ERROR_POPUP, "Zust\u00E4ndiges Bundesland");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Krankheit");
        });

    And(
        "^I check that new travel entry form contains all the necessary fields$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REPORT_DATE);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              CreateNewTravelEntryPage.EXTERNAL_ID_INPUT);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DISEASE_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DISEASE_VARIANT_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_REGION_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_DISTRICT_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_COMMUNITY_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(DIFFERENT_POINT_OF_ENTRY_CHECKBOX);
          webDriverHelpers.clickOnWebElementBySelector(DIFFERENT_POINT_OF_ENTRY_CHECKBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POINT_OF_ENTRY_REGION_INPUT);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POINT_OF_ENTRY_DISTRICT_INPUT);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POINT_OF_ENTRY_COMBOBOX);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(FIRST_NAME_OF_CONTACT_PERSON_INPUT);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(LAST_NAME_OF_CONTACT_PERSON_INPUT);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SEX_COMBOBOX);
        });

    And(
        "^I clear report date and disease fields in the new travel entry form$",
        () -> {
          webDriverHelpers.clearWebElement(REPORT_DATE);
          fillDisease("");
        });

    When(
        "^I fill all required fields except person-related fields in the new travel entry form DE specific$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryWithDifferentPointOfEntryDE();
          fillReportDate(travelEntry.getReportDate(), Locale.GERMAN);
          fillDateOfArrival(travelEntry.getDateOfArrival(), Locale.GERMAN);
          selectResponsibleRegion(travelEntry.getResponsibleRegion());
          selectResponsibleDistrict(travelEntry.getResponsibleDistrict());
          fillDisease(travelEntry.getDisease());
          disease = travelEntry.getDisease();
          if (travelEntry.getDisease().equals("Andere epidemische Krankheit"))
            fillOtherDisease("Test");
          selectPointOfEntryRegion(travelEntry.getPointOfEntryRegion());
          selectPointOfEntryDistrict(travelEntry.getPointOfEntryDistrict());
          fillPointOfEntry(travelEntry.getPointOfEntry());
          fillPointOfEntryDetails(travelEntry.getPointOfEntryDetails());
        });

    And(
        "^I fill the person-related required fields in the new entry form DE specific$",
        () -> {
          travelEntry = travelEntryService.buildGeneratedEntryWithDifferentPointOfEntryDE();
          fillFirstName(travelEntry.getFirstName());
          fillLastName(travelEntry.getLastName());
          selectSex(travelEntry.getSex());
        });

    Then(
        "^I check that person-related fields are mandatory in the new entry form DE specific$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INPUT_DATA_ERROR_POPUP);
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Vorname");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Geschlecht");
          webDriverHelpers.checkWebElementContainsText(INPUT_DATA_ERROR_POPUP, "Nachname");
        });

    And(
        "^I close input data error popup$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(INPUT_DATA_ERROR_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(INPUT_DATA_ERROR_POPUP);
        });
  }

  private void selectPointOfEntryDistrict(String pointOfEntryDistrict) {
    webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_DISTRICT_BUTTON, pointOfEntryDistrict);
  }

  private void selectPointOfEntryRegion(String pointOfEntryRegion) {
    webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_REGION_BUTTON, pointOfEntryRegion);
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

  private void fillReportDate(LocalDate reportDate, Locale locale) {
    if (locale.equals(Locale.GERMAN))
      webDriverHelpers.clearAndFillInWebElement(REPORT_DATE, DATE_FORMATTER_DE.format(reportDate));
    else webDriverHelpers.clearAndFillInWebElement(REPORT_DATE, formatter.format(reportDate));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
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
    System.out.println(webDriverHelpers.getValueFromWebElement(UUID_INPUT));
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
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .build();
  }

  private TravelEntry collectTravelEntryDifferentPointOfEntryJurisdictionData() {
    return TravelEntry.builder()
        .pointOfEntryRegion(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.POINT_OF_ENTRY_REGION))
        .pointOfEntryDistrict(
            webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.POINT_OF_ENTRY_DISTRICT))
        .build();
  }

  private TravelEntry collectTravelEntryUuid() {
    return TravelEntry.builder().uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT)).build();
  }

  private String collectTravelEntryPersonUuid() {
    return webDriverHelpers.getValueFromWebElement(UUID_INPUT);
  }

  private TravelEntry collectTravelEntryPersonData() {
    return TravelEntry.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_INPUT))
        .sex(webDriverHelpers.getValueFromCombobox(EditTravelEntryPage.SEX_COMBOBOX))
        .personUuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
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
