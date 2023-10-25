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
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum DistrictsValues {
  VoreingestellterLandkreis(
      "Voreingestellter Landkreis");

  private final String name;

  DistrictsValues(String name) {
    this.name = name;
  }

  @SneakyThrows
  public static String getNameValueFor(String option) {
    log.warn("Please migrate to new implementation and take data from EnvironmentManager class");
    DistrictsValues[] districtsValues = DistrictsValues.values();
    for (DistrictsValues value : districtsValues) {
      if (value.getName().equalsIgnoreCase(option)) return value.getName();
    }
    throw new Exception("Unable to find " + option + " value in District Enum");
  }
}
