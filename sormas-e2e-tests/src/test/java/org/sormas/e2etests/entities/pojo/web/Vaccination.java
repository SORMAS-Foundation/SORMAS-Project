package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Vaccination {
  String uuid;
  LocalDate reportDate;
  LocalDate vaccinationDate;
  String vaccineName;
  String vaccineManufacturer;
  String vaccineType;
  String vaccinationInfoSource;
  String vaccineDose;
  String inn;
  String uniiCode;
  String batchNumber;
  String atcCode;
}
