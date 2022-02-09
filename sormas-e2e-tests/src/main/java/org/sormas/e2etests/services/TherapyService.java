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

package org.sormas.e2etests.services;

import static org.sormas.e2etests.enums.RouteValues.getRandomRouteValuesWithoutOther;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import org.sormas.e2etests.enums.TreatmentValues;
import org.sormas.e2etests.pojo.web.Therapy;

public class TherapyService {
  private final Faker faker;

  @Inject
  public TherapyService(Faker faker) {
    this.faker = faker;
  }

  public Therapy buildPrescriptionDrugIntake() {
    return Therapy.builder()
        .prescriptionType(TreatmentValues.DRUG_INTAKE.getValueType())
        .prescriptionDetails(faker.medical().medicineName())
        .prescriptionDate(LocalDate.now())
        .prescribingClinician(faker.name().firstName() + " " + faker.name().lastName())
        .prescriptionStartDate(LocalDate.now().minusDays(3))
        .prescriptionEndDate(LocalDate.now())
        .prescriptionFrequency(String.valueOf(faker.number().numberBetween(1, 100)))
        .prescriptionDose(String.valueOf(faker.number().numberBetween(1, 15)))
        .prescriptionRoute(getRandomRouteValuesWithoutOther())
        .prescriptionAdditionalNotes(faker.howIMetYourMother().catchPhrase())
        .build();
  }

  public Therapy buildTreatmentDrugIntake() {
    return Therapy.builder()
        .treatmentType(TreatmentValues.DRUG_INTAKE.getValueType())
        .treatmentDetails(faker.medical().medicineName())
        .treatmentDate(LocalDate.now().minusDays(1))
        .treatmentTime(LocalTime.of(07, 30))
        .treatmentExecutingStaffMember(faker.name().firstName() + " " + faker.name().lastName())
        .treatmentDose(String.valueOf(faker.number().numberBetween(1, 15)))
        .treatmentRoute(getRandomRouteValuesWithoutOther())
        .treatmentAdditionalNotes(faker.chuckNorris().fact())
        .build();
  }
}
