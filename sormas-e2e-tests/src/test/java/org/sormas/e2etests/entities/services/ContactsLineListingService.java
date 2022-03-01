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

import static org.sormas.e2etests.enums.DiseasesValues.getRandomDiseaseCaption;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.entities.pojo.web.ContactsLineListing;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;

public class ContactsLineListingService {
  private final Faker faker;

  private String firstName;
  private String lastName;

  @Inject
  public ContactsLineListingService(Faker faker) {
    this.faker = faker;
  }

  public ContactsLineListing buildGeneratedLineListingContacts() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return ContactsLineListing.builder()
        .disease(getRandomDiseaseCaption())
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .dateOfReport(LocalDate.now().minusDays(5))
        .dateOfLastContact(LocalDate.now().minusDays(8))
        .typeOfContact("Was in same room or house with source case")
        .relationshipWithCase("Live in the same household")
        .firstName(firstName)
        .lastName(lastName)
        .birthYear(String.valueOf(faker.number().numberBetween(1900, 2008)))
        .birthMonth("May")
        .birthDay(String.valueOf(faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .build();
  }
}
