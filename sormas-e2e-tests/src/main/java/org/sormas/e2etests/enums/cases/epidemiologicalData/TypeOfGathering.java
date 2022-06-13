package org.sormas.e2etests.enums.cases.epidemiologicalData;

import lombok.SneakyThrows;

public enum TypeOfGathering {
  PARTY("Party", "Feier"),
  RELIGIOUS_GATHERING("Religious Gathering", "Religi\u00F6se Versammlung"),
  CHOIR_SINGING_CLUB_ORCHESTRA("Choir/Singing Club/Orchestra", "Chor/Gesangverein/Orchester"),
  CONCERT("Concert", "Konzert"),
  DEMONSTRATION("Demonstration", "Demonstration"),
  CARNIVAL("Carnival", "Karneval"),
  FAIR("Fair", "Messe"),
  SPORTING_EVENT("Sporting event", "Sportveranstaltung"),
  OTHER("Other", "Sonstiges");

  private final String gatheringType;
  private final String gatheringTypeDE;

  TypeOfGathering(String gatheringType, String gatheringTypeDE) {
    this.gatheringType = gatheringType;
    this.gatheringTypeDE = gatheringTypeDE;
  }

  @Override
  public String toString() {
    return this.gatheringType;
  }

  @SneakyThrows
  public static String getNameForDE(String option) {
    TypeOfGathering[] typeOfGatheringOption = TypeOfGathering.values();
    for (TypeOfGathering value : typeOfGatheringOption) {
      if (value.gatheringType.equalsIgnoreCase(option)) return value.gatheringTypeDE;
    }
    throw new Exception("Unable to find " + option + " value in TypeOfGathering Enum");
  }
}
