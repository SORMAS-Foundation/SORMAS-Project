package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.CLOSE_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.GET_NEW_MESSAGES_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.NO_NEW_REPORTS_POPUP;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.SAVE_POPUP_CONTENT_FIRST_BUTTON;

import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;

@Slf4j
public class MessagesDirectorySteps implements En {

  @Inject
  public MessagesDirectorySteps(WebDriverHelpers webDriverHelpers, BaseSteps baseSteps) {

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
  }
}
