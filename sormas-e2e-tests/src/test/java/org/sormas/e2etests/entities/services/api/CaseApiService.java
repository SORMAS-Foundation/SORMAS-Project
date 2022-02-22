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

import com.google.inject.Inject;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.Case;
import org.sormas.e2etests.entities.pojo.api.ClinicalCourse;
import org.sormas.e2etests.entities.pojo.api.Community;
import org.sormas.e2etests.entities.pojo.api.District;
import org.sormas.e2etests.entities.pojo.api.EpiData;
import org.sormas.e2etests.entities.pojo.api.HealthConditions;
import org.sormas.e2etests.entities.pojo.api.HealthFacility;
import org.sormas.e2etests.entities.pojo.api.Hospitalization;
import org.sormas.e2etests.entities.pojo.api.MaternalHistory;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.pojo.api.PortHealthInfo;
import org.sormas.e2etests.entities.pojo.api.Region;
import org.sormas.e2etests.entities.pojo.api.ReportingUser;
import org.sormas.e2etests.entities.pojo.api.SurveillanceOfficer;
import org.sormas.e2etests.entities.pojo.api.Symptoms;
import org.sormas.e2etests.entities.pojo.api.Therapy;
import org.sormas.e2etests.enums.CaseClassification;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;

public class CaseApiService {

  private static EnvironmentManager environmentManager;

  @Inject
  public CaseApiService(EnvironmentManager environmentManager) {
    this.environmentManager = environmentManager;
  }

  public Case buildGeneratedCase(Person person) {
    return Case.builder()
        .disease(DiseasesValues.getRandomDiseaseName())
        .diseaseDetails("Test Disease")
        .pseudonymized(false)
        .uuid(UUID.randomUUID().toString())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    environmentManager
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .district(
            District.builder().uuid(DistrictsValues.VoreingestellterLandkreis.getUuid()).build())
        .region(Region.builder().uuid(RegionsValues.VoreingestellteBundeslander.getUuid()).build())
        .responsibleDistrict(
            District.builder().uuid(DistrictsValues.VoreingestellterLandkreis.getUuid()).build())
        .responsibleRegion(
            Region.builder().uuid(RegionsValues.VoreingestellteBundeslander.getUuid()).build())
        .community(
            Community.builder().uuid(CommunityValues.VoreingestellteGemeinde.getUuid()).build())
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .caseClassification(CaseClassification.getRandomAPIClassification())
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(UUID.randomUUID().toString()).build())
        .hospitalization(Hospitalization.builder().uuid(UUID.randomUUID().toString()).build())
        .symptoms(
            Symptoms.builder()
                .uuid(UUID.randomUUID().toString())
                .pseudonymized(true)
                .symptomatic(false)
                .build())
        .therapy(Therapy.builder().uuid(UUID.randomUUID().toString()).build())
        .healthFacility(HealthFacility.builder().uuid("WYPOCQ-IWVWGQ-XU7YCF-OSQJSAD4").build())
        .maternalHistory(
            MaternalHistory.builder()
                .uuid(UUID.randomUUID().toString())
                .pseudonymized(true)
                .build())
        .portHealthInfo(PortHealthInfo.builder().uuid(UUID.randomUUID().toString()).build())
        .clinicalCourse(
            ClinicalCourse.builder()
                .uuid(UUID.randomUUID().toString())
                .healthConditions(
                    HealthConditions.builder().uuid(UUID.randomUUID().toString()).build())
                .build())
        .surveillanceOfficer(
            SurveillanceOfficer.builder()
                .uuid(
                    environmentManager
                        .getUserByRole(locale, UserRoles.SurveillanceOfficer.getRole())
                        .getUuid())
                .build())
        .healthFacilityDetails("Details")
        .caseOrigin("IN_COUNTRY")
        .facilityType("HOSPITAL")
        .pointOfEntryDetails("")
        .sharedToCountry(false)
        .nosocomialOutbreak(false)
        .quarantineOrderedVerbally(false)
        .quarantineOrderedOfficialDocument(false)
        .quarantineExtended(false)
        .quarantineReduced(false)
        .quarantineOfficialOrderSent(false)
        .followUpUntil(new Date())
        .overwriteFollowUpUntil(false)
        .ownershipHandedOver(false)
        .notACaseReasonNegativeTest(false)
        .notACaseReasonPhysicianInformation(false)
        .notACaseReasonDifferentPathogen(false)
        .notACaseReasonOther(false)
        .dontShareWithReportingTool(false)
        .caseReferenceDefinition("NOT_FULFILLED")
        .vaccinationStatus("VACCINATED")
        .quarantine("HOME")
        .reInfection("YES")
        .reinfectionStatus("CONFIRMED")
        .reinfectionDetails(
            new LinkedHashMap<String, Boolean>() {
              {
                put("GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN", true);
                put("GENOME_SEQUENCES_NOT_MATCHING", true);
                put("GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN", true);
              }
            })
        .build();
  }

  public Case buildCaseWithClassification(Person person, String classification) {
    Case caze = buildGeneratedCase(person);
    return caze.toBuilder()
        .caseClassification(CaseClassification.getAPIValueFor(classification))
        .build();
  }
}
