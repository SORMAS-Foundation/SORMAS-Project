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
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;
import org.sormas.e2etests.pojo.web.Person;

public class PersonService {
  private final Faker faker;

  private String firstName;
  private String lastName;
  private final String emailDomain = "@PERSON.com";

  @Inject
  public PersonService(Faker faker) {
    this.faker = faker;
  }

  public Person updateExistentPerson(
      Person person, String firstName, String lastName, String uuid, String email, String phone) {
    return person.toBuilder()
        .firstName(firstName)
        .lastName(lastName)
        .uuid(uuid)
        .emailAddress(ASCIIHelper.convertASCIIToLatin(email))
        .phoneNumber(phone)
        .build();
  }

  public Person buildGeneratedPerson() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();

    return Person.builder()
        .firstName(firstName)
        .lastName(lastName)
        .salutation("Dear Sir")
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .emailAddress(ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .phoneNumber(faker.phoneNumber().phoneNumber())
        .presentConditionOfPerson("Alive")
        .externalId(UUID.randomUUID().toString())
        .externalToken(UUID.randomUUID().toString())
        .typeOfOccupation("Farmer")
        .staffOfArmedForces("Unknown")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory("Accommodation")
        .facilityType("Campsite")
        .facility("Other facility")
        .facilityNameAndDescription("Dummy description" + System.currentTimeMillis())
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation("Dummy description" + System.currentTimeMillis())
        .postalCode(faker.address().zipCode())
        .city(faker.address().cityName())
        .areaType("Urban")
        .contactPersonFirstName(faker.name().firstName())
        .contactPersonLastName(faker.name().lastName())
        .birthName(firstName + " " + lastName)
        .nameOfGuardians(faker.name().firstName() + "Guardian")
        .personContactDetailsTypeOfContactDetails("Other")
        .personContactDetailsContactInformation(faker.funnyName().name())
        .build();
  }
}
