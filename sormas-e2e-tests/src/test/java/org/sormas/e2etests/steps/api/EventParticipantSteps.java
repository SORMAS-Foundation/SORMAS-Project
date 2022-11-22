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

package org.sormas.e2etests.steps.api;

import static org.sormas.e2etests.constants.api.Endpoints.EVENT_PARTICIPANTS_PATH;
import static org.sormas.e2etests.pages.application.events.EditEventPage.EVENT_PARTICIPANTS_TAB;
import static org.sormas.e2etests.pages.application.events.EventDirectoryPage.getByEventUuid;

import cucumber.api.java8.En;
import io.restassured.http.Method;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.EventParticipant;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.services.api.EventParticipantApiService;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.api.sormasrest.EventParticipantHelper;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class EventParticipantSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private final RestAssuredClient restAssuredClient;

  @Inject
  public EventParticipantSteps(
      EventParticipantHelper eventParticipantHelper,
      EventParticipantApiService eventParticipantApiService,
      ApiState apiState,
      WebDriverHelpers webDriverHelpers,
      RestAssuredClient restAssuredClient,
      SoftAssert softly) {

    this.webDriverHelpers = webDriverHelpers;
    this.restAssuredClient = restAssuredClient;

    When(
        "API: I create a new event participant with creation date {int} days ago",
        (Integer creationDate) -> {
          EventParticipant evPart =
              eventParticipantApiService.buildGeneratedEventParticipantWithCreationDate(
                  apiState.getCreatedEvent().getUuid(),
                  apiState.getLastCreatedPerson().getUuid(),
                  apiState.getLastCreatedPerson().getSex(),
                  creationDate);
          eventParticipantHelper.createEventParticipant(evPart);
          apiState.setCreatedEventParticipant(evPart);
        });

    When(
        "I check if participant created via API appears in the event participants list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getByEventUuid(apiState.getCreatedEventParticipant().getUuid()));
        });

    When(
        "I check if event participant created via API still appears in the event participant list",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EVENT_PARTICIPANTS_TAB);
          softly.assertFalse(
              webDriverHelpers.isElementPresent(
                  getByEventUuid(apiState.getCreatedEventParticipant().getUuid())),
              "Event participant still appears");
          softly.assertAll();
        });

    When(
        "I check if created event participant is available in API",
        () -> {
          getEventParticipantByUUID(apiState.getCreatedEventParticipant().getUuid());
        });
  }

  @SneakyThrows
  public void getEventParticipantByUUID(String evPartUUID) {
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.GET)
            .path(EVENT_PARTICIPANTS_PATH + "/" + evPartUUID)
            .build());
  }
}
