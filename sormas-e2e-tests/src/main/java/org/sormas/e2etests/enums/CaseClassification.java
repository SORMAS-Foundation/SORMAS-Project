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
  NOT_CLASSIFIED("Not yet classified", "NOT_CLASSIFIED"),
  SUSPECT("Suspect case", "SUSPECT"),
  PROBABLE("Probable case", "PROBABLE"),
  CONFIRMED("Confirmed case", "CONFIRMED"),
  CONFIRMED_NO_SYMPTOMS("Confirmed case with no symptoms", "CONFIRMED_NO_SYMPTOMS"),
  CONFIRMED_UNKNOWN_SYMPTOMS("Confirmed case with unknown symptoms", "CONFIRMED_UNKNOWN_SYMPTOMS"),
  NO_CASE("Not a case", "NO_CASE");

  private final String classificationUIvalue;
  private final String classificationAPIvalue;

  CaseClassification(String uiValue, String APIvalue) {
    classificationUIvalue = uiValue;
    classificationAPIvalue = APIvalue;
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
  public static String getAPIValueFor(String option) {
    CaseClassification[] classifications = CaseClassification.values();
    for (CaseClassification value : classifications) {
      if (value.getClassificationAPIvalue().replaceAll("_", " ").contains(option.toUpperCase()))
        return value.getClassificationAPIvalue();
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

  public static String getRandomAPIClassification() {
    Random random = new Random();
    return String.valueOf(
        CaseClassification.values()[random.nextInt(values().length)].classificationAPIvalue);
  }
}
