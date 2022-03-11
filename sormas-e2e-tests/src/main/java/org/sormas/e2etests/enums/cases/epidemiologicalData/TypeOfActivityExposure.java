package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  public static String[] ListOfTypeOfActivityExposure = {
    "Work", "Travel", "Sport", "Visit", "Habitation", "Personal Services", "Unknown"
  };

  public String getActivity() {
    return activity;
  }

  public static TypeOfActivityExposure fromString(String activity) {
    for (TypeOfActivityExposure typeOfActivityExposure : TypeOfActivityExposure.values()) {
      if (typeOfActivityExposure.activity.equalsIgnoreCase(activity)) {
        return typeOfActivityExposure;
      }
    }
    log.error("Couldn't map activity!");
    return null;
  }
}
