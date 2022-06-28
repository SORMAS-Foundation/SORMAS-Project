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

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.entities.pojo.web.Vaccination;

public class VaccinationService {
  private final Faker faker;

  @Inject
  public VaccinationService(Faker faker) {
    this.faker = faker;
  }

  public Vaccination buildGeneratedVaccinationDE() {
    return Vaccination.builder()
        .vaccinationDate(LocalDate.now().minusDays(1))
        .vaccineName("COVID-19 Impfstoff Moderna (mRNA-Impfstoff)")
        .vaccineManufacturer("Moderna")
        .vaccineType(faker.medical().medicineName())
        .vaccinationInfoSource("Impfpass")
        .vaccineDose(String.valueOf(faker.number().numberBetween(0, 3)))
        .inn(String.valueOf(faker.idNumber()))
        .uniiCode(String.valueOf(faker.idNumber()))
        .batchNumber(String.valueOf(faker.idNumber()))
        .atcCode(String.valueOf(faker.idNumber()))
        .build();
  }

  public Vaccination buildGeneratedVaccination() {
    return Vaccination.builder()
        .vaccinationDate(LocalDate.now().minusDays(1))
        .vaccineName("Unknown")
        .vaccineManufacturer("Unknown")
        .vaccineType(faker.medical().medicineName())
        .vaccinationInfoSource("Vaccination card")
        .vaccineDose(String.valueOf(faker.number().numberBetween(0, 3)))
        .inn(String.valueOf(faker.idNumber()))
        .uniiCode(String.valueOf(faker.idNumber()))
        .batchNumber(String.valueOf(faker.idNumber()))
        .atcCode(String.valueOf(faker.idNumber()))
        .build();
  }
}
