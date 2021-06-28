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
import java.time.LocalTime;
import org.sormas.e2etests.pojo.web.FollowUpVisit;

public class FollowUpVisitService {
  private final Faker faker;
  private long currentTimeMillis = System.currentTimeMillis();

  @Inject
  public FollowUpVisitService(Faker faker) {
    this.faker = faker;
  }

  public FollowUpVisit buildGeneratedFollowUpVisit() {
    return FollowUpVisit.builder()
        .personAvailableAndCooperative("AVAILABLE AND COOPERATIVE")
        .dateOfVisit(LocalDate.now())
        .timeOfVisit("10:15")
        .visitRemarks("visit remark" + currentTimeMillis)
        .currentBodyTemperature("35.0 °C")
        .sourceOfBodyTemperature("oral")
        .chillsOrSweats("YES")
        .feelingIll("YES")
        .fever("YES")
        .headache("NO")
        .musclePain("NO")
        .shivering("NO")
        .acuteRespiratoryDistressSyndrome("NO")
        .cough("NO")
        .difficultyBreathing("NO")
        .oxygenSaturation94("UNKNOWN")
        .pneumoniaClinicalRadiologic("UNKNOWN")
        .rapidBreathing("UNKNOWN")
        .respiratoryDiseaseRequiringVentilation("UNKNOWN")
        .runnyNose("UNKNOWN")
        .soreThroatPharyngitis("UNKNOWN")
        .fastHeartRate("NO")
        .diarrhea("YES")
        .nausea("NO")
        .newLossOfSmell("YES")
        .newLossOfTaste("NO")
        .otherClinicalSymptoms("UNKNOWN")
        .comments("Automated -comment" + LocalTime.now())
        .firstSymptom("Diarrhea")
        .dateOfSymptomOnset(LocalDate.now().minusDays(1))
        .build();
  }

  public FollowUpVisit buildEditFollowUpVisit() {
    return FollowUpVisit.builder()
        .personAvailableAndCooperative("UNAVAILABLE")
        .dateOfVisit(LocalDate.now())
        .timeOfVisit("10:15")
        .visitRemarks("Automated - visit remark" + currentTimeMillis)
        .build();
  }
}
