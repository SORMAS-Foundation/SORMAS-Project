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

package org.sormas.e2etests.entities.services.api;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.pojo.api.chunks.Address;
import org.sormas.e2etests.entities.pojo.api.chunks.Country;
import org.sormas.e2etests.entities.pojo.api.chunks.PersonContactDetails;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;

public class PersonApiService {
  private final Faker faker;
  private final Random random = new Random();
  private RestAssuredClient restAssuredClient;

  private String firstName;
  private String lastName;
  private final String emailDomain = "@PERSON-API.com";

  private String contactFirstName;
  private String contactLastName;
  private final String contactEmailDomain = "@PERSON-API-CONTACT.com";

  @Inject
  public PersonApiService(Faker faker, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.faker = faker;
  }

  public Person buildGeneratedPerson() {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    String personUUID = UUID.randomUUID().toString();
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    contactFirstName = faker.name().firstName();
    contactLastName = faker.name().lastName();

    Address address =
        Address.builder()
            .latitude(48 + (random.nextInt(6)) + ThreadLocalRandom.current().nextDouble(0, 1))
            .longitude(8 + (random.nextInt(5)) + ThreadLocalRandom.current().nextDouble(0, 1))
            .country(Country.builder().uuid(environmentManager.getCountryUUID("Germany")).build())
            .region(
                environmentManager.getRegionUUID(
                    RegionsValues.VoreingestellteBundeslander.getName()))
            .continent(environmentManager.getContinentUUID(ContinentUUIDs.Europe.name()))
            .subcontinent(
                environmentManager.geSubcontinentUUID(SubcontinentUUIDs.WesternEurope.getName()))
            .district(
                environmentManager.getDistrictUUID(
                    DistrictsValues.VoreingestellterLandkreis.getName()))
            .community(
                environmentManager.getCommunityUUID(
                    CommunityValues.VoreingestellteGemeinde.getName()))
            .city(faker.address().cityName())
            .areaType(AreaTypeValues.getRandomAreaType())
            .postalCode(faker.address().zipCode())
            .street(faker.address().streetName())
            .houseNumber(faker.address().buildingNumber())
            .facilityType("CAMPSITE")
            .facility(FacilityUUIDs.OtherFacility.toString())
            .facilityDetails("Dummy description")
            .details("Dummy text")
            .contactPersonFirstName(contactFirstName)
            .contactPersonLastName(contactLastName)
            .contactPersonPhone(faker.phoneNumber().cellPhone())
            .contactPersonEmail(
                ASCIIHelper.convertASCIIToLatin(
                    contactFirstName + "." + contactLastName + contactEmailDomain))
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
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .birthdateDD(faker.number().numberBetween(1, 27))
        .birthdateMM(faker.number().numberBetween(1, 12))
        .birthdateYYYY(faker.number().numberBetween(1900, 2005))
        .sex(GenderValues.getRandomGender().toUpperCase())
        .phone(faker.phoneNumber().phoneNumber())
        .address(address)
        .personContactDetails(Collections.singletonList(personContactDetails))
        .presentCondition(PresentCondition.getRandomPresentCondition().toUpperCase())
        .build();
  }
}
