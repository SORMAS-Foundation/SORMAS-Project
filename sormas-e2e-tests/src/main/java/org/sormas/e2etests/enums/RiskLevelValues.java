package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum RiskLevelValues {
  LOW("LOW", "Low risk"),
  Moderate("MODERATE", "Moderate risk"),
  HIGH("HIGH", "High risk"),
  UNKNOWN("UNKNOWN", "Unknown");

  private final String riskLevelName;
  private final String riskLevelCaption;
  private static Random random = new Random();

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
  /** Returns values used for API tests */
  public static String getRandomRiskLevelName() {
    return String.valueOf(RiskLevelValues.values()[random.nextInt(values().length)].riskLevelName);
  }
  /** Returns values used for UI tests */
  public static String getRandomRiskLevelCaption() {
    return String.valueOf(
        RiskLevelValues.values()[random.nextInt(values().length)].riskLevelCaption);
  }
}
