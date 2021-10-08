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

package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import org.sormas.e2etests.pojo.web.Sample;

public class SampleService {
  private final Faker faker;

  @Inject
  public SampleService(Faker faker) {
    this.faker = faker;
  }

  public Sample buildGeneratedSample() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .purposeOfTheSample("EXTERNAL LAB TESTING")
        .dateOfCollection(LocalDate.now().minusDays(10))
        .timeOfCollection(LocalTime.of(11, 30))
        .sampleType("Blood")
        .reasonForSample("Presence of symptoms")
        .sampleID(faker.number().randomNumber(7, false))
        .laboratory("Other facility")
        .laboratoryName("Laboratory Create")
        .received("Received")
        .receivedDate(LocalDate.now().minusDays(5))
        .specimenCondition("Adequate")
        .labSampleId(faker.number().randomNumber(6, false))
        .commentsOnSample(currentTimeMillis + "Comment on Create Sample requests or received")
        .build();
  }

  public Sample buildGeneratedSampleTestResult() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .sampleTestResults("Pending")
        .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest("Culture")
        .testedDisease("COVID-19")
        .dateOfResult(LocalDate.now().minusDays(10))
        .timeOfResult(LocalTime.of(11, 30))
        .laboratory("Other facility")
        .resultVerifiedByLabSupervisor("YES")
        .testResultsComment(currentTimeMillis + "Comment on new Pathogen requests or received")
        .build();
  }

  public Sample buildEditSample() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .purposeOfTheSample("EXTERNAL LAB TESTING")
        .dateOfCollection(LocalDate.now().minusDays(15))
        .timeOfCollection(LocalTime.of(15, 15))
        .sampleType("Stool")
        .reasonForSample("Screening")
        .sampleID(faker.number().randomNumber(8, false))
        .laboratory("Other facility")
        .laboratoryName("Laboratory Edit")
        .received("Received")
        .receivedDate(LocalDate.now().minusDays(10))
        .specimenCondition("Adequate")
        .labSampleId(faker.number().randomNumber(7, false))
        .commentsOnSample(currentTimeMillis + "Comment on Edit requests or received")
        .build();
  }

  public Sample buildEditSampleEditTestResult() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest("Histopathology")
        .testedDisease("Anthrax")
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(15, 15))
        .resultVerifiedByLabSupervisor("NO")
        .laboratory("Voreingestelltes Labor")
        .testResultsComment(currentTimeMillis + "Comment on Edit Pathogen requests or received")
        .build();
  }

  public Sample buildPathogenTestResult() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest("Histopathology")
        .testedDisease("Anthrax")
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(15, 15))
        .laboratory("Voreingestelltes Labor")
        .sampleTestResults("Positive")
        .resultVerifiedByLabSupervisor("NO")
        .testResultsComment(currentTimeMillis + "Comment on Edit Pathogen requests or received")
        .build();
  }
}
