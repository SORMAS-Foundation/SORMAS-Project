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
public enum CaseOutcome {
  NO_OUTCOME("No Outcome Yet", "Noch kein Ergebnis bekannt"),
  NOT_YET_CLASSIFIED("Not yet classified", "0. Nicht klassifiziert"),
  INVESTIGATION_PENDING("Investigation pending", "Untersuchung ausstehend"),
  INVESTIGATION_DONE("Investigation done", "Untersuchung durchgef\u00FChrt"),
  INVESTIGATION_DISCARDED("Investigation discarded", "Untersuchung verworfen"),
  DECEASED("Deceased", "Verstorben"),
  RECOVERED("Recovered", "Genesen"),
  UNKNOWN("Unknown", "Unbekannt"),
  SEQUELAE_YES("Yes", "Ja"),
  SEQUELAE_NO("No", "Nein"),
  SEQUELAE_UNKNOWN("Unknown", "Unbekannt"),
  PLACE_OF_STAY_HOME("Home", "Zuhause"),
  PLACE_OF_STAY_FACILITY("Facility", "Einrichtung"),
  FACILITY_STANDARD_EINRICHTUNG("Standard Einrichtung", "Standard Einrichtung"),
  FACILITY_VOREINGESTELLTE_GESUNDHEITSEINRICHTUNG(
      "Voreingestellte Gesundheitseinrichtung", "Voreingestellte Gesundheitseinrichtung"),
  FACILITY_OTHER("Other facility", "Andere Einrichtung"),
  QUARANTINE_HOME("Home", "H\u00E4uslich"),
  QUARANTINE_INSTITUTIONAL("Institutional", "Institutionell"),
  QUARANTINE_NONE("None", "Keine"),
  QUARANTINE_UNKNOWN("Unknown", "Unbekannt"),
  QUARANTINE_OTHER("Other", "Sonstiges"),
  VACCINATED_STATUS_VACCINATED("Vaccinated", "Geimpft"),
  VACCINATED_STATUS_UNVACCINATED("Unvaccinated", "Ungeimpft"),
  VACCINATED_STATUS_UNKNOWN("Unknown", "Unbekannt");

  // TODO refactor all these values to cover UI values and API values to have a common Enum for both

  private final String name;
  private final String nameDE;

  CaseOutcome(String name, String nameDE) {
    this.name = name;
    this.nameDE = nameDE;
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

  @SneakyThrows
  public static String getValueForDE(String option) {
    CaseOutcome[] caseOutcomeOptions = CaseOutcome.values();
    for (CaseOutcome value : caseOutcomeOptions) {
      if (value.getNameDE().equalsIgnoreCase(option)) return value.getNameDE();
    }
    throw new Exception("Unable to find " + option + " value in CaseOutcome Enum");
  }
}
