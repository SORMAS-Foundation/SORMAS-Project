package customreport.data;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class TableApiRowObject {
  String testName;
  String currentTime;
  String maxTime;
}
