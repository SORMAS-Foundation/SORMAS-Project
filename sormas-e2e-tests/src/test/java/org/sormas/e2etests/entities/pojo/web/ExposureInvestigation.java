package org.sormas.e2etests.entities.pojo.web;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class ExposureInvestigation {
  String exposureDetailsKnown;
  String highTransmissionRiskArea;
  String largeOutbreaksArea;
  Boolean exposureNewEntry;
}
