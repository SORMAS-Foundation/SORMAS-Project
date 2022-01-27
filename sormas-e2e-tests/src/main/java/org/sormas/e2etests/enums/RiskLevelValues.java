package org.sormas.e2etests.enums;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum RiskLevelValues {
  LOW("LOW", "Low risk"),
  Moderate("Moderate", "Moderate risk"),
  HIGH("HIGH", "High risk"),
  UNKNOWN("UNKNOWN", "Unknown");

  private final String riskLevelName;
  private final String riskLevelCaption;

  RiskLevelValues(String riskLevelName, String riskLevelCaption) {
    this.riskLevelName = riskLevelName;
    this.riskLevelCaption = riskLevelCaption;
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    RiskLevelValues[] riskyLevelOptions = RiskLevelValues.values();
    for (RiskLevelValues value : riskyLevelOptions) {
      if (value.riskLevelName.equalsIgnoreCase(option)) return value.riskLevelCaption;
    }
    throw new Exception("Unable to find " + option + " value in RiskLevelValues Enum");
  }
}
