package org.sormas.e2etests.entities.services;

import static org.sormas.e2etests.enums.YesNoUnknownOptions.YES;

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
        .facilityNameDescription(faker.beer().name())
        .reasonForHospitalization("Other reason")
        .specifyReason(faker.book().title())
        .stayInTheIntensiveCareUnit(YES.toString())
        .startOfStayDate(LocalDate.now().minusDays(9))
        .endOfStayDate(LocalDate.now().minusDays(7))
        .description(faker.cat().breed() + " " + faker.color().name())
        .build();
  }
}
