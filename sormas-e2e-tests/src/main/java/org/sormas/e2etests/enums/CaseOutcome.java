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

@Getter
public enum CaseOutcome {
  NO_OUTCOME("No Outcome Yet"),
  NOT_CLASSIFIED("Not yet classified"),
  PENDING("Investigation pending"),
  DECEASED("Deceased"),
  RECOVERED("Recovered"),
  UNKNOWN("Unknown"),
  CORONAVIRUS("COVID-19"),
  RESPONSIBLE_REGION("Voreingestellte Bundesl"),
  RESPONSIBLE_DISTRICT("Voreingestellter Landkreis"),
  HEALTH_FACILITY("Standard Einrichtung - Details");

  private final String outcome;

  CaseOutcome(String outcomeCase) {
    outcome = outcomeCase;
  }

  public static String getRandomOutcome() {
    Random random = new Random();
    return String.valueOf(CaseOutcome.values()[random.nextInt(values().length)]);
  }
}
