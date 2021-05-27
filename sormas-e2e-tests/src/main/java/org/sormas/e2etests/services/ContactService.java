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
import org.sormas.e2etests.pojo.web.Contact;

public class ContactService {
  private final Faker faker;

  @Inject
  public ContactService(Faker faker) {
    this.faker = faker;
  }

  public Contact buildGeneratedContact() {
    return Contact.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .dateOfBirth(LocalDate.of(1904, 3, 7))
        .sex("Male")
        .nationalHealthId(UUID.randomUUID().toString())
        .passportNumber(String.valueOf(System.currentTimeMillis()))
        .primaryEmailAddress(faker.internet().emailAddress())
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .returningTraveler("NO")
        .reportDate(LocalDate.now())
        .diseaseOfSourceCase("COVID-19")
        .caseIdInExternalSystem(UUID.randomUUID().toString())
        .dateOfLastContact(LocalDate.now())
        .caseOrEventInformation("Automated test dummy description")
        .responsibleRegion("Voreingestellte Bundesl\u00E4nder")
        .responsibleDistrict("Voreingestellter Landkreis")
        .responsibleCommunity("Voreingestellte Gemeinde")
        .additionalInformationOnContactType("Automated test dummy description")
        .typeOfContact("Touched fluid of source case")
        .contactCategory("Low risk contact")
        .relationshipWithCase("Work in the same environment")
        .descriptionOfHowContactTookPlace("Automated test dummy description")
        .build();
  }
}
