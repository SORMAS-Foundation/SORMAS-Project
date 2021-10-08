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

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import javax.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.User;

public class EditUserSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  protected User user;

  @Inject
  public EditUserSteps(final WebDriverHelpers webDriverHelpers, final SoftAssertions softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I check the created data is correctly displayed on Edit User Page for selected ([^\"]*)$",
        (String role) -> {
          user = collectUserData();

          softly
              .assertThat(user.getFirstName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFirstName());
          softly
              .assertThat(user.getLastName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLastName());
          softly
              .assertThat(user.getEmailAddress())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getEmailAddress());
          softly
              .assertThat(user.getPhoneNumber())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getPhoneNumber());
          softly
              .assertThat(user.getLanguage())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLanguage());
          softly
              .assertThat(user.getCountry())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCountry());
          softly
              .assertThat(user.getRegion())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getRegion());
          softly
              .assertThat(user.getDistrict())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getDistrict());
          softly
              .assertThat(user.getCommunity())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCommunity());
          softly
              .assertThat(user.getFacilityCategory())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacilityCategory());
          softly
              .assertThat(user.getFacilityType())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacilityType());
          softly
              .assertThat(user.getFacility())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacility());
          softly
              .assertThat(user.getFacilityNameAndDescription())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getFacilityNameAndDescription());
          softly
              .assertThat(user.getStreet())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getStreet());
          softly
              .assertThat(user.getHouseNumber())
              .isEqualTo(CreateNewUserSteps.user.getHouseNumber());
          softly
              .assertThat(user.getAdditionalInformation())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getAdditionalInformation());
          softly
              .assertThat(user.getPostalCode())
              .isEqualTo(CreateNewUserSteps.user.getPostalCode());
          softly
              .assertThat(user.getCity())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getCity());
          softly
              .assertThat(user.getAreaType())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getAreaType());
          softly
              .assertThat(user.getGpsLongitude())
              .isEqualTo(CreateNewUserSteps.user.getGpsLongitude());
          softly
              .assertThat(user.getGpsLatitude())
              .isEqualTo(CreateNewUserSteps.user.getGpsLatitude());
          softly
              .assertThat(user.getGpsAccuracy())
              .isEqualTo(CreateNewUserSteps.user.getGpsAccuracy());
          softly
              .assertThat(user.getUserName())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getUserName());
          softly.assertThat(user.getUserRole()).isEqualToIgnoringCase(role);
          softly
              .assertThat(user.getLimitedDisease())
              .isEqualToIgnoringCase(CreateNewUserSteps.user.getLimitedDisease());
          softly.assertAll();
        });

    Then(
        "^I check the edited data is correctly displayed on Edit User page$",
        () -> {
          user = collectEditUserData();
          Truth.assertThat(CreateNewUserSteps.editUser).isEqualTo(user);
        });
  }

  public User collectEditUserData() {
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
        .facilityNameAndDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_VALUE))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromWebElement(AREA_TYPE_COMBOBOX_INPUT))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(LATITUDE_INPUT))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(LONGITUDE_INPUT))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(LAT_LON_ACCURACY_INPUT))
        .active(webDriverHelpers.getTextFromLabelIfCheckboxIsChecked(ACTIVE_CHECKBOX))
        .userName(webDriverHelpers.getValueFromWebElement(USER_NAME_INPUT))
        .limitedDisease(webDriverHelpers.getValueFromWebElement(LIMITED_DISEASE_COMBOBOX_INPUT))
        .userRole(webDriverHelpers.getTextFromWebElement(USER_ROLE_CHECKBOX_TEXT))
        .build();
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
        .facilityNameAndDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_VALUE))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromWebElement(AREA_TYPE_COMBOBOX_INPUT))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(LATITUDE_INPUT))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(LONGITUDE_INPUT))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(LAT_LON_ACCURACY_INPUT))
        .userName(webDriverHelpers.getValueFromWebElement(USER_NAME_INPUT))
        .limitedDisease(webDriverHelpers.getValueFromWebElement(LIMITED_DISEASE_COMBOBOX_INPUT))
        .userRole(webDriverHelpers.getTextFromWebElement(USER_ROLE_CHECKBOX_TEXT))
        .build();
  }
}
