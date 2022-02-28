package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.entities.pojo.web.Hospitalization;

public class HospitalizationService {
  private final Faker faker;

  @Inject
  public HospitalizationService(Faker faker) {
    this.faker = faker;
  }

  public Hospitalization generateHospitalization() {
    return Hospitalization.builder()
        .dateOfVisitOrAdmission(LocalDate.now().minusDays(3))
        .dateOfDischargeOrTransfer(LocalDate.now().plusDays(2))
        .reasonForHospitalization("Other reason")
        .specifyReason(faker.book().title() + " " + faker.dragonBall().character())
        .stayInTheIntensiveCareUnit(YES.toString())
        .startOfStayDate(LocalDate.now().minusDays(2))
        .endOfStayDate(LocalDate.now().minusDays(1))
        .isolation(YES.toString())
        .dateOfIsolation(LocalDate.now().minusDays(6))
        .wasThePatientHospitalizedPreviously(YES.toString())
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(YES.toString())
        .leftAgainstMedicalAdvice(YES.toString())
        .build();
  }
}
