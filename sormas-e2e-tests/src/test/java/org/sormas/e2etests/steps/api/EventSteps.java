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

import static org.sormas.e2etests.constants.api.Endpoints.EVENTS_PATH;

import cucumber.api.java8.En;
import io.restassured.http.Method;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Event;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.services.api.EventApiService;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.api.sormasrest.EventHelper;
import org.sormas.e2etests.state.ApiState;

public class EventSteps implements En {

  private final RestAssuredClient restAssuredClient;

  @Inject
  public EventSteps(
      EventHelper eventHelper,
      EventApiService eventApiService,
      ApiState apiState,
      RestAssuredClient restAssuredClient) {

    this.restAssuredClient = restAssuredClient;

    When(
        "API: I create a new event",
        () -> {
          Event eve = eventApiService.buildGeneratedEvent();
          eventHelper.createEvent(eve);
          apiState.setCreatedEvent(eve);
        });

    When(
        "API: I create a new event with creation date {int} days ago",
        (Integer creationDate) -> {
          Event eve = eventApiService.buildGeneratedEventWithCreationDate(creationDate);
          eventHelper.createEvent(eve);
          apiState.setCreatedEvent(eve);
        });

    When(
        "I check if created event is available in API",
        () -> {
          getEventByUUID(apiState.getCreatedEvent().getUuid());
        });
  }

  @SneakyThrows
  public void getEventByUUID(String eventUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(EVENTS_PATH + "/" + eventUUID).build());
  }
}
