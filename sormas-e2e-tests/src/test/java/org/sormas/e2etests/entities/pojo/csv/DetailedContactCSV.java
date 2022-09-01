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

package org.sormas.e2etests.entities.pojo.csv;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Builder(toBuilder = true, builderClassName = "builder")
public class DetailedContactCSV {

  String uuid;
  String externalID;
  String sourceCaseUuid;
  String caseClassification;
  String disease;
  String diseaseDetails;
  String contactClassification;
  String multiDayContact;
  String firstContactDate;
  String lastContactDate;
  String creationDate;
  String personUuid;
  String personFirstName;
  String personLastName;
  String personSex;
  String birthdate;
  String personApproximateAge;
  String reportDateTime;
  String region;
  String district;
  String community;
  String contactIdentificationSource;
  String contactIdentificationSourceDetails;
  String tracingApp;
  String tracingAppDetails;
  String contactProximity;
  String contactStatus;
  String completeness;
  String followUpStatus;
  String followUpUntil;
  String quarantine;
  String quarantineTypeDetails;
  String quarantineFrom;
  String quarantineTo;
  String previousQuarantineTo;
  String quarantineChangeComment;
  String quarantineHelpNeeded;
  String quarantineExtended;
  String quarantineReduced;
  String prohibitionToWork;
  String prohibitionToWorkFrom;
  String prohibitionToWorkUntil;
  String personPresentCondition;
  String personDeathDate;
  String personAddressRegion;
  String personAddressDistrict;
  String personAddressCommunity;
  String personAddressCity;
  String personAddressStreet;
  String personAddressHouseNumber;
  String personAddressAdditionalInformation;
  String personAddressPostalCode;
  String personAddressFacility;
  String personAddressFacilityDetails;
  String personPhone;
  String personPhoneOwner;
  String personEmailAddress;
  String personOtherContactDetails;
  String personOccupationType;
  String personOccupationDetails;
  String personArmedForcesRelationType;
  String numberOfVisits;
  String lastCooperativeVisitSymptomatic;
  String lastCooperativeVisitDate;
  String lastCooperativeVisitSymptoms;
  String traveled;
  String travelHistory;
  String burialAttended;
  String epiDataContactWithSourceCaseKnown;
  String returningTraveler;
  String vaccinationStatus;
  String numberOfDoses;
  String vaccinationInfoSource;
  String firstVaccinationDate;
  String lastVaccinationDate;
  String vaccineName;
  String otherVaccineName;
  String vaccineManufacturer;
  String otherVaccineManufacturer;
  String vaccineInn;
  String vaccineBatchNumber;
  String vaccineUniiCode;
  String vaccineAtcCode;
  String latestEventId;
  String latestEventTitle;
  String eventCount;
  String externalToken;
  String internalToken;
  String personSymptomJournalStatus;
  String reportingUserName;
  String reportingUserRoles;
  String followUpStatusChangeUserName;
  String followUpStatusChangeUserRoles;
}
