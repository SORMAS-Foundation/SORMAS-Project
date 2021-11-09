/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.steps.web.application.events;

import static org.sormas.e2etests.pages.application.events.EditEventPage.*;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.events.EventDirectoryPage;
import org.sormas.e2etests.state.ApiState;

public class EventDirectorySteps implements En {

  @Inject
  public EventDirectorySteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      @Named("ENVIRONMENT_URL") String environmentUrl) {

    When(
        "I click on the NEW EVENT button",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                EventDirectoryPage.NEW_EVENT_BUTTON, TITLE_INPUT));

    When(
        "^I check if it appears under ([^\"]*) filter in event directory",
        (String eventStatus) -> {
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(eventUuid));
          webDriverHelpers.clickWebElementByText(EVENT_STATUS_FILTER_BUTTONS, eventStatus);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_STATUS_FILTER_BUTTONS);
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EVENT_PARTICIPANTS_TAB);
        });

    When(
        "^I search for specific event in event directory",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(RESET_FILTER, 35);
          webDriverHelpers.clickOnWebElementBySelector(RESET_FILTER);
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SEARCH_EVENT_BY_FREE_TEXT_INPUT, 15);
          webDriverHelpers.fillAndSubmitInWebElement(SEARCH_EVENT_BY_FREE_TEXT_INPUT, eventUuid);
          webDriverHelpers.clickOnWebElementBySelector(APPLY_FILTER);
        });

    When(
        "I click on the searched event",
        () -> {
          final String eventUuid = CreateNewEventSteps.newEvent.getUuid();
          webDriverHelpers.clickOnWebElementBySelector(getByEventUuid(eventUuid));
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I check if participant appears in the event participants list",
        () -> {
          final String personUuid = EditEventSteps.person.getUuid();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(getByEventUuid(personUuid));
        });

    When(
        "I am accessing the event tab using the created event via api",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.EVENTS_BUTTON);
          final String eventUuid = apiState.getCreatedEvent().getUuid();
          final String eventLinkPath = "/sormas-ui/#!events/data/";
          webDriverHelpers.accessWebSite(environmentUrl + eventLinkPath + eventUuid);
        });

    When(
        "I click on New Task from event tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_TASK_BUTTON));
  }
}
