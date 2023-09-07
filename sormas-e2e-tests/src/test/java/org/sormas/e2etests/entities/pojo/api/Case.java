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
package org.sormas.e2etests.entities.pojo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.LinkedHashMap;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Case {
  Long creationDate;
  String caseClassification;
  String uuid;
  String followUpStatus;
  ClinicalCourse clinicalCourse;
  Date reportDate;
  ReportingUser reportingUser;
  SurveillanceOfficer surveillanceOfficer;
  String outcome;
  String investigationStatus;
  String caseOrigin;
  String disease;
  String diseaseDetails;
  String healthFacilityDetails;
  Hospitalization hospitalization;
  Therapy therapy;
  Community community;
  Date followUpUntil;
  Symptoms symptoms;
  EpiData epiData;
  Boolean pseudonymized;
  Person person;
  PortHealthInfo portHealthInfo;
  District district;
  Region region;
  District responsibleDistrict;
  Region responsibleRegion;
  HealthFacility healthFacility;
  HealthConditions healthConditions;
  MaternalHistory maternalHistory;
  String facilityType;
  String pointOfEntryDetails;
  PointOfEntryDetails pointOfEntry;
  Boolean sharedToCountry;
  Boolean nosocomialOutbreak;
  Boolean quarantineOrderedVerbally;
  Boolean quarantineOrderedOfficialDocument;
  Boolean quarantineExtended;
  Boolean quarantineReduced;
  Boolean quarantineOfficialOrderSent;
  Boolean overwriteFollowUpUntil;
  Boolean ownershipHandedOver;
  Boolean notACaseReasonNegativeTest;
  Boolean notACaseReasonPhysicianInformation;
  Boolean notACaseReasonDifferentPathogen;
  Boolean notACaseReasonOther;
  Boolean dontShareWithReportingTool;
  String caseReferenceDefinition;
  String vaccinationStatus;
  String quarantine;
  String reInfection;
  String reinfectionStatus;
  LinkedHashMap<String, Boolean> reinfectionDetails;
}
