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

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum AreaTypeValues {
  URBAN("Urban", "URBAN"),
  RURAL("Rural", "RURAL"),
  UNKNOWN("Unknown", "UNKNOWN");

  private final String areaTypeUIvalue;
  private final String areaTypeAPIvalue;

  private static Random random = new Random();

  AreaTypeValues(String uiValue, String apiValue) {
    this.areaTypeUIvalue = uiValue;
    this.areaTypeAPIvalue = apiValue;
  }

  @SneakyThrows
  public static String getUIValueFor(String option) {
    AreaTypeValues[] areaTypeOptions = AreaTypeValues.values();
    for (AreaTypeValues value : areaTypeOptions) {
      if (value.getAreaTypeUIvalue().equalsIgnoreCase(option)) return value.getAreaTypeUIvalue();
    }
    throw new Exception("Unable to find " + option + " value in AreaType Enum");
  }

  @SneakyThrows
  public static String getAPIValueFor(String option) {
    AreaTypeValues[] areaTypeOptions = AreaTypeValues.values();
    for (AreaTypeValues value : areaTypeOptions) {
      if (value.getAreaTypeAPIvalue().replaceAll("_", " ").contains(option.toUpperCase()))
        return value.getAreaTypeAPIvalue();
    }
    throw new Exception("Unable to find " + option + " value in AreaType Enum");
  }

  public static String getRandomAreaUIType() {
    return String.valueOf(AreaTypeValues.values()[random.nextInt(values().length)].areaTypeUIvalue);
  }

  public static String getRandomAreaAPIType() {
    return String.valueOf(
        AreaTypeValues.values()[random.nextInt(values().length)].areaTypeAPIvalue);
  }
}
