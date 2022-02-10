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
public enum DistrictsValues {
  VoreingestellterLandkreis("Voreingestellter Landkreis", "SZ75BK-5OUMFU-V2DTKG-5BYACHFE");

  private final String name;
  private final String uuid;
  private static Random random = new Random();

  DistrictsValues(String name, String uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    DistrictsValues[] districtsValues = DistrictsValues.values();
    for (DistrictsValues value : districtsValues) {
      if (value.getName().equalsIgnoreCase(option)) return value.getName();
    }
    throw new Exception("Unable to find " + option + " value in DistrictsValues Enum");
  }

  public static String getRandomDistrictsValuesName() {
    return String.valueOf(DistrictsValues.values()[random.nextInt(values().length)].getName());
  }
}
