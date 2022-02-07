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

import java.util.Date;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.TestDataUser;
import org.sormas.e2etests.pojo.api.*;

public class ContactApiService {

  private static PersonApiService personApiService;

  @Inject
  public ContactApiService(PersonApiService personApiService) {
    this.personApiService = personApiService;
  }
  ;

  public Contact buildGeneratedContact(Person person) {
    return Contact.builder()
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .uuid(UUID.randomUUID().toString())
        .reportDateTime(new Date())
        .reportingUser(ReportingUser.builder().uuid(TestDataUser.REST_AUTOMATION.getUuid()).build())
        .district(
            District.builder()
                .caption(DistrictsValues.VoreingestellterLandkreis.getName())
                .uuid(DistrictsValues.VoreingestellterLandkreis.getUuid())
                .build())
        .region(
            Region.builder()
                .caption(RegionsValues.VoreingestellteBundeslander.getName())
                .uuid(RegionsValues.VoreingestellteBundeslander.getUuid())
                .build())
        .relationToCase("")
        .contactClassification("UNCONFIRMED")
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .epiData(EpiData.builder().uuid(UUID.randomUUID().toString()).build())
        .healthConditions(HealthConditions.builder().uuid(UUID.randomUUID().toString()).build())
        .relationToCase("SAME_HOUSEHOLD")
        .build();
  }

  public Contact buildGeneratedContactWithLinkedCase(Person person, Case caze) {
    return Contact.builder()
        .uuid(UUID.randomUUID().toString())
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .reportDateTime(new Date())
        .reportingUser(ReportingUser.builder().uuid(TestDataUser.REST_AUTOMATION.getUuid()).build())
        .district(
            District.builder()
                .caption(DistrictsValues.VoreingestellterLandkreis.getName())
                .uuid(DistrictsValues.VoreingestellterLandkreis.getUuid())
                .build())
        .region(
            Region.builder()
                .caption(RegionsValues.VoreingestellteBundeslander.getName())
                .uuid(RegionsValues.VoreingestellteBundeslander.getUuid())
                .build())
        .relationToCase("")
        .contactClassification("UNCONFIRMED")
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .epiData(EpiData.builder().uuid(UUID.randomUUID().toString()).build())
        .healthConditions(HealthConditions.builder().uuid(UUID.randomUUID().toString()).build())
        .relationToCase("SAME_HOUSEHOLD")
        .caze(Case.builder().uuid(caze.getUuid()).build())
        .build();
  }
}
