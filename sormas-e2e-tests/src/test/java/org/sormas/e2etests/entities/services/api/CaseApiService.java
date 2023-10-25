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

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.*;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class CaseApiService {

  private static RunningConfiguration runningConfiguration;
  private RestAssuredClient restAssuredClient;

  @Inject
  public CaseApiService(
      RunningConfiguration runningConfiguration, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.runningConfiguration = runningConfiguration;
  }

  @SneakyThrows
  public Case buildGeneratedCase(Person person) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Case.builder()
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .pseudonymized(false)
        .uuid(generateShortUUID())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .district(
            District.builder()
                .caption(DistrictsValues.VoreingestellterLandkreis.getName())
                .uuid(
                    environmentManager.getDistrictUUID(
                        DistrictsValues.VoreingestellterLandkreis.getName()))
                .build())
        .region(
            Region.builder()
                .caption(RegionsValues.VoreingestellteBundeslander.getName())
                .uuid(
                    environmentManager.getRegionUUID(
                        RegionsValues.VoreingestellteBundeslander.getName()))
                .build())
        .responsibleDistrict(
            District.builder()
                .uuid(
                    environmentManager.getDistrictUUID(
                        DistrictsValues.VoreingestellterLandkreis.getName()))
                .build())
        .responsibleRegion(
            Region.builder()
                .uuid(
                    environmentManager.getRegionUUID(
                        RegionsValues.VoreingestellteBundeslander.getName()))
                .build())
        .community(
            Community.builder()
                .uuid(
                    environmentManager.getCommunityUUID(
                        CommunityValues.VoreingestellteGemeinde.getName()))
                .build())
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(generateShortUUID()).build())
        .hospitalization(Hospitalization.builder().uuid(generateShortUUID()).build())
        .symptoms(
            Symptoms.builder()
                .uuid(generateShortUUID())
                .pseudonymized(true)
                .symptomatic(false)
                .build())
        .therapy(Therapy.builder().uuid(generateShortUUID()).build())
        .healthFacility(
            HealthFacility.builder()
                .uuid(
                    environmentManager.getHealthFacilityUUID(
                        RegionsValues.VoreingestellteBundeslander.getName(),
                        HealthFacilityValues.StandardEinrichtung.getName()))
                .build())
        .maternalHistory(
            MaternalHistory.builder().uuid(generateShortUUID()).pseudonymized(true).build())
        .portHealthInfo(PortHealthInfo.builder().uuid(generateShortUUID()).build())
        .clinicalCourse(ClinicalCourse.builder().uuid(generateShortUUID()).build())
        .healthConditions(HealthConditions.builder().uuid(generateShortUUID()).build())
        .surveillanceOfficer(
            SurveillanceOfficer.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.SurveillanceOfficer.getRole())
                        .getUuid())
                .build())
        .healthFacilityDetails("Details")
        .caseOrigin("IN_COUNTRY")
        .facilityType("HOSPITAL")
        .pointOfEntry(null)
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

  @SneakyThrows
  public Case buildGeneratedCaseWithCustomDistrictRegion(
      Person person, RegionsValues region, DistrictsValues district) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Case.builder()
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .pseudonymized(false)
        .uuid(generateShortUUID())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .district(
            District.builder()
                .caption(district.getName())
                .uuid(environmentManager.getDistrictUUID(district.getName()))
                .build())
        .region(
            Region.builder()
                .caption(region.getName())
                .uuid(environmentManager.getRegionUUID(region.getName()))
                .build())
        .responsibleDistrict(
            District.builder().uuid(environmentManager.getDistrictUUID(district.getName())).build())
        .responsibleRegion(
            Region.builder().uuid(environmentManager.getRegionUUID(region.getName())).build())
        .community(
            Community.builder()
                .uuid(
                    environmentManager.getCommunityUUID(
                        CommunityValues.CharlottenburgNord.getName()))
                .build())
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(generateShortUUID()).build())

        // .hospitalization(Hospitalization.builder().uuid(generateShortUUID()).build())
        // .symptoms(
        //  Symptoms.builder()
        //    .uuid(generateShortUUID())
        //  .pseudonymized(true)
        // .symptomatic(false)
        // .build())
        // .therapy(Therapy.builder().uuid(generateShortUUID()).build())
        // .healthFacility(
        //  HealthFacility.builder()
        //    .uuid(environmentManager.getHealthFacilityUUID(region.getName(), "Other facility"))
        //  .build())
        // .maternalHistory(
        //  MaternalHistory.builder().uuid(generateShortUUID()).pseudonymized(true).build())
        // .portHealthInfo(PortHealthInfo.builder().uuid(generateShortUUID()).build())
        // .clinicalCourse(ClinicalCourse.builder().uuid(generateShortUUID()).build())
        // .healthConditions(HealthConditions.builder().uuid(generateShortUUID()).build())
        // .surveillanceOfficer(
        //  SurveillanceOfficer.builder()
        //    .uuid(
        //      runningConfiguration
        //        .getUserByRole(locale, UserRoles.SurveillanceOfficer.getRole())
        //      .getUuid())
        // .build())
        // .healthFacilityDetails("Details")
        // .caseOrigin("IN_COUNTRY")
        // .facilityType("HOSPITAL")
        // .pointOfEntry(null)
        // .sharedToCountry(false)
        // .nosocomialOutbreak(false)
        // .quarantineOrderedVerbally(false)
        // .quarantineOrderedOfficialDocument(false)
        // .quarantineExtended(false)
        // .quarantineReduced(false)
        // .quarantineOfficialOrderSent(false)
        // .followUpUntil(new Date())
        // .overwriteFollowUpUntil(false)
        // .ownershipHandedOver(false)
        // .notACaseReasonNegativeTest(false)
        // .notACaseReasonPhysicianInformation(false)
        // .notACaseReasonDifferentPathogen(false)
        // .notACaseReasonOther(false)
        // .dontShareWithReportingTool(false)
        // .caseReferenceDefinition("NOT_FULFILLED")
        // .vaccinationStatus("VACCINATED")
        // .quarantine("HOME")
        // .reInfection("YES")
        // .reinfectionStatus("CONFIRMED")
        // .reinfectionDetails(
        //  new LinkedHashMap<String, Boolean>() {
        //  {
        //  put("GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN", true);
        // put("GENOME_SEQUENCES_NOT_MATCHING", true);
        // put("GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN", true);
        // }
        // })
        .build();
  }

  @SneakyThrows
  public Case buildGeneratedCaseWithCreationDate(Person person, Integer days) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Case.builder()
        .creationDate(
            LocalDateTime.parse(LocalDateTime.now().minusDays(days).toString())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli())
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .diseaseDetails("Test Disease")
        .pseudonymized(false)
        .uuid(generateShortUUID())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .district(
            District.builder()
                .uuid(
                    environmentManager.getDistrictUUID(
                        DistrictsValues.VoreingestellterLandkreis.getName()))
                .build())
        .region(
            Region.builder()
                .uuid(
                    environmentManager.getRegionUUID(
                        RegionsValues.VoreingestellteBundeslander.getName()))
                .build())
        .responsibleDistrict(
            District.builder()
                .uuid(
                    environmentManager.getDistrictUUID(
                        DistrictsValues.VoreingestellterLandkreis.getName()))
                .build())
        .responsibleRegion(
            Region.builder()
                .uuid(
                    environmentManager.getRegionUUID(
                        RegionsValues.VoreingestellteBundeslander.getName()))
                .build())
        .community(
            Community.builder()
                .uuid(
                    environmentManager.getCommunityUUID(
                        CommunityValues.VoreingestellteGemeinde.getName()))
                .build())
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(generateShortUUID()).build())
        .hospitalization(Hospitalization.builder().uuid(generateShortUUID()).build())
        .symptoms(
            Symptoms.builder()
                .uuid(generateShortUUID())
                .pseudonymized(true)
                .symptomatic(false)
                .build())
        .therapy(Therapy.builder().uuid(generateShortUUID()).build())
        .healthFacility(
            HealthFacility.builder()
                .uuid(
                    environmentManager.getHealthFacilityUUID(
                        RegionsValues.VoreingestellteBundeslander.getName(),
                        HealthFacilityValues.StandardEinrichtung.getName()))
                .build())
        .maternalHistory(
            MaternalHistory.builder().uuid(generateShortUUID()).pseudonymized(true).build())
        .portHealthInfo(PortHealthInfo.builder().uuid(generateShortUUID()).build())
        .clinicalCourse(ClinicalCourse.builder().uuid(generateShortUUID()).build())
        .healthConditions(HealthConditions.builder().uuid(generateShortUUID()).build())
        .surveillanceOfficer(
            SurveillanceOfficer.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.SurveillanceOfficer.getRole())
                        .getUuid())
                .build())
        .healthFacilityDetails("Details")
        .caseOrigin("IN_COUNTRY")
        .facilityType("HOSPITAL")
        .pointOfEntry(null)
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

  @SneakyThrows
  public Case buildGeneratedCaseWithParamRegionAndDistrictAndFacility(
      Person person, String region, String district, String facility) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Case.builder()
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .diseaseDetails("Test Disease")
        .pseudonymized(false)
        .uuid(generateShortUUID())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .district(District.builder().uuid(environmentManager.getDistrictUUID(district)).build())
        .region(Region.builder().uuid(environmentManager.getRegionUUID(region)).build())
        .responsibleDistrict(
            District.builder().uuid(environmentManager.getDistrictUUID(district)).build())
        .responsibleRegion(Region.builder().uuid(environmentManager.getRegionUUID(region)).build())
        .followUpStatus("FOLLOW_UP")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .epiData(EpiData.builder().uuid(generateShortUUID()).build())
        .hospitalization(Hospitalization.builder().uuid(generateShortUUID()).build())
        .symptoms(
            Symptoms.builder()
                .uuid(generateShortUUID())
                .pseudonymized(true)
                .symptomatic(false)
                .build())
        .therapy(Therapy.builder().uuid(generateShortUUID()).build())
        .healthFacility(
            HealthFacility.builder()
                .uuid(environmentManager.getHealthFacilityUUID(region, facility))
                .build())
        .maternalHistory(
            MaternalHistory.builder().uuid(generateShortUUID()).pseudonymized(true).build())
        .portHealthInfo(PortHealthInfo.builder().uuid(generateShortUUID()).build())
        .clinicalCourse(ClinicalCourse.builder().uuid(generateShortUUID()).build())
        .healthConditions(HealthConditions.builder().uuid(generateShortUUID()).build())
        .surveillanceOfficer(
            SurveillanceOfficer.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.SurveillanceOfficer.getRole())
                        .getUuid())
                .build())
        .healthFacilityDetails("Details")
        .caseOrigin("IN_COUNTRY")
        .facilityType("HOSPITAL")
        .pointOfEntryDetails("")
        .pointOfEntry(null)
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

  @SneakyThrows
  public Case buildGeneratedCaseTypePointOfEntry(Person person, String region, String district) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Case.builder()
        .uuid(generateShortUUID())
        .disease(DiseasesValues.CORONAVIRUS.getDiseaseName())
        .diseaseDetails("Test Disease")
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .reportDate(new Date())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .caseClassification("NOT_CLASSIFIED")
        .investigationStatus("PENDING")
        .outcome("NO_OUTCOME")
        .responsibleRegion(Region.builder().uuid(environmentManager.getRegionUUID(region)).build())
        .responsibleDistrict(
            District.builder().uuid(environmentManager.getDistrictUUID(district)).build())
        .district(District.builder().uuid(environmentManager.getDistrictUUID(district)).build())
        .region(Region.builder().uuid(environmentManager.getRegionUUID(region)).build())
        .healthFacilityDetails("")
        .healthConditions(
            HealthConditions.builder()
                .uuid(generateShortUUID())
                .creationDate(new Date())
                .changeDate(new Date())
                .build())
        .hospitalization(Hospitalization.builder().uuid(generateShortUUID()).build())
        .epiData(
            EpiData.builder()
                .uuid(generateShortUUID())
                .changeDate(new Date())
                .creationDate(new Date())
                .build())
        .therapy(
            Therapy.builder()
                .uuid(generateShortUUID())
                .changeDate(new Date())
                .creationDate(new Date())
                .build())
        .portHealthInfo(
            PortHealthInfo.builder()
                .uuid(generateShortUUID())
                .changeDate(new Date())
                .creationDate(new Date())
                .build())
        .caseOrigin("POINT_OF_ENTRY")
        .pointOfEntry(
            PointOfEntryDetails.builder()
                .uuid("SORMAS-CONSTID-OTHERS-AIRPORTX")
                .pointOfEntryType("AIRPORT")
                .build())
        .pointOfEntryDetails("other")
        .build();
  }

  public Case buildCaseWithClassification(Person person, String classification) {
    Case caze = buildGeneratedCase(person);
    return caze.toBuilder()
        .caseClassification(CaseClassification.getAPIValueFor(classification))
        .build();
  }
}
