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
  private final String emailDomain = "@CONTACT.com";

  @Inject
  public TravelEntryService(Faker faker) {
    this.faker = faker;
  }

  public TravelEntry buildGeneratedEntry() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return TravelEntry.builder()
        .firstName(firstName)
        .lastName(lastName)
        .sex(GenderValues.getRandomGenderDE())
        .reportDate(LocalDate.now().minusDays(random.nextInt(10)))
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .disease("COVID-19")
        .pointOfEntry("Anderer Einreiseort")
        .pointOfEntryDetails("Automated test dummy description")
        .build();
  }
}
