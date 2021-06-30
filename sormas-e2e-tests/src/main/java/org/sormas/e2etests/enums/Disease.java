/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
public enum Disease {
  AFP("Acute Flaccid Paralysis"),
  ANTHRAX("Anthrax"),
  CORONAVIRUS("COVID-19"),
  CHOLERA("Cholera"),
  DENGUE("Dengue Fever"),
  EVD("Ebola Virus Disease"),
  GUINEA_WORM("Guinea Worm"),
  RABIES("Human Rabies"),
  NEW_INFLUENZA("Influenza (New subtype)"),
  LASSA("Lassa"),
  MEASLES("Measles"),
  CSM("Meningitis (CSM)"),
  MONKEYPOX("Monkeypox"),
  UNSPECIFIED_VHF("Unspecified VHF"),
  YELLOW_FEVER("Yellow Fever");

  private final String disease;

  Disease(String aDisease) {
    disease = aDisease;
  }

  public static String getRandomDisease() {
    Random random = new Random();
    return String.valueOf(Disease.values()[random.nextInt(values().length)]);
  }

  public static String getRandomDiseaseValue() {
    Random random = new Random();
    return String.valueOf(Disease.values()[random.nextInt(values().length)].disease);
  }
}
