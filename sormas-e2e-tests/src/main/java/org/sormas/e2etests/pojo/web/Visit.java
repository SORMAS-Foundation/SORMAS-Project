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

package org.sormas.e2etests.pojo.web;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Visit {
  String personAvailableAndCooperative;
  LocalDate dateOfVisit;
  LocalTime timeOfVisit;
  String visitRemarks;
  String currentBodyTemperature;
  String sourceOfBodyTemperature;
  String setClearToNo;
  String chillsAndSweats;
  String feelingIll;
  String fever;
  String comments;
  String firstSymptom;
  LocalDate dateOfSymptom;
}
