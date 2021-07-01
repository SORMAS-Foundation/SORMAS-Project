package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TypeOfPlace {
  FACILITY("Facility"),
  FESTIVITIES("Festivities"),
  HOME("Home"),
  MEANS_OF_TRANSPORT("Means of transport"),
  PUBLIC_PLACE("Public Place"),
  SCATTERED("Scattered"),
  UNKNOWN("Unknown"),
  OTHER("Other");

  private String place;

  TypeOfPlace(String place) {
    this.place = place;
  }

  public String getPlace() {
    return place;
  }

  public static TypeOfPlace fromString(String place) {
    for (TypeOfPlace typeOfPlace : TypeOfPlace.values()) {
      if (typeOfPlace.place.equalsIgnoreCase(place)) {
        return typeOfPlace;
      }
    }
    log.error("Couldn't map place!");
    return null;
  }
}
