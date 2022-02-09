package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum CaseOrigin {
  IN_COUNTRY("In-Country"),
  POINT_OF_ENTRY("Point of Entry");

  private final String name;

  CaseOrigin(String name) {
    this.name = name;
  }

  public static String getRandomOrigin() {
    Random random = new Random();
    return String.valueOf(CaseOrigin.values()[random.nextInt(values().length)]);
  }

  @SneakyThrows
  public static String getValueFor(String option) {
    CaseOrigin[] caseOriginOptions = CaseOrigin.values();
    for (CaseOrigin value : caseOriginOptions) {
      if (value.getName().equalsIgnoreCase(option)) return value.getName();
    }
    throw new Exception("Unable to find " + option + " value in CaseOrigin Enum");
  }
}
