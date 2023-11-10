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
public enum CommunityValues {
  VoreingestellteGemeinde(
      "Voreingestellte Gemeinde", "QWK33J-XYN3DE-5CSXFJ-MMFOKNKM", "UPKYUC-HD6D2W-EZVF5S-Q3PQSE5A"),
  CharlottenburgNord(
      "Charlottenburg-Nord", "S3P6UC-PW3FDK-XLHWVD-DMA7NT7E", "U3T36E-L5GNAA-ULMAW6-3RMVSLAI");

  private final String name;
  private final String uuidMain;
  private final String uuidDE;

  CommunityValues(String name, String uuidMain, String uuidDE) {
    this.name = name;
    this.uuidMain = uuidMain;
    this.uuidDE = uuidDE;
  }

  @SneakyThrows
  public static String getNameValueForUuid(String option) {
    log.warn("Please migrate to new implementation and take data from EnvironmentManager class");
    CommunityValues[] communityValuesOptions = CommunityValues.values();
    for (CommunityValues value : communityValuesOptions) {
      if (value.uuidMain.equalsIgnoreCase(option) || value.uuidDE.equalsIgnoreCase(option))
        return value.name;
    }
    throw new Exception("Unable to find " + option + " value in Community Enum");
  }

  @SneakyThrows
  public static String getUuidValueForLocale(String communityName, String locale) {
    log.warn("Please migrate to new implementation and take data from EnvironmentManager class");
    CommunityValues[] communityValues = CommunityValues.values();
    for (CommunityValues value : communityValues) {
      if (value.name().equalsIgnoreCase(communityName)) {
        if (locale.equalsIgnoreCase("main") || locale.equalsIgnoreCase("performance")) {
          return value.getUuidMain();
        }
        if (locale.equalsIgnoreCase("DE")) {
          return value.getUuidDE();
        }
      }
    }
    throw new Exception(
        String.format(
            "Unable to find uuid for community: %s and locale: %s", communityName, locale));
  }
}
