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

package org.sormas.e2etests.entities.services.api;

import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.util.Date;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.Community;
import org.sormas.e2etests.entities.pojo.api.District;
import org.sormas.e2etests.entities.pojo.api.Event;
import org.sormas.e2etests.entities.pojo.api.EventLocation;
import org.sormas.e2etests.entities.pojo.api.Region;
import org.sormas.e2etests.entities.pojo.api.ReportingUser;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.EventManagementStatusValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.SourceTypeValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class EventApiService {
  RunningConfiguration runningConfiguration;
  private final Faker faker;
  private RestAssuredClient restAssuredClient;

  @Inject
  public EventApiService(
      RunningConfiguration runningConfiguration, Faker faker, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.runningConfiguration = runningConfiguration;
    this.faker = faker;
  }

  //  public EventApiService(Faker faker) {
  //    this.faker = faker;
  //  }

  public Event buildGeneratedEvent() {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Event.builder()
        .uuid(UUID.randomUUID().toString())
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .eventStatus("SIGNAL")
        .srcType(SourceTypeValues.getRandomSourceTypeName())
        .eventInvestigationStatus("PENDING")
        .eventTitle(String.valueOf(System.currentTimeMillis()))
        .eventDesc(faker.chuckNorris().fact())
        .startDate(new Date())
        .reportDateTime(new Date())
        .riskLevel("LOW")
        .typeOfPlace("HOME")
        .eventManagementStatus(EventManagementStatusValues.ONGOING.getValue())
        .eventLocation(
            EventLocation.builder()
                .uuid(UUID.randomUUID().toString())
                .community(
                    Community.builder()
                        .uuid(
                            environmentManager.getCommunityUUID(
                                CommunityValues.VoreingestellteGemeinde.getName()))
                        .build())
                .region(
                    Region.builder()
                        .uuid(
                            environmentManager.getRegionUUID(
                                RegionsValues.VoreingestellteBundeslander.getName()))
                        .build())
                .district(
                    District.builder()
                        .uuid(
                            environmentManager.getDistrictUUID(
                                DistrictsValues.VoreingestellterLandkreis.getName()))
                        .build())
                .build())
        .build();
  }
}
