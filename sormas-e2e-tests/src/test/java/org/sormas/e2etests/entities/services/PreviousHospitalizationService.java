package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.ReasonForHospitalization.getRandomReasonForHospitalizationDE;
import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;
import static org.sormas.e2etests.enums.YesNoUnknownOptionsDE.JA;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import org.sormas.e2etests.entities.pojo.web.PreviousHospitalization;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;

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
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .hospital("Other facility")
        .isolation(YES.toString())
        .dateOfIsolation(LocalDate.now().minusDays(8))
        .facilityNameDescription(faker.beer().name())
        .reasonForHospitalization("Other reason")
        .specifyReason(faker.book().title())
        .stayInTheIntensiveCareUnit(YES.toString())
        .startOfStayDate(LocalDate.now().minusDays(9))
        .endOfStayDate(LocalDate.now().minusDays(7))
        .description(faker.cat().breed() + " " + faker.color().name())
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(YES.toString())
        .build();
  }

  public PreviousHospitalization generatePreviousHospitalizationDE() {
    return PreviousHospitalization.builder()
        .dateOfVisitOrAdmission(LocalDate.now().minusDays(10))
        .dateOfDischargeOrTransfer(LocalDate.now().minusDays(5))
        .region("Berlin")
        .district("SK Berlin Mitte")
        .community("Gesundbrunnen")
        .hospital("Andere Einrichtung")
        .isolation(JA.toString())
        .dateOfIsolation(LocalDate.now().minusDays(8))
        .facilityNameDescription(faker.beer().name())
        .reasonForHospitalization(getRandomReasonForHospitalizationDE())
        .specifyReason(faker.book().title())
        .stayInTheIntensiveCareUnit(JA.toString())
        .startOfStayDate(LocalDate.now().minusDays(9))
        .endOfStayDate(LocalDate.now().minusDays(7))
        .description(faker.cat().breed() + " " + faker.color().name())
        .wasPatientAdmittedAtTheFacilityAsAnInpatient(JA.toString())
        .build();
  }
}
