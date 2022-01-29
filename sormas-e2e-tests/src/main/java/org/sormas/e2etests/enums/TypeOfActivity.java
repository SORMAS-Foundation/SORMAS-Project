package org.sormas.e2etests.enums;

public enum TypeOfActivity {
  WORK("Work"),
  TRAVEL("Travel"),
  SPORT("Sport"),
  VISIT("Visit"),
  HABITATION("Habitation"),
  PERSONAL_SERVICES("Personal Services"),
  UNKNOWN("Unknown"),
  GATHERING("Gathering"),
  OTHER("Other");

  private final String activityType;

  TypeOfActivity(String activityType) {
    this.activityType = activityType;
  }

  @Override
  public String toString() {
    return this.activityType;
  }
}
