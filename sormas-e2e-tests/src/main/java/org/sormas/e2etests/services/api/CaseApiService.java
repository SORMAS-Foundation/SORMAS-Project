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

import com.google.inject.Inject;
import java.util.Date;
import java.util.UUID;
import org.sormas.e2etests.pojo.api.*;

public class CaseApiService {

  @Inject
  public CaseApiService() {}

  public Case buildGeneratedCase(Person person) {
    return Case.builder()
        .disease("CORONAVIRUS")
        .diseaseDetails("Test Disease")
        .pseudonymized(false)
        .uuid(UUID.randomUUID().toString())
        .reportDate(new Date())
        .reportingUser(ReportingUser.builder().uuid("QLW4AN-TGWLRA-3UQVEM-WCDFCIVM").build())
        .district(District.builder().uuid("SZ75BK-5OUMFU-V2DTKG-5BYACHFE").build())
        .region(Region.builder().uuid("RKVAOM-ZNAAFU-R2KF6Z-6BENKHEY").build())
        .community(Community.builder().uuid("QWK33J-XYN3DE-5CSXFJ-MMFOKNKM").build())
        .followUpStatus("FOLLOW_UP")
        .person(person)
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(UUID.randomUUID().toString()).build())
        .hospitalization(Hospitalization.builder().uuid(UUID.randomUUID().toString()).build())
        .symptoms(Symptoms.builder().uuid(UUID.randomUUID().toString()).build())
        .therapy(Therapy.builder().uuid(UUID.randomUUID().toString()).build())
        .healthFacility(HealthFacility.builder().uuid("WYPOCQ-IWVWGQ-XU7YCF-OSQJSAD4").build())
        .maternalHistory(MaternalHistory.builder().uuid(UUID.randomUUID().toString()).build())
        .portHealthInfo(PortHealthInfo.builder().uuid(UUID.randomUUID().toString()).build())
        .clinicalCourse(
            ClinicalCourse.builder()
                .uuid(UUID.randomUUID().toString())
                .healthConditions(
                    HealthConditions.builder().uuid(UUID.randomUUID().toString()).build())
                .build())
        .surveillanceOfficer(
            SurveillanceOfficer.builder().uuid("Q2IYCN-TNYTOY-4OAYCA-DW662MTA").build())
        .healthFacilityDetails("Details")
        .caseOrigin("IN_COUNTRY")
        .facilityType("HOSPITAL")
        .build();
  }
}
