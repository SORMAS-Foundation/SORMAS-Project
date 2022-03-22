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
public enum FacilityType {
  CAMPSITE("Campsite", "Campingplatz/Zeltplatz"),
  CRUISE_SHIP("Cruise ship", "Kreuzfahrtschiff"),
  HOTEL_LODGE("Hotel, B&B, inn, lodge", "Hotel, B&B, Gasthof"),
  MASS_ACCOMMODATION(
          "Mass accommodation (e.g. guest and harvest workers)",
          "Massenunterkunft (z.B. Gast- und Erntearbeiter)"),
  OTHER_ACCOMMODATION("Other Accommodation", "Andere Beherbergungsst\u00E4tte");

  private final String type;
  private final String typeDE;

  FacilityType(String aType, String aTypeDE) {

    type = aType;
    typeDE = aTypeDE;
  }

  public static String getRandomFacilityType() {
    Random random = new Random();
    return String.valueOf(FacilityType.values()[random.nextInt(values().length)].type);
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    FacilityType[] facilityTypes = FacilityType.values();
    for (FacilityType value : facilityTypes) {
      if (value.getType().equalsIgnoreCase(option)) return value.getType();
    }
    throw new Exception("Unable to find " + option + " value in FacilityType Enum");
  }
}
