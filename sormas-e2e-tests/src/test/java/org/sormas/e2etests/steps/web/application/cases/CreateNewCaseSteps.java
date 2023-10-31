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

import static org.sormas.e2etests.constants.api.Endpoints.CASES_PATH;
import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_APPLY_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_PRESENT_CONDITION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_RESET_FILTERS_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_POPUP;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CONFIRM_YOUR_CHOICE_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ERROR_MESSAGE_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.FIRST_CASE_ID_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MERGE_DUPLICATED_CASES_WARNING_POPUP_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.MERGE_MESSAGE_HEADER_DE;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.SHOW_MORE_LESS_FILTERS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getMergeButtonForCaseForSourceSystem;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.getMergeButtonForCaseForTargetSystem;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_DISEASE_VARIANT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_DOCUMENT_EMPTY_TEXT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_ORIGIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_UPLOADED_TEST_FILE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONFIRM_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONTACT_CASE_POPUP_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_CASE_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CREATE_NEW_CASE_CONFIRMATION_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_SYMPTOM_ONSET_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DELETE_LAST_UPDATED_CASE_DOCUMENT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DIFFERENT_PLACE_OF_STAY_CHECKBOX_LABEL;
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
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_NO_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_NO_POPUP_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LINE_LISTING_DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NAME_UUID_EXTERNAL_ID_TOKEN_LIKE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NATIONAL_HEALTH_ID_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NEW_DOCUMENT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.NICKNAME_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PASSPORT_NUMBER_ATTRIBUTE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_CASE_WINDOW_SEARCH_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PERSON_SEARCH_LOCATOR_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PICK_AN_EXISTING_CASE_CONFIRMATION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY_LABEL;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_DETAILS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_DISTRICT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.POINT_OF_ENTRY_REGION_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRESENT_CONDITION_OF_PERSON_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRIMARY_EMAIL_ADDRESS_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRIMARY_PHONE_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.REINFECTION_SPAN;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_JURISDICTION_LABEL;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SELECT_EXISTING_CASE_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SELECT_MATCHING_PERSON_CHECKBOX_DE;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SELECT_PERSON_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.START_DATA_IMPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_THE_CASES_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_CASE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISCARD_BUTTON_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_CASE_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_TITLE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SELECT_MATCHING_PERSON_CHECKBOX;
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
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.DISCARD_TASK_BUTTON;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.NEW_PERSON_RADIOBUTTON_DE;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PICK_OR_CREATE_PERSON_HEADER_DE;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.SEARCH_PERSON_BY_FREE_TEXT;
import static org.sormas.e2etests.pages.application.shares.EditSharesPage.SHARE_UUID_CASE_TITLE;
import static org.sormas.e2etests.steps.web.application.cases.EditCaseSteps.aCase;
import static org.sormas.e2etests.steps.web.application.contacts.CreateNewContactSteps.contact;
import static org.sormas.e2etests.steps.web.application.persons.PersonDirectorySteps.personSharedForAllEntities;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import io.restassured.http.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.files.FilesHelper;
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
  public static Case oneCaseDe;
  public static Case survnetCase;
  public static final String userDirPath = System.getProperty("user.dir");
  public static List<String> casesUUID = new ArrayList<>();
  private static String currentUrl;
  private static String phoneNumber;
  private final RestAssuredClient restAssuredClient;

  @SneakyThrows
  @Inject
  public CreateNewCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
      ApiState apiState,
      Faker faker,
      SoftAssert softly,
      BaseSteps baseSteps,
      RestAssuredClient restAssuredClient) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.softly = softly;
    this.baseSteps = baseSteps;
    this.restAssuredClient = restAssuredClient;
    Random r = new Random();
    char c = (char) (r.nextInt(26) + 'a');
    String firstName = faker.name().firstName() + c;
    String lastName = faker.name().lastName() + c;
    String personSex = GenderValues.getRandomGenderDE();
    LocalDate dateOfBirth =
        LocalDate.of(
            faker.number().numberBetween(1900, 2002),
            faker.number().numberBetween(1, 12),
            faker.number().numberBetween(1, 27));
    String randomUUID_first_user = generateShortUUID();
    String randomUUID_second_user = generateShortUUID();

    oneCase = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
    oneCase = oneCase.toBuilder().disease("COVID-19").build();

    oneCaseDe = caseService.buildGeneratedCaseForOnePersonDE(firstName, lastName, dateOfBirth);
    oneCaseDe = oneCaseDe.toBuilder().disease("COVID-19").build();

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
        "I fill new case data for duplicates merge with for one person data for DE",
        () -> {
          selectCaseOrigin(oneCaseDe.getCaseOrigin());
          fillDisease(oneCaseDe.getDisease());
          selectResponsibleRegion(oneCaseDe.getResponsibleRegion());
          selectResponsibleDistrict(oneCaseDe.getResponsibleDistrict());
          selectPlaceOfStay(oneCaseDe.getPlaceOfStay());
          fillFirstName(oneCaseDe.getFirstName());
          fillLastName(oneCaseDe.getLastName());
          fillDateOfBirth(oneCaseDe.getDateOfBirth(), Locale.GERMAN);
          selectSex(oneCaseDe.getSex());
          fillDateOfReport(oneCaseDe.getDateOfReport(), Locale.GERMAN);
        });

    When(
        "I fill new case with for one person with specified date for month ago",
        () -> {
          LocalDate date = LocalDate.now().minusMonths(1);
          caze = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(randomUUID_first_user);
          fillEpidNumber(randomUUID_first_user);
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
          // fillExternalId(randomUUID_second_user);
          fillEpidNumber(randomUUID_second_user);
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

    // TODO this method should be refactored since it has 2 logics inside
    When(
        "I select ([^\"]*) created case for person from Cases list",
        (String option) -> {
          if (option.equals("first")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(0));
            TimeUnit.SECONDS.sleep(3);
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
        "I check if created case is available in API",
        () -> {
          getCaseByUUID(casesUUID.get(0));
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

    Then(
        "I create a new case with specific data and ([^\"]*) region",
        (String region) -> {
          caze = caseService.buildGeneratedCase();
          fillAllCaseFieldsWithSpecificRegion(caze, region);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(
              CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When("I choose {string} as a disease", (String disease) -> fillDisease(disease));

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
        "^I fill new case form with chosen data without personal data on Case directory page for DE$",
        () -> {
          caze = caseService.buildGeneratedCaseDE();
          selectCaseOrigin(caze.getCaseOrigin());
          // field that is no longer available
          // fillExternalId(caze.getExternalId());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.GERMAN);
          fillPrimaryPhoneNumber(caze.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(caze.getPrimaryEmailAddress());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
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
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EXTERNAL_ID_TOKEN_LIKE, aCase.getFirstName() + " " + aCase.getLastName());
          webDriverHelpers.clickOnWebElementBySelector(PERSON_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    When(
        "^I search for the person data shared across all entities by First Name and Last Name in popup on Select Person window$",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EXTERNAL_ID_TOKEN_LIKE,
              personSharedForAllEntities.getFirstName()
                  + " "
                  + personSharedForAllEntities.getLastName());
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
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.clickOnWebElementBySelector(SELECT_PERSON_WINDOW_CONFIRM_BUTTON);
        });

    When(
        "^I click on Save button in Case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 3)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          }
          TimeUnit.SECONDS.sleep(1);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });
    When(
        "^I check if National Health Id, Nickname and Passport number do not appear in Pick or create person popup$",
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
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_CASE_CONFIRMATION_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "^I pick an existing case in pick or create a case popup$",
        () -> {
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
        "^I pick a new person in Pick or create person popup during case creation for DE$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_A_NEW_PERSON_CONFIRMATION_BUTTON_DE);
          TimeUnit.SECONDS.sleep(2);
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
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
            if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_CASE_POPUP_HEADER, 1)) {
              webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CHECKBOX);
              webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            }
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
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
          String file = "testCase_" + fileType + "." + fileType;
          FilesHelper.waitForFileToDownload(file, 40);
          FilesHelper.deleteFile(file);
        });
    When(
        "^I check that ([^\"]*) is not visible in Person search popup$",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          String selector =
              "//div[@class='popupContent']//div[contains(@class,'v-grid-column-header-content') and text()=\"%s\"]";
          Boolean elementVisible = true;
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(
                By.xpath(String.format(selector, option)));
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

    When(
        "I check if first and last person name for case person tab is correct",
        () -> {
          softly.assertEquals(
              caze.getFirstName(),
              webDriverHelpers.getValueFromWebElement(FIRST_NAME_NO_POPUP_INPUT),
              "First names are not equal");
          softly.assertEquals(
              caze.getLastName(),
              webDriverHelpers.getValueFromWebElement(LAST_NAME_NO_POPUP_INPUT),
              "First names are not equal");
          softly.assertAll();
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
        });

    When(
        "I filter with last created case using case UUID",
        () -> {
          webDriverHelpers.fillInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, casesUUID.get(0));
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "^I fill only mandatory fields for a new case form$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
        });

    And(
        "^I fill only mandatory fields for a new case form for DE$",
        () -> {
          LocalDate reportDate = LocalDate.now().minusDays(2);
          caze =
              caseService.buildGeneratedCaseDEForOnePerson(
                  firstName, lastName, dateOfBirth, reportDate, personSex);
          selectCaseOrigin(caze.getCaseOrigin());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
        });

    And(
        "I fill only mandatory fields to convert a contact into a case for DE",
        () -> {
          fillDateOfReport(LocalDate.now(), Locale.GERMAN);
          selectPlaceOfStay("ZUHAUSE");
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
        });

    And(
        "^I fill only mandatory fields to convert laboratory message into a case for DE$",
        () -> {
          LocalDate reportDate = LocalDate.now().minusDays(2);
          selectResponsibleRegion("Hamburg");
          selectResponsibleDistrict("SK Hamburg");
          selectPlaceOfStay("ZUHAUSE");
          fillDateOfReport(reportDate, Locale.GERMAN);
        });

    And(
        "^I click SAVE button on Create New Case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(NEW_PERSON_RADIOBUTTON_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
        });

    And(
        "^I create a new case with specific mandatory fields with saved person details from contact for DE$",
        () -> {
          selectResponsibleRegion(contact.getResponsibleRegion());
          selectResponsibleDistrict(contact.getResponsibleDistrict());
          selectPlaceOfStay("ZUHAUSE");
          fillFirstName(contact.getFirstName());
          fillLastName(contact.getLastName());
          selectSex(contact.getSex());
          fillDateOfReport(LocalDate.now().minusDays(189), Locale.GERMAN);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I click on Discard button in Create New Case form",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_TASK_BUTTON);
        });

    Then(
        "^I check if place of stay is split to responsible jurisdiction and place of stay$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_JURISDICTION_LABEL);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(PLACE_OF_STAY);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(RESPONSIBLE_JURISDICTION_LABEL),
              "Responsible jurisdiction");
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_LABEL), "Place of stay");
          softly.assertAll();
        });

    And(
        "^I fill new case form without epid number$",
        () -> {
          caze = caseService.buildGeneratedCase();
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
          fillPlaceDescription(caze.getPlaceDescription());
        });

    And(
        "^I click on Place of stay of this case differs from its responsible jurisdiction in New case form$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DIFFERENT_PLACE_OF_STAY_CHECKBOX_LABEL);
        });

    And(
        "^I fill new case form with different place of stay region and district$",
        () -> {
          caze = caseService.buildGeneratedCaseWithDifferentPlaceOfStay();
          selectCaseOrigin(caze.getCaseOrigin());
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStayRegion(caze.getPlaceOfStayRegion());
          selectPlaceOfStayDistrict(caze.getPlaceOfStayDistrict());
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

    And(
        "^I create a new case with different place of stay and Facility as a Place of stay$",
        () -> {
          caze = caseService.buildCaseWithFacilityAndDifferentPlaceOfStay();
          fillAllCaseFieldsForFacilityAndDifferentPlaceOfStay(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    And(
        "^I set Place of stay to ([^\"]*) in New case form$",
        (String option) -> {
          selectPlaceOfStay(option);
        });

    And(
        "I check that {string} option is available in Facility dropdown",
        (String facilityOption) -> {
          webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facilityOption);
        });

    And(
        "I set Responsible region to {string} and District to {string}",
        (String aRegion, String aDistrict) -> {
          selectResponsibleRegion(aRegion);
          selectResponsibleDistrict(aDistrict);
        });

    And(
        "I set Place of stay region to {string} and Place of stay district to {string}",
        (String aRegion, String arDistrict) -> {
          selectPlaceOfStayRegion(aRegion);
          selectPlaceOfStayDistrict(arDistrict);
        });

    And(
        "I check that {string} option is available in Point of entry dropdown",
        (String pointOfEntryOption) -> {
          webDriverHelpers.selectFromCombobox(POINT_OF_ENTRY_COMBOBOX, pointOfEntryOption);
        });

    And(
        "^I create a new case with different place of stay and Point of entry as a Case origin$",
        () -> {
          caze = caseService.buildCaseWithPointOfEntryAndDifferentPlaceOfStay();
          fillAllCaseFieldsForPointOfEntryAndDifferentPlaceOfStay(caze);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    And(
        "I fill only mandatory fields for a new case form and set {string} as a last name",
        (String surname) -> {
          caze = caseService.buildGeneratedCaseForOnePerson(firstName, surname, dateOfBirth);
          selectCaseOrigin(caze.getCaseOrigin());
          fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
          fillDisease(caze.getDisease());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          selectSex(caze.getSex());
        });

    When(
        "I check if received case id is equal with sent",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(SHARE_UUID_CASE_TITLE),
              casesUUID.get(0).substring(0, 6).toUpperCase(),
              "UUIDs are not equal");
          softly.assertAll();
        });

    And(
        "^I choose create new case in Pick or create entry form for DE$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_CASE_CONFIRMATION_BUTTON_DE);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CONFIRM_BUTTON_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_BUTTON_POPUP);
        });

    And(
        "^I click on SAVE new case button and choose same person in duplicate detection$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(SELECT_MATCHING_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_HEADER_DE, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(SELECT_MATCHING_PERSON_CHECKBOX_DE);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
        });

    And(
        "^I choose same case in duplicate detection and save for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SELECT_EXISTING_CASE_DE);
          webDriverHelpers.clickOnWebElementBySelector(SELECT_EXISTING_CASE_DE);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
        });

    When(
        "I create a new case with specific person name and {string} region and {string} district for DE version",
        (String reg, String disctr) -> {
          LocalDate reportDate = LocalDate.now();
          caze =
              caseService.buildGeneratedCaseDEForOnePerson(
                  firstName, lastName, dateOfBirth, reportDate, personSex);
          selectCaseOrigin(caze.getCaseOrigin());
          fillDisease(caze.getDisease());
          fillDiseaseVariant(caze.getDiseaseVariant());
          selectResponsibleRegion(reg);
          selectResponsibleDistrict(disctr);
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.GERMAN);
          selectSex(caze.getSex());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
        });

    When(
        "I create a new case with mandatory data with person name and {string} region and {string} district for DE version",
        (String reg, String disctr) -> {
          LocalDate reportDate = LocalDate.now();
          caze =
              caseService.buildGeneratedCaseDEForOnePerson(
                  firstName, lastName, dateOfBirth, reportDate, personSex);
          fillDisease(caze.getDisease());
          selectResponsibleRegion(reg);
          selectResponsibleDistrict(disctr);
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          selectSex(caze.getSex());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
        });

    When(
        "I click on Merge button for target system from received case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getMergeButtonForCaseForTargetSystem(firstName, lastName));
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeButtonForCaseForTargetSystem(firstName, lastName));
        });

    When(
        "I click on Merge button for source system from received case",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getMergeButtonForCaseForSourceSystem(firstName, lastName));
        });

    When(
        "I confirm merge duplicated case",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(CONFIRM_YOUR_CHOICE_HEADER_DE, 5));
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_POPUP);
        });

    When(
        "I check if popup with merge duplicated case appears",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  MERGE_DUPLICATED_CASES_WARNING_POPUP_DE, 30));
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(MERGE_DUPLICATED_CASES_WARNING_POPUP_DE);
        });

    When(
        "I check if popup with error message appears",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_MESSAGE_HEADER_DE, 5));
          softly.assertAll();
        });

    When(
        "I check if popup with merge message in german appears",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(MERGE_MESSAGE_HEADER_DE, 60),
              "element was not visible");
          softly.assertAll();
        });

    When(
        "I click on cancel button in merge duplicated cases popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL);
        });

    And(
        "I fill a new case form for DE version with mandatory data with {string} as a region and {string} as a district",
        (String region, String district) -> {
          caze = caseService.buildGeneratedCaseDE();
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          selectSex(caze.getSex());
          selectResponsibleRegion(region);
          selectResponsibleDistrict(district);
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillDisease(caze.getDisease());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
        });

    And(
        "I fill a new case form for DE version with mandatory data forced by positive sample with {string} as a region and {string} as a district",
        (String region, String district) -> {
          caze = caseService.buildGeneratedCaseDE();
          selectResponsibleRegion(region);
          selectResponsibleDistrict(district);
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
        });

    When(
        "I search for the last created case uuid in the CHOOSE SOURCE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(SOURCE_CASE_WINDOW_CASE_INPUT, casesUUID.get(0));
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
        });

    And(
        "^I create a new case with mandatory data only for Survnet DE$",
        () -> {
          survnetCase = caseService.buildCaseForSurvnetFeature();
          fillDateOfReport(survnetCase.getDateOfReport(), Locale.GERMAN);
          selectResponsibleRegion(survnetCase.getResponsibleRegion());
          selectResponsibleDistrict(survnetCase.getResponsibleDistrict());
          selectPlaceOfStay(survnetCase.getPlaceOfStay());
          fillFirstName(survnetCase.getFirstName());
          fillLastName(survnetCase.getLastName());
          selectSex(survnetCase.getSex());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    And(
        "^I create a new case with specific data using created facility for Survnet DE$",
        () -> {
          survnetCase = caseService.buildCaseWithFacilitiesForSurvnetFeature();
          fillDateOfReport(survnetCase.getDateOfReport(), Locale.GERMAN);
          selectResponsibleRegion(survnetCase.getResponsibleRegion());
          selectResponsibleDistrict(survnetCase.getResponsibleDistrict());
          selectPlaceOfStay(survnetCase.getPlaceOfStay());
          selectFacilityCategory(survnetCase.getFacilityCategory());
          selectFacilityType(survnetCase.getFacilityType());
          selectFacility(survnetCase.getFacility());
          fillFirstName(survnetCase.getFirstName());
          fillLastName(survnetCase.getLastName());
          selectSex(survnetCase.getSex());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    And(
        "^I create a new case with mandatory data only with Reinfection option for Survnet DE$",
        () -> {
          survnetCase = caseService.buildCaseForSurvnetFeatureWithReinfection();
          fillDateOfReport(survnetCase.getDateOfReport(), Locale.GERMAN);
          selectResponsibleRegion(survnetCase.getResponsibleRegion());
          selectResponsibleDistrict(survnetCase.getResponsibleDistrict());
          selectReinfection(survnetCase.getReinfection());
          selectPlaceOfStay(survnetCase.getPlaceOfStay());
          fillFirstName(survnetCase.getFirstName());
          fillLastName(survnetCase.getLastName());
          selectSex(survnetCase.getSex());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    And(
        "^I create a new case with mandatory data only and specific sex for Survnet DE$",
        () -> {
          survnetCase = caseService.buildCaseForSurvnetFeatureXMLCheck();
          fillDateOfReport(survnetCase.getDateOfReport(), Locale.GERMAN);
          selectResponsibleRegion(survnetCase.getResponsibleRegion());
          selectResponsibleDistrict(survnetCase.getResponsibleDistrict());
          selectPlaceOfStay(survnetCase.getPlaceOfStay());
          fillFirstName(survnetCase.getFirstName());
          fillLastName(survnetCase.getLastName());
          selectSex(survnetCase.getSex());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I create a new case with specific data and report date set to yesterday for DE version$",
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
          fillDateOfReport(LocalDate.now(), Locale.GERMAN);
          fillPlaceDescription(caze.getPlaceDescription());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    And(
        "^I create a new case with mandatory data only and birth date for Survnet DE$",
        () -> {
          survnetCase = caseService.buildCaseForSurvnetFeatureWithDateOfBirth();
          fillDateOfReport(survnetCase.getDateOfReport(), Locale.GERMAN);
          selectResponsibleRegion(survnetCase.getResponsibleRegion());
          selectResponsibleDistrict(survnetCase.getResponsibleDistrict());
          selectPlaceOfStay(survnetCase.getPlaceOfStay());
          fillFirstName(survnetCase.getFirstName());
          fillLastName(survnetCase.getLastName());
          selectSex(survnetCase.getSex());
          fillDateOfBirth(survnetCase.getDateOfBirth(), Locale.GERMAN);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });
  }

  private void selectPlaceOfStayDistrict(String placeOfStayDistrict) {
    webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_DISTRICT_COMBOBOX, placeOfStayDistrict);
  }

  private void selectPlaceOfStayRegion(String placeOfStayRegion) {
    webDriverHelpers.selectFromCombobox(PLACE_OF_STAY_REGION_COMBOBOX, placeOfStayRegion);
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

  private void selectReinfection(String reinfection) {
    webDriverHelpers.clickWebElementByText(REINFECTION_SPAN, reinfection);
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

  private void fillAllCaseFieldsWithSpecificRegion(Case caze, String region) {
    selectCaseOrigin(caze.getCaseOrigin());
    fillDisease(caze.getDisease());
    switch (region) {
      case "Bayern":
        selectResponsibleRegion("Bayern");
        selectResponsibleDistrict("LK Ansbach");
        selectResponsibleCommunity("Aurach");
        break;
      case "Saarland":
        selectResponsibleRegion("Saarland");
        selectResponsibleDistrict("LK Saarlouis");
        selectResponsibleCommunity("Lebach");
        break;
      case "Berlin":
        selectResponsibleRegion("Berlin");
        selectResponsibleDistrict("SK Berlin Charlottenburg-Wilmersdorf");
        selectResponsibleCommunity("Charlottenburg-Nord");
        break;
      case "Baden-W\u00FCrttemberg":
        selectResponsibleRegion("Baden-W\u00FCrttemberg");
        selectResponsibleDistrict("LK Alb-Donau-Kreis");
        selectResponsibleCommunity("Allmendingen");
        break;
    }
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

  private void fillAllCaseFieldsForFacilityAndDifferentPlaceOfStay(Case caze) {
    selectCaseOrigin(caze.getCaseOrigin());
    fillDisease(caze.getDisease());
    selectResponsibleRegion(caze.getResponsibleRegion());
    selectResponsibleDistrict(caze.getResponsibleDistrict());
    selectPlaceOfStay(caze.getPlaceOfStay());
    selectPlaceOfStayRegion(caze.getPlaceOfStayRegion());
    selectPlaceOfStayDistrict(caze.getPlaceOfStayDistrict());
    fillFirstName(caze.getFirstName());
    fillLastName(caze.getLastName());
    fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
    selectSex(caze.getSex());
    fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
    selectFacility(caze.getFacility());
  }

  private void fillAllCaseFieldsForPointOfEntryAndDifferentPlaceOfStay(Case caze) {
    selectCaseOrigin(caze.getCaseOrigin());
    fillDisease(caze.getDisease());
    selectResponsibleRegion(caze.getResponsibleRegion());
    selectResponsibleDistrict(caze.getResponsibleDistrict());
    selectPlaceOfStayRegion(caze.getPlaceOfStayRegion());
    selectPlaceOfStayDistrict(caze.getPlaceOfStayDistrict());
    fillFirstName(caze.getFirstName());
    fillLastName(caze.getLastName());
    fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
    selectSex(caze.getSex());
    fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
    selectPointOfEntry(caze.getPointOfEntry());
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

  @SneakyThrows
  public void getCaseByUUID(String caseUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(CASES_PATH + "/" + caseUUID).build());
  }
}
