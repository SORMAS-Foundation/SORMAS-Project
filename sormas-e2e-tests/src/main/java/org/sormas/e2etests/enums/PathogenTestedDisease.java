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
  ACUTE_FLACCID_PARALYSIS("Acute Flaccid Paralysis"),
  ANTHRAX("Anthrax"),
  COVID19("COVID-19"),
  CHOLERA("Cholera"),
  CONGENITAL_RUBELLA("Congenital Rubella"),
  DENGUE_FEVER("Dengue Fever"),
  EBOLA("Ebola Virus Disease"),
  GUINEA_WORM("Guinea Worm"),
  HUMAN_RABIES("Human Rabies"),
  INFLUENZA("Influenza (New subtype)"),
  LASSA("Lassa"),
  MEASLES("Measles"),
  MENINGITIS("Meningitis (CSM)"),
  MONKEYPOX("Monkeypox"),
  NOT_YET_DEFINED("Not Yet Defined"),
  OTHER_EPIDEMIC_DISEASE("Other Epidemic Disease"),
  PLAGUE("Plague"),
  POLIOMYELITIS("Poliomyelitis"),
  UNSPECIFIED_VHF("Unspecified VHF"),
  YELLOW_FEVER("Yellow Fever"),
  ADENOVIRUS("Adenovirus"),
  C_PNEUMONIAE("C.pneumoniae"),
  ENTEROVIRUS("Enterovirus"),
  H_METAPNEUMOVIRUS("H.metapneumovirus"),
  INFLUENZA_A("Influenza A"),
  INFLUENZA_B("Influenza B"),
  M_PNEUMONIAE("M.pneumoniae"),
  PARAINFLUENZA("Parainfluenza (1-4)"),
  PNEUMONIA("Pneumonia"),
  RESPIRATORY_SYNCYTIAL_VIRUS_RSV("Respiratory syncytial virus (RSV)"),
  RHINOVIRUS("Rhinovirus"),
  WEST_NILE_FEVER("West Nile Fever");

  private final String pathogenResults;

  PathogenTestedDisease(String vPathogenDisease) {
    pathogenResults = vPathogenDisease;
  }

  public static String getRandomPathogenTestedDisease() {
    Random random = new Random();
    return String.valueOf(
        PathogenTestedDisease.values()[random.nextInt(values().length)].pathogenResults);
  }
}
