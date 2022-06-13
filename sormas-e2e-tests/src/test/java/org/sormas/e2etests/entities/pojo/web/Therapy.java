package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Therapy {

  String prescriptionType;
  String prescriptionDetails;
  LocalDate prescriptionDate;
  String prescribingClinician;
  LocalDate prescriptionStartDate;
  LocalDate prescriptionEndDate;
  String typeOfDrug;
  String prescriptionFrequency;
  String prescriptionDose;
  String prescriptionRoute;
  String prescriptionAdditionalNotes;
  String treatmentType;
  String treatmentDetails;
  LocalDate treatmentDate;
  LocalTime treatmentTime;
  String treatmentExecutingStaffMember;
  String treatmentDose;
  String treatmentRoute;
  String treatmentAdditionalNotes;
}
