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

package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ActivityAsCaseType {
  WORK("Work"),
  HABITATION("Habitation"),
  CAREDFOR("Cared for"),
  OTHER("Other"),
  UNKNOWN("Unknown");

  private String activityCase;

  ActivityAsCaseType(String activity) {
    this.activityCase = activity;
  }

  public String getActivityCase() {
    return activityCase;
  }

  public static ActivityAsCaseType fromString(String activity) {
    for (ActivityAsCaseType activityAsCaseType : ActivityAsCaseType.values()) {
      if (activityAsCaseType.activityCase.equalsIgnoreCase(activity)) {
        return activityAsCaseType;
      }
    }
    log.error("Couldn't map activity !");
    return null;
  }
}
