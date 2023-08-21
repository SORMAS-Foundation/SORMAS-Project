package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.ReasonForHospitalization.getRandomReasonForHospitalizationDE;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;
import static org.sormas.e2etests.enums.YesNoUnknownOptionsDE.JA;

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
        .dateOfDischargeOrTransfer(LocalDate.now().minusDays(1))
        .reasonForHospitalization("Other reason")
        .specifyReason("Exotic disease")
        .stayInTheIntensiveCareUnit(YES.toString())
        .startOfStayDate(LocalDate.now().minusDays(2))
        .endOfStayDate(LocalDate.now().minusDays(1))
        .isolation(YES.toString())
        .dateOfIsolation(LocalDate.now().minusDays(6))
        .wasThePatientHospitalizedPreviously(YES.toString())
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(YES.toString())
        .leftAgainstMedicalAdvice(YES.toString())
        .description("Additional description.")
        .build();
  }

  public Hospitalization generateCurrentHospitalizationForDE() {
    return Hospitalization.builder()
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(JA.toString())
        .dateOfVisitOrAdmission(LocalDate.now().minusDays(3))
        .dateOfDischargeOrTransfer(LocalDate.now().minusDays(1))
        .reasonForHospitalization(getRandomReasonForHospitalizationDE())
        .specifyReason("Exotic disease")
        .stayInTheIntensiveCareUnit(JA.toString())
        .startOfStayDate(LocalDate.now().minusDays(2))
        .endOfStayDate(LocalDate.now().minusDays(1))
        .isolation(JA.toString())
        .dateOfIsolation(LocalDate.now().minusDays(6))
        .wasThePatientHospitalizedPreviously(JA.toString())
        .leftAgainstMedicalAdvice(JA.toString())
        .description("Additional description.")
        .build();
  }
}
