package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Hospitalization {
  LocalDate dateOfVisitOrAdmission;
  LocalDate dateOfDischargeOrTransfer;
  String reasonForHospitalization;
  String specifyReason;
  String stayInTheIntensiveCareUnit;
  LocalDate startOfStayDate;
  LocalDate endOfStayDate;
  String isolation;
  LocalDate dateOfIsolation;
  String wasThePatientHospitalizedPreviously;
  String wasPatientAdmittedAtTheFacilityAsAnInpatient;
  String leftAgainstMedicalAdvice;
}
