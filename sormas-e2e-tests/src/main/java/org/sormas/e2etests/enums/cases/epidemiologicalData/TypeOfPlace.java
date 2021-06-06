package org.sormas.e2etests.enums.cases.epidemiologicalData;

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
    for (TypeOfPlace b : TypeOfPlace.values()) {
      if (b.place.equalsIgnoreCase(place)) {
        return b;
      }
    }
    return null;
  }
}
