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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum DiseasesValues {
  AFP_UI_VALUE("Acute Flaccid Paralysis"),
  ANTHRAX_UI_VALUE("Anthrax"),
  CORONAVIRUS_UI_VALUE("COVID-19"),
  CHOLERA_UI_VALUE("Cholera"),
  DENGUE_UI_VALUE("Dengue Fever"),
  EVD_UI_VALUE("Ebola Virus Disease"),
  GUINEA_WORM_UI_VALUE("Guinea Worm"),
  RABIES_UI_VALUE("Human Rabies"),
  NEW_INFLUENZA_UI_VALUE("Influenza (New subtype)"),
  LASSA_UI_VALUE("Lassa"),
  MEASLES_UI_VALUE("Measles"),
  CSM_UI_VALUE("Meningitis (CSM)"),
  MONKEYPOX_UI_VALUE("Monkeypox"),
  UNSPECIFIED_VHF_UI_VALUE("Unspecified VHF"),
  YELLOW_FEVER_UI_VALUE("Yellow Fever"),

  AFP_API_VALUE("AFP"),
  ANTHRAX_API_VALUE("ANTHRAX"),
  CORONAVIRUS_API_VALUE("CORONAVIRUS"),
  GUINEA_WORM_API_VALUE("GUINEA_WORM"),
  MEASLES_API_VALUE("MEASLES"),
  POLIO_API_VALUE("POLIO"),
  CSM_API_VALUE("CSM"),
  YELLOW_FEVER_API_VALUE("YELLOW_FEVER"),
  RABIES_API_VALUE("RABIES"),
  UNSPECIFIED_VHF_API_VALUE("UNSPECIFIED_VHF"),
  OTHER_API_VALUE("OTHER");

  private final String disease;
  private static Random random = new Random();

  DiseasesValues(String aDisease) {
    disease = aDisease;
  }

  public static String getRandomUiDiseaseValue() {
    List<DiseasesValues> uiDiseasesList =
        Arrays.stream(DiseasesValues.values())
            .filter(disease -> disease.getDisease().contains("UI_VALUE"))
            .collect(Collectors.toList());
    return uiDiseasesList.get(random.nextInt(uiDiseasesList.size())).disease;
  }

  public static String getRandomAPIDiseaseValue() {
    List<DiseasesValues> apiDiseasesList =
        Arrays.stream(DiseasesValues.values())
            .filter(disease -> disease.name().contains("API_VALUE"))
            .collect(Collectors.toList());
    return apiDiseasesList.get(random.nextInt(apiDiseasesList.size())).disease;
  }
}
