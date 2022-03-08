package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.SourceOfTemperature.getRandomTemperature;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import org.sormas.e2etests.entities.pojo.web.Visit;

public class ClinicalCourseVisitService {
  private final Faker faker;
  private long currentTimeMillis = System.currentTimeMillis();

  @Inject
  public ClinicalCourseVisitService(Faker faker) {
    this.faker = faker;
  }

  public Visit buildClinicalCourseVisit() {
    return Visit.builder()
        .dateOfVisit(LocalDate.now().minusDays(faker.number().numberBetween(1, 10)))
        .timeOfVisit(LocalTime.of(faker.number().numberBetween(10, 23), 30))
        .visitRemarks(faker.book().title())
        .currentBodyTemperature("36,6")
        .sourceOfBodyTemperature(getRandomTemperature())
        .build();
  }
}
