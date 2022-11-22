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

import com.google.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.District;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.pojo.api.PointOfEntry;
import org.sormas.e2etests.entities.pojo.api.Region;
import org.sormas.e2etests.entities.pojo.api.ReportingUser;
import org.sormas.e2etests.entities.pojo.api.TravelEntry;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class TravelEntryApiService {

  private static RunningConfiguration runningConfiguration;
  private RestAssuredClient restAssuredClient;
  private EnvironmentManager environmentManager;

  @Inject
  public TravelEntryApiService(
      RunningConfiguration runningConfiguration, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.runningConfiguration = runningConfiguration;
  }

  public TravelEntry buildGeneratedTravelEntryWithCreationDate(
      String personUUID, String personFirstName, String personLastName, Integer days) {
    environmentManager = new EnvironmentManager(restAssuredClient);
    return TravelEntry.builder()
        .creationDate(
            LocalDateTime.parse(LocalDateTime.now().minusDays(days).toString())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli())
        .uuid(UUID.randomUUID().toString())
        .person(
            Person.builder()
                .uuid(personUUID)
                .firstName(personFirstName)
                .lastName(personLastName)
                .build())
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .reportDate(
            LocalDateTime.parse(LocalDateTime.now().toString())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli())
        .responsibleRegion(
            Region.builder()
                .caption(RegionsValues.VoreingestellteBundeslander.getName())
                .uuid(
                    environmentManager.getRegionUUID(
                        RegionsValues.VoreingestellteBundeslander.getName()))
                .build())
        .responsibleDistrict(
            District.builder()
                .caption(DistrictsValues.VoreingestellterLandkreis.getName())
                .uuid(
                    environmentManager.getDistrictUUID(
                        DistrictsValues.VoreingestellterLandkreis.getName()))
                .build())
        .pointOfEntry(
            PointOfEntry.builder()
                .uuid("XM7STG-3C4LCN-EEXEDE-LDJPKIMA")
                .pointOfEntryType("AIRPORT")
                .build())
        .pointOfEntryDetails("test details")
        .dateOfArrival(
            LocalDateTime.parse(LocalDateTime.now().toString())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli())
        .build();
  }
}
