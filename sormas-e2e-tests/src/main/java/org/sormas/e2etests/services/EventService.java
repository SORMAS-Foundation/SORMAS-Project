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

package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.pojo.web.Event;

public class EventService {
  private final Faker faker;

  @Inject
  public EventService(Faker faker) {
    this.faker = faker;
  }

  public Event buildGeneratedEvent() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EVENT")
        .investigationStatus("INVESTIGATION PENDING") // change back to ongoing after bug fix 5547
        .eventManagementStatus("ONGOING")
        .disease("COVID-19")
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .eventDate(LocalDate.now().minusDays(1))
        .reportDate(LocalDate.now())
        .eventLocation("Home")
        .riskLevel("Moderate risk")
        .sourceType("Not applicable")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .build();
  }

  public Event buildEditEvent() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("DROPPED")
        .investigationStatus("INVESTIGATION DONE")
        .eventManagementStatus("DONE")
        .disease("COVID-19")
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .eventDate(LocalDate.now().minusDays(1))
        .reportDate(LocalDate.now())
        .eventLocation("Public place")
        .riskLevel("High risk")
        .sourceType("Mathematical model")
        .build();
  }
}
