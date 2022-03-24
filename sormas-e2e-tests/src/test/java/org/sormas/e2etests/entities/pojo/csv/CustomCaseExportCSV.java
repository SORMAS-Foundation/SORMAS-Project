package org.sormas.e2etests.entities.pojo.csv;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class CustomCaseExportCSV {
  String disease;
  String firstName;
  String lastName;
}
