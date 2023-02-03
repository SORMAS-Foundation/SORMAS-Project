package org.sormas.e2etests.steps.web.application.messages;

import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.FETCH_MESSAGES_BUTTON;
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
          webDriverHelpers.clickOnWebElementBySelector(getProcessMessageButtonByIndex(resultNumber));
        });
  }
}
