package org.sormas.e2etests.pojo;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Address {
  boolean pseudonymized;
  long changeDate;
  long creationDate;
  String uuid;
}
