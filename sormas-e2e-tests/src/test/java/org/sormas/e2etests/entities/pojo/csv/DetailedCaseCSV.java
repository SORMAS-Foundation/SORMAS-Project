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
@AllArgsConstructor // can take only 255 parameters in one pojo
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Builder(toBuilder = true, builderClassName = "builder")
public class DetailedCaseCSV {

  String country;
  String id;
  String uuid;
  String epidNumber;
  String externalID;
  String externalToken;
  String internalToken;
  String disease;
  String diseaseDetails;
  String diseaseVariant;
  String diseaseVariantDetails;
  String personUuid;
  String personFirstName;
  String personLastName;
  String personSex;
  String pregnant;
  String trimester;
  String postpartum;
  String personApproximateAge;
  String personAgeGroup;
  String birthdate;
  String reportDate;
  String responsibleRegion;
  String responsibleDistrict;
  String responsibleCommunity;
  String region;
  String district;
  String community;
  String facilityType;
  String healthFacility;
  String healthFacilityDetails;
  String pointOfEntry;
  String pointOfEntryDetails;
  String initialDetectionPlace;
  String caseClassification;
  String clinicalConfirmation;
  String epidemiologicalConfirmation;
  String laboratoryDiagnosticConfirmation;
  String investigationStatus;
  String investigatedDate;
  String outcome;
  String outcomeDate;
  String sequelae;
  String sequelaeDetails;
  String quarantine;
  String quarantineTypeDetails;
  String quarantineFrom;
  String quarantineTo;
  String previousQuarantineTo;
  String quarantineChangeComment;
  String quarantineHelpNeeded;
  String quarantineExtended;
  String quarantineReduced;
  String maxSourceCaseClassification;
  String associatedWithOutbreak;
  String hospitalizationAdmittedToHealthFacility;
  String hospitalizationAdmissionDate;
  String hospitalizationDischargeDate;
  String hospitalizationLeftAgainstAdvice;
  String personPresentCondition;
  String personDeathDate;
  String burialInfo;
  String personAddressRegion;
  String personAddressDistrict;
  String personAddressCommunity;
  String personAddressCity;
  String personAddressStreet;
  String personAddressHouseNumber;
  String personAddressAdditionalInformation;
  String personAddressPostalCode;
  String addressGpsCoordinates;
  String personAddressFacility;
  String personAddressFacilityDetails;
  String personPhone;
  String personPhoneOwner;
  String personEmailAddress;
  String personOtherContactDetails;
  String personEducationType;
  String personEducationDetails;
  String personOccupationType;
  String personOccupationDetails;
  String personArmedForcesRelationType;
  String traveled;
  String travelHistory;
  String burialAttended;
  String epiDataContactWithSourceCaseKnown;
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
}
