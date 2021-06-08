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
public enum LabCaption {
  VOREINGESTELLTES_LABOR("Voreingestelltes Labor", "Voreingestelltes Labor"),
  OTHER_FACILITY("Andere Einrichtung", "Other facility");

  private final String caption;
  private final String captionEnglish;

  LabCaption(String captionType, String captionTypeEnglish) {
    caption = captionType;
    captionEnglish = captionTypeEnglish;
  }

  public static String getRandomCaption() {
    Random random = new Random();
    return String.valueOf(LabCaption.values()[random.nextInt(values().length)].caption);
  }
}
