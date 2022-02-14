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
public enum SpecimenConditions {
  ADEQUATE("Adequate"),
  NOT_ADEQUATE("Not adequate");

  private final String condition;

  SpecimenConditions(String aSpecimen) {
    condition = aSpecimen;
  }

  @SneakyThrows
  public static String getForName(String option) {
    SpecimenConditions[] specimenConditionOptions = SpecimenConditions.values();
    for (SpecimenConditions value : specimenConditionOptions) {
      if (value.condition.equalsIgnoreCase(option)) return value.condition;
    }
    throw new Exception("Unable to find " + option + " value in SpecimenConditions Enum");
  }

  public static String getRandomCondition() {
    Random random = new Random();
    return String.valueOf(SpecimenConditions.values()[random.nextInt(values().length)]);
  }

  public static String getRandomConditionName() {
    Random random = new Random();
    return String.valueOf(SpecimenConditions.values()[random.nextInt(values().length)].condition);
  }
}
