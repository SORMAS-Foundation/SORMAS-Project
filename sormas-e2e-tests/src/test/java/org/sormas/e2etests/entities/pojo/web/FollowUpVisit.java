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

package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class FollowUpVisit {
  String personAvailableAndCooperative;
  LocalDate dateOfVisit;
  LocalTime timeOfVisit;
  String visitRemarks;
  String currentBodyTemperature;
  String sourceOfBodyTemperature;
  String chillsOrSweats;
  String feelingIll;
  String fever;
  String headache;
  String musclePain;
  String shivering;
  String acuteRespiratoryDistressSyndrome;
  String cough;
  String difficultyBreathing;
  String oxygenSaturation94;
  String pneumoniaClinicalRadiologic;
  String rapidBreathing;
  String respiratoryDiseaseRequiringVentilation;
  String runnyNose;
  String soreThroatPharyngitis;
  String fastHeartRate;
  String diarrhea;
  String nausea;
  String newLossOfSmell;
  String newLossOfTaste;
  String otherClinicalSymptoms;
  String comments;
  String firstSymptom;
  LocalDate dateOfSymptomOnset;
}
