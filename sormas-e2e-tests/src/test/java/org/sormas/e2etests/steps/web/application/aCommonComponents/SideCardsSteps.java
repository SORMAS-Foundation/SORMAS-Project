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
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.NUMBER_OF_TESTS_IN_SAMPLES;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.ONE_TEST_IN_SAMPLES_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;

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
        "I check if handover card contains shared with {string} information",
        (String environmentIdentifier) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(HANDOVER_SIDE_CARD);
          TimeUnit.SECONDS.sleep(3);
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
              () -> webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_BUTTON));
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
          softly.assertTrue(
              webDriverHelpers.isElementPresent(
                  checkTextInReportSideComponent(formatter.format(LocalDate.now()))));
          softly.assertAll();
        });
    Then(
        "^I check that the number of added samples on the Edit case page is (\\d+)$",
        (Integer numberOfSamples) -> {
          Integer actualNumberOfSamples =
              webDriverHelpers.getNumberOfElements(ADDED_SAMPLES_IN_SAMPLE_CARD);
          softly.assertEquals(
              actualNumberOfSamples,
              numberOfSamples,
              "Number of samples added in sample ard is different then expected!");
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              LINKED_SHARED_ORGANIZATION_SELECTED_VALUE, 60);
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
  }
}
