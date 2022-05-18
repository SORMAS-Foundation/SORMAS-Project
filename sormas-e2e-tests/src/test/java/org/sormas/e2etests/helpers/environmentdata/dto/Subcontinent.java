package org.sormas.e2etests.helpers.environmentdata.dto;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Subcontinent {

  String uuid;
  String defaultName;
}
