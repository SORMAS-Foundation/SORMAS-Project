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

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Person {
  String uuid;
  String firstName;
  String lastName;
  String salutation;
  LocalDate dateOfBirth;
  String sex;
  String presentConditionOfPerson;
  String passportNumber;
  String nationalHealthId;
  String externalId;
  String externalToken;
  String typeOfOccupation;
  String staffOfArmedForces;
  String education;
  String region;
  String district;
  String community;
  String facilityCategory;
  String facilityType;
  String facility;
  String facilityNameAndDescription;
  String street;
  int houseNumber;
  String additionalInformation;
  String postalCode;
  String city;
  String areaType;
  String contactPersonFirstName;
  String contactPersonLastName;
  String contactPersonPhoneNumber;
  String contactPersonEmailAddress;
  String communityContactPerson;
  String birthName;
  String nickname;
  String motherMaidenName;
  String motherName;
  String fatherName;
  String nameOfGuardians;
  boolean covidCodeDelivered;
  Address address;
  long creationDate;
  String emailAddress;
  String phoneNumber;
  boolean pseudonymized;
  long changeDate;
  boolean hasCovidApp;
  List<Object> personContactDetails;
  List<Object> addresses;
}
