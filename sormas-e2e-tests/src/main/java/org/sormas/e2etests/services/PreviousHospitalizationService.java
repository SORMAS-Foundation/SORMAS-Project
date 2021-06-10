package org.sormas.e2etests.services;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.pojo.web.PreviousHospitalization;

public class PreviousHospitalizationService {
  private final Faker faker;

  @Inject
  public PreviousHospitalizationService(Faker faker) {
    this.faker = faker;
  }

  public PreviousHospitalization generatePreviousHospitalization() {
    return PreviousHospitalization.builder()
        .dateOfVisitOrAdmission(LocalDate.now().minusDays(10))
        .dateOfDischargeOrTransfer(LocalDate.now().minusDays(5))
        .region("Voreingestellte Bundesl\u00E4nder")
        .district("Voreingestellter Landkreis")
        .community("Voreingestellte Gemeinde")
        .hospital("Other facility")
        .isolation(YES.toString())
        .facilityNameDescription(faker.book().title())
        .reasonForHospitalization("Other reason")
        .specifyReason(faker.book().title())
        .stayInTheIntensiveCareUnit(YES.toString())
        .startOfStayDate(LocalDate.now().minusDays(9))
        .endOfStayDate(LocalDate.now().minusDays(7))
        .description(faker.book().title())
        .build();
  }
}
