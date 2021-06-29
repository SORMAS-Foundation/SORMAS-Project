package org.sormas.e2etests.services;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

import org.sormas.e2etests.pojo.web.ExposureInvestigation;

public class ExposureInvestigationService {

  public ExposureInvestigation buildInputExposureInvestigation() {
    return ExposureInvestigation.builder()
        .exposureDetailsKnown(YES.toString())
        .highTransmissionRiskArea(YES.toString())
        .largeOutbreaksArea(YES.toString())
        .exposureNewEntry(true)
        .build();
  }
}
