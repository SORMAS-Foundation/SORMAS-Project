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
public enum CaseOutcome {
  NO_OUTCOME("No Outcome Yet"),
  NOT_YET_CLASSIFIED("Not yet classified"),
  INVESTIGATION_PENDING("Investigation pending"),
  INVESTIGATION_DONE("Investigation done"),
  INVESTIGATION_DISCARDED("Investigation discarded"),
  DECEASED("Deceased"),
  RECOVERED("Recovered"),
  UNKNOWN("Unknown"),
  SEQUELAE_YES("Yes"),
  SEQUELAE_NO("No"),
  SEQUELAE_UNKNOWN("Unknown"),
  PLACE_OF_STAY_HOME("Home"),
  PLACE_OF_STAY_FACILITY("Facility"),
  FACILITY_STANDARD_EINRICHTUNG("Standard Einrichtung"),
  FACILITY_VOREINGESTELLTE_GESUNDHEITSEINRICHTUNG("Voreingestellte Gesundheitseinrichtung"),
  FACILITY_OTHER("Other facility"),
  QUARANTINE_HOME("Home"),
  QUARANTINE_INSTITUTIONAL("Institutional"),
  QUARANTINE_NONE("None"),
  QUARANTINE_UNKNOWN("Unknown"),
  QUARANTINE_OTHER("Other"),
  VACCINATED_STATUS_VACCINATED("Vaccinated"),
  VACCINATED_STATUS_UNVACCINATED("Unvaccinated"),
  VACCINATED_STATUS_UNKNOWN("Unknown");

  // TODO refactor all these values to cover UI values and API values to have a common Enum for both

  private final String name;

  CaseOutcome(String name) {
    this.name = name;
  }

  public static String getRandomOutcome() {
    Random random = new Random();
    return String.valueOf(CaseOutcome.values()[random.nextInt(values().length)]);
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    CaseOutcome[] caseOutcomeOptions = CaseOutcome.values();
    for (CaseOutcome value : caseOutcomeOptions) {
      if (value.getName().equalsIgnoreCase(option)) return value.getName();
    }
    throw new Exception("Unable to find " + option + " value in CaseOutcome Enum");
  }
}
