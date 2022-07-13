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

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum TaskTypeValues {
  CONTACT_MANAGEMENT("contact management"),
  SOURCE_CASE_TRACING("source case tracing"),
  SAMPLE_COLLECTION("sample collection"),
  CONTACT_INVESTIGATION("contact investigation"),
  CONTACT_FOLLOW_UP("contact follow up"),
  QUARANTINE_MANAGEMENT("quarantine management"),
  SEND_QUARANTINE_ORDER("send quarantine order");

  private final String taskType;

  TaskTypeValues(String taskType) {
    this.taskType = taskType;
  }

  public String getTaskType() {
    return taskType;
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    TaskTypeValues[] taskTypeValuesOption = TaskTypeValues.values();
    for (TaskTypeValues value : taskTypeValuesOption) {
      if (value.name().equalsIgnoreCase(option)) return value.getTaskType();
    }
    throw new Exception("Unable to find " + option + " value in TaskTypeValues Enum");
  }
}
