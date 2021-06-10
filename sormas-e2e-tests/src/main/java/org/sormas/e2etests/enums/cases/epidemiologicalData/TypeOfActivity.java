package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum TypeOfActivity {
  WORK("Work"),
  TRAVEL("Travel"),
  SPORT("Sport"),
  VISIT("Visit"),
  GATHERING("Gathering");

  private String activity;

  TypeOfActivity(String activity) {
    this.activity = activity;
  }
}
