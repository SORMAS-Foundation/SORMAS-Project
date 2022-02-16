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

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum DistrictsValues {
  VoreingestellterLandkreis("Voreingestellter Landkreis", "SZ75BK-5OUMFU-V2DTKG-5BYACHFE");

  private final String name;
  private final String uuid;

  DistrictsValues(String name, String uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    DistrictsValues[] districtValuesOptions = DistrictsValues.values();
    for (DistrictsValues value : districtValuesOptions) {
      if (value.uuid.equalsIgnoreCase(option)) return value.name;
    }
    throw new Exception("Unable to find " + option + " value in District Enum");
  }

  @SneakyThrows
  public static String getNameFor(String option) {
    DistrictsValues[] districtsValues = DistrictsValues.values();
    for (DistrictsValues value : districtsValues) {
      if (value.uuid.equalsIgnoreCase(option)) return value.name;
    }
    throw new Exception("Unable to find " + option + " value in DistrictsValues Enum");
  }
}
