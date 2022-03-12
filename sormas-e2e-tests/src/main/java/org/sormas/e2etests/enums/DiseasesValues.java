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
  AFP("AFP", "Acute Flaccid Paralysis", "Akute schlaffe L\u00E4hmung"),
  ANTHRAX("ANTHRAX", "Anthrax", "Milzbrand"),
  CORONAVIRUS("CORONAVIRUS", "COVID-19", "COVID-19"),
  CHOLERA("CHOLERA", "Cholera", "Cholera"),
  DENGUE("DENGUE", "Dengue Fever", "Dengue-Fieber"),
  EVD("EVD", "Ebola Virus Disease", "Ebola"),
  GUINEA_WORM("GUINEA_WORM", "Guinea Worm", "Medinawurm"),
  RABIES("RABIES", "Human Rabies", "Tollwut"),
  NEW_INFLUENZA("NEW_INFLUENZA", "Influenza (New subtype)", "Influenza (neuer Subtyp)"),
  LASSA("LASSA", "Lassa", "Lassa"),
  MEASLES("MEASLES", "Measles", "Masern"),
  CSM("CSM", "Meningitis (CSM)", "Meningitis (CSM)"),
  MONKEYPOX("MONKEYPOX", "Monkeypox", "Affenpocken"),
  UNSPECIFIED_VHF(
      "UNSPECIFIED_VHF",
      "Unspecified VHF",
      "Nicht n\u00E4her bezeichnete h\u00E4morrhagische Viruskrankheit"),
  POLIO("POLIO", "Poliomyelitis", "Poliomyelitis"),
  OTHER("OTHER", "Other Epidemic Disease", "Andere epidemische Krankheit"),
  YELLOW_FEVER("YELLOW_FEVER", "Yellow Fever", "Gelbfieber");

  private final String diseaseName;
  private final String diseaseCaption;
  private final String diseaseCaptionDE;
  private static Random random = new Random();

  DiseasesValues(String diseaseName, String diseaseCaption, String diseaseCaptionDE) {
    this.diseaseName = diseaseName;
    this.diseaseCaption = diseaseCaption;
    this.diseaseCaptionDE = diseaseCaptionDE;
  }

  /** Returns values used for UI tests */
  public static String getRandomDiseaseCaption() {
    return String.valueOf(DiseasesValues.values()[random.nextInt(values().length)].diseaseCaption);
  }

  @SneakyThrows
  public static String getRandomDiseaseCaptionDifferentThan(String excludedOption) {
    DiseasesValues[] diseasesValues = DiseasesValues.values();
    for (DiseasesValues value : diseasesValues) {
      if (!value.getDiseaseCaption().equalsIgnoreCase(excludedOption))
        return value.getDiseaseCaption();
    }
    throw new Exception("Unable to provide option different than: " + excludedOption);
  }

  /** Returns values used for German UI tests */
  public static String getRandomDiseaseCaptionDE() {
    return String.valueOf(
        DiseasesValues.values()[random.nextInt(values().length)].diseaseCaptionDE);
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
