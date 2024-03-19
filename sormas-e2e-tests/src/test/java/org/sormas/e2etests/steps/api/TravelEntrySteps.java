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

import static org.sormas.e2etests.constants.api.Endpoints.TRAVEL_ENTRIES;
import static org.sormas.e2etests.pages.application.entries.EditTravelEntryPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.entries.TravelEntryPage.PERSON_FILTER_INPUT;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import io.restassured.http.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.entities.pojo.api.TravelEntry;
import org.sormas.e2etests.entities.services.api.TravelEntryApiService;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.api.sormasrest.TravelEntryHelper;
import org.sormas.e2etests.pages.application.entries.EditTravelEntryPage;
import org.sormas.e2etests.state.ApiState;

public class TravelEntrySteps implements En {

  private static WebDriverHelpers webDriverHelpers;
  private final RestAssuredClient restAssuredClient;
  public static List<String> travelEntriesUUID = new ArrayList<>();

  @Inject
  public TravelEntrySteps(
      TravelEntryHelper travelEntryHelper,
      ApiState apiState,
      TravelEntryApiService travelEntryStepsApiService,
      RunningConfiguration runningConfiguration,
      WebDriverHelpers webDriverHelpers,
      RestAssuredClient restAssuredClient) {
    this.webDriverHelpers = webDriverHelpers;
    this.restAssuredClient = restAssuredClient;

    When(
        "API: I create a new travel entry with creation date {int} days ago",
        (Integer creationDate) -> {
          TravelEntry trEntry =
              travelEntryStepsApiService.buildGeneratedTravelEntryWithCreationDate(
                  apiState.getLastCreatedPerson().getUuid(),
                  apiState.getLastCreatedPerson().getFirstName(),
                  apiState.getLastCreatedPerson().getLastName(),
                  creationDate);
          travelEntryHelper.createTravelEntry(trEntry);
          apiState.setCreatedTravelEntry(trEntry);
        });

    When(
        "I open the last created travel entry via api",
        () -> {
          String LAST_CREATED_TRAVEL_ENTRY_URL =
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!travelEntries/data/"
                  + apiState.getCreatedTravelEntry().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_TRAVEL_ENTRY_URL);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    When(
        "I collect uuid of the new travel entry",
        () -> {
          travelEntriesUUID.add(
              webDriverHelpers.getValueFromWebElement(EditTravelEntryPage.UUID_INPUT));
        });

    When(
        "I filter by last created travel entry via API",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              PERSON_FILTER_INPUT, apiState.getCreatedTravelEntry().getUuid());
          TimeUnit.SECONDS.sleep(2); // wait for system reaction
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if created travel entry is available in API",
        () -> {
          getTravelEntryByUUID(apiState.getCreatedTravelEntry().getUuid());
        });
  }

  @SneakyThrows
  public void getTravelEntryByUUID(String trEntUUID) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(TRAVEL_ENTRIES + "/" + trEntUUID).build());
  }
}
