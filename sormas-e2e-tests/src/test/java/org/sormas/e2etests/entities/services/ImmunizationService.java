package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.enums.immunizations.ImmunizationManagementStatusValues;
import org.sormas.e2etests.enums.immunizations.StatusValues;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;

public class ImmunizationService {

  private final Faker faker;
  private String firstName;
  private String lastName;
  private final String emailDomain = "@IMMUNIZATION.com";

  @Inject
  public ImmunizationService(Faker faker) {
    this.faker = faker;
  }

  public Immunization buildGeneratedImmunization() {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return Immunization.builder()
        .dateOfReport(LocalDate.now().minusDays(1))
        .externalId(UUID.randomUUID().toString())
        .disease(DiseasesValues.getRandomDiseaseCaption())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .responsibleCommunity(CommunityValues.VoreingestellteGemeinde.getName())
        .presentConditionOfPerson("Alive")
        .primaryPhoneNumber(faker.phoneNumber().phoneNumber())
        .primaryEmailAddress(
            ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(
            LocalDate.of(
                faker.number().numberBetween(1900, 2002),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 27)))
        .sex(GenderValues.getRandomGender())
        .meansOfImmunization("Vaccination")
        .immunizationStatus(StatusValues.getRandomImmunizationStatus())
        .managementStatus(
            ImmunizationManagementStatusValues.getRandomImmunizationManagementStatus())
        .facilityCategory("Care facility")
        .facilityType("Elderly care facility")
        .facility("Other facility")
        .facilityDescription("Dummy description " + System.currentTimeMillis())
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now())
        .validFrom(LocalDate.now().minusDays(1))
        .validUntil(LocalDate.now())
        .build();
  }
}
