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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_DISEASE_VARIANT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_DOCUMENT_EMPTY_TEXT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_ORIGIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_UPLOADED_TEST_FILE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONFIRM_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONTACT_CASE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_CASE_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_SYMPTOM_ONSET_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DELETE_LAST_UPDATED_CASE_DOCUMENT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DIFFERENT_POINT_OF_ENTRY_JURISDICTION;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_VARIANT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DOWNLOAD_LAST_UPDATED_CASE_DOCUMENT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ENTER_HOME_ADDRESS_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.EPID_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FACILITY_CATEGORY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FACILITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FACILITY_TYPE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NATIONAL_HEALTH_ID_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NATIONAL_HEALTH_ID_POPUP_TABLE_HEADER;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NEW_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NICKNAME_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NICKNAME_POPUP_TABLE_HEADER;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PASSPORT_NUMBER_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PASSPORT_NUMBER_POPUP_TABLE_HEADER;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_SEARCH_LOCATOR_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PICK_AN_EXISTING_CASE_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_DETAILS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_DISTRICT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_REGION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRESENT_CONDITION_OF_PERSON_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRIMARY_EMAIL_ADDRESS_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRIMARY_PHONE_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SELECT_PERSON_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SELECT_PERSON_WINDOW_CONFIRM_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_THE_CASES_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_CASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISCARD_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_CASE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_TITLE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.FollowUpTabPage.CONTACT_PERSONS_PHONE_NUMBER;
import static org.sormas.e2etests.pages.application.configuration.DocumentTemplatesPage.FILE_PICKER;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_FIRST_TABLE_ROW;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_TABLE_DATA;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_CASE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_CASE_LABEL_DE;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_PERSON_LABEL_DE;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.SEARCH_PERSON_BY_FREE_TEXT;
import static org.sormas.e2etests.steps.web.application.cases.EditCaseSteps.aCase;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.state.ApiState;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class CreateNewCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;
  private final Faker faker;
  private final SoftAssert softly;
  private static BaseSteps baseSteps;
  protected static Case oneCase;
  public static final String userDirPath = System.getProperty("user.dir");
  public static List<String> casesUUID = new ArrayList<>();
  private static String currentUrl;
  private static String phoneNumber;

  @Inject
  public CreateNewCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      ApiState apiState,
      Faker faker,
      SoftAssert softly,
      BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.softly = softly;
    this.baseSteps = baseSteps;
    Random r = new Random();
    char c = (char) (r.nextInt(26) + 'a');
    String firstName = faker.name().firstName() + c;
    String lastName = faker.name().lastName() + c;
    LocalDate dateOfBirth =
        LocalDate.of(
            faker.number().numberBetween(1900, 2002),
            faker.number().numberBetween(1, 12),
            faker.number().numberBetween(1, 27));
    UUID randomUUID_first_user = UUID.randomUUID();
    UUID randomUUID_second_user = UUID.randomUUID();

    oneCase = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
    oneCase = oneCase.toBuilder().disease("COVID-19").build();

    When(
        "I fill new case data for duplicates merge with for one person data",
        () -> {
          selectCaseOrigin(oneCase.getCaseOrigin());
          fillDisease(oneCase.getDisease());
          selectResponsibleRegion(oneCase.getResponsibleRegion());
          selectResponsibleDistrict(oneCase.getResponsibleDistrict());
          selectResponsibleCommunity(oneCase.getResponsibleCommunity());
          selectPlaceOfStay(oneCase.getPlaceOfStay());
          fillFirstName(oneCase.getFirstName());
          fillLastName(oneCase.getLastName());
          fillDateOfBirth(oneCase.getDateOfBirth(), Locale.ENGLISH);
          selectSex(oneCase.getSex());
          selectPresentConditionOfPerson(oneCase.getPresentConditionOfPerson());
          fillDateOfReport(oneCase.getDateOfReport(), Locale.ENGLISH);
        });

    When(
        "I fill new case with for one person with specified date for month ago",
        () -> {
          LocalDate date = LocalDate.now().minusMonths(1);
          caze = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(randomUUID_first_user.toString());
          fillEpidNumber(randomUUID_first_user.toString());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
          selectSex("Male");
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(date, Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "I fill second new case with for one person with specified date for present day",
        () -> {
          LocalDate date = LocalDate.now();
          caze = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(randomUUID_second_user.toString());
          fillEpidNumber(randomUUID_second_user.toString());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
          selectSex("Male");
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(date, Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "I confirm Pick person in Case",
        () -> {
          String expectedTitle = "Pick or create person";
          String checkPopupTitle =
              webDriverHelpers.getTextFromWebElement(PICK_OR_CREATE_PERSON_TITLE);
          softly.assertEquals(
              checkPopupTitle, expectedTitle, "Wrong popup title for Pick or create person");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
        });

    When(
        "I filter Cases by created person name",
        () -> {
          webDriverHelpers.fillInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, firstName + " " + lastName);
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I filter for SAMPLE TOKEN in Cases Directory",
        () -> {
          webDriverHelpers.fillInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, "SAMPLE TOKEN");
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I collect uuid of the case",
        () -> {
          casesUUID.add(webDriverHelpers.getValueFromWebElement(UUID_INPUT));
        });

    When(
        "I select ([^\"]*) created case for person from Cases list",
        (String option) -> {
          if (option.equals("first")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(0));
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          if (option.equals("second")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(1));
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON);
        });

    When(
        "I reset filter from Case Directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CASE_RESET_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(2); // waiting for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.clickOnWebElementBySelector(SHOW_MORE_LESS_FILTERS);
        });

    When(
        "I confirm changes in selected Case",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I back to the cases list from edit case",
        () -> {
          webDriverHelpers.scrollToElement(BACK_TO_THE_CASES_LIST_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(BACK_TO_THE_CASES_LIST_BUTTON);
        });

    When(
        "I filter by ([^\"]*) user condition",
        (String userCondition) -> {
          if (userCondition.equals("unspecified")) {
            webDriverHelpers.selectFromCombobox(CASE_PRESENT_CONDITION_COMBOBOX, "");
          } else {
            webDriverHelpers.selectFromCombobox(CASE_PRESENT_CONDITION_COMBOBOX, userCondition);
          }
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(2); // Wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I filter with ([^\"]*) Case ID",
        (String option) -> {
          if (option.equals("first")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(0));
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          if (option.equals("second")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(1));
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
        });

    When(
        "I check if created person is on filtered list with ([^\"]*) status",
        (String status) -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedCasesTableRow = tableRowsData.get(0);
          softly.assertEquals(
              detailedCasesTableRow.get(CaseDetailedTableViewHeaders.OUTCOME_OF_CASE.toString()),
              status,
              "Outcome of case status is invalid");
          softly.assertAll();
        });

    When(
        "I filter Persons by created person name in cases",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SEARCH_PERSON_BY_FREE_TEXT);
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_PERSON_BY_FREE_TEXT, firstName + " " + lastName);
          TimeUnit.SECONDS.sleep(3); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I search for the last case uuid created by UI in the CHOOSE SOURCE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(SOURCE_CASE_WINDOW_CASE_INPUT, aCase.getUuid());
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    When(
        "I create a new case with specific data for DE version",
        () -> {
          caze = caseService.buildGeneratedCaseDE();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          fillDiseaseVariant(caze.getDiseaseVariant());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.GERMAN);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.GERMAN);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I create a new case with Point Of Entry for DE version$",
        () -> {
          caze = caseService.buildGeneratedCaseWithPointOfEntryDE();
          selectCaseOrigin(caze.getCaseOrigin());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectPointOfEntryRegion(caze.getPointOfEntryRegion());
          selectPointOfEntryDistrict(caze.getPointOfEntryDistrict());
          selectPointOfEntry(caze.getPointOfEntry());
          fillPointOfEntryDetails(caze.getPointOfEntryDetails());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.GERMAN);
          selectSex(caze.getSex());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAVE_BUTTON);
        });

    When(
        "I select {string} as a Case Origin in Case Popup",
        (String option) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CASE_ORIGIN_OPTIONS);
          webDriverHelpers.clickWebElementByText(CASE_ORIGIN_OPTIONS, option);
        });

    When(
        "I create a new case with only the required data for DE version",
        () -> {
          caze = caseService.buildGeneratedCaseDE();
          selectCaseOrigin(caze.getCaseOrigin());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          selectSex(caze.getSex());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I create a new case with specific data for positive pathogen test result",
        () -> {
          caze = caseService.buildEditGeneratedCaseForPositivePathogenTestResult();
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
        });

    When(
        "^I save the new case$",
        () -> {
          webDriverHelpers.scrollToElement(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I save a new case",
        () -> {
          webDriverHelpers.scrollToElement(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I confirm creating a new case$",
        () -> {
          webDriverHelpers.scrollToElement(ACTION_CONFIRM_POPUP_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I create a new case with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillAllCaseFields(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);

          TimeUnit.SECONDS.sleep(2);
        });

    When(
        "^I create a new case with Facility as a Place of stay$",
        () -> {
          caze = caseService.buildGeneratedCaseWithFacility();
          fillAllCaseFieldsForFacility(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I create a new case with specific data and new person$",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillAllCaseFields(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(
              CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);

          TimeUnit.SECONDS.sleep(2);
        });

    When(
        "I create a new case with disease {string}",
        (String caseDisease) -> {
          caze = caseService.buildCaseWithDisease(caseDisease);
          fillAllCaseFields(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
            if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_CASE_POPUP_HEADER, 1)) {
              webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            }
          }
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I fill new case form with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(caze.getExternalId());
          fillEpidNumber(caze.getEpidNumber());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
        });
    When(
        "I set Place of stay to {string}, Facility Category to {string} and  Facility Type to {string} in Case creation",
        (String placeOfStay, String facilityCategory, String facilityType) -> {
          selectPlaceOfStay(placeOfStay);
          selectFacilityCategory(facilityCategory);
          selectFacilityType(facilityType);
          selectFacility("Other facility");
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "^I fill new case form with chosen data without personal data on Case directory page$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "^I click on the clear button in new add new event participant form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });

    When(
        "^I click on the person search button in new case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });
    When(
        "^I click on the clear button in new case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PERSON_SEARCH_LOCATOR_BUTTON);
        });

    When(
        "^I search for the last created person via Api by uuid in popup on Select Person window$",
        () -> {
          webDriverHelpers.fillInWebElement(
              UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT,
              apiState.getLastCreatedPerson().getUuid());
          webDriverHelpers.clickOnWebElementBySelector(PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    When(
        "^I search for the last created person by First Name and Last Name in popup on Select Person window$",
        () -> {
          webDriverHelpers.fillInWebElement(FIRST_NAME_LIKE_INPUT, aCase.getFirstName());
          webDriverHelpers.fillInWebElement(LAST_NAME_LIKE_INPUT, aCase.getLastName());
          webDriverHelpers.clickOnWebElementBySelector(PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    When(
        "^I open the first found result in the popup of Select Person window$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SELECT_PERSON_WINDOW_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SELECT_PERSON_WINDOW_CONFIRM_BUTTON);
        });

    When(
        "^I open the first found result in the popup of Select Person window for DE version$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SELECT_PERSON_WINDOW_CONFIRM_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(SELECT_PERSON_WINDOW_CONFIRM_BUTTON_DE);
        });

    When(
        "^I click on Save button in Case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          //   webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });
    When(
        "^I check if National Health Id, Nickname and Passport number appear in Pick or create person popup$",
        () -> {
          softly.assertEquals(
              false, webDriverHelpers.isElementVisibleWithTimeout(NICKNAME_ATTRIBUTE, 2));
          softly.assertEquals(
              false, webDriverHelpers.isElementVisibleWithTimeout(PASSPORT_NUMBER_ATTRIBUTE, 2));
          softly.assertEquals(
              false, webDriverHelpers.isElementVisibleWithTimeout(NATIONAL_HEALTH_ID_ATTRIBUTE, 2));
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(PICK_A_EXISTING_PERSON_LABEL_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_A_EXISTING_CASE_LABEL_DE, 4)) {
            webDriverHelpers.clickOnWebElementBySelector(PICK_A_EXISTING_CASE_LABEL_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          }
        });

    When(
        "^I pick a new case in pick or create a case popup$",
        () -> {
          // webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_CASE_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(PICK_AN_EXISTING_CASE_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "^I Pick an existing case in Pick or create person popup in Case entry$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_CASE_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I Pick a new person in Pick or create person popup during case creation$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "^I create a new case with specific data using line listing feature$",
        () -> {
          caze = caseService.buildCaseForLineListingFeature();

          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "^I create a new case for contact with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          selectCaseOrigin(caze.getCaseOrigin());
          //          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());

          selectPlaceOfStay(caze.getPlaceOfStay());
          fillPlaceDescription(caze.getPlaceDescription());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);

          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_POPUP_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I create a new case for contact with specific data for DE$",
        () -> {
          caze = caseService.buildGeneratedCaseDE();
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillPlaceDescription(caze.getPlaceDescription());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.GERMAN);
          webDriverHelpers.selectFromCombobox(
              CASE_DISEASE_VARIANT_COMBOBOX, caze.getDiseaseVariant());
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_POPUP_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    Then(
        "^I click on save case button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I fill all fields for a new case created for event participant$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          // field no longer available
          //          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
        });

    When(
        "^I click on Enter Home Address of the Case Person Now in the Create New Case popup$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ENTER_HOME_ADDRESS_CHECKBOX);
        });

    When(
        "I click on save button in the case popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for spinner
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "^I check if default disease value is set for ([^\"]*)$",
        (String disease) -> {
          String getDisease = webDriverHelpers.getValueFromCombobox(DISEASE_COMBOBOX);
          softly.assertEquals(disease, getDisease, "Diseases are not equal");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON_POPUP);
        });

    When(
        "^I check if default disease value in the Line listing is set for ([^\"]*)$",
        (String disease) -> {
          String getDisease = webDriverHelpers.getValueFromCombobox(LINE_LISTING_DISEASE_COMBOBOX);
          softly.assertEquals(disease, getDisease, "Diseases are not equal");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_DISCARD_BUTTON);
        });

    When(
        "I click on ([^\"]*) button from New document in case tab",
        (String buttonName) -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_DOCUMENT_BUTTON);
          webDriverHelpers.clickWebElementByText(START_DATA_IMPORT_BUTTON, buttonName);
        });

    When(
        "I upload ([^\"]*) file to the case",
        (String fileType) -> {
          webDriverHelpers.sendFile(
              FILE_PICKER, userDirPath + "/uploads/testCase_" + fileType + "." + fileType);
          TimeUnit.SECONDS.sleep(2); // wait for upload file
        });

    When(
        "I check if ([^\"]*) file is available in case documents",
        (String fileType) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              By.xpath(String.format(CASE_UPLOADED_TEST_FILE, fileType)), 5);
        });

    When(
        "I download last updated document file from case tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DOWNLOAD_LAST_UPDATED_CASE_DOCUMENT);
          TimeUnit.SECONDS.sleep(3); // wait for download
        });

    When(
        "I delete last uploaded document file from case tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DELETE_LAST_UPDATED_CASE_DOCUMENT);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
          TimeUnit.SECONDS.sleep(5); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if last uploaded file was deleted from document files in case tab",
        () -> {
          webDriverHelpers.checkWebElementContainsText(
              CASE_DOCUMENT_EMPTY_TEXT, "There are no documents for this Case");
        });

    When(
        "I check if ([^\"]*) file is downloaded correctly",
        (String fileType) -> {
          String file = "./downloads/testCase_" + fileType + "." + fileType;
          Path path = Paths.get(file);
          softly.assertTrue(Files.exists(path));
          softly.assertAll();
          Files.delete(path); // clean
        });
    When(
        "I check that ([^\"]*) is not visible in Person search popup",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          Boolean elementVisible = true;
          switch (option) {
            case "Passport Number":
              selector = PASSPORT_NUMBER_POPUP_TABLE_HEADER;
              break;
            case "National Health ID":
              selector = NATIONAL_HEALTH_ID_POPUP_TABLE_HEADER;
              break;
            case "Nickname":
              selector = NICKNAME_POPUP_TABLE_HEADER;
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

    Then(
        "^I check if Different Point Of Entry Jurisdiction checkbox appears$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              DIFFERENT_POINT_OF_ENTRY_JURISDICTION);
        });

    When(
        "^I select Different Point Of Entry Jurisdiction checkbox$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DIFFERENT_POINT_OF_ENTRY_JURISDICTION);
        });

    Then(
        "^I check if additional Point Of Entry fields appear$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(POINT_OF_ENTRY_REGION_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(POINT_OF_ENTRY_DISTRICT_BUTTON);
        });

    When(
        "I check if collected case UUID is equal with current",
        () -> {
          softly.assertEquals(
              casesUUID.get(0),
              webDriverHelpers.getValueFromWebElement(UUID_INPUT),
              "UUIDs of cases are not equal");
          softly.assertAll();
        });

    When(
        "I create new case with COVID-19 and variant {string}",
        (String variant) -> {
          caze = caseService.buildGeneratedCaseWithCovidVariant(variant);
          fillDisease(caze.getDisease());
          fillDiseaseVariant(caze.getDiseaseVariant());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When("I copy url of current case", () -> currentUrl = webDriverHelpers.returnURL());

    When("I back to deleted case by url", () -> webDriverHelpers.accessWebSite(currentUrl));

    When(
        "^I create a new case and save phone number$",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillAllCaseFields(caze);
          phoneNumber = caze.getPrimaryPhoneNumber();
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I check if phone number is displayed in Create new visit popup",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CONTACT_PERSONS_PHONE_NUMBER);
          softly.assertEquals(
              phoneNumber,
              webDriverHelpers.getTextFromPresentWebElement(CONTACT_PERSONS_PHONE_NUMBER),
              "Phone numbers are not equal");
          softly.assertAll();
        });

    When(
        "I create a new case with specific data for DE version with date {int} days ago",
        (Integer daysAgo) -> {
          caze = caseService.buildGeneratedCaseDEDaysAgo(daysAgo);
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          fillDiseaseVariant(caze.getDiseaseVariant());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.GERMAN);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.GERMAN);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I filter with last created case using case UUID",
        () -> {
          webDriverHelpers.fillInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(0));
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
  }

  private void fillPointOfEntryDetails(String pointOfEntryDetails) {
    webDriverHelpers.fillInWebElement(POINT_OF_ENTRY_DETAILS, pointOfEntryDetails);
  }

  private void selectPointOfEntry(String pointOfEntry) {
    webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_COMBOBOX, pointOfEntry);
  }

  private void selectPointOfEntryDistrict(String pointOfEntryDistrict) {
    webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_DISTRICT_BUTTON, pointOfEntryDistrict);
  }

  private void selectPointOfEntryRegion(String pointOfEntryRegion) {
    webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_REGION_BUTTON, pointOfEntryRegion);
  }

  private void selectCaseOrigin(String caseOrigin) {
    webDriverHelpers.clickWebElementByText(CASE_ORIGIN_OPTIONS, caseOrigin);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  private void fillEpidNumber(String epidNumber) {
    webDriverHelpers.fillInWebElement(EPID_NUMBER_INPUT, epidNumber);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void fillDiseaseVariant(String diseaseVariant) {
    webDriverHelpers.selectFromCombobox(DISEASE_VARIANT_COMBOBOX, diseaseVariant);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectFacilityCategory(String selectFacilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, selectFacilityCategory);
  }

  private void selectFacilityType(String selectFacilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, selectFacilityType);
  }

  private void selectFacility(String selectFacility) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, selectFacility);
  }

  private void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.clickWebElementByText(PLACE_OF_STAY, placeOfStay);
  }

  private void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate, Locale locale) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX, localDate.getMonth().getDisplayName(TextStyle.FULL, locale));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void selectPresentConditionOfPerson(String presentConditionOfPerson) {
    webDriverHelpers.selectFromCombobox(
        PRESENT_CONDITION_OF_PERSON_COMBOBOX, presentConditionOfPerson);
  }

  private void fillDateOfSymptomOnset(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_ONSET_INPUT, formatter.format(date));
  }

  private void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  private void fillDateOfSymptomOnsetDE(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_ONSET_INPUT, formatter.format(date));
  }

  private void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }

  private void fillAllCaseFields(Case caze) {
    selectCaseOrigin(caze.getCaseOrigin());
    // field that is no longer available
    // fillExternalId(caze.getExternalId());
    // new field
    // fillEpidNumber(caze.getEpidNumber());
    fillDisease(caze.getDisease());
    selectResponsibleRegion(caze.getResponsibleRegion());
    selectResponsibleDistrict(caze.getResponsibleDistrict());
    selectResponsibleCommunity(caze.getResponsibleCommunity());
    selectPlaceOfStay(caze.getPlaceOfStay());
    fillFirstName(caze.getFirstName());
    fillLastName(caze.getLastName());
    fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
    selectSex(caze.getSex());
    selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
    fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
    fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
    fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
    fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
    fillPlaceDescription(caze.getPlaceDescription());
  }

  private void fillAllCaseFieldsForFacility(Case caze) {
    selectCaseOrigin(caze.getCaseOrigin());
    fillDisease(caze.getDisease());
    selectResponsibleRegion(caze.getResponsibleRegion());
    selectResponsibleDistrict(caze.getResponsibleDistrict());
    selectResponsibleCommunity(caze.getResponsibleCommunity());
    selectPlaceOfStay(caze.getPlaceOfStay());
    fillFirstName(caze.getFirstName());
    fillLastName(caze.getLastName());
    fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
    selectSex(caze.getSex());
    selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
    fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);
    fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
    fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
    fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
    selectFacility(caze.getFacility());
    fillPlaceDescription(caze.getFacilityNameAndDescription());
  }

  private List<WebElement> getTableRows() {
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        CONTACTS_DETAILED_COLUMN_HEADERS);
    return baseSteps.getDriver().findElements(CONTACTS_DETAILED_FIRST_TABLE_ROW);
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        CONTACTS_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(CONTACTS_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(CONTACTS_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

  private List<Map<String, String>> getTableRowsData() {
    Map<String, Integer> headers = extractColumnHeadersHashMap();
    List<WebElement> tableRows = getTableRows();
    List<HashMap<Integer, String>> tableDataList = new ArrayList<>();
    tableRows.forEach(
        table -> {
          HashMap<Integer, String> indexWithData = new HashMap<>();
          AtomicInteger atomicInt = new AtomicInteger();
          List<WebElement> tableData = table.findElements(CONTACTS_DETAILED_TABLE_DATA);
          tableData.forEach(
              dataText -> {
                webDriverHelpers.scrollToElementUntilIsVisible(dataText);
                indexWithData.put(atomicInt.getAndIncrement(), dataText.getText());
              });
          tableDataList.add(indexWithData);
        });
    List<Map<String, String>> tableObjects = new ArrayList<>();
    tableDataList.forEach(
        row -> {
          ConcurrentHashMap<String, String> objects = new ConcurrentHashMap<>();
          headers.forEach((headerText, index) -> objects.put(headerText, row.get(index)));
          tableObjects.add(objects);
        });
    return tableObjects;
  }
}
