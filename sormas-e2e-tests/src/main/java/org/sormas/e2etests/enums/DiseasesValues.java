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
public enum DiseasesValues {
  AFP("AFP", "Acute Flaccid Paralysis"),
  ANTHRAX("ANTHRAX", "Anthrax"),
  CORONAVIRUS("CORONAVIRUS", "COVID-19"),
  CHOLERA("CHOLERA", "Cholera"),
  DENGUE("DENGUE", "Dengue Fever"),
  EVD("EVD", "Ebola Virus Disease"),
  GUINEA_WORM("GUINEA_WORM", "Guinea Worm"),
  RABIES("RABIES", "Human Rabies"),
  NEW_INFLUENZA("NEW_INFLUENZA", "Influenza (New subtype)"),
  LASSA("LASSA", "Lassa"),
  MEASLES("MEASLES", "Measles"),
  CSM("CSM", "Meningitis (CSM)"),
  MONKEYPOX("MONKEYPOX", "Monkeypox"),
  UNSPECIFIED_VHF("UNSPECIFIED_VHF", "Unspecified VHF"),
  POLIO("POLIO", "Poliomyelitis"),
  OTHER("OTHER", "Other Epidemic Disease"),
  YELLOW_FEVER("YELLOW_FEVER", "Yellow Fever");

  private final String diseaseName;
  private final String diseaseCaption;
  private static Random random = new Random();

  DiseasesValues(String diseaseName, String diseaseCaption) {
    this.diseaseName = diseaseName;
    this.diseaseCaption = diseaseCaption;
  }

  /** Returns values used for UI tests */
  public static String getRandomDiseaseCaption() {
    return String.valueOf(DiseasesValues.values()[random.nextInt(values().length)].diseaseCaption);
  }

  @SneakyThrows
  public static String getCaptionFor(String option) {
    DiseasesValues[] diseasesOptions = DiseasesValues.values();
    for (DiseasesValues value : diseasesOptions) {
      if (value.getDiseaseCaption().equalsIgnoreCase(option)) return value.getDiseaseCaption();
    }
    throw new Exception("Unable to find " + option + " value in DiseasesValues Enum");
  }

  @SneakyThrows
  public static String getCaptionForName(String option) {
    DiseasesValues[] diseasesOptions = DiseasesValues.values();
    for (DiseasesValues value : diseasesOptions) {
      if (value.getDiseaseName().equalsIgnoreCase(option)) return value.getDiseaseCaption();
    }
    throw new Exception("Unable to find " + option + " value in DiseasesValues Enum");
  }

  /** Returns values used for API tests */
  public static String getRandomDiseaseName() {
    return String.valueOf(DiseasesValues.values()[random.nextInt(values().length)].diseaseName);
  }
}
