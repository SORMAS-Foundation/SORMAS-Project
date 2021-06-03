package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.persons.EditPersonPage.*;
import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.events.EditEventSteps;

public class PersonDirectorySteps implements En {

  @Inject
  public PersonDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "I search for specific person in person directory",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SEARCH_PERSON_BY_FREE_TEXT);
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_PERSON_BY_FREE_TEXT, personUuid);
        });

    When(
        "I click on specific person in person directory",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.clickOnWebElementBySelector(getByPersonUuid(personUuid));
        });
  }
}
