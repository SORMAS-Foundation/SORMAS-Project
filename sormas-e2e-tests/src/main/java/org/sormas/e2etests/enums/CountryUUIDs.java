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
public enum CountryUUIDs {
  Germany("SUSZ6P-4YQIB3-WMSLMG-IAPRKJ4Y", "XZGQNV-LR5PHR-XGX55Q-PXD4CJMI");

  private final String uuidMain;
  private final String uuidDe;

  CountryUUIDs(String uuidMain, String uuidDe) {
    this.uuidMain = uuidMain;
    this.uuidDe = uuidDe;
  }

  @SneakyThrows
  public static String getUuidValueForLocale(String country, String locale) {
    CountryUUIDs[] countryUUIDs = CountryUUIDs.values();
    for (CountryUUIDs value : countryUUIDs) {
      if (value.name().equalsIgnoreCase(country)) {
        if (locale.equalsIgnoreCase("main") || locale.equalsIgnoreCase("performance")) {
          return value.getUuidMain();
        }
        if (locale.equalsIgnoreCase("DE")) {
          return value.getUuidDe();
        }
      }
    }
    throw new Exception(
        String.format("Unable to find uuid for country: %s and locale: %s", country, locale));
  }
}
