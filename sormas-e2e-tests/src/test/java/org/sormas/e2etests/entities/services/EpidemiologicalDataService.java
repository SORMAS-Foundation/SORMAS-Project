/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.sormas.e2etests.entities.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Activity;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.*;

public class EpidemiologicalDataService {
  private final Faker faker;

  @Inject
  public EpidemiologicalDataService(Faker faker) {
    this.faker = faker;
  }

  public EpidemiologicalData buildGeneratedEpidemiologicalData(boolean isCovid) {

    List<Exposure> exposures = new ArrayList<Exposure>();
    exposures.add(this.buildGeneratedExposureData(isCovid));
    List<Activity> activities = new ArrayList<Activity>();
    activities.add(this.buildGeneratedActivityData());

    return EpidemiologicalData.builder()
        .exposures(exposures)
        .activities(activities)
        .exposureDetailsKnown(YesNoUnknownOptions.YES)
        .activityDetailsKnown(YesNoUnknownOptions.YES)
        .residingAreaWithRisk(YesNoUnknownOptions.YES)
        .largeOutbreaksArea(YesNoUnknownOptions.NO)
        .contactsWithSourceCaseKnown(YesNoUnknownOptions.NO)
        .build();
  }

  public Exposure buildGeneratedExposureData(boolean isCovid) {
    YesNoUnknownOptions animalMarketValue = (isCovid) ? null : YesNoUnknownOptions.NO;

    return Exposure.builder()
        .startOfExposure(LocalDate.now().minusDays(3))
        .endOfExposure(LocalDate.now().minusDays(1))
        .exposureDescription("had coffee")
        .typeOfActivity(TypeOfActivityExposure.VISIT)
        .exposureDetailsRole(ExposureDetailsRole.MEDICAL_STAFF)
        .riskArea(YesNoUnknownOptions.NO)
        .indoors(YesNoUnknownOptions.YES)
        .outdoors(YesNoUnknownOptions.NO)
        .wearingMask(YesNoUnknownOptions.NO)
        .wearingPpe(YesNoUnknownOptions.NO)
        .otherProtectiveMeasures(YesNoUnknownOptions.NO)
        .shortDistance(YesNoUnknownOptions.YES)
        .longFaceToFaceContact(YesNoUnknownOptions.YES)
        .animalMarket(animalMarketValue)
        .percutaneous(YesNoUnknownOptions.NO)
        .contactToBodyFluids(YesNoUnknownOptions.NO)
        .handlingSamples(YesNoUnknownOptions.NO)
        .typeOfPlace(TypeOfPlace.HOME)
        .continent("Africa")
        .subcontinent("Central Africa")
        .country("Cameroon")
        .build();
  }

  public Activity buildGeneratedActivityData() {
    return Activity.builder()
        .startOfActivity(LocalDate.now().minusDays(3))
        .endOfActivity(LocalDate.now().minusDays(1))
        .description("played sports and had lunch")
        .typeOfActivity(ActivityAsCaseType.WORK)
        .continent("Africa")
        .subcontinent("Central Africa")
        .country("Cameroon")
        .build();
  }
}
