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
import java.util.UUID;
import org.sormas.e2etests.pojo.web.Person;

public class PersonService {
  private final Faker faker;

  @Inject
  public PersonService(Faker faker) {
    this.faker = faker;
  }

  public Person updateExistentPerson(
      Person person,
      String firstName,
      String lastName,
      String uuid,
      String passportNumber,
      String nationalHealthId,
      String email,
      String phone) {
    return person.toBuilder()
        .firstName(firstName)
        .lastName(lastName)
        .uuid(uuid)
        .passportNumber(passportNumber)
        .nationalHealthId(nationalHealthId)
        .emailAddress(email)
        .phoneNumber(phone)
        .build();
  }

  public Person buildGeneratedPerson() {
    String firstName = faker.name().firstName();
    String lastName = faker.name().lastName();
    return Person.builder()
        .firstName(firstName)
        .lastName(lastName)
        .salutation("Dear Sir")
        .dateOfBirth(LocalDate.of(1904, 3, 7))
        .sex("Male")
        .emailAddress(faker.internet().emailAddress())
        .phoneNumber(faker.phoneNumber().phoneNumber())
        .presentConditionOfPerson("Alive")
        .nationalHealthId(UUID.randomUUID().toString())
        .passportNumber(String.valueOf(System.currentTimeMillis()))
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
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation("Dummy description")
        .postalCode(faker.address().zipCode())
        .city(faker.address().cityName())
        .areaType("Urban")
        .contactPersonFirstName(faker.name().firstName())
        .contactPersonLastName(faker.name().lastName())
        .communityContactPerson(faker.name().firstName() + " " + faker.name().lastName())
        .birthName(firstName + " " + lastName)
        .nickname(firstName + "pie")
        .motherMaidenName(faker.name().firstName() + " " + faker.name().lastName())
        .motherName(faker.name().firstName() + " " + faker.name().lastName())
        .fatherName(faker.name().firstName() + " " + faker.name().lastName())
        .nameOfGuardians(faker.name().firstName())
        .personContactDetailsTypeOfContactDetails("Other")
        .personContactDetailsContactInformation(faker.funnyName().name())
        .build();
  }
}
