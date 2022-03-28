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
public enum FacilityCategory {
  ACCOMMODATION("Accommodation", "Beherbergungsst\u00E4tten"),
  CARE_FACILITY("Care facility", "Pflegeeinrichtung"),
  MEDICAL_FACILITY("Medical facility", "Medizinische Einrichtung"),
  RESIDENCE("Residence", "Wohnst\u00E4tte");

  private final String facility;
  private final String facilityDE;

  FacilityCategory(String aFacilityCategory, String aFacilityCategoryDE) {
    facility = aFacilityCategory;
    facilityDE = aFacilityCategoryDE;
  }

  public static String getRandomFacility() {
    Random random = new Random();
    return String.valueOf(FacilityCategory.values()[random.nextInt(values().length)].facility);
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    FacilityCategory[] facilityCategories = FacilityCategory.values();
    for (FacilityCategory value : facilityCategories) {
      if (value.getFacility().equalsIgnoreCase(option)) return value.getFacility();
    }
    throw new Exception("Unable to find " + option + " value in FacilityCategory Enum");
  }
}
