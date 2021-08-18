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
package org.sormas.e2etests.steps.api;

import com.github.javafaker.Faker;
import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import io.restassured.response.Response;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.helpers.api.PersonsHelper;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.pojo.api.chunks.Address;
import org.sormas.e2etests.pojo.api.chunks.Country;
import org.sormas.e2etests.pojo.api.chunks.PersonContactDetails;
import org.sormas.e2etests.state.ApiState;

public class PersonSteps implements En {

  @Inject
  public PersonSteps(PersonsHelper personsHelper, ApiState apiState, Faker faker) {

    When(
        "API: I receive the person",
        () -> {
          List<String> personUuids = apiState.getResponse().jsonPath().get();
        });

    When(
        "API: I create a new person",
        () -> {
          String personUUID = UUID.randomUUID().toString();

          Address address =
              Address.builder()
                  .latitude(52.28339547689361)
                  .longitude(10.5794084300839)
                  .country(
                      Country.builder()
                          .uuid(CountryUUIDs.Germany.toString())
                          .caption("Deutschland")
                          .externalId(null)
                          .isoCode("DEU")
                          .build())
                  .region(RegionUUIDs.VoreingestellteBundeslander.toString())
                  .continent(ContinentUUIDs.Europe.toString())
                  .subcontinent(SubcontinentUUIDs.WesternEurope.toString())
                  .district(DistrictUUIDs.VoreingestellterLandkreis.toString())
                  .community(CommunityUUIDs.VoreingestellteGemeinde.toString())
                  .city(faker.address().cityName())
                  .areaType("URBAN")
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

          Person createPersonObject =
              Person.builder()
                  .uuid(personUUID)
                  .firstName(faker.name().firstName())
                  .lastName(faker.name().lastName())
                  .birthdateDD(faker.number().numberBetween(1, 29))
                  .birthdateMM(faker.number().numberBetween(1, 12))
                  .birthdateYYYY(faker.number().numberBetween(1900, 2005))
                  .sex("MALE")
                  .phone(faker.phoneNumber().phoneNumber())
                  .address(address)
                  .personContactDetails(Collections.singletonList(personContactDetails))
                  .build();
          apiState.setLastCreatedPerson(createPersonObject);
          personsHelper.createNewPerson(createPersonObject);
        });

    When("API: I receive all person ids", personsHelper::getAllPersonUuid);

    Then(
        "API: I check that POST person call body is {string}",
        (String expectedBody) -> {
          Response response =
              personsHelper.getPersonBasedOnUUID(apiState.getLastCreatedPerson().getUuid());
          String responseBody = response.getBody().toString();
          Truth.assertThat(expectedBody.equals(String.valueOf(responseBody)));
        });
  }
}
