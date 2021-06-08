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
import org.sormas.e2etests.pojo.api.*;

public class ContactApiService {

  public Contact buildGeneratedContact(Person person) {
    return Contact.builder()
        .disease("CORONAVIRUS")
        .uuid(UUID.randomUUID().toString())
        .reportDateTime(new Date())
        .reportingUser(ReportingUser.builder().uuid("QLW4AN-TGWLRA-3UQVEM-WCDFCIVM").build())
        .district(
            District.builder()
                .caption("Voreingestellter Landkreis")
                .uuid("SZ75BK-5OUMFU-V2DTKG-5BYACHFE")
                .build())
        .region(
            Region.builder()
                .caption("Voreingestellte Bundesl\u00E4nder")
                .uuid("RKVAOM-ZNAAFU-R2KF6Z-6BENKHEY")
                .build())
        .relationToCase("")
        .contactClassification("UNCONFIRMED")
        .followUpStatus("FOLLOW_UP")
        .person(person)
        .epiData(EpiData.builder().uuid(UUID.randomUUID().toString()).build())
        .healthConditions(HealthConditions.builder().uuid(UUID.randomUUID().toString()).build())
        .vaccinationInfo(VaccinationInfo.builder().uuid(UUID.randomUUID().toString()).build())
        .relationToCase("SAME_HOUSEHOLD")
        .build();
  }
}
