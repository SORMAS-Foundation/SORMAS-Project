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
import java.util.Calendar;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.enums.CommunityUUIDs;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictUUIDs;
import org.sormas.e2etests.enums.RegionUUIDs;
import org.sormas.e2etests.enums.TestDataUser;
import org.sormas.e2etests.enums.immunizations.ImmunizationManagementStatusValues;
import org.sormas.e2etests.enums.immunizations.ImmunizationStatusValues;
import org.sormas.e2etests.enums.immunizations.MeansOfImmunizationValues;
import org.sormas.e2etests.helpers.api.ImmunizationHelper;
import org.sormas.e2etests.pojo.api.Immunization;
import org.sormas.e2etests.pojo.api.Person;
import org.sormas.e2etests.state.ApiState;

public class ImmunizationSteps implements En {

  @Inject
  public ImmunizationSteps(ImmunizationHelper immunizationHelper, ApiState apiState, Faker faker) {

    When(
        "API: I create {int} new immunizations for last created person",
        (Integer numberOfImmunizations) -> {
          String lastCreatedUserUUID = apiState.getLastCreatedPerson().getUuid();
          Person person = Person.builder().uuid(lastCreatedUserUUID).build();
          int immunizationAttempts = 1;
          while (immunizationAttempts <= numberOfImmunizations) {
            String immunizationUUID = UUID.randomUUID().toString();
            Immunization createImmunizationObject =
                Immunization.builder()
                    .uuid(immunizationUUID)
                    .pseudonymized(false)
                    .person(person)
                    .reportDate(Calendar.getInstance().getTimeInMillis())
                    .positiveTestResultDate(Calendar.getInstance().getTimeInMillis())
                    .recoveryDate(Calendar.getInstance().getTimeInMillis())
                    .startDate(Calendar.getInstance().getTimeInMillis())
                    .endDate(Calendar.getInstance().getTimeInMillis())
                    .externalId(faker.number().digits(9))
                    .reportingUser(TestDataUser.NATIONAL_USER.getUuid())
                    .archived(false)
                    .disease(DiseasesValues.getRandomDiseaseName())
                    .immunizationStatus(ImmunizationStatusValues.getRandomImmunizationStatus())
                    .meansOfImmunization(MeansOfImmunizationValues.getRandomMeansOfImmunization())
                    .immunizationManagementStatus(
                        ImmunizationManagementStatusValues.getRandomImmunizationManagementStatus())
                    .responsibleRegion(RegionUUIDs.VoreingestellteBundeslander.toString())
                    .responsibleDistrict(DistrictUUIDs.VoreingestellterLandkreis.toString())
                    .responsibleCommunity(CommunityUUIDs.VoreingestellteGemeinde.toString())
                    .build();
            apiState.setCreatedImmunization(createImmunizationObject);
            immunizationHelper.createNewImmunization(createImmunizationObject);
            immunizationAttempts++;
          }
        });

    When("API: I receive all immunizations ids", immunizationHelper::getAllImmunizationsUUIDs);

    Then(
        "API: I check that POST immunization call body is {string}",
        (String expectedBody) -> {
          Response response =
              immunizationHelper.getImmunizationBasedOnUUID(
                  apiState.getCreatedImmunization().getUuid());
          String responseBody = response.getBody().toString();
          Truth.assertThat(expectedBody.equals(String.valueOf(responseBody)));
        });
  }
}
