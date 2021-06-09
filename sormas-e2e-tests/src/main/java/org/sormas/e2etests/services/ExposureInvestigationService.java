package org.sormas.e2etests.services;

import org.sormas.e2etests.pojo.web.ExposureInvestigation;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

public class ExposureInvestigationService {

    public ExposureInvestigation buildInputExposureInvestigation() {
        return ExposureInvestigation.builder()
                .exposureDetailsKnown(YES.toString())
                .highTransmissionRiskArea(YES.toString())
                .largeOutbreaksArea(YES.toString())
                .build();
    }
}
