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
public enum GenderValues {
  MALE("Male", "M\u00E4nnlich"),
  FEMALE("Female", "Weiblich"),
  OTHER("Other", "Divers"),
  UNKNOWN("Unknown", "Unbekannt");

  private final String gender;
  private final String genderDE;

  GenderValues(String humanGender, String humanGenderDE) {
    gender = humanGender;
    genderDE = humanGenderDE;
  }

  public static String getRandomGender() {
    Random random = new Random();
    return String.valueOf(GenderValues.values()[random.nextInt(values().length)].gender);
  }

  public static String getRandomGenderDE() {
    Random random = new Random();
    return String.valueOf(GenderValues.values()[random.nextInt(values().length)].genderDE);
  }

  @SneakyThrows
  public static String getValueForDE(String option) {
    GenderValues[] genderValues = GenderValues.values();
    for (GenderValues value : genderValues) {
      if (value.name().equalsIgnoreCase(option)) return value.getGenderDE();
    }
    throw new Exception("Unable to find " + option + " value in GenderValues Enum");
  }
}
