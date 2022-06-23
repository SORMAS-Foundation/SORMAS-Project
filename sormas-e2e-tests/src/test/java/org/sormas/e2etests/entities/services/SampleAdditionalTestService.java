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
import java.time.LocalTime;
import org.sormas.e2etests.entities.pojo.web.SampleAdditionalTest;

public class SampleAdditionalTestService {
  private final Faker faker;

  @Inject
  public SampleAdditionalTestService(Faker faker) {
    this.faker = faker;
  }

  public SampleAdditionalTest buildSampleAdditionalTestResult() {
    long currentTimeMillis = System.currentTimeMillis();
    return SampleAdditionalTest.builder()
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(15, 15))
        .haemoglobinInUrine("Positive")
        .proteinInUrine("Positive")
        .redBloodCellsInUrine("Positive")
        .ph(String.valueOf(faker.number().numberBetween(0, 9)))
        .pCO2(String.valueOf(faker.number().numberBetween(0, 9)))
        .pAO2(String.valueOf(faker.number().numberBetween(0, 9)))
        .hCO3(String.valueOf(faker.number().numberBetween(0, 9)))
        .oxygen(String.valueOf(faker.number().numberBetween(0, 9)))
        .sgpt(String.valueOf(faker.number().numberBetween(0, 9)))
        .totalBilirubin(String.valueOf(faker.number().numberBetween(0, 9)))
        .sgot(String.valueOf(faker.number().numberBetween(0, 9)))
        .conjBilirubin(String.valueOf(faker.number().numberBetween(0, 9)))
        .creatine(String.valueOf(faker.number().numberBetween(0, 9)))
        .wbc(String.valueOf(faker.number().numberBetween(0, 9)))
        .potassium(String.valueOf(faker.number().numberBetween(0, 9)))
        .platelets(String.valueOf(faker.number().numberBetween(0, 9)))
        .urea(String.valueOf(faker.number().numberBetween(0, 9)))
        .prothrombin(String.valueOf(faker.number().numberBetween(0, 9)))
        .haemoglobin(String.valueOf(faker.number().numberBetween(0, 9)))
        .otherResults(currentTimeMillis + "Other results for Additional Test in Sample Directory")
        .build();
  }
}
