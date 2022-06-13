package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Immunization {
  LocalDate dateOfReport;
  String externalId;
  String disease;
  String meansOfImmunization;
  String managementStatus;
  String immunizationStatus;
  String responsibleRegion;
  String responsibleDistrict;
  String responsibleCommunity;
  String facilityCategory;
  String facilityType;
  String facility;
  String facilityDescription;
  LocalDate startDate;
  LocalDate endDate;
  LocalDate validFrom;
  LocalDate validUntil;
  String firstName;
  String lastName;
  LocalDate dateOfBirth;
  String sex;
  String presentConditionOfPerson;
  String primaryPhoneNumber;
  String primaryEmailAddress;
}
