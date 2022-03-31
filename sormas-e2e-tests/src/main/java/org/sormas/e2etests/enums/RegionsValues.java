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
public enum RegionsValues {
  VoreingestellteBundeslander(
      "Voreingestellte Bundesl\u00E4nder",
      "RKVAOM-ZNAAFU-R2KF6Z-6BENKHEY",
      "UXYTS3-SYILLD-2YI5UM-BZD62B6I");

  private final String name;
  private final String uuidMain;
  private final String uuidDE;

  RegionsValues(String name, String uuidMain, String uuidDE) {
    this.name = name;
    this.uuidMain = uuidMain;
    this.uuidDE = uuidDE;
  }

  @SneakyThrows
  public static String getNameValueForUuid(String option) {
    RegionsValues[] regionValuesOptions = RegionsValues.values();
    for (RegionsValues value : regionValuesOptions) {
      if (value.uuidMain.equalsIgnoreCase(option) || value.uuidDE.equalsIgnoreCase(option))
        return value.name;
    }
    throw new Exception("Unable to find " + option + " value in Region Enum");
  }

  @SneakyThrows
  public static String getUuidValueForLocale(String regionName, String locale) {
    RegionsValues[] regionValuesOptions = RegionsValues.values();
    for (RegionsValues value : regionValuesOptions) {
      if (value.name.equalsIgnoreCase(regionName)) {
        if (locale.equalsIgnoreCase("main")) {
          return value.getUuidMain();
        }
        if (locale.equalsIgnoreCase("DE")) {
          return value.getUuidDE();
        }
      }
    }
    throw new Exception(
        String.format("Unable to find uuid for region: %s and locale: %s", regionName, locale));
  }
}
