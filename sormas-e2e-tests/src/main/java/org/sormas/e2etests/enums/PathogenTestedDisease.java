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

@Getter
public enum PathogenTestedDisease {
  ACUTE_FLACCID_PARALYSIS("ACUTE_VIRAL_HEPATITIS", "Acute Flaccid Paralysis"),
  ANTHRAX("ANTHRAX", "Anthrax"),
  COVID19("CORONAVIRUS", "COVID-19"),
  CHOLERA("CHOLERA", "Cholera"),
  CONGENITAL_RUBELLA("CONGENITAL_RUBELLA", "Congenital Rubella"),
  DENGUE_FEVER("DENGUE", "Dengue Fever"),
  EBOLA("EVD", "Ebola Virus Disease"),
  GUINEA_WORM("GUINEA_WORM", "Guinea Worm"),
  HUMAN_RABIES("RABIES", "Human Rabies"),
  INFLUENZA("NEW_INFLUENZA", "Influenza (New subtype)"),
  LASSA("LASSA", "Lassa"),
  MEASLES("MEASLES", "Measles"),
  MENINGITIS("CSM", "Meningitis (CSM)"),
  MONKEYPOX("MONKEYPOX", "Monkeypox"),
  NOT_YET_DEFINED("UNDEFINED", "Not Yet Defined"),
  OTHER_EPIDEMIC_DISEASE("OTHER", "Other Epidemic Disease"),
  PLAGUE("PLAGUE", "Plague"),
  POLIOMYELITIS("POLIO", "Poliomyelitis"),
  UNSPECIFIED_VHF("UNSPECIFIED_VHF", "Unspecified VHF"),
  YELLOW_FEVER("YELLOW_FEVER", "Yellow Fever"),
  ADENOVIRUS("ADENOVIRUS", "Adenovirus"),
  C_PNEUMONIAE("C_PNEUMONIAE", "C.pneumoniae"),
  ENTEROVIRUS("ENTEROVIRUS", "Enterovirus"),
  H_METAPNEUMOVIRUS("H_METAPNEUMOVIRUS", "H.metapneumovirus"),
  INFLUENZA_A("INFLUENZA_A", "Influenza A"),
  INFLUENZA_B("INFLUENZA_B", "Influenza B"),
  M_PNEUMONIAE("M_PNEUMONIAE", "M.pneumoniae"),
  PARAINFLUENZA("PARAINFLUENZA_1_4", "Parainfluenza (1-4)"),
  PNEUMONIA("PNEUMONIA", "Pneumonia"),
  RESPIRATORY_SYNCYTIAL_VIRUS_RSV(
      "RESPIRATORY_SYNCYTIAL_VIRUS", "Respiratory syncytial virus (RSV)"),
  RHINOVIRUS("RHINOVIRUS", "Rhinovirus"),
  WEST_NILE_FEVER("WEST_NILE_FEVER", "West Nile Fever");

  private final String pathogenTestedDiseaseName;
  private final String pathogenTestedDiseaseCaption;

  PathogenTestedDisease(String pathogenTestedDiseaseName, String pathogenTestedDiseaseCaption) {

    this.pathogenTestedDiseaseName = pathogenTestedDiseaseName;
    this.pathogenTestedDiseaseCaption = pathogenTestedDiseaseCaption;
  }
  /** Returns values used for UI tests */
  public static String getRandomPathogenTestedDisease() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)]
            .pathogenTestedDiseaseCaption);
  }
  /** Returns values used for API tests */
  public static String getRandomPathogenTestedDiseaseName() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)].pathogenTestedDiseaseName);
  }
}
