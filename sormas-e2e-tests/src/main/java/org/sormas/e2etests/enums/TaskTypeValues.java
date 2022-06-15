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
