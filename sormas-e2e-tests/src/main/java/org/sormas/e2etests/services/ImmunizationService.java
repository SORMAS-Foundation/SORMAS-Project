package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.immunizations.ImmunizationManagementStatusValues;
import org.sormas.e2etests.enums.immunizations.StatusValues;
import org.sormas.e2etests.pojo.web.Immunization;

public class ImmunizationService {

  private final Faker faker;

  @Inject
  public ImmunizationService(Faker faker) {
    this.faker = faker;
  }

  public Immunization buildGeneratedImmunization() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return Immunization.builder()
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease("COVID-19")
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .presentConditionOfPerson("Alive")
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(faker.internet().emailAddress())
        .passportNumber(String.valueOf(System.currentTimeMillis()))
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .dateOfBirth(LocalDate.of(1902, 3, 7))
        .sex("Male")
        .nationalHealthId(UUID.randomUUID().toString())
        .meansOfImmunization("Vaccination")
        .immunizationStatus(StatusValues.getRandomImmunizationStatus())
        .managementStatus(
            ImmunizationManagementStatusValues.getRandomImmunizationManagementStatus())
        .facilityCategory("Care facility")
        .facilityType("Elderly care facility")
        .facility("Other facility")
        .facilityDescription("dummy description")
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now())
        .validFrom(LocalDate.now().minusDays(1))
        .validUntil(LocalDate.now())
        .build();
  }
}
