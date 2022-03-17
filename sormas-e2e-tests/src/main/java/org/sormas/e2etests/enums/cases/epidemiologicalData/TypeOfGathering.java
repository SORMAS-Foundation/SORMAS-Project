package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum TypeOfGathering {
  PARTY("Party"),
  RELIGIOUS_GATHERING("Religious Gathering"),
  CHOIR_SINGING_CLUB_ORCHESTRA("Choir/Singing Club/Orchestra"),
  CONCERT("Concert"),
  DEMONSTRATION("Demonstration"),
  CARNIVAL("Carnival"),
  FAIR("Fair"),
  SPORTING_EVENT("Sporting event"),
  OTHER("Other");

  private final String gatheringType;

  TypeOfGathering(String gatheringType) {
    this.gatheringType = gatheringType;
  }

  @Override
  public String toString() {
    return this.gatheringType;
  }
}
