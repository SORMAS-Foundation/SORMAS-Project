package org.sormas.e2etests.enums;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum TypeOfPlaceValues {
  FACILITY("FACILITY", "Facility"),
  FESTIVITIES("FESTIVITIES", "Festivities"),
  HOME("HOME", "Home"),
  MEANS_OF_TRANSPORT("MEANS_OF_TRANSPORT", "Means of transport"),
  PUBLIC_PLACE("PUBLIC_PLACE", "Public place"),
  SCATTERED("SCATTERED", "Scattered"),
  UNKNOWN("UNKNOWN", "Unknown"),
  OTHER("OTHER", "Other");

  private final String typeOfPlaceName;
  private final String typeOfPlaceCaption;

  TypeOfPlaceValues(String typeOfPlaceName, String typeOfPlaceCaption) {
    this.typeOfPlaceName = typeOfPlaceName;
    this.typeOfPlaceCaption = typeOfPlaceCaption;
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    TypeOfPlaceValues[] typeOfPlaceOptions = TypeOfPlaceValues.values();
    for (TypeOfPlaceValues value : typeOfPlaceOptions) {
      if (value.typeOfPlaceName.equalsIgnoreCase(option)) return value.typeOfPlaceCaption;
    }
    throw new Exception("Unable to find " + option + " value in TypeOfPlaceValues Enum");
  }
}
