package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.ExposureDetails;
import org.sormas.e2etests.enums.ActivityTypes;
import org.sormas.e2etests.enums.RolesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;

public class ExposureDetailsService {

  private final Faker faker;

  @Inject
  public ExposureDetailsService(Faker faker) {
    this.faker = faker;
  }

  public ExposureDetails buildInputExposureDetails() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    return ExposureDetails.builder()
        .startOfExposure(
            LocalDate.now().minusDays(5).format(formatter).replaceFirst("^0+(?!$)", ""))
        .endOfExposure(LocalDate.now().minusDays(2).format(formatter).replaceFirst("^0+(?!$)", ""))
        .exposureDescription(faker.medical().symptoms())
        .typeOfActivity(ActivityTypes.VISIT.toString())
        .exposureDetailsRole(RolesValues.VISITOR.toString())
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
        .typeOfPlace("Home")
        .continent("Europe")
        .subcontinent("Central Europe")
        .country("Austria")
        .exposureRegion("")
        .district("")
        .community("")
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation(faker.expression("additional information"))
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        .gpsLatitude(String.valueOf(faker.number().numberBetween(-90, 90)))
        .gpsLongitude(String.valueOf(faker.number().numberBetween(-180, 180)))
        .gpsAccuracy(String.valueOf(faker.number().numberBetween(-180, 180)))
        .build();
  }
}
