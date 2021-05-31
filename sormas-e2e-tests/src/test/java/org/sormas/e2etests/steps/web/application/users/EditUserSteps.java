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

package org.sormas.e2etests.steps.web.application.users;

import static org.sormas.e2etests.pages.application.users.EditUserPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.User;

public class EditUserSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected User aUser;

  @Inject
  public EditUserSteps(final WebDriverHelpers webDriverHelpers, final SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit User Page",
        () -> {
          aUser = collectUserData();

          softly
              .assertThat(aUser.getFirstName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFirstName());
          softly
              .assertThat(aUser.getLastName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLastName());
          softly
              .assertThat(aUser.getEmailAddress())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getEmailAddress());
          softly
              .assertThat(aUser.getPhoneNumber())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getPhoneNumber());
          softly
              .assertThat(aUser.getLanguage())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLanguage());
          softly
              .assertThat(aUser.getCountry())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCountry());
          softly
              .assertThat(aUser.getRegion())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getRegion());
          softly
              .assertThat(aUser.getDistrict())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getDistrict());
          softly
              .assertThat(aUser.getCommunity())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCommunity());
          softly
              .assertThat(aUser.getFacilityCategory())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacilityCategory());
          softly
              .assertThat(aUser.getFacilityType())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacilityType());
          softly
              .assertThat(aUser.getFacility())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacility());
          softly
              .assertThat(aUser.getStreet())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getStreet());
          softly
              .assertThat(aUser.getHouseNumber())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getHouseNumber());
          softly
              .assertThat(aUser.getAdditionalInformation())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getAdditionalInformation());
          softly
              .assertThat(aUser.getPostalCode())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getPostalCode());
          softly
              .assertThat(aUser.getCity())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCity());
          softly
              .assertThat(aUser.getAreaType())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getAreaType());
          //          softly
          //              .assertThat(aUser.getCommunityContactPerson())
          //
          // .isEqualToIgnoringCase(CreateNewUserSteps.user.getCommunityContactPerson());
          softly
              .assertThat(aUser.getGpsLongitude())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getGpsLongitude());
          softly
              .assertThat(aUser.getGpsLatitude())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getGpsLatitude());
          softly
              .assertThat(aUser.getGpsAccuracy())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getGpsAccuracy());
          softly
              .assertThat(aUser.getUserName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getUserName());
          softly
              .assertThat(aUser.getUserRole())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getUserRole());
          softly
              .assertThat(aUser.getLimitedDisease())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLimitedDisease());
          softly.assertAll();
        });
  }

  public User collectUserData() {
    return User.builder()
        .firstName(webDriverHelpers.getValueFromWebElement(FIRST_NAME_OF_USER_INPUT))
        .lastName(webDriverHelpers.getValueFromWebElement(LAST_NAME_OF_USER_INPUT))
        .emailAddress(webDriverHelpers.getValueFromWebElement(EMAIL_ADDRESS_INPUT))
        .phoneNumber(webDriverHelpers.getValueFromWebElement(PHONE_INPUT))
        .language(webDriverHelpers.getValueFromWebElement(LANGUAGE_COMBOBOX_INPUT))
        .country(webDriverHelpers.getValueFromWebElement(COUNTRY_COMBOBOX_INPUT))
        .region(webDriverHelpers.getValueFromWebElement(REGION_COMBOBOX_INPUT))
        .district(webDriverHelpers.getValueFromWebElement(DISTRICT_COMBOBOX_INPUT))
        .community(webDriverHelpers.getValueFromWebElement(COMMUNITY_COMBOBOX_INPUT))
        .facilityCategory(webDriverHelpers.getValueFromWebElement(FACILITY_CATEGORY_COMBOBOX_INPUT))
        .facilityType(webDriverHelpers.getValueFromWebElement(FACILITY_TYPE_COMBOBOX_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_COMBOBOX_INPUT))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromWebElement(AREA_TYPE_COMBOBOX_INPUT))
        // .communityContactPerson(webDriverHelpers.getValueFromWebElement(COMMUNITY_CONTACT_PERSON))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(LATITUDE_INPUT))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(LONGITUDE_INPUT))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(LAT_LON_ACCURACY_INPUT))
        .active(webDriverHelpers.getValueFromWebElement(ACTIVE_CHECKBOX))
        .userName(webDriverHelpers.getValueFromWebElement(USER_NAME_INPUT))
        .limitedDisease(webDriverHelpers.getValueFromWebElement(LIMITED_DISEASE_COMBOBOX_INPUT))
        .userRole(webDriverHelpers.getTextFromWebElement(USER_ROLE_CHECKBOX_TEXT))
        .build();
  }
}
