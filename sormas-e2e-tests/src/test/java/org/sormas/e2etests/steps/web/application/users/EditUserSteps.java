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

package org.sormas.e2etests.steps.web.application.users;

import static org.sormas.e2etests.pages.application.users.EditUserPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.User;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class EditUserSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  protected User collectedUser;
  private User createdUser;

  @Inject
  public EditUserSteps(final WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I check the created data is correctly displayed on Edit User Page for selected ([^\"]*)$",
        (String role) -> {
          collectedUser = collectUserData();
          createdUser = CreateNewUserSteps.user;
          ComparisonHelper.compareEqualEntities(collectedUser, createdUser);
        });

    Then(
        "^I check the edited data is correctly displayed on Edit User page$",
        () -> {
          collectedUser = collectEditUserData();
          ComparisonHelper.compareEqualEntities(CreateNewUserSteps.editUser, collectedUser);
        });
  }

  private User collectEditUserData() {
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
        .active(webDriverHelpers.getWebElement(ACTIVE_CHECKBOX).isSelected())
        .userName(webDriverHelpers.getValueFromWebElement(USER_NAME_INPUT))
        .limitedDisease(webDriverHelpers.getValueFromWebElement(LIMITED_DISEASE_COMBOBOX_INPUT))
        .userRole(webDriverHelpers.getTextFromWebElement(USER_ROLE_CHECKBOX_TEXT))
        .build();
  }

  private User collectUserData() {
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
        .active(webDriverHelpers.getWebElement(ACTIVE_CHECKBOX).isSelected())
        .limitedDisease(webDriverHelpers.getValueFromWebElement(LIMITED_DISEASE_COMBOBOX_INPUT))
        .userRole(webDriverHelpers.getTextFromWebElement(USER_ROLE_CHECKBOX_TEXT))
        .build();
  }
}
