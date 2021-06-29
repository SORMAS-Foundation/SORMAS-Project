package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum TypeOfActivityExposure {
  WORK("Work"),
  TRAVEL("Travel"),
  Sport("Sport"),
  VISIT("Visit"),
  GATHERING("Gathering"),
  Habitation("Habitation"),
  PERSONALSERVICES("Personal Services"),
  BURIAL("Animal Contact"),
  OTHER("Other"),
  UNKNOWN("Unknown");

  private String activity;

  TypeOfActivityExposure(String activity) {
    this.activity = activity;
  }

  public String getActivity() {
    return activity;
  }

  public static TypeOfActivityExposure fromString(String activity) {
    for (TypeOfActivityExposure typeOfActivityExposure : TypeOfActivityExposure.values()) {
      if (typeOfActivityExposure.activity.equalsIgnoreCase(activity)) {
        return typeOfActivityExposure;
      }
    }
    System.out.println("Could not map " + activity + " to TypeOfActivityExposure");
    return null;
  }
}
