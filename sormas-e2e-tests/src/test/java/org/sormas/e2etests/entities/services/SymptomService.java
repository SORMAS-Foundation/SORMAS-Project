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

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.NO;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.UNKNOWN;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;
import static org.sormas.e2etests.enums.YesNoUnknownOptionsDE.*;

import com.google.inject.Inject;
import java.time.LocalDate;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.web.Symptoms;

public class SymptomService {

  @Inject
  public SymptomService() {}

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptoms() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35.2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(YES.toString())
        .headache(YES.toString())
        .musclePain(YES.toString())
        .fever(YES.toString())
        .acuteRespiratoryDistressSyndrome(YES.toString())
        .cough(YES.toString())
        .pneumoniaClinicalOrRadiologic(YES.toString())
        .difficultyBreathing(YES.toString())
        .rapidBreathing(YES.toString())
        .runnyNose(YES.toString())
        .soreThroat(YES.toString())
        .diarrhea(YES.toString())
        .nausea(YES.toString())
        .lossOfSmell(YES.toString())
        .lossOfTaste(YES.toString())
        .abnormalLungXrayFindings(YES.toString())
        .fatigueWeakness(YES.toString())
        .jointPain(YES.toString())
        .coughWithHeamoptysis(YES.toString())
        .coughWithSputum(YES.toString())
        .fluidInLungCavityXray(YES.toString())
        .fluidInLungCavityAuscultation(YES.toString())
        .inDrawingOfChestWall(YES.toString())
        .abdominalPain(YES.toString())
        .vomiting(YES.toString())
        .skinUlcers(YES.toString())
        .unexplainedBleeding(YES.toString())
        .coma(YES.toString())
        .lymphadenopathy(YES.toString())
        .inabilityToWalk(YES.toString())
        .skinRash(YES.toString())
        .confusedDisoriented(YES.toString())
        .seizures(YES.toString())
        .otherNonHemorrhagicSymptoms(YES.toString())
        .symptomsComments(generateShortUUID())
        .firstSymptom("Diarrhea")
        .dateOfSymptom(LocalDate.now().minusDays(2))
        .build();
  }

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptomsWithNoOptions() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35.2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(NO.toString())
        .headache(NO.toString())
        .musclePain(NO.toString())
        .fever(NO.toString())
        .acuteRespiratoryDistressSyndrome(NO.toString())
        .cough(NO.toString())
        .pneumoniaClinicalOrRadiologic(NO.toString())
        .difficultyBreathing(NO.toString())
        .rapidBreathing(NO.toString())
        .runnyNose(NO.toString())
        .soreThroat(NO.toString())
        .diarrhea(NO.toString())
        .nausea(NO.toString())
        .lossOfSmell(NO.toString())
        .lossOfTaste(NO.toString())
        .abnormalLungXrayFindings(NO.toString())
        .fatigueWeakness(NO.toString())
        .jointPain(NO.toString())
        .coughWithHeamoptysis(NO.toString())
        .coughWithSputum(NO.toString())
        .fluidInLungCavityXray(NO.toString())
        .fluidInLungCavityAuscultation(NO.toString())
        .inDrawingOfChestWall(NO.toString())
        .abdominalPain(NO.toString())
        .vomiting(NO.toString())
        .skinUlcers(NO.toString())
        .unexplainedBleeding(NO.toString())
        .coma(NO.toString())
        .lymphadenopathy(NO.toString())
        .inabilityToWalk(NO.toString())
        .skinRash(NO.toString())
        .confusedDisoriented(NO.toString())
        .seizures(NO.toString())
        .otherComplications(NO.toString())
        .symptomsComments(generateShortUUID())
        .build();
  }

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptomsWithUnknownOptions() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35.2")
        .sourceOfBodyTemperature("rectal")
        .chillsOrSweats(UNKNOWN.toString())
        .headache(UNKNOWN.toString())
        .musclePain(UNKNOWN.toString())
        .fever(UNKNOWN.toString())
        .acuteRespiratoryDistressSyndrome(UNKNOWN.toString())
        .cough(UNKNOWN.toString())
        .pneumoniaClinicalOrRadiologic(UNKNOWN.toString())
        .difficultyBreathing(UNKNOWN.toString())
        .rapidBreathing(UNKNOWN.toString())
        .runnyNose(UNKNOWN.toString())
        .soreThroat(UNKNOWN.toString())
        .diarrhea(UNKNOWN.toString())
        .nausea(UNKNOWN.toString())
        .lossOfSmell(UNKNOWN.toString())
        .lossOfTaste(UNKNOWN.toString())
        .abnormalLungXrayFindings(UNKNOWN.toString())
        .fatigueWeakness(UNKNOWN.toString())
        .jointPain(UNKNOWN.toString())
        .coughWithHeamoptysis(UNKNOWN.toString())
        .coughWithSputum(UNKNOWN.toString())
        .fluidInLungCavityXray(UNKNOWN.toString())
        .fluidInLungCavityAuscultation(UNKNOWN.toString())
        .inDrawingOfChestWall(UNKNOWN.toString())
        .abdominalPain(UNKNOWN.toString())
        .vomiting(UNKNOWN.toString())
        .skinUlcers(UNKNOWN.toString())
        .unexplainedBleeding(UNKNOWN.toString())
        .coma(UNKNOWN.toString())
        .lymphadenopathy(UNKNOWN.toString())
        .inabilityToWalk(UNKNOWN.toString())
        .skinRash(UNKNOWN.toString())
        .confusedDisoriented(UNKNOWN.toString())
        .seizures(UNKNOWN.toString())
        .otherComplications(UNKNOWN.toString())
        .symptomsComments(generateShortUUID())
        .build();
  }

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptomsSurvnetDE() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rektal")
        .fever(JA.toString())
        .shivering(JA.toString())
        .headache(JA.toString())
        .musclePain(JA.toString())
        .feelingIll(JA.toString())
        .chillsOrSweats(JA.toString())
        .acuteRespiratoryDistressSyndrome(JA.toString())
        .soreThroat(JA.toString())
        .cough(JA.toString())
        .runnyNose(JA.toString())
        .pneumoniaClinicalOrRadiologic(JA.toString())
        .respiratoryDiseaseVentilation(JA.toString())
        .oxygenSaturationLower94(JA.toString())
        .rapidBreathing(JA.toString())
        .difficultyBreathing(JA.toString())
        .fastHeartRate(JA.toString())
        .diarrhea(JA.toString())
        .nausea(JA.toString())
        .lossOfSmell(JA.toString())
        .lossOfTaste(JA.toString())
        .otherNonHemorrhagicSymptoms(JA.toString())
        .symptomsComments(generateShortUUID())
        .firstSymptom("Andere klinische Symptome")
        .dateOfSymptom(LocalDate.now().minusDays(2))
        .build();
  }

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptomsSurvnetWithNoOptionsDE() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rektal")
        .fever(NEIN.toString())
        .shivering(NEIN.toString())
        .headache(NEIN.toString())
        .musclePain(NEIN.toString())
        .feelingIll(NEIN.toString())
        .chillsOrSweats(NEIN.toString())
        .acuteRespiratoryDistressSyndrome(NEIN.toString())
        .soreThroat(NEIN.toString())
        .cough(NEIN.toString())
        .runnyNose(NEIN.toString())
        .pneumoniaClinicalOrRadiologic(NEIN.toString())
        .respiratoryDiseaseVentilation(NEIN.toString())
        .oxygenSaturationLower94(NEIN.toString())
        .rapidBreathing(NEIN.toString())
        .difficultyBreathing(NEIN.toString())
        .fastHeartRate(NEIN.toString())
        .diarrhea(NEIN.toString())
        .nausea(NEIN.toString())
        .lossOfSmell(NEIN.toString())
        .lossOfTaste(NEIN.toString())
        .otherNonHemorrhagicSymptoms(NEIN.toString())
        .symptomsComments(generateShortUUID())
        .build();
  }

  @SneakyThrows
  public Symptoms buildEditGeneratedSymptomsSurvnetWithUnknownOptionsDE() {
    return Symptoms.builder()
        .maximumBodyTemperatureInC("35,2")
        .sourceOfBodyTemperature("rektal")
        .fever(UNBEKANNT.toString())
        .shivering(UNBEKANNT.toString())
        .headache(UNBEKANNT.toString())
        .musclePain(UNBEKANNT.toString())
        .feelingIll(UNBEKANNT.toString())
        .chillsOrSweats(UNBEKANNT.toString())
        .acuteRespiratoryDistressSyndrome(UNBEKANNT.toString())
        .soreThroat(UNBEKANNT.toString())
        .cough(UNBEKANNT.toString())
        .runnyNose(UNBEKANNT.toString())
        .pneumoniaClinicalOrRadiologic(UNBEKANNT.toString())
        .respiratoryDiseaseVentilation(UNBEKANNT.toString())
        .oxygenSaturationLower94(UNBEKANNT.toString())
        .rapidBreathing(UNBEKANNT.toString())
        .difficultyBreathing(UNBEKANNT.toString())
        .fastHeartRate(UNBEKANNT.toString())
        .diarrhea(UNBEKANNT.toString())
        .nausea(UNBEKANNT.toString())
        .lossOfSmell(UNBEKANNT.toString())
        .lossOfTaste(UNBEKANNT.toString())
        .otherNonHemorrhagicSymptoms(UNBEKANNT.toString())
        .symptomsComments(generateShortUUID())
        .build();
  }
}
