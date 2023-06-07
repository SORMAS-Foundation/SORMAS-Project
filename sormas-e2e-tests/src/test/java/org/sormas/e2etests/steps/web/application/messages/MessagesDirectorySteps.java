package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.ACTION_CONFIRM_POPUP_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.ACTION_YES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CLOSE_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_CASE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_SAMPLE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_NULL_DATE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_NULL_TIME_COMBOBOX;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FIRST_RECORD_DISEASE_VARIANT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FIRST_TIME_FETCH_MESSAGE_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FORWARDED_MESSAGE_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.GET_NEW_MESSAGES_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MARK_AS_FORWARDED_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MARK_AS_UNCLEAR_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_DIRECTORY_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.MESSAGE_UUID_TEXT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_CASE_EMAIL_ADDRESS_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_CASE_PHONE_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_SPECIMEN_CONDITION_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_TESTED_DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_TEST_RESULT_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_TEST_RESULT_VERIFIED_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NEW_SAMPLE_TEST_RESULT_VERIFIED_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NO_NEW_REPORTS_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.RESET_FILTER_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SEARCH_MESSAGE_INPUT;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.TOTAL_MESSAGE_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UNCLEAR_MESSAGE_COUNTER;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessMessageButtonByIndex;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessStatusByIndex;

import cucumber.api.java8.En;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            webDriverHelpers.clickOnWebElementBySelector(
                UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON);
          }
          if (webDriverHelpers.isElementVisibleWithTimeout(FIRST_TIME_FETCH_MESSAGE_POPUP, 1)) {
            webDriverHelpers.clickOnWebElementBySelector(ACTION_YES_BUTTON);
            webDriverHelpers.fillInWebElement(
                FETCH_MESSAGES_NULL_DATE,
                LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            webDriverHelpers.selectFromCombobox(FETCH_MESSAGES_NULL_TIME_COMBOBOX, "00:00");
            webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_POPUP_BUTTON);
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
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
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
        "^I filter messages by collected uuid$",
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

    And(
        "^I click on the Mark as unclear button$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(MARK_AS_UNCLEAR_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(MARK_AS_UNCLEAR_BUTTON);
        });

    And(
        "I filter messages by {string} in Message Directory",
        (String option) -> {
          switch (option) {
            case "Unclear":
              webDriverHelpers.clickOnWebElementBySelector(UNCLEAR_MESSAGE_COUNTER);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
            case "Forwarded":
              webDriverHelpers.clickOnWebElementBySelector(FORWARDED_MESSAGE_COUNTER);
              webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
              break;
          }
        });

    And(
        "^I check that number of displayed messages results for Unklar is (\\d+)$",
        (Integer number) -> {
          String textFromCounter =
              webDriverHelpers.getTextFromPresentWebElement(UNCLEAR_MESSAGE_COUNTER).substring(6);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(textFromCounter),
                      number.intValue(),
                      "Number of displayed messages is not correct"));
        });

    And(
        "^I click on the Mark as a forwarded button$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(MARK_AS_FORWARDED_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    And(
        "^I check that number of displayed messages results for Weitergeleitet is (\\d+)$",
        (Integer number) -> {
          String textFromCounter =
              webDriverHelpers
                  .getTextFromPresentWebElement(FORWARDED_MESSAGE_COUNTER)
                  .substring(14);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      Integer.parseInt(textFromCounter),
                      number.intValue(),
                      "Number of displayed messages is not correct"));
        });

    And(
        "I check if {string} is prefilled in New sample form while processing a DEMIS LabMessage",
        (String option) -> {
          switch (option) {
            case "date of report":
              softly.assertFalse(
                  webDriverHelpers
                      .getValueFromWebElement(NEW_SAMPLE_DATE_OF_REPORT_INPUT)
                      .isEmpty(),
                  "Date of report is empty!");
              softly.assertAll();
              break;
            case "test result":
              softly.assertFalse(
                  webDriverHelpers.getValueFromWebElement(NEW_SAMPLE_TEST_RESULT_INPUT).isEmpty(),
                  "Tested result is empty!");
              softly.assertAll();
              break;
            case "specimen condition":
              softly.assertFalse(
                  webDriverHelpers
                      .getValueFromWebElement(NEW_SAMPLE_SPECIMEN_CONDITION_INPUT)
                      .isEmpty(),
                  "Specimen condition is empty!");
              softly.assertAll();
              break;
            case "test result verified":
              softly.assertFalse(
                  webDriverHelpers.isElementChecked(NEW_SAMPLE_TEST_RESULT_VERIFIED_RADIOBUTTON),
                  "Test result verified is not checked!");
              softly.assertAll();
              break;
            case "tested disease":
              softly.assertFalse(
                  webDriverHelpers
                      .getValueFromWebElement(NEW_SAMPLE_TESTED_DISEASE_INPUT)
                      .isEmpty(),
                  "Tested disease is empty!");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I check if \"([^\"]*)\" is prefilled in New case form while processing a DEMIS LabMessage$",
        (String option) -> {
          switch (option) {
            case "email address":
              softly.assertFalse(
                  webDriverHelpers.getValueFromWebElement(NEW_CASE_EMAIL_ADDRESS_INPUT).isEmpty(),
                  "Email address is empty!");
              softly.assertAll();
              break;
            case "phone number":
              softly.assertFalse(
                  webDriverHelpers.getValueFromWebElement(NEW_CASE_PHONE_NUMBER_INPUT).isEmpty(),
                  "Phone number is empty!");
              softly.assertAll();
              break;
          }
        });

    Then(
        "^I check if \"([^\"]*)\" is set to \"([^\"]*)\"$",
        (String option, String value) -> {
          switch (option) {
            case "specimen condition":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(NEW_SAMPLE_SPECIMEN_CONDITION_INPUT),
                  value,
                  "Value in specimen condition is incorrect!");
              softly.assertAll();
              break;
            case "test result verified":
              softly.assertEquals(
                  webDriverHelpers.getTextFromWebElement(
                      NEW_SAMPLE_TEST_RESULT_VERIFIED_SELECTED_VALUE),
                  value,
                  "Value in test result verified is incorrect!");
              softly.assertAll();
              break;
            case "tested disease":
              softly.assertEquals(
                  webDriverHelpers.getValueFromWebElement(NEW_SAMPLE_TESTED_DISEASE_INPUT),
                  value,
                  "Value in tested disease is incorrect!");
              softly.assertAll();
              break;
          }
        });

    And(
       "^I check if disease variant field for first record is empty in Message Directory$",
       () -> {
         webDriverHelpers.waitUntilIdentifiedElementIsPresent(FIRST_RECORD_DISEASE_VARIANT);
         softly.assertNull(
                 webDriverHelpers.getValueFromWebElement(FIRST_RECORD_DISEASE_VARIANT),
                 "Disease variant is not null");
         softly.assertAll();
       });
  }
}
