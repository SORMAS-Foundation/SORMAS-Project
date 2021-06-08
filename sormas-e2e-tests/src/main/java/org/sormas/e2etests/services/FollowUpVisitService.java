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
import org.sormas.e2etests.pojo.web.FollowUpVisit;

import java.time.LocalDate;
import java.time.LocalTime;

public class FollowUpVisitService {
    private final Faker faker;

    @Inject
    public FollowUpVisitService(Faker faker) {
        this.faker = faker;
    }

    public FollowUpVisit buildGeneratedSample() {
        long currentTimeMillis = System.currentTimeMillis();
        return FollowUpVisit.builder()
                .personAvailableAndCooperative("Available and cooperative")
                .dateOfVisit(LocalDate.now())
                .timeOfVisit(LocalTime.of(11, 30))
                .visitRemarks("Automated - visit remark" + String.valueOf(System.currentTimeMillis()))
                .currentBodyTemperature("35.5 °C")
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
                .firstSymptom("Fever")
                .dateOfSymptomOnset(LocalDate.now())
                .build();
    }
}
