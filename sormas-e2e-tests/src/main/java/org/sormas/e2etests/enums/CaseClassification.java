/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.enums;

import java.util.Random;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum CaseClassification {
  NOT_CLASSIFIED("Not yet classified", "NOT_CLASSIFIED", "0. Nicht klassifiziert"),
  SUSPECT("Suspect case", "SUSPECT", "A. Klinisch diagnostiziert"),
  PROBABLE("Probable case", "PROBABLE", "B. Klinisch-epidemiologisch best\u00E4tigt"),
  CONFIRMED("Confirmed case", "CONFIRMED", "C. Klinisch-labordiagnostisch best\u00E4tigt"),
  CONFIRMED_NO_SYMPTOMS(
      "Confirmed case with no symptoms",
      "CONFIRMED_NO_SYMPTOMS",
      "D. Labordiagnostisch bei nicht erf\u00F7llter Klinik"),
  CONFIRMED_UNKNOWN_SYMPTOMS(
      "Confirmed case with unknown symptoms",
      "CONFIRMED_UNKNOWN_SYMPTOMS",
      "E. Labordiagnostisch bei unbekannter Klinik"),
  NO_CASE("Not a case", "NO_CASE", "X. kein Fall");

  private final String classificationUIvalue;
  private final String classificationAPIvalue;
  private final String classificationUIvalueDE;

  CaseClassification(String uiValue, String APIvalue, String uiValueDE) {
    classificationUIvalue = uiValue;
    classificationAPIvalue = APIvalue;
    classificationUIvalueDE = uiValueDE;
  }

  @SneakyThrows
  public static String getUIValueFor(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationUIvalue().equalsIgnoreCase(option))
        return value.getClassificationUIvalue();
    }
    throw new Exception("Unable to find " + option + " value in CaseClassification Enum");
  }

  @SneakyThrows
  public static String getDeUIValueFor(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationUIvalue().equalsIgnoreCase(option))
        return value.getClassificationUIvalueDE();
    }
    throw new Exception("Unable to find " + option + " value in CaseClassification Enum");
  }

  @SneakyThrows
  public static String getAPIValueFor(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationAPIvalue().replaceAll("_", " ").contains(option.toUpperCase()))
        return value.getClassificationAPIvalue();
    }
    throw new Exception("Unable to find " + option + " value in CaseClassification Enum");
  }

  @SneakyThrows
  public static String getUIValueForGivenAPIValue(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationAPIvalue().equalsIgnoreCase(option))
        return value.getClassificationUIvalue();
    }
    throw new Exception("Unable to find " + option + " value in CaseClassification Enum");
  }

  @SneakyThrows
  public static String getCaptionValueFor(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationAPIvalue().equalsIgnoreCase(option))
        return value.getClassificationUIvalue();
    }
    throw new Exception("Unable to find " + option + " value in CaseClassification Enum");
  }

  public static String getRandomUIClassification() {
    Random random = new Random();
    return String.valueOf(
        CaseClassification.values()[random.nextInt(values().length)].classificationUIvalue);
  }

  @SneakyThrows
  public static String getRandomUIClassificationDifferentThan(String excludedOption) {
    CaseClassification[] caseClassifications = CaseClassification.values();
    for (CaseClassification value : caseClassifications) {
      if (!value.getClassificationUIvalue().equalsIgnoreCase(excludedOption)
          && !value.getClassificationAPIvalue().equalsIgnoreCase(excludedOption))
        return value.getClassificationUIvalue();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  @SneakyThrows
  public static String getRandomUIClassificationDifferentThanDE(String excludedOption) {
    CaseClassification[] caseClassifications = CaseClassification.values();
    for (CaseClassification value : caseClassifications) {
      if (!value.getClassificationUIvalue().equalsIgnoreCase(excludedOption)
          && !value.getClassificationAPIvalue().equalsIgnoreCase(excludedOption))
        return value.getClassificationUIvalueDE();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  public static String getRandomAPIClassification() {
    Random random = new Random();
    return String.valueOf(
        CaseClassification.values()[random.nextInt(values().length)].classificationAPIvalue);
  }
}
