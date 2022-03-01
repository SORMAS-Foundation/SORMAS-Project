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

package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Case {
  String caseOrigin;
  LocalDate dateOfBirth;
  LocalDate dateOfReport;
  LocalDate dateOfSymptomOnset;
  String disease;
  String reinfection;
  String outcomeOfCase;
  String reportingDistrict;
  String externalId;
  String externalToken;
  String uuid;
  String facilityCategory;
  String facilityType;
  String firstName;
  String lastName;
  String placeDescription;
  String responsibleJurisdiction;
  String placeOfStay;
  String region;
  String district;
  String pointOfEntry;
  String presentConditionOfPerson;
  String primaryEmailAddress;
  String primaryPhoneNumber;
  String responsibleCommunity;
  String community;
  String responsibleDistrict;
  String responsibleRegion;
  String prohibitionToWork;
  String homeBasedQuarantinePossible;
  String quarantine;
  String reportGpsLatitude;
  String reportGpsLongitude;
  String reportGpsAccuracyInM;
  String sequelae;
  String bloodOrganTissueDonationInTheLast6Months;
  String vaccinationStatusForThisDisease;
  String responsibleSurveillanceOfficer;
  LocalDate dateReceivedAtDistrictLevel;
  LocalDate dateReceivedAtRegionLevel;
  LocalDate dateReceivedAtNationalLevel;
  String generalComment;
  String caseIdentificationSource;
  String nosocomialOutbreak;
  String sex;
  String caseClassification;
  String clinicalConfirmation;
  String epidemiologicalConfirmation;
  String laboratoryDiagnosticConfirmation;
  String investigationStatus;
  String differentPlaceOfStayJurisdiction;
  String facility;
  String quarantineOrderedVerbally;
  String quarantineOrderedByDocument;
  String quarantineOrderSet;
  String vaccinationStatus;
  String facilityNameAndDescription;
  String street;
  String houseNumber;
  String additionalInformation;
  String postalCode;
  String city;
  String areaType;
  String country;
}
