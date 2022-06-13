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

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;

@Getter
public enum SourceOfTemperature {
  AXILLARY("axillary"),
  ORAL("oral"),
  RECTAL("rectal"),
  NON_CONTACT_INFRARED("Non-contact (infrared)");

  private final String temperature;

  SourceOfTemperature(String sourceOfTemp) {
    temperature = sourceOfTemp;
  }

  public static String getRandomTemperature() {
    Random random = new Random();
    return String.valueOf(
        SourceOfTemperature.values()[random.nextInt(values().length)].temperature);
  }
}
