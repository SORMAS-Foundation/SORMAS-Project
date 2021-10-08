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

import static org.sormas.e2etests.enums.DiseasesValues.getRandomDiseaseCaption;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.pojo.web.ContactsLineListing;

public class ContactsLineListingService {
  private final Faker faker;

  @Inject
  public ContactsLineListingService(Faker faker) {
    this.faker = faker;
  }

  public ContactsLineListing buildGeneratedLineListingContacts() {
    long currentTimeMillis = System.currentTimeMillis();
    return ContactsLineListing.builder()
        .disease(getRandomDiseaseCaption())
        .region("Voreingestellte")
        .district("Voreingestellter Landkreis")
        .dateOfReport(LocalDate.now().minusDays(5))
        .dateOfLastContact(LocalDate.now().minusDays(8))
        .typeOfContact("Was in same room or house with source case")
        .relationshipWithCase("Live in the same household")
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .birthYear("1991")
        .birthMonth("May")
        .birthDay("15")
        .sex("Male")
        .build();
  }

  public ContactsLineListing buildGeneratedSecondLine() {
    long currentTimeMillis = System.currentTimeMillis();
    return ContactsLineListing.builder()
        .dateOfReport(LocalDate.now().minusDays(6))
        .dateOfLastContact(LocalDate.now().minusDays(9))
        .typeOfContact("Airplane, sitting up to two rows in front or behind the source case ")
        .relationshipWithCase("Work in the same environment")
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .birthYear("1992")
        .birthMonth("April")
        .birthDay("25")
        .sex("Female")
        .build();
  }
}
