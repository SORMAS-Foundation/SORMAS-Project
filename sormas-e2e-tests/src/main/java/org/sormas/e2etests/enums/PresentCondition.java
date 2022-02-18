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
public enum PresentCondition {
  ALIVE("Alive"),
  DEAD("Dead"),
  BURIED("Buried"),
  UNKNOWN("Unknown");

  private final String condition;

  PresentCondition(String condition) {
    this.condition = condition;
  }

  public static String getRandomPresentCondition() {
    Random random = new Random();
    return String.valueOf(PresentCondition.values()[random.nextInt(values().length)].condition);
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    PresentCondition[] caseOutcomeOptions = PresentCondition.values();
    for (PresentCondition value : caseOutcomeOptions) {
      if (value.getCondition().equalsIgnoreCase(option)) return value.getCondition();
    }
    throw new Exception("Unable to find " + option + " value in PresentCondition Enum");
  }
}
