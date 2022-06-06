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
}
