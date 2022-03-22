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
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.*;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CONTACT_CASE_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.BACK_TO_THE_CASES_LIST_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_TITLE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_FIRST_TABLE_ROW;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_TABLE_DATA;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_CASE_INPUT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.SEARCH_PERSON_BY_FREE_TEXT;
import static org.sormas.e2etests.steps.web.application.cases.EditCaseSteps.aCase;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
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
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class CreateNewCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;
  private final Faker faker;
  private final SoftAssert softly;
  private static BaseSteps baseSteps;

  @Inject
  public CreateNewCaseSteps(
      WebDriverHelpers webDriverHelpers,
      CaseService caseService,
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

    When(
        "I fill new case with for one person with specified date for month ago",
        () -> {
          LocalDate date = LocalDate.now().minusMonths(1);
          caze = caseService.buildGeneratedCaseForOnePerson(firstName, lastName, dateOfBirth);
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(randomUUID_first_user.toString());
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
          fillExternalId(randomUUID_second_user.toString());
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
        "I select ([^\"]*) created case for person from Cases list",
        (String option) -> {
          if (option.equals("first")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, randomUUID_first_user.toString());
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          if (option.equals("second")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, randomUUID_second_user.toString());
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          webDriverHelpers.clickOnWebElementBySelector(FIRST_CASE_ID_BUTTON);
          webDriverHelpers.waitForPageLoaded();
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
          webDriverHelpers.waitForPageLoaded();
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
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, randomUUID_first_user.toString());
            webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
          }
          if (option.equals("second")) {
            webDriverHelpers.fillInWebElement(
                CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, randomUUID_second_user.toString());
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
        "I save a new case",
        () -> {
          webDriverHelpers.scrollToElement(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I create a new case with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
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
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "^I fill new case form with specific data$",
        () -> {
          caze = caseService.buildGeneratedCase();
          selectCaseOrigin(caze.getCaseOrigin());
          fillExternalId(caze.getExternalId());
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
          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());

          selectPlaceOfStay(caze.getPlaceOfStay());
          fillPlaceDescription(caze.getPlaceDescription());
          selectPresentConditionOfPerson(caze.getPresentConditionOfPerson());
          fillDateOfSymptomOnset(caze.getDateOfSymptomOnset(), Locale.ENGLISH);

          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
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
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_CASE_SAVE_BUTTON);
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
          fillExternalId(caze.getExternalId());
          selectResponsibleRegion(caze.getResponsibleRegion());
          selectResponsibleDistrict(caze.getResponsibleDistrict());
          selectResponsibleCommunity(caze.getResponsibleCommunity());
          selectPlaceOfStay(caze.getPlaceOfStay());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
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
