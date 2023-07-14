/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.enums.immunizations;

import java.util.Random;
import lombok.Getter;

@Getter
public enum VaccineNameValues {
  COMIRNATY("Comirnaty (COVID-19-mRNA Impfstoff)"),
  MRNA_BIVALENT_BA_1_BIONTECH_PFIZER("mRNA/bivalent BA.1 (BioNTech/Pfizer)"),
  MRNA_BIVALENT_BA_4_5_BIONTECH_PFIZER("mRNA/bivalent BA.4/5 (BioNTech/Pfizer)"),
  COVID_19_IMPFSTOFF_MODERNA("COVID-19 Impfstoff Moderna (mRNA-Impfstoff)"),
  MRNA_BIVALENT_BA_1_MODERNA("mRNA/bivalent BA.1 (Moderna)"),
  MRNA_BIVALENT_BA_4_5_MODERNA("mRNA/bivalent BA.4/5 (Moderna)"),
  INAKTIVIERT("inaktiviert (Valneva)"),
  NVX_COV2373_COVID19("NVX-CoV2373 COVID-19 Impfstoff (Novavax)"),
  PROTEINBASIERT("proteinbasiert, rekombinant (Novavax)");

  private final String vaccineName;

  private static Random random = new Random();

  VaccineNameValues(String vaccineName) {
    this.vaccineName = vaccineName;
  }

  public static String getRandomVaccineName() {
    return String.valueOf(VaccineNameValues.values()[random.nextInt(values().length)].vaccineName);
  }
}