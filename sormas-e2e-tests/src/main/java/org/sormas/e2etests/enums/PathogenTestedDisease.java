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
  ACUTE_FLACCID_PARALYSIS(
      "ACUTE_VIRAL_HEPATITIS", "Acute Flaccid Paralysis", "Akute schlaffe L\u00E4hmung"),
  ANTHRAX("ANTHRAX", "Anthrax", "Milzbrand"),
  COVID19("CORONAVIRUS", "COVID-19", "COVID-19"),
  CHOLERA("CHOLERA", "Cholera", "Cholera"),
  CONGENITAL_RUBELLA("CONGENITAL_RUBELLA", "Congenital Rubella", "Kongenitale Röteln"),
  DENGUE_FEVER("DENGUE", "Dengue Fever", "Dengue-Fieber"),
  EBOLA("EVD", "Ebola Virus Disease", "Ebola"),
  GUINEA_WORM("GUINEA_WORM", "Guinea Worm", "Medinawurm"),
  HUMAN_RABIES("RABIES", "Human Rabies", "Tollwut"),
  INFLUENZA("NEW_INFLUENZA", "Influenza (New subtype)", "Influenza (neuer Subtyp)"),
  LASSA("LASSA", "Lassa", "Lassa"),
  MEASLES("MEASLES", "Measles", "Masern"),
  MENINGITIS("CSM", "Meningitis (CSM)", "Meningitis (CSM)"),
  MONKEYPOX("MONKEYPOX", "Monkeypox", "Affenpocken"),
  NOT_YET_DEFINED("UNDEFINED", "Not Yet Defined", "Noch nicht definiert"),
  OTHER_EPIDEMIC_DISEASE("OTHER", "Other Epidemic Disease", "Andere epidemische Krankheit"),
  PLAGUE("PLAGUE", "Plague", "Pest"),
  POLIOMYELITIS("POLIO", "Poliomyelitis", "Poliomyelitis"),
  UNSPECIFIED_VHF(
      "UNSPECIFIED_VHF",
      "Unspecified VHF",
      "Nicht n\u00E4her bezeichnete h\u00E4morrhagische Viruskrankheit"),
  YELLOW_FEVER("YELLOW_FEVER", "Yellow Fever", "Gelbfieber"),
  ADENOVIRUS("ADENOVIRUS", "Adenovirus", "Adenovirus"),
  C_PNEUMONIAE("C_PNEUMONIAE", "C.pneumoniae", "Chlamydophila pneumoniae"),
  ENTEROVIRUS("ENTEROVIRUS", "Enterovirus", "Enterovirus"),
  H_METAPNEUMOVIRUS("H_METAPNEUMOVIRUS", "H.metapneumovirus", "Humanes Metapneumovirus"),
  INFLUENZA_A("INFLUENZA_A", "Influenza A", "Influenza A"),
  INFLUENZA_B("INFLUENZA_B", "Influenza B", "Influenza B"),
  M_PNEUMONIAE("M_PNEUMONIAE", "M.pneumoniae", "Mycoplasma pneumoniae"),
  PARAINFLUENZA("PARAINFLUENZA_1_4", "Parainfluenza (1-4)", "Parainfluenza (1-4)"),
  PNEUMONIA("PNEUMONIA", "Pneumonia", "Lungenentz\u00FCndung"),
  RESPIRATORY_SYNCYTIAL_VIRUS_RSV(
      "RESPIRATORY_SYNCYTIAL_VIRUS",
      "Respiratory syncytial virus (RSV)",
      "Humanes Respiratorisches Synzytial-Virus (RSV)"),
  RHINOVIRUS("RHINOVIRUS", "Rhinovirus", "Rhinovirus"),
  WEST_NILE_FEVER("WEST_NILE_FEVER", "West Nile Fever", "West-Nil-Fieber");

  private final String pathogenTestedDiseaseName;
  private final String pathogenTestedDiseaseCaption;
  private final String pathogenTestedDiseaseCaptionDE;

  PathogenTestedDisease(
      String pathogenTestedDiseaseName,
      String pathogenTestedDiseaseCaption,
      String pathogenTestedDiseaseCaptionDE) {

    this.pathogenTestedDiseaseName = pathogenTestedDiseaseName;
    this.pathogenTestedDiseaseCaption = pathogenTestedDiseaseCaption;
    this.pathogenTestedDiseaseCaptionDE = pathogenTestedDiseaseCaptionDE;
  }
  /** Returns values used for UI tests */
  public static String getRandomPathogenTestedDisease() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)]
            .pathogenTestedDiseaseCaption);
  }

  /** Returns values used for German UI tests */
  public static String getRandomPathogenTestedDiseaseDE() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)]
            .pathogenTestedDiseaseCaptionDE);
  }

  /** Returns values used for API tests */
  public static String getRandomPathogenTestedDiseaseName() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)].pathogenTestedDiseaseName);
  }
}
