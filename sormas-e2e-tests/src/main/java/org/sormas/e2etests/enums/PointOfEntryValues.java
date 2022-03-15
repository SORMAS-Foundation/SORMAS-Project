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

@Getter
public enum PointOfEntryValues {
  AIRPORT("Other airport", "Anderer Flughafen"),
  SEAPORT("Other seaport", "Anderer Seehafen"),
  GROUND("Other ground crossing", "Anderer Grenz\u00FCbergang/Landweg"),
  OTHER("Other point of entry", "Anderer Einreiseort");

  private final String pointOfEntry;
  private final String pointOfEntryDE;

  PointOfEntryValues(String genericPointOfEntry, String genericPointOfEntryDE) {
    pointOfEntry = genericPointOfEntry;
    pointOfEntryDE = genericPointOfEntryDE;
  }

  public static String getRandomPointOfEntry() {
    Random random = new Random();
    return String.valueOf(
        PointOfEntryValues.values()[random.nextInt(values().length)].pointOfEntry);
  }

  public static String getRandomPointOfEntryDE() {
    Random random = new Random();
    return String.valueOf(
        PointOfEntryValues.values()[random.nextInt(values().length)].pointOfEntryDE);
  }
}
