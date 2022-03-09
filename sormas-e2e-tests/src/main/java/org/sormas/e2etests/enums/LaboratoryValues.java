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
public enum LaboratoryValues {
  VOREINGESTELLTES_LABOR(
      "Voreingestelltes Labor", "Voreingestelltes Labor", "VQL6NJ-HPJY24-56F2R5-T5UV2HUI"),
  OTHER_FACILITY("Andere Einrichtung", "Other facility", "SORMAS-CONSTID-OTHERS-FACILITY");

  private final String caption;
  private final String captionEnglish;
  private final String uuidValue;

  LaboratoryValues(String captionType, String captionTypeEnglish, String idValue) {
    caption = captionType;
    captionEnglish = captionTypeEnglish;
    uuidValue = idValue;
  }

  public static String getRandomCaption() {
    Random random = new Random();
    return String.valueOf(LaboratoryValues.values()[random.nextInt(values().length)].caption);
  }

  public static String getRandomUUID() {
    Random random = new Random();
    return String.valueOf(LaboratoryValues.values()[random.nextInt(values().length)].uuidValue);
  }
}
