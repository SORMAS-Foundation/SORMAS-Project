package org.sormas.e2etests.enums.cases.epidemiologicalData;

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
    System.out.println("Could not map " + activity + " to ActivityAsCaseType");
    return null;
  }
}
