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

package org.sormas.e2etests.services.api;

import com.google.inject.Inject;
import java.util.Date;
import java.util.UUID;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.EventManagementStatusValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.RiskLevelValues;
import org.sormas.e2etests.enums.SourceTypeValues;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.pojo.api.Community;
import org.sormas.e2etests.pojo.api.District;
import org.sormas.e2etests.pojo.api.Event;
import org.sormas.e2etests.pojo.api.EventLocation;
import org.sormas.e2etests.pojo.api.Region;
import org.sormas.e2etests.pojo.api.ReportingUser;

public class EventApiService {

  @Inject
  public EventApiService() {}

  public Event buildGeneratedEvent() {
    return Event.builder()
        .uuid(UUID.randomUUID().toString())
        .disease(DiseasesValues.getRandomDiseaseName())
        .reportingUser(ReportingUser.builder().uuid("QLW4AN-TGWLRA-3UQVEM-WCDFCIVM").build())
        .eventStatus("SIGNAL")
        .srcType(SourceTypeValues.getRandomSourceTypeName())
        .eventInvestigationStatus("PENDING")
        .eventTitle(String.valueOf(System.currentTimeMillis()))
        .startDate(new Date())
        .reportDateTime(new Date())
        .riskLevel(RiskLevelValues.getRandomRiskLevelName())
        .typeOfPlace(TypeOfPlace.getRandomTypeOfPlace())
        .eventManagementStatus(EventManagementStatusValues.ONGOING.getValue())
        .eventLocation(
            EventLocation.builder()
                .uuid(UUID.randomUUID().toString())
                .community(
                    Community.builder()
                        .uuid(CommunityValues.VoreingestellteGemeinde.getUuid())
                        .build())
                .region(
                    Region.builder()
                        .uuid(RegionsValues.VoreingestellteBundeslander.getUuid())
                        .build())
                .district(
                    District.builder()
                        .uuid(DistrictsValues.VoreingestellterLandkreis.getUuid())
                        .build())
                .build())
        .build();
  }
}
