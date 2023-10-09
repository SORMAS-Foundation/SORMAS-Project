/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.entities.pojo.web.Event;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;

public class EventService {

  private final Faker faker;

  @Inject
  public EventService(Faker faker) {
    this.faker = faker;
  }

  public Event buildGeneratedEventDE() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EREIGNIS")
        .investigationStatus("UNTERSUCHUNG AUSSTEHEND")
        .eventManagementStatus("FORTLAUFEND")
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseCaption())
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .eventDate(LocalDate.now().minusDays(2))
        .reportDate(LocalDate.now().minusDays(1))
        .eventLocation("Zuhause")
        .riskLevel("Geringes Risiko")
        .sourceType("Nicht erhoben")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .build();
  }

  public Event buildGeneratedEventWithCorrectRegionAndDisctrictDE() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EREIGNIS")
        .investigationStatus("UNTERSUCHUNG AUSSTEHEND")
        .eventManagementStatus("FORTLAUFEND")
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseCaption())
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .eventDate(LocalDate.now().minusDays(2))
        .reportDate(LocalDate.now().minusDays(1))
        .eventLocation("Zuhause")
        .riskLevel("Geringes Risiko")
        .sourceType("Nicht erhoben")
        .region("Bremen")
        .district("SK Bremen")
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .build();
  }

  public Event buildGeneratedEventWithCreatedFacilityDE(
      String facilityCategory, String facilityType, String facilityName) {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EREIGNIS")
        .investigationStatus("UNTERSUCHUNG AUSSTEHEND")
        .eventManagementStatus("FORTLAUFEND")
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseCaption())
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .eventDate(LocalDate.now().minusDays(2))
        .reportDate(LocalDate.now().minusDays(1))
        .eventLocation("Einrichtung")
        .riskLevel("Geringes Risiko")
        .sourceType("Nicht erhoben")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory(facilityCategory)
        .facilityType(facilityType)
        .facility(facilityName)
        .build();
  }

  public Event buildGeneratedEvent() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EVENT")
        .investigationStatus("INVESTIGATION PENDING")
        .eventManagementStatus("ONGOING")
        .disease("COVID-19")
        .diseaseVariant("B.1.617.3")
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

  public Event buildGeneratedEventWithDate(LocalDate date) {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("EVENT")
        .investigationStatus("INVESTIGATION PENDING") // change back to ongoing after bug fix 5547
        .eventManagementStatus("ONGOING")
        .disease("COVID-19")
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .reportDate(date)
        .eventDate(date)
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

  public Event buildClusterWithMandatoryFields() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Event.builder()
        .eventStatus("CLUSTER")
        .reportDate(LocalDate.now())
        .title("EVENT_AUTOMATION_" + timestamp + faker.address().city())
        .region("Berlin")
        .district("SK Berlin Mitte")
        .build();
  }

  public LocalDate[] timeRange;
}
