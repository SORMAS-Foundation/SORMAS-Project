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

package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.DiseasesValues.getRandomDiseaseCaptionDE;
import static org.sormas.e2etests.enums.PointOfEntryValues.getRandomPointOfEntryDE;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.Random;
import org.sormas.e2etests.entities.pojo.web.TravelEntry;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;

public class TravelEntryService {
  private final Faker faker;
  private static Random random = new Random();

  private String firstName;
  private String lastName;

  @Inject
  public TravelEntryService(Faker faker) {
    this.faker = faker;
  }

  public TravelEntry buildGeneratedEntryDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return TravelEntry.builder()
        .reportDate(LocalDate.now())
        .dateOfArrival(LocalDate.now())
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .reportDate(LocalDate.now())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .disease("COVID-19")
        .pointOfEntry(getRandomPointOfEntryDE())
        .pointOfEntryDetails("Automated test dummy description " + LocalDate.now())
        .build();
  }

  public TravelEntry buildGeneratedEntryWithParametrizedPersonDataDE(
      String firstName, String lastName, String sex) {
    return TravelEntry.builder()
        .reportDate(LocalDate.now())
        .dateOfArrival(LocalDate.now())
        .firstName(firstName)
        .lastName(lastName)
        .sex(sex)
        .reportDate(LocalDate.now())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .disease("COVID-19")
        .pointOfEntry(getRandomPointOfEntryDE())
        .pointOfEntryDetails("Automated test dummy description " + LocalDate.now())
        .build();
  }

  public TravelEntry buildGeneratedEntryWithPointOfEntryDetailsDE(String pointOfEntry) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return TravelEntry.builder()
        .reportDate(LocalDate.now())
        .dateOfArrival(LocalDate.now())
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .reportDate(LocalDate.now())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .disease(getRandomDiseaseCaptionDE())
        .pointOfEntry(getRandomPointOfEntryDE())
        .pointOfEntryDetails(pointOfEntry)
        .build();
  }

  public TravelEntry buildGeneratedEntryWithDifferentPointOfEntryDE() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return TravelEntry.builder()
        .reportDate(LocalDate.now())
        .dateOfArrival(LocalDate.now())
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .reportDate(LocalDate.now())
        .pointOfEntryRegion("Berlin")
        .pointOfEntryDistrict("SK Berlin Mitte")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .disease("COVID-19")
        .pointOfEntry("Anderer Einreiseort")
        .pointOfEntryDetails("Automated test dummy description " + LocalDate.now())
        .build();
  }
}
