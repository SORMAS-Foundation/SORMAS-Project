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

package org.sormas.e2etests.steps.web.application.shares;

import static org.sormas.e2etests.pages.application.aCommonComponents.SideCards.SURVEILLANCE_REPORT_NOTIFICATION_DETAILS;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_OKAY;
import static org.sormas.e2etests.pages.application.shares.EditSharesPage.*;

import com.github.javafaker.Faker;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

@Slf4j
public class SharesDirectorySteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Faker faker;
  private static String generatedRandomStringCase;
  private static String generatedRandomStringContact;

  @Inject
  public SharesDirectorySteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, ApiState apiState, Faker faker) {
    this.webDriverHelpers = webDriverHelpers;
    this.faker = faker;

    When(
        "I accept first entity from table in Shares Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(ACCEPT_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for results to reload
        });

    When(
        "I click on {string} shared contact button with copied contact description",
        (String option) -> {
          switch (option) {
            case "reject":
              webDriverHelpers.clickOnWebElementBySelector(
                  getActionRejectButtonByContactDescription(generatedRandomStringContact));
              break;
            case "accept":
              webDriverHelpers.clickOnWebElementBySelector(
                  getActionAcceptButtonByContactDescription(generatedRandomStringContact));
              break;
          }
        });

    When(
        "I click on {string} shared case button with copied case description",
        (String option) -> {
          switch (option) {
            case "reject":
              webDriverHelpers.clickOnWebElementBySelector(
                  getActionRejectButtonByCaseDescription(generatedRandomStringCase));
              break;
            case "accept":
              webDriverHelpers.clickOnWebElementBySelector(
                  getActionAcceptButtonByCaseDescription(generatedRandomStringCase));
              break;
          }
        });

    And(
        "^I check that accept shared case button with copied case description is visible in Share Directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getActionAcceptButtonByCaseDescription(generatedRandomStringCase));
        });

    And(
        "^I check that accept shared contact button with copied contact description is visible in Share Directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getActionAcceptButtonByContactDescription(generatedRandomStringContact));
        });

    And(
        "^I check that entity not found error popup is displayed in Share Directory page$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(ENTITY_NOT_FOUND_POPUP);
        });

    And(
        "I fill comment in share popup with {string}",
        (String comment) -> {
          webDriverHelpers.fillInWebElement(EXTRA_COMMENT_INPUT_SHARE_POPUP, comment);
        });

    And(
        "I fill comment in surveillance report notification details with random string",
        () -> {
          generatedRandomStringCase = faker.animal().name();
          webDriverHelpers.fillInWebElement(
              SURVEILLANCE_REPORT_NOTIFICATION_DETAILS, generatedRandomStringCase);
        });

    When(
        "I check if accept button does not appear in Shares Page",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ACCEPT_BUTTON, 3),
              "Accept button is visible!");
          softly.assertAll();
        });

    And(
        "I fill comment in share popup for case with random string",
        () -> {
          generatedRandomStringCase = faker.beer().name();
          webDriverHelpers.fillInWebElement(
              EXTRA_COMMENT_INPUT_SHARE_POPUP, generatedRandomStringCase);
        });

    And(
        "I fill comment in share popup for contact with random string",
        () -> {
          generatedRandomStringContact = faker.beer().name();
          webDriverHelpers.fillInWebElement(
              EXTRA_COMMENT_INPUT_SHARE_POPUP, generatedRandomStringContact);
        });

    When(
        "I click on the The Eye Icon located in the Shares Page",
        () -> {
          TimeUnit.SECONDS.sleep(3); // wait for the page to load all records
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SHARE_FIRST_EYE_ICON);
          webDriverHelpers.clickOnWebElementBySelector(SHARE_FIRST_EYE_ICON);
        });

    And(
        "^I check that first shared result has different id then deleted shared case$",
        () -> {
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SHARE_UUID_CASE_TITLE),
              apiState.getCreatedCase().getUuid().substring(0, 6).toUpperCase(),
              "UUIDs are equal!");
          softly.assertAll();
        });

    And(
        "^I check that first shared result has different id then deleted shared contact$",
        () -> {
          softly.assertNotEquals(
              webDriverHelpers.getTextFromWebElement(SHARE_UUID_CASE_TITLE),
              apiState.getCreatedContact().getUuid().substring(0, 6).toUpperCase(),
              "UUID are equal!!");
          softly.assertAll();
        });

    And(
        "^I click on the shortened case/contact ID to open the case$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SHARE_UUID_CASE_TITLE);
          webDriverHelpers.clickOnWebElementBySelector(SHARE_UUID_CASE_TITLE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
        });

    And(
        "^I check that message popup about Request in not yet accepted is appear$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SHARE_REQUEST_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(SHARE_REQUEST_POPUP);
        });

    And(
        "^I close share request details window$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(CLOSE_SHARE_REQUEST_DETAILS);
        });

    When(
        "I select German Status Dropdown to ([^\"]*)",
        (String status) -> {
          switch (status) {
            case "Accepted":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(STATUS_COMBOBOX);
              webDriverHelpers.selectFromCombobox(STATUS_COMBOBOX, "Ausstehend");
              webDriverHelpers.clickOnWebElementBySelector(
                  SHARE_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
              break;
            case "Pending":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(STATUS_COMBOBOX);
              webDriverHelpers.selectFromCombobox(STATUS_COMBOBOX, "\u00DCbernommen");
              webDriverHelpers.clickOnWebElementBySelector(
                  SHARE_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
              break;
            case "Rejected":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(STATUS_COMBOBOX);
              webDriverHelpers.selectFromCombobox(STATUS_COMBOBOX, "Abgelehnt");
              webDriverHelpers.clickOnWebElementBySelector(
                  SHARE_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
              break;
            case "Revoked":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(STATUS_COMBOBOX);
              webDriverHelpers.selectFromCombobox(STATUS_COMBOBOX, "Widerrufen");
              webDriverHelpers.clickOnWebElementBySelector(
                  SHARE_DIRECTORY_PAGE_APPLY_FILTER_BUTTON);
              break;
          }
        });

    When(
        "I check that ([^\"]*) status value is corresponding with entities",
        (String status) -> {
          TimeUnit.SECONDS.sleep(3); // waiting for page loaded
          switch (status) {
            case "Accepted":
              softly.assertEquals(
                  webDriverHelpers.getTextFromWebElement(STATUS_HEADER),
                  "Ausstehend",
                  "The corresponding entity has incorrect status");
              softly.assertAll();
              break;
            case "Pending":
              softly.assertEquals(
                  webDriverHelpers.getTextFromWebElement(STATUS_HEADER),
                  "\u00DCbernommen",
                  "The corresponding entity has incorrect status");
              softly.assertAll();
              break;
            case "Rejected":
              softly.assertEquals(
                  webDriverHelpers.getTextFromWebElement(STATUS_HEADER),
                  "Abgelehnt",
                  "The corresponding entity has incorrect status");
              softly.assertAll();
              break;
            case "Revoked":
              softly.assertEquals(
                  webDriverHelpers.getTextFromWebElement(STATUS_HEADER),
                  "Widerrufen",
                  "The corresponding entity has incorrect status");
              softly.assertAll();
              break;
          }
        });

    When(
        "I pick ([^\"]*) tab on Share Directory page",
        (String status) -> {
          switch (status) {
            case "OUTGOING":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(OUTGOING_OPTION);
              webDriverHelpers.clickOnWebElementBySelector(OUTGOING_OPTION);
              break;
            case "INCOMING":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(INCOMING_OPTION);
              webDriverHelpers.clickOnWebElementBySelector(INCOMING_OPTION);
              break;
          }
        });

    And(
        "^I check that share associated contacts checkbox is not visible in Share form for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SHARE_OPTION_CHECKBOX);
          int numberOfCheckboxes = webDriverHelpers.getNumberOfElements(SHARE_OPTION_CHECKBOX);
          for (int i = 1; i <= numberOfCheckboxes; i++) {
            softly.assertNotEquals(
                webDriverHelpers.getTextFromWebElement(getCheckBoxFromShareFormByIndex(i)),
                "Zugehörige Kontakte teilen",
                "Share associated contacts checkbox is displayed!");
            softly.assertAll();
          }
        });

    And(
        "I check that {string} column header is not visible in Share request details window for DE",
        (String columnHeaderType) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_COLUMN_HEADER);
          int numberOfColumnHeaders = webDriverHelpers.getNumberOfElements(POPUP_COLUMN_HEADER);
          for (int i = 1; i <= numberOfColumnHeaders; i++) {
            softly.assertNotEquals(
                webDriverHelpers.getTextFromWebElement(getPopupColumnHeaderByIndex(i)),
                columnHeaderType,
                "Contact column header is displayed!");
            softly.assertAll();
          }
        });

    When(
        "I check if a warning pop-up message appears that the Case should be accepted first",
        () -> {
          softly.assertTrue(
              webDriverHelpers.isElementVisibleWithTimeout(
                  WARNING_ACCEPT_CASE_BEFORE_CONTACT_HEADER_DE, 5));
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ACTION_OKAY);
        });

    Then(
        "^I check if Share request not found popup message appeared for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SHARE_REQUEST_NOT_FOUND_HEADER_DE);
        });

    When(
        "^I click on the Outgoing radio button in Share Directory page DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              OUTGOING_VIEW_SWITCHER_RADIO_BUTTON_DE);
          webDriverHelpers.clickOnWebElementBySelector(OUTGOING_VIEW_SWITCHER_RADIO_BUTTON_DE);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
        });
  }
}
