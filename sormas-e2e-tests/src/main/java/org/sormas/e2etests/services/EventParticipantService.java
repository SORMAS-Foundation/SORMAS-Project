package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.GenderValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.pojo.web.EventParticipant;

public class EventParticipantService {

  private final Faker faker;

  @Inject
  public EventParticipantService(Faker faker) {
    this.faker = faker;
  }

  public EventParticipant buildGeneratedEventParticipant() {
    return EventParticipant.builder()
        .involvementDescription("Dummy Description")
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .sex(GenderValues.getRandomGender())
        .responsibleRegion(RegionsValues.VoreingestellteBundeslander.getName())
        .responsibleDistrict(DistrictsValues.VoreingestellterLandkreis.getName())
        .build();
  }
}
