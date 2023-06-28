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

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.ACTION_OKAY;
import static org.sormas.e2etests.pages.application.shares.EditSharesPage.*;

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

  @Inject
  public SharesDirectorySteps(
      WebDriverHelpers webDriverHelpers, SoftAssert softly, ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I accept first entity from table in Shares Page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(ACCEPT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACCEPT_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for results to reload
        });

    When(
        "I check if accept button does not appear in Shares Page",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ACCEPT_BUTTON, 3),
              "Accept button is visible!");
          softly.assertAll();
        });

    When(
        "I click on the The Eye Icon located in the Shares Page",
        () -> {
          TimeUnit.SECONDS.sleep(2); // wait for the page to load all records
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
  }
}
