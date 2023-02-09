package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CLOSE_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_CASE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_SAMPLE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.GET_NEW_MESSAGES_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DIRECTORY_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_UUID_TEXT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NO_NEW_REPORTS_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.RESET_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SAVE_POPUP_CONTENT_FIRST_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SEARCH_MESSAGE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.TOTAL_MESSAGE_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessMessageButtonByIndex;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessStatusByIndex;

import cucumber.api.java8.En;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

@Slf4j
public class MessagesDirectorySteps implements En {

  public static List<String> uuids = new ArrayList<>();

  @Inject
  public MessagesDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      BaseSteps baseSteps,
      SoftAssert softly,
      AssertHelpers assertHelpers) {

    When(
        "I click on fetch messages button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FETCH_MESSAGES_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for fetch
          if (webDriverHelpers.isElementVisibleWithTimeout(
              NO_NEW_REPORTS_POPUP, 1)) { // because of tests works in parallel
            webDriverHelpers.clickOnWebElementBySelector(CLOSE_POPUP);
            Actions action = new Actions(baseSteps.getDriver());
            action.sendKeys(Keys.ESCAPE);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(GET_NEW_MESSAGES_POPUP, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT_FIRST_BUTTON);
          }
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
        "^I check that new sample form with pathogen detection reporting process is displayed$",
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

    And(
        "I click on {string} button in new sample form with pathogen detection reporting process",
        (String option) -> {
          switch (option) {
            case "save and open case":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(
                  POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON);
              webDriverHelpers.clickOnWebElementBySelector(POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON);
              break;
            case "cancel":
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

    And(
        "^I collect message uuid$",
        () -> {
          uuids.add(webDriverHelpers.getValueFromWebElement(MESSAGE_UUID_TEXT));
        });

    Then(
        "^I click Delete button in Message form$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(MESSAGE_DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(MESSAGE_DELETE_BUTTON);
        });

    And(
        "^I confirm message deletion$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(POPUP_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(POPUP_CONFIRM_BUTTON);
        });

    And(
        "^I filter last deleted message$",
        () -> {
          System.out.println("UUID: " + uuids.get(0));
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_MESSAGE_INPUT, uuids.get(0));
          TimeUnit.SECONDS.sleep(2); // wait for reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "^I check that number of displayed messages results is (\\d+)$",
        (Integer number) -> {
          String textFromCounter =
              webDriverHelpers.getTextFromPresentWebElement(TOTAL_MESSAGE_COUNTER).substring(4);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(textFromCounter),
                      number.intValue(),
                      "Number of displayed messages is not correct"));
        });

    And(
        "^I check that the Delete button is not available$",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(MESSAGE_DELETE_BUTTON, 2),
              "Delete message button is visible!");
          softly.assertAll();
        });

    And(
        "^I click on reset filters button from Message Directory$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });
  }
}
