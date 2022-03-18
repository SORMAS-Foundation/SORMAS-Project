/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalTime;
import org.sormas.e2etests.entities.pojo.User;
import org.sormas.e2etests.enums.CommunityValues;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.DistrictsValues;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.helpers.strings.ASCIIHelper;

public class UserService {
  private final Faker faker;

  @Inject
  public UserService(Faker faker) {
    this.faker = faker;
  }

  private String firstName;
  private String lastName;
  private final String emailDomain = "@USER.com";

  public User buildGeneratedUserWithRole(String role) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return User.builder()
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .phoneNumber(generatePhoneNumber())
        .language("Dari")
        .country("Germany")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory("Accommodation")
        .facilityType("Campsite")
        .facility("Other facility")
        .facilityNameAndDescription("qa-automation run")
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation(
            "Additional Information ".concat(String.valueOf(System.currentTimeMillis())))
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        .gpsLatitude(faker.random().nextInt(10, 20).toString())
        .gpsLongitude(faker.random().nextInt(20, 40).toString())
        .gpsAccuracy("1")
        .active(true)
        .userName("AutomationUser-".concat(LocalTime.now().toString()))
        .userRole(role)
        .limitedDisease(DiseasesValues.getRandomDiseaseCaption())
        .build();
  }

  public User buildGeneratedUserWithRoleAndDisease(String role, String disease) {
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return User.builder()
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .phoneNumber(generatePhoneNumber())
        .language("English")
        .country("Germany")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory("Accommodation")
        .facilityType("Campsite")
        .facility("Other facility")
        .facilityNameAndDescription("qa-automation run")
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation(
            "Additional Information ".concat(String.valueOf(System.currentTimeMillis())))
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        .gpsLatitude(faker.random().nextInt(10, 20).toString())
        .gpsLongitude(faker.random().nextInt(20, 40).toString())
        .gpsAccuracy("1")
        .active(true)
        .userName("AutomationUser-".concat(LocalTime.now().toString()))
        .userRole(role)
        .limitedDisease(disease)
        .build();
  }

  public User buildEditUser() {
    long currentTimeMillis = System.currentTimeMillis();
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    return User.builder()
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(ASCIIHelper.convertASCIIToLatin(firstName + "." + lastName + emailDomain))
        .phoneNumber(generatePhoneNumber())
        .language("Deutsch")
        .country("Germany")
        .region(RegionsValues.VoreingestellteBundeslander.getName())
        .district(DistrictsValues.VoreingestellterLandkreis.getName())
        .community(CommunityValues.VoreingestellteGemeinde.getName())
        .facilityCategory("Care facility")
        .facilityType("Elderly day care")
        .facility("Other facility")
        .facilityNameAndDescription("description edit-case" + currentTimeMillis)
        .street(faker.address().streetAddress())
        .houseNumber(faker.address().buildingNumber())
        .additionalInformation("Additional Information" + currentTimeMillis)
        .postalCode(faker.address().zipCode())
        .city(faker.address().city())
        .areaType("Urban")
        .gpsLatitude(faker.random().nextInt(10, 20).toString())
        .gpsLongitude(faker.random().nextInt(20, 40).toString())
        .gpsAccuracy(faker.random().nextInt(2, 5).toString())
        .active(true)
        .userRole("ReST User")
        .userName("userName" + currentTimeMillis)
        .limitedDisease(DiseasesValues.getRandomDiseaseCaption())
        .build();
  }

  private String generatePhoneNumber() {
    String phone = faker.phoneNumber().phoneNumber();
    if (phone.startsWith("(")) {
      return phone.replace("(", "+").replace(")", "");
    }
    return phone;
  }
}
