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

import static org.sormas.e2etests.enums.YesNoUnknownOptions.NO;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.UNKNOWN;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.web.Symptoms;

public class SymptomService {

  @Inject
  public SymptomService() {}

  public Symptoms buildEditGeneratedSymptoms() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(YES.toString())
        .headache(YES.toString())
        .feelingIll(YES.toString())
        .musclePain(YES.toString())
        .fever(YES.toString())
        .shivering(YES.toString())
        .acuteRespiratoryDistressSyndrome(YES.toString())
        .oxygenSaturationLower94(YES.toString())
        .cough(YES.toString())
        .pneumoniaClinicalOrRadiologic(YES.toString())
        .difficultyBreathing(YES.toString())
        .rapidBreathing(YES.toString())
        .respiratoryDiseaseVentilation(YES.toString())
        .runnyNose(YES.toString())
        .soreThroat(YES.toString())
        .fastHeartRate(YES.toString())
        .diarrhea(YES.toString())
        .nausea(YES.toString())
        .lossOfSmell(YES.toString())
        .lossOfTaste(YES.toString())
        .otherNonHemorrhagicSymptoms(YES.toString())
        .symptomsComments(UUID.randomUUID().toString())
        .firstSymptom("Diarrhea")
        .dateOfSymptom(LocalDate.now().minusDays(2))
        .build();
  }

  public Symptoms buildEditGeneratedSymptomsWithNoOptions() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(NO.toString())
        .headache(NO.toString())
        .feelingIll(NO.toString())
        .musclePain(NO.toString())
        .fever(NO.toString())
        .shivering(NO.toString())
        .acuteRespiratoryDistressSyndrome(NO.toString())
        .oxygenSaturationLower94(NO.toString())
        .cough(NO.toString())
        .pneumoniaClinicalOrRadiologic(NO.toString())
        .difficultyBreathing(NO.toString())
        .rapidBreathing(NO.toString())
        .respiratoryDiseaseVentilation(NO.toString())
        .runnyNose(NO.toString())
        .soreThroat(NO.toString())
        .fastHeartRate(NO.toString())
        .diarrhea(NO.toString())
        .nausea(NO.toString())
        .lossOfSmell(NO.toString())
        .lossOfTaste(NO.toString())
        .symptomsComments(UUID.randomUUID().toString())
        .build();
  }

  public Symptoms buildEditGeneratedSymptomsWithUnknownOptions() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(UNKNOWN.toString())
        .headache(UNKNOWN.toString())
        .feelingIll(UNKNOWN.toString())
        .musclePain(UNKNOWN.toString())
        .fever(UNKNOWN.toString())
        .shivering(UNKNOWN.toString())
        .acuteRespiratoryDistressSyndrome(UNKNOWN.toString())
        .oxygenSaturationLower94(UNKNOWN.toString())
        .cough(UNKNOWN.toString())
        .pneumoniaClinicalOrRadiologic(UNKNOWN.toString())
        .difficultyBreathing(UNKNOWN.toString())
        .rapidBreathing(UNKNOWN.toString())
        .respiratoryDiseaseVentilation(UNKNOWN.toString())
        .runnyNose(UNKNOWN.toString())
        .soreThroat(UNKNOWN.toString())
        .fastHeartRate(UNKNOWN.toString())
        .diarrhea(UNKNOWN.toString())
        .nausea(UNKNOWN.toString())
        .lossOfSmell(UNKNOWN.toString())
        .lossOfTaste(UNKNOWN.toString())
        .symptomsComments(UUID.randomUUID().toString())
        .build();
  }
}
