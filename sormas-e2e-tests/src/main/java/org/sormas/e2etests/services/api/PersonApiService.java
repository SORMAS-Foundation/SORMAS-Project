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

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.sormas.e2etests.enums.AreaTypeValues;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.ContinentUUIDs;
import org.sormas.e2etests.enums.CountryUUIDs;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.FacilityUUIDs;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.SubcontinentUUIDs;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.pojo.api.chunks.Address;
import org.sormas.e2etests.pojo.api.chunks.Country;
import org.sormas.e2etests.pojo.api.chunks.PersonContactDetails;

public class PersonApiService {
  private final Faker faker;
  private final Random random = new Random();

  @Inject
  public PersonApiService(Faker faker) {
    this.faker = faker;
  }

  // TODO check if this can be changed with the other builder method
  public Person buildSimpleGeneratedPerson() {
    return Person.builder()
        .uuid(UUID.randomUUID().toString())
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .sex("Male")
        .phone(faker.phoneNumber().phoneNumber())
        .build();
  }

  public Person buildGeneratedPerson() {
    String personUUID = UUID.randomUUID().toString();

    Address address =
        Address.builder()
            .latitude(48 + (random.nextInt(6)) + ThreadLocalRandom.current().nextDouble(0, 1))
            .longitude(8 + (random.nextInt(5)) + ThreadLocalRandom.current().nextDouble(0, 1))
            .country(
                Country.builder()
                    .uuid(CountryUUIDs.Germany.toString())
                    .caption("Deutschland")
                    .externalId(null)
                    .isoCode("DEU")
                    .build())
            .region(RegionsValues.VoreingestellteBundeslander.getUuid())
            .continent(ContinentUUIDs.Europe.toString())
            .subcontinent(SubcontinentUUIDs.WesternEurope.toString())
            .district(DistrictsValues.VoreingestellterLandkreis.getUuid())
            .community(CommunityValues.VoreingestellteGemeinde.getUuid())
            .city(faker.address().cityName())
            .areaType(AreaTypeValues.getRandomAreaType())
            .postalCode(faker.address().zipCode())
            .street(faker.address().streetName())
            .houseNumber(faker.address().buildingNumber())
            .facilityType("CAMPSITE")
            .facility(FacilityUUIDs.OtherFacility.toString())
            .facilityDetails("Dummy description")
            .details("Dummy text")
            .contactPersonFirstName(faker.name().firstName())
            .contactPersonLastName(faker.name().lastName())
            .contactPersonPhone(faker.phoneNumber().cellPhone())
            .contactPersonEmail(faker.internet().emailAddress())
            .uuid(personUUID)
            .build();

    PersonContactDetails personContactDetails =
        PersonContactDetails.builder()
            .uuid(UUID.randomUUID().toString())
            .person(Person.builder().uuid(personUUID).build())
            .primaryContact(true)
            .thirdParty(false)
            .personContactDetailType("PHONE")
            .contactInformation(faker.phoneNumber().phoneNumber())
            .build();

    return Person.builder()
        .uuid(personUUID)
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .birthdateDD(faker.number().numberBetween(1, 29))
        .birthdateMM(faker.number().numberBetween(1, 12))
        .birthdateYYYY(faker.number().numberBetween(1900, 2005))
        .sex(GenderValues.getRandomGender().toUpperCase())
        .phone(faker.phoneNumber().phoneNumber())
        .address(address)
        .personContactDetails(Collections.singletonList(personContactDetails))
        .build();
  }
}
