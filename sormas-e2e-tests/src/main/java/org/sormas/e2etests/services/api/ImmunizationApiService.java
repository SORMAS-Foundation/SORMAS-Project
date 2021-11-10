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
import java.util.Calendar;
import java.util.UUID;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.TestDataUser;
import org.sormas.e2etests.enums.immunizations.ImmunizationManagementStatusValues;
import org.sormas.e2etests.enums.immunizations.MeansOfImmunizationValues;
import org.sormas.e2etests.enums.immunizations.StatusValues;
import org.sormas.e2etests.pojo.api.Immunization;
import org.sormas.e2etests.pojo.api.Person;

public class ImmunizationApiService {
  private final Faker faker;

  @Inject
  public ImmunizationApiService(Faker faker) {
    this.faker = faker;
  }

  public Immunization buildGeneratedImmunizationForPerson(Person person) {
    String immunizationUUID = UUID.randomUUID().toString();
    return Immunization.builder()
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
        .immunizationStatus(StatusValues.getRandomImmunizationStatus())
        .meansOfImmunization(MeansOfImmunizationValues.getRandomMeansOfImmunization())
        .immunizationManagementStatus(
            ImmunizationManagementStatusValues.getRandomImmunizationManagementStatus())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getUuid())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getUuid())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getUuid())
        .build();
  }
}
