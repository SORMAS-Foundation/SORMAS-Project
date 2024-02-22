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

package org.sormas.e2etests.steps.web.application.aCommonComponents;

import static org.sormas.e2etests.pages.application.aCommonComponents.SideCards.*;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EDIT_SAMPLE_PENCIL_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EYE_SAMPLE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SEE_SAMPLE_BUTTON_DE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.VACCINATION_CARD_INFO_ICON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.EDIT_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.NUMBER_OF_TESTS_IN_SAMPLES;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.ONE_TEST_IN_SAMPLES_DE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EDIT_CASES_ICON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EDIT_CONTACTS_ICON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.EDIT_PARTICIPANT_ICON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.NEW_TRAVEL_ENTRY_BUTTON_DE;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.VIEW_CASES_ICON_BUTTON;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.VIEW_CONTACTS_ICON_BUTTON;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.CLOSE_DIALOG_BUTTON;
import static org.sormas.e2etests.steps.web.application.messages.MessagesDirectorySteps.convertStringToChosenFormatDate;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class SideCardsSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private final BaseSteps baseSteps;
  public static String reportUuid;
  public static String reportingUser;
  public static String typeOfReporting;
  public static String externalId;
  public static String facilityRegion;
  public static String facilityDistrict;
  public static String facilityCategory;
  public static String facilityType;
  public static LocalDate dateOfReport;
  public static final DateTimeFormatter formatterDE = DateTimeFormatter.ofPattern("d.M.yyyy");

  /**
   * This class contains Contacts, Cases etc right side mini components (Tasks, Samples, Events,
   * Immunizations etc)
   */
  @SneakyThrows
  @Inject
  public SideCardsSteps(
      WebDriverHelpers webDriverHelpers,
      SoftAssert softly,
      BaseSteps baseSteps,
      Faker faker,
      RunningConfiguration runningConfiguration) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;
    this.baseSteps = baseSteps;
    When(
        "I check if handover card contains {string} information",
        (String information) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(HANDOVER_SIDE_CARD);
          TimeUnit.SECONDS.sleep(3);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(checkTextInHandoverSideComponent(information)),
              information
                  + " text is not present in handover component. Found only "
                  + webDriverHelpers.getTextFromPresentWebElement(HANDOVER_SIDE_CARD));
          softly.assertAll();
        });

    When(
        "I check if handover card not contains {string} shared information",
        (String information) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(HANDOVER_SIDE_CARD);
          TimeUnit.SECONDS.sleep(3);
          softly.assertFalse(
              webDriverHelpers.isElementPresent(checkTextInHandoverSideComponent(information)),
              information
                  + " text is not present in handover component. Found only "
                  + webDriverHelpers.getTextFromPresentWebElement(HANDOVER_SIDE_CARD));
          softly.assertAll();
        });

    When(
        "I check if handover card contains shared with {string} information",
        (String environmentIdentifier) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(HANDOVER_SIDE_CARD);
          TimeUnit.SECONDS.sleep(4); // waiting for page loaded
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  checkTextInHandoverSideComponent(
                      runningConfiguration.getSurvnetResponsible(environmentIdentifier))),
              environmentIdentifier
                  + " text is not present in handover component. Found only "
                  + webDriverHelpers.getTextFromPresentWebElement(HANDOVER_SIDE_CARD));
          softly.assertAll();
        });
    When(
        "I click on share button",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHARE_SORMAS_2_SORMAS_BUTTON));
    When(
        "I check if share button is unavailable",
        () -> {
          Assert.assertFalse(
              webDriverHelpers.isElementPresent(SHARE_SORMAS_2_SORMAS_BUTTON),
              "Share button is displayed");
        });
    When(
        "I select organization to share with {string}",
        (String organization) -> {
          String survnetOrganization = runningConfiguration.getSurvnetResponsible(organization);
          webDriverHelpers.selectFromCombobox(
              SHARE_ORGANIZATION_POPUP_COMBOBOX, survnetOrganization);
        });
    When(
        "I click to hand over the ownership in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(HAND_THE_OWNERSHIP_CHECKBOX));

    When(
        "I click to exclude personal data in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(EXCLUDE_PERSONAL_DATA_CHECKBOX));

    When(
        "I click to share report data in Share popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(SHARE_REPORT_CHECKBOX));

    When(
        "I click on Save popup button",
        () -> webDriverHelpers.clickOnWebElementBySelector(POPUP_EDIT_REPORT_WINDOW_SAVE_BUTTON));
    When(
        "I click on Discard popup button",
        () ->
            webDriverHelpers.clickOnWebElementBySelector(POPUP_EDIT_REPORT_WINDOW_DISCARD_BUTTON));
    When(
        "I check if sample card has {string} information",
        (String information) -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(checkTextInSampleSideComponent(information)),
              information + " text is not present in sample component");
          softly.assertAll();
        });

    When(
        "I check if Immunization area contains {string}",
        (String name) -> {
            TimeUnit.SECONDS.sleep(2); //waiting for page loaded
          softly.assertTrue(
              webDriverHelpers.isElementPresent(checkTextInImmunizationSideComponent(name)),
              "Element is not present");
          softly.assertAll();
        });

    When(
        "I check if Immunization area does not contains {string}",
        (String name) -> {
          softly.assertFalse(
              webDriverHelpers.isElementPresent(checkTextInImmunizationSideComponent(name)),
              "Element is present");
          softly.assertAll();
        });
    And(
        "I check if report side component in Edit Case has {string}",
        (String text) -> {
          softly.assertTrue(
              webDriverHelpers.isElementPresent(checkTextInReportSideComponent(text)));
          softly.assertAll();
        });
    And(
        "I check if report side component in Edit Case has today date",
        () -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
          webDriverHelpers.scrollToElement(EDIT_REPORT_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EDIT_REPORT_BUTTON);
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  checkTextInReportSideComponent(formatter.format(LocalDate.now()))));
          softly.assertAll();
        });
    Then(
        "^I check that the number of added samples on the Edit case page is (\\d+)$",
        (Integer numberOfSamples) -> {
          webDriverHelpers.waitForElementPresent(ADDED_SAMPLES_IN_SAMPLE_CARD, 2);
          Integer actualNumberOfSamples =
              webDriverHelpers.getNumberOfElements(ADDED_SAMPLES_IN_SAMPLE_CARD);
          softly.assertEquals(
              actualNumberOfSamples,
              numberOfSamples,
              "Number of samples added in sample ard is different then expected!");
          softly.assertAll();
        });

    When(
        "I check that number of displayed samples with {string} icon is {int} for Samples Side Card",
        (String icon, Integer number) -> {
          switch (icon) {
            case "pencil":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EDIT_SAMPLE_PENCIL_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EDIT_SAMPLE_PENCIL_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
            case "eye":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EYE_SAMPLE_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EYE_SAMPLE_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
          }
          softly.assertAll();
        });

    When(
        "I check if Side card has available {string} button for DE",
        (String button) -> {
          switch (button) {
            case "see sample for this person":
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(SEE_SAMPLE_BUTTON_DE),
                  "Element is not present");
              break;
            case "new travel entry":
              softly.assertTrue(
                  webDriverHelpers.isElementPresent(NEW_TRAVEL_ENTRY_BUTTON_DE),
                  "Element is not present");
              break;
          }
          softly.assertAll();
        });

    When(
        "I check that number of displayed cases with {string} icon is {int} for Cases Side Card",
        (String icon, Integer number) -> {
          switch (icon) {
            case "pencil":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EDIT_CASES_ICON_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EDIT_CASES_ICON_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
            case "eye":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  VIEW_CASES_ICON_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(VIEW_CASES_ICON_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
          }
          softly.assertAll();
        });

    When(
        "I check that number of displayed contacts with {string} icon is {int} for Contacts Side Card",
        (String icon, Integer number) -> {
          switch (icon) {
            case "pencil":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EDIT_CONTACTS_ICON_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EDIT_CONTACTS_ICON_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
            case "eye":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  VIEW_CONTACTS_ICON_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(VIEW_CONTACTS_ICON_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
          }
          softly.assertAll();
        });

    When(
        "I check that number of displayed vaccinations with {string} icon is {int} for Vaccinations Side Card",
        (String icon, Integer number) -> {
          switch (icon) {
            case "pencil":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EDIT_VACCINATION_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EDIT_VACCINATION_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
            case "eye":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EYE_SAMPLE_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(VACCINATION_CARD_INFO_ICON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
          }
          softly.assertAll();
        });

    When(
        "I check that number of displayed linked events with {string} icon is {int} for Events Side Card",
        (String icon, Integer number) -> {
          TimeUnit.SECONDS.sleep(2); // waiting for page loaded
          switch (icon) {
            case "pencil":
              webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                  EDIT_PARTICIPANT_ICON_BUTTON, 5);
              softly.assertEquals(
                  webDriverHelpers.getNumberOfElements(EDIT_PARTICIPANT_ICON_BUTTON),
                  number.intValue(),
                  "Number of displayed samples is not valid");
              break;
              // place for eye icon option
          }
          softly.assertAll();
        });

    Then(
        "^I check that the case has no samples on side card for DE$",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(ADDED_SAMPLES_IN_SAMPLE_CARD),
              "Es gibt keine Proben f\u00FCr diesen Fall",
              "The case has sample!");
          softly.assertAll();
        });

    When(
        "I click on edit Sample",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EDIT_SAMPLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_SAMPLE_BUTTON);
        });

    When(
        "I click on edit surveillance report",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              EDIT_SURVEILLANCE_REPORT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EDIT_SURVEILLANCE_REPORT_BUTTON);
        });

    When(
        "I collect data from surveillance report",
        () -> {
          reportUuid = webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_UUID_TEXT);
          reportingUser =
              webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_REPORTING_USER_TEXT);
          typeOfReporting = webDriverHelpers.getValueFromCombobox(TYPE_OF_REPORTING_COMBOBOX);
          externalId =
              webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_EXTERNAL_ID_TEXT);
          facilityRegion = webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_REGION_COMBOBOX);
          facilityDistrict =
              webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_DISTRICT_COMBOBOX);
          facilityCategory =
              webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_CATEGORY_COMBOBOX);
          facilityType = webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_TYPE_COMBOBOX);
          dateOfReport =
              LocalDate.parse(
                  webDriverHelpers.getValueFromWebElement(SURVEILLANCE_DATE_OF_REPORT),
                  formatterDE);
        });

    When(
        "I check that data present in target are match to data from source in surveillance report",
        () -> {
          softly.assertEquals(
              reportUuid,
              webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_UUID_TEXT),
              "Report uuid is not equal");
          softly.assertEquals(
              reportingUser,
              webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_REPORTING_USER_TEXT),
              "Reporting user is not equal");
          softly.assertEquals(
              typeOfReporting,
              webDriverHelpers.getValueFromCombobox(TYPE_OF_REPORTING_COMBOBOX),
              "Type of reporting is not equal");
          softly.assertEquals(
              externalId,
              webDriverHelpers.getValueFromWebElement(SURVEILLANCE_REPORT_EXTERNAL_ID_TEXT),
              "External Id is not equal");
          softly.assertEquals(
              facilityRegion,
              webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_REGION_COMBOBOX),
              "Facility region is not equal");
          softly.assertEquals(
              facilityDistrict,
              webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_DISTRICT_COMBOBOX),
              "Facility district is not equal");
          softly.assertEquals(
              facilityType,
              webDriverHelpers.getValueFromCombobox(REPORTER_FACILITY_TYPE_COMBOBOX),
              "Facility district is not equal");
          LocalDate expectedDate =
              convertStringToChosenFormatDate(
                  "dd.MM.yyyy",
                  "yyyy-MM-dd",
                  webDriverHelpers.getValueFromWebElement(SURVEILLANCE_DATE_OF_REPORT));

          softly.assertEquals(dateOfReport, expectedDate, "Date of report is not equal");
          softly.assertAll();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(CLOSE_DIALOG_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_DIALOG_BUTTON);
        });

    When(
        "I click on view surveillance report",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              VIEW_SURVEILLANCE_REPORT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(VIEW_SURVEILLANCE_REPORT_BUTTON);
        });

    When(
        "I check that that surveillance report has no connected with lab message",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementPresent(DISPLAY_ASSOCIATED_EXTERNAL_MESSAGE_BUTTON));
          softly.assertAll();
        });

    When(
        "I check if edit sample button is unavailable",
        () -> {
          softly.assertFalse(webDriverHelpers.isElementPresent(EDIT_SAMPLE_BUTTON));
          softly.assertAll();
        });

    When(
        "^I validate only one sample is created with two pathogen tests",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(EDIT_SAMPLE_BUTTON),
              1,
              "Number of samples is not correct");
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(NUMBER_OF_TESTS_IN_SAMPLES),
              "Number of tests: 2",
              "Number of tests is correct!");
          softly.assertAll();
        });

    When(
        "^I check that case created from laboratory message contains a sample with one test",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getNumberOfElements(EDIT_SAMPLE_BUTTON),
              1,
              "Number of samples is not correct");
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(ONE_TEST_IN_SAMPLES_DE);
        });

    And(
        "^I click on Display associated lab messages button from Samples side component$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              SAMPLES_DISPLAY_ASSOCIATED_LAB_MESSAGES_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              SAMPLES_DISPLAY_ASSOCIATED_LAB_MESSAGES_BUTTON);
        });

    And(
        "^I click on Display associated external messages button from Reports side component$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              REPORTS_DISPLAY_ASSOCIATED_EXTERNAL_MESSAGES_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(
              REPORTS_DISPLAY_ASSOCIATED_EXTERNAL_MESSAGES_BUTTON);
        });

    When(
        "I click on share button in s2s share popup and wait for share to finish",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SHARE_SORMAS_2_SORMAS_POPUP_BUTTON);
          Boolean warningMessage =
              webDriverHelpers.isElementVisibleWithTimeout(CLOSE_POPUP_SHARING_MESSAGE, 20);
          if (warningMessage) {
            webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                CLOSE_POPUP_SHARING_MESSAGE);
            softly.assertTrue(
                webDriverHelpers.isElementPresent(CLOSE_POPUP_SHARING_MESSAGE),
                "The popup warning message has not been present");
            softly.assertAll();
            webDriverHelpers.clickOnWebElementBySelector(CLOSE_POPUP_SHARING_MESSAGE);
            webDriverHelpers.clickOnWebElementBySelector(POPUP_EDIT_REPORT_WINDOW_DISCARD_BUTTON);
          } else {
            webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
            webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
                LINKED_SHARED_ORGANIZATION_SELECTED_VALUE, 60);
          }
        });

    When(
        "I click on share button in s2s share popup",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SHARE_SORMAS_2_SORMAS_POPUP_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    And(
        "^I click on edit sample icon of the (\\d+) displayed sample on Edit Case page$",
        (Integer sampleNumber) -> {
          webDriverHelpers.clickOnWebElementBySelector(getEditSampleButtonByNumber(sampleNumber));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    When(
        "I click on eye sample icon of the (\\d+) displayed sample on Edit Case Page",
        (Integer sampleNumber) -> {
          webDriverHelpers.clickOnWebElementBySelector(getEyeSampleIconByNumber(sampleNumber));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });
  }
}
