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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.REINFECTION_STATUS_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_ORIGIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_DAY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_MONTH_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_BIRTH_YEAR_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_VARIANT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PRESENT_CONDITION_OF_PERSON_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COVID_GENOME_COPY_NUMBER_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_A_NEW_CASE_FOR_THE_SAME_PERSON_DE_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_VARIANT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PREVIOUS_AND_CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REINFECTION_EYE_ICON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REINFECTION_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REINFECTION_STATUS_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.TOOLTIP_EYE_ICON_HOVER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_COLUMN_HEADERS;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_FIRST_TABLE_ROW;
import static org.sormas.e2etests.pages.application.contacts.ContactDirectoryPage.CONTACTS_DETAILED_TABLE_DATA;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.CreateNewTravelEntryPage.PICK_A_EXISTING_PERSON_LABEL_DE;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.openqa.selenium.WebElement;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

public class CaseReinfectionSteps implements En {
  Faker faker = new Faker();
  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;
  public static Case aCase;
  private final SoftAssert softly;
  public static final DateTimeFormatter DATE_FORMATTER_DE = DateTimeFormatter.ofPattern("d.M.yyyy");
  private static BaseSteps baseSteps;

  @Inject
  public CaseReinfectionSteps(
      WebDriverHelpers webDriverHelpers,
      Faker faker,
      CaseService caseService,
      SoftAssert softly,
      BaseSteps baseSteps) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;
    this.baseSteps = baseSteps;

    List<String> extIds = new ArrayList<String>();
    List<String> collectedUUIDs = new ArrayList<String>();
    String firstName = faker.name().firstName();
    String lastName = faker.name().lastName();
    String personSex = GenderValues.getRandomGenderDE();
    LocalDate dateOfBirth =
        LocalDate.of(
            faker.number().numberBetween(1900, 2002),
            faker.number().numberBetween(1, 12),
            faker.number().numberBetween(1, 27));

    When(
        "I choose ([^\"]*) option in reinfection",
        (String reinfection) -> {
          webDriverHelpers.scrollToElement(REINFECTION_OPTIONS);
          webDriverHelpers.clickWebElementByText(REINFECTION_OPTIONS, reinfection);
        });

    When(
        "I check if reinfection checkboxes for DE version are displayed correctly",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PREVIOUS_AND_CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(COVID_GENOME_COPY_NUMBER_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL);
        });

    When(
        "I create a new case with specific data for DE version with saved person details",
        () -> {
          LocalDate reportDate = LocalDate.now().minusDays(2);
          caze =
              caseService.buildGeneratedCaseDEForOnePerson(
                  firstName, lastName, dateOfBirth, reportDate, personSex);
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
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I create a new case with specific data for DE version with saved person details with earlier report date",
        () -> {
          LocalDate reportDate = LocalDate.now().minusDays(3);
          caze =
              caseService.buildGeneratedCaseDEForOnePerson(
                  firstName, lastName, dateOfBirth, reportDate, personSex);
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
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I check the created data is correctly displayed on Edit case page for DE version for reinfection",
        () -> {
          extIds.add(caze.getExternalId());

          aCase = collectCasePersonDataDE();
          collectedUUIDs.add(aCase.getUuid());
          ComparisonHelper.compareEqualFieldsOfEntities(
              aCase,
              caze,
              List.of(
                  "dateOfReport",
                  "disease",
                  "externalId",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "placeOfStay",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
        });

    Then(
        "I choose select a matching person in pick or create popup for DE case version",
        () -> webDriverHelpers.clickOnWebElementBySelector(PICK_A_EXISTING_PERSON_LABEL_DE));

    When(
        "I choose create a new case for the same person for DE version",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(
                CREATE_A_NEW_CASE_FOR_THE_SAME_PERSON_DE_CHECKBOX));

    When(
        "I search first created case with reinfection",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              CASE_DIRECTORY_DETAILED_PAGE_FILTER_INPUT, extIds.get(0));
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check data from the eye icon in reinfection section in Edit case for DE version",
        () -> {
          DateTimeFormatter testFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          String caseId = collectedUUIDs.get(1).substring(0, 6).toUpperCase();
          String expectedHover =
              "Previous case:\n"
                  + "\n"
                  + "Fall-ID: "
                  + caseId
                  + "\n"
                  + "Meldedatum: "
                  + LocalDate.now().minusDays(3).format(testFormatter)
                  + "\n"
                  + "Externes Aktenzeichen:\n"
                  + "Krankheitsvariante: B.1.617.1\n"
                  + "Datum des Symptombeginns:";

          webDriverHelpers.waitUntilIdentifiedElementIsPresent(REINFECTION_EYE_ICON);
          webDriverHelpers.scrollToElement(REINFECTION_EYE_ICON);
          String prevCaseDescription;
          webDriverHelpers.hoverToElement(REINFECTION_EYE_ICON);
          prevCaseDescription = webDriverHelpers.getTextFromWebElement(TOOLTIP_EYE_ICON_HOVER);
          softly.assertEquals(expectedHover, prevCaseDescription);
          softly.assertAll();
        });

    When(
        "I check all checkboxes with genome sequence in reinfection section in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PREVIOUS_AND_CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
        });

    When(
        "I clear all checkboxes with genome sequence in reinfection section in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PREVIOUS_AND_CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
        });

    When(
        "I set checkboxes from Combination1 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(COVID_GENOME_COPY_NUMBER_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL);
        });

    When(
        "I clear all checkboxes for Combination1 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(PREVIOUS_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(COVID_GENOME_COPY_NUMBER_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL);
        });

    When(
        "I check if reinfection status is set to ([^\"]*) for DE version",
        (String expectedReinfectionStatus) -> {
          String actualReinfectionStatus;
          actualReinfectionStatus =
              webDriverHelpers.getValueFromWebElement(REINFECTION_STATUS_LABEL);
          softly.assertEquals(
              expectedReinfectionStatus,
              actualReinfectionStatus,
              "Reinfection statuses are not equal");
          softly.assertAll();
        });

    When(
        "I set checkboxes from Combination2 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(COVID_GENOME_COPY_NUMBER_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL);
        });

    When(
        "I clear all checkboxes for Combination2 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CURRENT_COVID_INFECTION_IS_KNOWN_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(COVID_GENOME_COPY_NUMBER_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL);
        });

    When(
        "I set checkboxes from Combination3 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL);
        });

    When(
        "I clear all checkboxes for Combination3 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAS_OVERCOME_ACUTE_RESPIRATORY_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_TESTED_CONCLUSIVELY_NEGATIVE_BY_PRC_LABEL);
        });
    When(
        "I set checkboxes from Combination4 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL);
        });

    When(
        "I clear all checkboxes for Combination4 from the test scenario for reinfection in Edit case for DE version",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              PERSON_HAD_AN_ASYMPTOMATIC_COVID_INFECTION_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              INDIVIDUAL_TESTED_POSITIVE_FOR_COVID_BY_PCR_DE_LABEL);
          webDriverHelpers.clickOnWebElementBySelector(
              THE_LAST_POSITIVE_PCR_DETECTION_WAS_MORE_THAN_3_MONTHS_AGO_DE_LABEL);
        });

    When(
        "I filter by reinfection status as a ([^\"]*)",
        (String status) -> {
          webDriverHelpers.selectFromCombobox(REINFECTION_STATUS_COMBOBOX, status);
          webDriverHelpers.clickOnWebElementBySelector(
              CASE_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if created case with reinfection status is displayed in the Case table for DE version",
        () -> {
          List<Map<String, String>> tableRowsData = getTableRowsData();
          Map<String, String> detailedCasesTableRow = tableRowsData.get(0);
          softly.assertEquals(
              detailedCasesTableRow.get(CaseDetailedTableViewHeaders.EXTERNAL_ID_DE.toString()),
              extIds.get(0),
              "IDs are not equal");
          softly.assertAll();
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

  private Case collectCasePersonDataDE() {
    Case userInfo = getUserInformationDE();

    return Case.builder()
        .dateOfReport(getDateOfReportDE())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EditCasePage.EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .diseaseVariant(webDriverHelpers.getValueFromWebElement(DISEASE_VARIANT_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(
            webDriverHelpers.getValueFromWebElement(EditCasePage.PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case getUserInformationDE() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER_DE);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  private LocalDate getDateOfReportDE() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
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
