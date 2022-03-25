package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ActivityAsCaseType {
  WORK("Work", "T\u00E4tig in"),
  HABITATION("Habitation", "Untergebracht in"),
  CAREDFOR("Cared for", "Betreut in"),
  OTHER("Other", "Sonstiges"),
  UNKNOWN("Unknown", "Unbekannt");

  private String activityCase;
  private String activityCaseDE;

  ActivityAsCaseType(String activity, String activityDE) {
    this.activityCase = activity;
    this.activityCaseDE = activityDE;
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
  public static String getForNameDE(String option) {
    ActivityAsCaseType[] options = ActivityAsCaseType.values();
    for (ActivityAsCaseType value : options) {
      if (value.getActivityCase().equalsIgnoreCase(option)) return value.activityCaseDE;
    }
    throw new Exception("Unable to find " + option + " value in ActivityAsCase Enum");
  }
}
