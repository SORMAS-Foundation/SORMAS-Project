/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalTime;
import org.sormas.e2etests.pojo.User;

public class UserService {
  private final Faker faker;

  @Inject
  public UserService(Faker faker) {
    this.faker = faker;
  }

  public User buildGeneratedUser() {
    return User.builder()
        .firstName(faker.name().firstName())
        .lastName(faker.name().lastName())
        .emailAddress(faker.internet().emailAddress())
        .phoneNumber("+49-4178-24704421")
        .language("English")
        .country("Germany")
        .region("Berlin")
        .district("")
        .community("")
        .facilityCategory("Accommodation")
        .facilityType("Campsite")
        .facility("")
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation(
            "Additional Information".concat(String.valueOf(System.currentTimeMillis())))
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        // .community("CommunityContact".concat(faker.name().firstName()))
        .gpsLatitude("22")
        .gpsLongitude("44")
        .gpsAccuracy("1")
        .active("yes")
        .userName("userName".concat(LocalTime.now().toString()))
        .userRole("National User")
        .limitedDisease("Anthrax")
        .build();
  }
}
