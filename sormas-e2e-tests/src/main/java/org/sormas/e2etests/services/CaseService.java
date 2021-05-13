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
import org.sormas.e2etests.pojo.Case;

public class CaseService {
  private final Faker faker;

  @Inject
  public CaseService(Faker faker) {
    this.faker = faker;
  }

  public Case buildGeneratedCase() {
    return Case.builder()
        .caseOrigin("IN-COUNTRY")
        .dateOfReport(LocalDate.now())
        .externalId(UUID.randomUUID().toString())
        .disease("COVID-19")
        .responsibleRegion("Voreingestellte Bundesländer")
        .responsibleDistrict("Voreingestellter Landkreis")
        .responsibleCommunity("Voreingestellte Gemeinde")
        .placeOfStay("HOME")
        .placeDescription(faker.address().streetAddressNumber())
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .dateOfBirth(LocalDate.of(1902, 3, 7))
        .sex("Male")
        .nationalHealthId(UUID.randomUUID().toString())
        .passportNumber(String.valueOf(System.currentTimeMillis()))
        .presentConditionOfPerson("Alive")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(faker.internet().emailAddress())
        .build();
  }
}
