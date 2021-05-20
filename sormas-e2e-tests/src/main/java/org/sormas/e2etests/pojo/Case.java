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
import java.time.LocalDate;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class Case {
  String caseOrigin;
  LocalDate dateOfBirth;
  LocalDate dateOfReport;
  LocalDate dateOfSymptomOnset;
  String disease;
  String externalId;
  String facilityCategory;
  String facilityType;
  String firstName;
  String lastName;
  String nationalHealthId;
  String passportNumber;
  String placeDescription;
  String placeOfStay;
  String pointOfEntry;
  String presentConditionOfPerson;
  String primaryEmailAddress;
  String primaryPhoneNumber;
  String responsibleCommunity;
  String responsibleDistrict;
  String responsibleRegion;
  String sex;
}
