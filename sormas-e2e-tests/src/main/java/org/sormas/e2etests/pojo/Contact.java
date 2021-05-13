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

package org.sormas.e2etests.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import java.time.LocalDate;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class Contact {
  String firstName;
  String lastName;
  LocalDate dateOfBirth;
  String sex;
  String nationalHealthId;
  String passportNumber;
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
  String contactCategory;
  String relationshipWithCase;
  String descriptionOfHowContactTookPlace;
}
