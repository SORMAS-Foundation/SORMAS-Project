package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_CASE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CREATE_NEW_SAMPLE_POPUP_WINDOW_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_CANCEL_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.POPUP_WINDOW_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.getProcessMessageButtonByIndex;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;

@Slf4j
public class MessagesDirectorySteps implements En {

  @Inject
  public MessagesDirectorySteps(WebDriverHelpers webDriverHelpers) {

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
        "^I check that case correction popup is displayed for DE$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_NEW_CASE_POPUP_WINDOW_DE);
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
        });

    Then(
        "^I check that correction popup contains cancel button$",
        () -> {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_CANCEL_BUTTON);
        });

    And(
        "^I check that correction popup contains discard button$",
        () -> {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_DISCARD_BUTTON);
        });

    And(
        "^I check that correction popup contains save button$",
            () -> {
            webDriverHelpers.waitUntilElementIsVisibleAndClickable(POPUP_WINDOW_SAVE_BUTTON);
        });
  }
}
