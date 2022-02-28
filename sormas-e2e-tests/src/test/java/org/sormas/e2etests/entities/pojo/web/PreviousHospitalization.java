package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class PreviousHospitalization {
  LocalDate dateOfVisitOrAdmission;
  LocalDate dateOfDischargeOrTransfer;
  String region;
  String district;
  String community;
  String hospital;
  String isolation;
  String facilityNameDescription;
  String reasonForHospitalization;
  String specifyReason;
  String stayInTheIntensiveCareUnit;
  LocalDate startOfStayDate;
  LocalDate endOfStayDate;
  String description;
}
