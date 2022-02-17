package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;

@Getter
public enum GenderValuesDE {
  MALE("M\u00E4nnlich"),
  FEMALE("Weiblich"),
  OTHER("Divers"),
  UNKNOWN("Unbekannt");

  private final String gender;

  GenderValuesDE(String humanGender) {
    gender = humanGender;
  }

  public static String getRandomGenderDE() {
    Random random = new Random();
    return String.valueOf(GenderValuesDE.values()[random.nextInt(values().length)].gender);
  }
}
