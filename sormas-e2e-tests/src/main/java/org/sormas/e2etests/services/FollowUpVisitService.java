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

import static org.sormas.e2etests.enums.AvailableAndCooperative.*;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.*;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import org.sormas.e2etests.enums.SourceOfTemperature;
import org.sormas.e2etests.pojo.web.*;

public class FollowUpVisitService {
  private final Faker faker;

  @Inject
  public FollowUpVisitService(Faker faker) {
    this.faker = faker;
  }

  public Visit buildVisit() {
    return Visit.builder()
        .personAvailableAndCooperative(AVAILABLE_AND_COOPERATIVE.getAvailable())
        .dateOfVisit(LocalDate.now().minusDays(1))
        .timeOfVisit(LocalTime.now())
        .visitRemarks(faker.book().title())
        .currentBodyTemperature("35.5")
        .sourceOfBodyTemperature(SourceOfTemperature.getRandomTemperature())
        .setClearToNo("Set cleared to No")
        .chillsAndSweats(YES.toString())
        .feelingIll(YES.toString())
        .fever(YES.toString())
        .comments(faker.book().title())
        .firstSymptom("Fever")
        .dateOfSymptom(LocalDate.now().minusDays(2))
        .build();
  }
}
