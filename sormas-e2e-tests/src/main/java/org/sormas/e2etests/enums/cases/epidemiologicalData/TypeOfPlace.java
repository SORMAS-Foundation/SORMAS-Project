package org.sormas.e2etests.enums.cases.epidemiologicalData;

import java.util.Random;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TypeOfPlace {
  FACILITY("Facility"),
  FESTIVITIES("Festivities"),
  HOME("Home"),
  MEANS_OF_TRANSPORT("Means of transport"),
  PUBLIC_PLACE("Public place"),
  SCATTERED("Scattered"),
  UNKNOWN("Unknown"),
  OTHER("Other");

  private final String uiValue;
  private static Random random = new Random();

  TypeOfPlace(String uiValue) {
    this.uiValue = uiValue;
  }

  public String getUiValue() {
    return uiValue;
  }

  public static TypeOfPlace fromString(String place) {
    for (TypeOfPlace typeOfPlace : TypeOfPlace.values()) {
      if (typeOfPlace.uiValue.equalsIgnoreCase(place)) {
        return typeOfPlace;
      }
    }
    log.error("Couldn't map place!");
    return null;
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    TypeOfPlace[] typeOfPlaceOptions = TypeOfPlace.values();
    for (TypeOfPlace value : typeOfPlaceOptions) {
      if (value.name().equalsIgnoreCase(option)) return value.getUiValue();
    }
    throw new Exception("Unable to find " + option + " value in TypeOfPlaceValues Enum");
  }

  public static String getRandomUITypeOfPlace() {
    return String.valueOf(TypeOfPlace.values()[random.nextInt(values().length)]);
  }
}
