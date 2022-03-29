package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.SneakyThrows;
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

  @SneakyThrows
  public static String getForName(String option) {
    ActivityAsCaseType[] options = ActivityAsCaseType.values();
    for (ActivityAsCaseType value : options) {
      if (value.getActivityCase().equalsIgnoreCase(option)) return value.activityCase;
    }
    throw new Exception("Unable to find " + option + " value in ActivityAsCase Enum");
  }
}
