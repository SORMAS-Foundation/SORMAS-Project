package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.enums.ActivityTypes;
import org.sormas.e2etests.enums.Roles;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.pojo.web.ExposureDetails;

public class ExposureDetailsService {

  private final Faker faker;

  @Inject
  public ExposureDetailsService(Faker faker) {
    this.faker = faker;
  }

  public ExposureDetails buildInputExposureDetails() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    return ExposureDetails.builder()
        .startOfExposure(formatter.format(LocalDate.now().minusDays(5)))
        .endOfExposure(formatter.format(LocalDate.now().minusDays(2)))
        .exposureDescription(faker.medical().symptoms())
        .typeOfActivity(ActivityTypes.VISIT.toString())
        .exposureDetailsRole(Roles.VISITOR.toString())
        .riskArea(YesNoUnknownOptions.YES.toString())
        .indoors(YesNoUnknownOptions.YES.toString())
        .outdoors(YesNoUnknownOptions.YES.toString())
        .wearingMask(YesNoUnknownOptions.YES.toString())
        .wearingPpe(YesNoUnknownOptions.YES.toString())
        .otherProtectiveMeasures(YesNoUnknownOptions.YES.toString())
        .shortDistance(YesNoUnknownOptions.YES.toString())
        .longFaceToFaceContact(YesNoUnknownOptions.YES.toString())
        .animalMarket(YesNoUnknownOptions.YES.toString())
        .percutaneous(YesNoUnknownOptions.YES.toString())
        .contactToBodyFluids(YesNoUnknownOptions.YES.toString())
        .handlingSamples(YesNoUnknownOptions.YES.toString())
        .contactToSourceCase("")
        .typeOfPlace("Home")
        .continent("Europe")
        .subcontinent("Central Europe")
        .country("Austria")
        .exposureRegion("")
        .district("")
        .community("")
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation(faker.expression("additional information").toString())
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        .communityContactPerson(faker.name().toString())
        .gpsLatitude("123")
        .gpsLongitude("456")
        .gpsAccuracy("789")
        .build();
  }
}
