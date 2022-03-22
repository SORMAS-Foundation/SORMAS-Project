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
import lombok.SneakyThrows;

@Getter
public enum AreaTypeValues {
  URBAN("Urban", "St\u00E4dtisch"),
  RURAL("Rural", "L\u00E4ndlich"),
  UNKNOWN("Unknown", "Unbekannt");

  private final String areaType;
  private final String areaTypeDE;

  private static Random random = new Random();

  AreaTypeValues(String areaType, String areaTypeDE) {
    this.areaType = areaType;
    this.areaTypeDE = areaTypeDE;
  }

  public static String getRandomAreaType() {
    return String.valueOf(AreaTypeValues.values()[random.nextInt(values().length)].areaType);
  }

  @SneakyThrows
  public static String getNameForDE(String option) {
    AreaTypeValues[] areaTypeOption = AreaTypeValues.values();
    for (AreaTypeValues value : areaTypeOption) {
      if (value.areaType.equalsIgnoreCase(option)) return value.getAreaTypeDE();
    }
    throw new Exception("Unable to find " + option + " value in TypeOfGathering Enum");
  }
}
