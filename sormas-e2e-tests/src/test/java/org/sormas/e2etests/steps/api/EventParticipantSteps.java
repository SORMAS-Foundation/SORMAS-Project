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

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.api.EventParticipant;
import org.sormas.e2etests.entities.services.api.EventParticipantApiService;
import org.sormas.e2etests.helpers.api.sormasrest.EventParticipantHelper;
import org.sormas.e2etests.state.ApiState;

public class EventParticipantSteps implements En {

  @Inject
  public EventParticipantSteps(
      EventParticipantHelper eventParticipantHelper,
      EventParticipantApiService eventParticipantApiService,
      ApiState apiState) {

    When(
        "API: I create a new event participant with creation date {int} years ago",
        (Integer creationDate) -> {
          String personUUID = apiState.getLastCreatedPerson().getUuid();
          String personSex = apiState.getLastCreatedPerson().getSex();
          String eventUUID = apiState.getCreatedEvent().getUuid();

          EventParticipant evPart =
              eventParticipantApiService.buildGeneratedEventParticipantWithCreationDate(
                  apiState.getCreatedEvent().getUuid(),
                  apiState.getLastCreatedPerson().getUuid(),
                  apiState.getLastCreatedPerson().getSex(),
                  creationDate);
          eventParticipantHelper.createEventParticipant(evPart);
          apiState.setCreatedEventParticipant(evPart);
        });
  }
}
