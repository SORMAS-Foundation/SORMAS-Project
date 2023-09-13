/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;

@Getter
public enum ReasonForHospitalization {
  REPORTEDDISEASE("Reported disease", "Gemeldete Krankheit"),
  ISOLATION("Isolation", "Isolation"),
  OTHERREASON("Other reason", "Anderer Grund"),
  UNKNOWN("Unknown", "Unbekannt");

  private final String reasonForHospitalization;
  private final String reasonForHospitalizationDE;

  ReasonForHospitalization(
      String genericReasonForHospitalization, String genericReasonForHospitalizationDE) {
    reasonForHospitalization = genericReasonForHospitalization;
    reasonForHospitalizationDE = genericReasonForHospitalizationDE;
  }

  public static String getRandomReasonForHospitalization() {
    Random random = new Random();
    return String.valueOf(
        ReasonForHospitalization.values()[random.nextInt(values().length)]
            .reasonForHospitalization);
  }

  public static String getRandomReasonForHospitalizationDE() {
    Random random = new Random();
    return String.valueOf(
        ReasonForHospitalization.values()[random.nextInt(values().length)]
            .reasonForHospitalizationDE);
  }
}
