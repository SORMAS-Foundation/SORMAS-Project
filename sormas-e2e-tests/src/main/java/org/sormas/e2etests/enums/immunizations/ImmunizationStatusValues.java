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

package org.sormas.e2etests.enums.immunizations;

import java.util.Random;
import lombok.Getter;

@Getter
public enum ImmunizationStatusValues {
  PENDING("PENDING"),
  ACQUIRED("ACQUIRED"),
  NOT_ACQUIRED("NOT_ACQUIRED"),
  EXPIRED("EXPIRED");

  private final String value;

  ImmunizationStatusValues(String value) {
    this.value = value;
  }

  public static String getRandomImmunizationStatus() {
    Random random = new Random();
    return String.valueOf(ImmunizationStatusValues.values()[random.nextInt(values().length)].value);
  }
}
