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
public enum ContinentUUIDs {
  Europe("W2FUSQ-PXGMRZ-V6ZTOE-6EPIKCSI", "VFJAQB-RGCNWG-7WS2IX-HJBECAVM");

  private final String uuidMain;
  private final String uuidDe;

  ContinentUUIDs(String uuidMain, String uuidDe) {
    this.uuidMain = uuidMain;
    this.uuidDe = uuidDe;
  }

  @SneakyThrows
  public static String getUuidValueForLocale(String continent, String locale) {
    ContinentUUIDs[] continentUUIDs = ContinentUUIDs.values();
    for (ContinentUUIDs value : continentUUIDs) {
      if (value.name().equalsIgnoreCase(continent)) {
        if (locale.equalsIgnoreCase("main") || locale.equalsIgnoreCase("performance")) {
          return value.getUuidMain();
        }
        if (locale.equalsIgnoreCase("DE")) {
          return value.getUuidDe();
        }
      }
    }
    throw new Exception(
        String.format("Unable to find uuid for continent: %s and locale: %s", continent, locale));
  }
}
