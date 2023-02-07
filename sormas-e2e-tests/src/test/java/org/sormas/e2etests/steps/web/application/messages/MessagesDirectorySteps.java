package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_CASE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_SAMPLE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DIRECTORY_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessMessageButtonByIndex;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessStatusByIndex;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

@Slf4j
public class MessagesDirectorySteps implements En {

  @Inject
  public MessagesDirectorySteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {

    When(
        "I click on fetch messages button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FETCH_MESSAGES_BUTTON);
          TimeUnit.SECONDS.sleep(5); // wait for fetch
        });

    And(
        "^I click on process button for (\\d+) result in Message Directory page$",
        (Integer resultNumber) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              getProcessMessageButtonByIndex(resultNumber));
        });

    And(
        "^I check that create new case form with pathogen detection reporting process is displayed for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_NEW_CASE_POPUP_WINDOW_DE);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE);
        });

    And(
        "^I check that sample correction popup is displayed$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_NEW_SAMPLE_POPUP_WINDOW_DE);
        });

    And(
        "^I click on YES button in Update case disease variant popup window$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON);
        });

    Then(
        "I check that popup window contains {string} button",
        (String option) -> {
          switch (option) {
            case "cancel":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_CANCEL_BUTTON);
              break;
            case "save":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_SAVE_BUTTON);
              break;
            case "discard":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_DISCARD_BUTTON);
              break;
          }
        });

    And(
        "I click on {string} button in sample correction popup",
        (String option) -> {
          switch (option) {
            case "save and open case":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_CANCEL_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_CANCEL_BUTTON);
              break;
            case "discard":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_DISCARD_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_DISCARD_BUTTON);
              break;
            case "save":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_SAVE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_BUTTON);
              break;
          }
        });

    Then(
        "^I back to message directory$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(MESSAGE_DIRECTORY_HEADER_DE);
        });

    And(
        "^I verify that status for result (\\d+) is set to processed in Message Directory page$",
        (Integer resultNumber) -> {
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(getProcessStatusByIndex(resultNumber)),
              "Verarbeitet",
              "This message is not processed!");
          softly.assertAll();
        });
  }
}
