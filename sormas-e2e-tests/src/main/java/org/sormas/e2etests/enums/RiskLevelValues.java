package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum RiskLevelValues {
  LOW("LOW", "Low risk", "Geringes Risiko"),
  Moderate("MODERATE", "Moderate risk", "M\u00E4\u00DFiges Risiko"),
  HIGH("HIGH", "High risk", "Hohes Risiko"),
  UNKNOWN("UNKNOWN", "Unknown", "Unbekannt");

  private final String riskLevelName;
  private final String riskLevelCaption;
  private final String riskLevelCaptionDE;
  private static Random random = new Random();

  RiskLevelValues(String riskLevelName, String riskLevelCaption, String riskLevelCaptionDE) {
    this.riskLevelName = riskLevelName;
    this.riskLevelCaption = riskLevelCaption;
    this.riskLevelCaptionDE = riskLevelCaptionDE;
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    RiskLevelValues[] riskyLevelOptions = RiskLevelValues.values();
    for (RiskLevelValues value : riskyLevelOptions) {
      if (value.riskLevelName.equalsIgnoreCase(option)) return value.riskLevelCaption;
    }
    throw new Exception("Unable to find " + option + " value in RiskLevelValues Enum");
  }

  @SneakyThrows
  public static String getCaptionForNameDE(String option) {
    RiskLevelValues[] riskyLevelOptions = RiskLevelValues.values();
    for (RiskLevelValues value : riskyLevelOptions) {
      if (value.riskLevelName.equalsIgnoreCase(option)) return value.riskLevelCaptionDE;
    }
    throw new Exception("Unable to find " + option + " value in RiskLevelValues Enum");
  }

  @SneakyThrows
  public static String getRandomUIRiskLevelDifferentThan(String excludedOption) {
    RiskLevelValues[] riskLevelValueOptions = RiskLevelValues.values();
    for (RiskLevelValues value : riskLevelValueOptions) {
      if (!value.getRiskLevelCaption().equalsIgnoreCase(excludedOption)
          && !value.getRiskLevelName().equalsIgnoreCase(excludedOption))
        return value.getRiskLevelCaption();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  @SneakyThrows
  public static String getRandomUIRiskLevelDifferentThanDE(String excludedOption) {
    RiskLevelValues[] riskLevelValueOptions = RiskLevelValues.values();
    for (RiskLevelValues value : riskLevelValueOptions) {
      if (!value.getRiskLevelCaption().equalsIgnoreCase(excludedOption)
          && !value.getRiskLevelName().equalsIgnoreCase(excludedOption))
        return value.getRiskLevelCaptionDE();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
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
