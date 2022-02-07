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
public enum FacilityType {
  CAMPSITE("Campsite"),
  CRUISE_SHIP("Cruise ship"),
  HOTEL_LODGE("Hotel, B&B, inn, lodge"),
  MASS_ACCOMMODATION("Mass accommodation (e.g. guest and harvest workers)"),
  OTHER_ACCOMMODATION("Other Accommodation)");

  private final String type;

  FacilityType(String aType) {

    type = aType;
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
