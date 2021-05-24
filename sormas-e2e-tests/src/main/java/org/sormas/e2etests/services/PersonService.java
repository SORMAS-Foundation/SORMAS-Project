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
import org.sormas.e2etests.pojo.Contact;
import org.sormas.e2etests.pojo.Person;

import java.time.LocalDate;
import java.util.UUID;

public class PersonService {
  private final Faker faker;

  @Inject
  public PersonService(Faker faker) {
    this.faker = faker;
  }

  public Person buildGeneratedPerson() {
    return Person.builder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .salutation("Dear Sir")
            .dateOfBirth(LocalDate.of(1904, 3, 7))
            .sex("Male")
            .presentConditionOfPerson("Alive")
            .nationalHealthId(UUID.randomUUID().toString())
            .passportNumber(String.valueOf(System.currentTimeMillis()))
            .emailAddress(faker.internet().emailAddress())
            .phoneNumber(faker.phoneNumber().phoneNumber())
            .externalId(UUID.randomUUID().toString())
            .externalToken(UUID.randomUUID().toString())
            .typeOfOccupation("Farmer")
            .staffOfArmedForces("Unknown")
            .education("Primary")
            .region("Voreingestellte Bundesl\u00E4nder")
            .district("Voreingestellter Landkreis")
            .community("Voreingestellte Gemeinde")
            .facilityCategory("Accommodation")
            .facilityType("Campsite")
            .facility("Other facility")
            .facilityNameAndDescription("Dummy description")
            
            .build();
  }
}
