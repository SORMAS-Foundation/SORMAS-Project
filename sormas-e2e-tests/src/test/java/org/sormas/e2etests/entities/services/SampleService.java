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
import org.sormas.e2etests.entities.pojo.web.Sample;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.LaboratoryValues;
import org.sormas.e2etests.enums.PathogenTestedDisease;

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
        .laboratoryName("Laboratory New - " + System.currentTimeMillis())
        .received("Received")
        .receivedDate(LocalDate.now().minusDays(5))
        .specimenCondition("Adequate")
        .labSampleId(faker.number().randomNumber(6, false))
        .commentsOnSample(currentTimeMillis + "Comment on Create Sample requests or received")
        .build();
  }

  public Sample buildAlternateSample() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .purposeOfTheSample("INTERNAL/IN-HOUSE TESTING")
        .dateOfCollection(LocalDate.now().minusDays(10))
        .timeOfCollection(LocalTime.of(11, 30))
        .sampleType("Blood")
        .reasonForSample("Presence of symptoms")
        .sampleID(faker.number().randomNumber(7, false))
        .commentsOnSample(currentTimeMillis + "Comment on Create Sample requests or received")
        .build();
  }

  public Sample buildAlternateSampleWithSelectableDisease(String disease) {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .purposeOfTheSample("INTERNAL/IN-HOUSE TESTING")
        .dateOfCollection(LocalDate.now().minusDays(10))
        .timeOfCollection(LocalTime.of(10, 30))
        .sampleType("Blood")
        .reasonForSample("Presence of symptoms")
        .sampleID(faker.number().randomNumber(7, false))
        .commentsOnSample(currentTimeMillis + "Comment on Create Sample requests or received")
        .sampleTestResults("Positive")
        .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest("Culture")
        .testedDisease(disease)
        .dateOfResult(LocalDate.now().minusDays(10))
        .timeOfResult(LocalTime.of(10, 30))
        .laboratory("Voreingestelltes Labor")
        .resultVerifiedByLabSupervisor("YES")
        .testResultsComment(currentTimeMillis + "Comment on new Pathogen requests or received")
        .testResults("Positive")
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

  public Sample buildGeneratedSampleTestResultForCovid() {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .sampleTestResults("Positive")
        //   .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest("PCR / RT-PCR")
        .testedDisease(DiseasesValues.CORONAVIRUS.getDiseaseCaption())
        .dateOfResult(LocalDate.now().minusDays(10))
        .timeOfResult(LocalTime.of(11, 30))
        .laboratory(LaboratoryValues.OTHER_FACILITY.getCaptionEnglish())
        .resultVerifiedByLabSupervisor("YES")
        .testResultsComment(currentTimeMillis + "Comment on new Pathogen requests or received")
        .build();
  }

  public Sample buildGeneratedSampleWithTestResultForSelectedDiseaseAndTestType(
      String disease, String testType) {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .purposeOfTheSample("INTERNAL/IN-HOUSE TESTING")
        .dateOfCollection(LocalDate.now())
        .sampleTestResults("Positive")
        .sampleType("Blood")
        .laboratory("Voreingestelltes Labor")
        .typeOfTest(testType)
        .testedDisease(disease)
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(11, 30))
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
        .sampleType("Sera")
        .reasonForSample("Screening")
        .sampleID(faker.number().randomNumber(8, false))
        .laboratory("Other facility")
        .laboratoryName("Laboratory Edit " + currentTimeMillis)
        .received("Received")
        .receivedDate(LocalDate.now().minusDays(10))
        .specimenCondition("Adequate")
        .labSampleId(faker.number().randomNumber(7, false))
        .commentsOnSample(currentTimeMillis + "Comment on Edit requests or received")
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

  public Sample buildPathogenTestResultType(String testType) {
    long currentTimeMillis = System.currentTimeMillis();
    String testedDiseaseType;
    if (testType == "PCR / RT-PCR") {
      testedDiseaseType = "COVID-19";
    } else {
      testedDiseaseType = PathogenTestedDisease.getRandomPathogenTestedDisease();
    }
    return Sample.builder()
        //   .reportDate(LocalDate.now().minusDays(10))
        .typeOfTest(testType)
        .testedDisease(testedDiseaseType)
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(15, 15))
        .laboratory("Voreingestelltes Labor")
        .sampleTestResults("Positive")
        .resultVerifiedByLabSupervisor("NO")
        .testResultsComment("Comment on Edit Pathogen requests or received " + currentTimeMillis)
        .build();
  }

  public Sample buildPathogenTestResultTypeVerified(String testType) {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        //   .reportDate(LocalDate.now().minusDays(2))
        .typeOfTest(testType)
        .testedDisease("COVID-19")
        .dateOfResult(LocalDate.now().minusDays(1))
        .timeOfResult(LocalTime.of(15, 15))
        .laboratory("Other facility")
        .laboratoryName("Test laboratory - " + currentTimeMillis)
        .sampleTestResults("Positive")
        .resultVerifiedByLabSupervisor("YES")
        .testResultsComment("Comment on Edit Pathogen requests or received " + currentTimeMillis)
        .build();
  }

  public Sample buildPathogenTestResultTypeVerified(String disease, String testType) {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .typeOfTest(testType)
        .testedDisease(disease)
        .dateOfResult(LocalDate.now().minusDays(1))
        .timeOfResult(LocalTime.of(15, 15))
        .laboratory("Other facility")
        .laboratoryName("Test laboratory - " + currentTimeMillis)
        .sampleTestResults("Positive")
        .resultVerifiedByLabSupervisor("YES")
        .testResultsComment("Comment on Edit Pathogen requests or received " + currentTimeMillis)
        .build();
  }

  public Sample buildPathogenTestUnverifiedDE(String testType) {
    long currentTimeMillis = System.currentTimeMillis();
    return Sample.builder()
        .reportDate(LocalDate.now())
        .typeOfTest(testType)
        .testedDisease("COVID-19")
        .dateOfResult(LocalDate.now())
        .timeOfResult(LocalTime.of(15, 15))
        .laboratory("Andere Einrichtung")
        .laboratoryName("Test name")
        .sampleTestResults("Positiv")
        .resultVerifiedByLabSupervisor("NEIN")
        .testResultsComment("Comment on Edit Pathogen requests or received " + currentTimeMillis)
        .build();
  }

  public Sample buildGeneratedPositiveSampleDE() {
    return Sample.builder()
        .purposeOfTheSample("INTERNER /IN-HOUSE TEST")
        .dateOfCollection(LocalDate.now().minusDays(10))
        .laboratory("Labor")
        .testedDisease("COVID-19")
        .sampleTestResults("Positiv")
        .resultVerifiedByLabSupervisor("JA")
        .sampleType("Nasen-Abstrich")
        .build();
  }

  public Sample buildOnlyRequiredSampleFieldsDE() {
    return Sample.builder()
        .dateOfCollection(LocalDate.now().minusDays(5))
        .laboratory("Labor")
        .sampleType("Nasen-Abstrich")
        .build();
  }

  public Sample buildSampleWithParametrizedLaboratory(String laboratory) {
    return Sample.builder()
        .purposeOfTheSample("EXTERNAL LAB TESTING")
        .dateOfCollection(LocalDate.now())
        .timeOfCollection(LocalTime.of(15, 15))
        .sampleType("Blood")
        .laboratory(laboratory)
        .build();
  }
}
