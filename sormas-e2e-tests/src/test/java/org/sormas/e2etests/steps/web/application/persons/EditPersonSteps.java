package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;

public class EditPersonSteps implements En {

  @Inject
  public EditPersonSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "I check if event is available at person information",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEE_EVENTS_FOR_PERSON);
          webDriverHelpers.clickOnWebElementBySelector(SEE_EVENTS_FOR_PERSON);
          final String eventUuid = EditEventSteps.event.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
        });
  }
}
