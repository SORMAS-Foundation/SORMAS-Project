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

package org.sormas.e2etests.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Contact {
  String firstName;
  String lastName;
  LocalDate dateOfBirth;
  String sex;
  String primaryEmailAddress;
  String primaryPhoneNumber;
  String returningTraveler;
  LocalDate reportDate;
  String diseaseOfSourceCase;
  String caseIdInExternalSystem;
  LocalDate dateOfLastContact;
  String caseOrEventInformation;
  String responsibleDistrict;
  String responsibleRegion;
  String responsibleCommunity;
  String additionalInformationOnContactType;
  String typeOfContact;
  String contactCategory;
  String relationshipWithCase;
  String descriptionOfHowContactTookPlace;
  String uuid;
  String classification;
  String status;
  String multiDay;
  LocalDate dateOfFirstContact;
  String externalId;
  String externalToken;
  String reportingDistrict;
  String identificationSource;
  String identificationSourceDetails;
  String category;
  String prohibitionToWork;
  String homeBasedQuarantinePossible;
  String quarantine;
  String highPriority;
  String diabetes;
  String immunodeficiencyIncludingHiv;
  String liverDisease;
  String malignancy;
  String chronicPulmonaryDisease;
  String renalDisease;
  String chronicNeurologicalNeuromuscularDisease;
  String cardiovascularDiseaseIncludingHypertension;
  String additionalRelevantPreexistingConditions;
  String vaccinationStatusForThisDisease;
  String immunosuppressiveTherapy;
  String activeInCare;
  boolean cancelFollowUp;
  String overwriteFollowUp;
  LocalDate dateOfFollowUpUntil;
  String followUpStatusComment;
  String responsibleContactOfficer;
  String generalComment;
}
