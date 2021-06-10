/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivity;
import org.sormas.e2etests.pojo.web.Case;
import org.sormas.e2etests.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.pojo.web.epidemiologicalData.Activity;
import org.sormas.e2etests.pojo.web.epidemiologicalData.Exposure;

public class EpidemiologicalDataService {
  private final Faker faker;

  @Inject
  public EpidemiologicalDataService(Faker faker) {
    this.faker = faker;
  }

  public EpidemiologicalData buildGeneratedEpidemiologicalData() {


    List<Exposure> exposures = new ArrayList<Exposure>();
    exposures.add(this.buildGeneratedExposureData());
    List<Activity> activities =new ArrayList<Activity>();
    activities.add(this.buildGeneratedActivityData());

    return EpidemiologicalData.builder()
        .exposures(exposures)
        .activities(activities)
       .residingAreaWithRisk(YesNoUnknownOptions.YES)
        .largeOutbreaksArea(YesNoUnknownOptions.NO)
        .contactsWithSourceCaseKnown(YesNoUnknownOptions.NO)
        .build();
  }

  public Exposure buildGeneratedExposureData() {
    return Exposure.builder()
        .startOfExposure(LocalDate.now().minusDays(3))
        .endOfExposure(LocalDate.now().minusDays(1))
        .exposureDescription("had coffee")
        .typeOfActivity(TypeOfActivity.WORK)
        .exposureDetailsRole(ExposureDetailsRole.PASSENGER)
        .build();
  }
  public Activity buildGeneratedActivityData() {
    return Activity.builder()
        .startOfActivity(LocalDate.now().minusDays(3))
        .endOfActivity(LocalDate.now().minusDays(1))
        .typeOfActivity("Work")
        .continent("Europe")
        .build();
  }
}
