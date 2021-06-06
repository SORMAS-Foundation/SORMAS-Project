package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum TypeOfActivityActivity {
  WORK("Work"),
  HABITATION("Habitation"),
  CAREDFOR("Cared for"),
  OTHER("Other"),
  UNKNOWN("Unknown");

  private String activityCase;

  TypeOfActivityActivity(String activity) {
    this.activityCase = activity;
  }

  public String getActivityCase() {
    return activityCase;
  }

  public static TypeOfActivityActivity fromString(String activity) {
    for (TypeOfActivityActivity b : TypeOfActivityActivity.values()) {
      if (b.activityCase.equalsIgnoreCase(activity)) {
        return b;
      }
    }
    return null;
  }
}
