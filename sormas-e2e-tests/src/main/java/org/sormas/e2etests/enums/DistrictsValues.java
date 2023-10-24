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
      "Voreingestellter Landkreis",
      "SZ75BK-5OUMFU-V2DTKG-5BYACHFE",
      "R5N4WB-3LGKHX-HGFZ3K-7POBSFBQ"),
  SKBerlinCharlottenburgWilmersdorf(
      "SK Berlin Charlottenburg-Wilmersdorf",
      "NBNTEP-2AVREB-XKXDET-PWYYBIOE",
      "UWRLRS-XKKOJZ-25ULJL-JAELCKYE");

  private final String name;
  private final String uuidMain;
  private final String uuidDE;

  DistrictsValues(String name, String uuidMain, String uuidDE) {
    this.name = name;
    this.uuidMain = uuidMain;
    this.uuidDE = uuidDE;
  }

  @SneakyThrows
  public static String getNameValueForUuid(String option) {
    log.warn("Please migrate to new implementation and take data from EnvironmentManager class");
    DistrictsValues[] districtValuesOptions = DistrictsValues.values();
    for (DistrictsValues value : districtValuesOptions) {
      if (value.uuidMain.equalsIgnoreCase(option) || value.uuidDE.equalsIgnoreCase(option))
        return value.name;
    }
    throw new Exception("Unable to find " + option + " value in District Enum");
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

  @SneakyThrows
  public static String getUuidValueForLocale(String districtName, String locale) {
    log.warn("Please migrate to new implementation and take data from EnvironmentManager class");
    DistrictsValues[] districtsValues = DistrictsValues.values();
    for (DistrictsValues value : districtsValues) {
      if (value.name().equalsIgnoreCase(districtName)) {
        if (locale.equalsIgnoreCase("main") || locale.equalsIgnoreCase("performance")) {
          return value.getUuidMain();
        }
        if (locale.equalsIgnoreCase("DE")) {
          return value.getUuidDE();
        }
      }
    }
    throw new Exception(
        String.format("Unable to find uuid for district: %s and locale: %s", districtName, locale));
  }
}
