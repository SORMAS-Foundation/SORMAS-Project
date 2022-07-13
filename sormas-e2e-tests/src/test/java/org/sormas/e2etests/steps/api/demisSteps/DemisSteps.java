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
package org.sormas.e2etests.steps.api.demisSteps;

import cucumber.api.java8.En;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.services.api.demis.DemisApiService;
import org.testng.Assert;

@Slf4j
public class DemisSteps implements En {

  @Inject
  public DemisSteps(DemisApiService demisApiService) {

    Given(
        "API : Login to DEMIS server",
        () -> {
          String loginToken = demisApiService.loginRequest();
          Assert.assertFalse(loginToken.isEmpty(), "DEMIS token wasn't received");
        });
  }
}
