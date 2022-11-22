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
import org.sormas.e2etests.entities.pojo.api.TravelEntry;
import org.sormas.e2etests.entities.services.api.TravelEntryApiService;
import org.sormas.e2etests.helpers.api.sormasrest.TravelEntryHelper;
import org.sormas.e2etests.state.ApiState;

public class TravelEntrySteps implements En {

  @Inject
  public TravelEntrySteps(
      TravelEntryHelper travelEntryHelper,
      ApiState apiState,
      TravelEntryApiService travelEntryStepsApiService) {

    When(
        "API: I create a new travel entry with creation date {int} days ago",
        (Integer creationDate) -> {
          System.out.println("Hello world");
          TravelEntry trEntry =
              travelEntryStepsApiService.buildGeneratedTravelEntryWithCreationDate(
                  apiState.getLastCreatedPerson().getUuid(),
                  apiState.getLastCreatedPerson().getFirstName(),
                  apiState.getLastCreatedPerson().getLastName(),
                  creationDate);
          travelEntryHelper.createTravelEntry(trEntry);
          apiState.setCreatedTravelEntry(trEntry);
        });
  }
}
