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

import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.util.Calendar;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.Immunization;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.enums.immunizations.ImmunizationManagementStatusValues;
import org.sormas.e2etests.enums.immunizations.MeansOfImmunizationValues;
import org.sormas.e2etests.enums.immunizations.StatusValues;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.steps.BaseSteps;

public class ImmunizationApiService {
  private final Faker faker;
  private static RunningConfiguration runningConfiguration;

  @Inject
  public ImmunizationApiService(
      Faker faker, BaseSteps baseSteps, RunningConfiguration runningConfiguration) {
    this.faker = faker;
    this.runningConfiguration = runningConfiguration;
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
        .reportingUser(
            runningConfiguration.getUserByRole(locale, UserRoles.NationalUser.getRole()).getUuid())
        .archived(false)
        .disease(DiseasesValues.getRandomDiseaseName())
        .immunizationStatus(StatusValues.getRandomImmunizationStatus())
        .meansOfImmunization(MeansOfImmunizationValues.getRandomMeansOfImmunization())
        .immunizationManagementStatus(
            ImmunizationManagementStatusValues.getRandomImmunizationManagementStatus())
        .responsibleRegion(
            RegionsValues.getUuidValueForLocale(
                RegionsValues.VoreingestellteBundeslander.getName(), locale))
        .responsibleDistrict(
            DistrictsValues.getUuidValueForLocale(
                DistrictsValues.VoreingestellterLandkreis.name(), locale))
        .responsibleCommunity(
            CommunityValues.getUuidValueForLocale(
                CommunityValues.VoreingestellteGemeinde.name(), locale))
        .build();
  }
}
