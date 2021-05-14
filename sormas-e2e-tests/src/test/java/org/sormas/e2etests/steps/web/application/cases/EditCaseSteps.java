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

package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.Case;

public class EditCaseSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Case aCase;

  @Inject
  public EditCaseSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit case page",
        () -> {
          aCase = collectCasePersonData();
          Truth.assertThat(aCase.getDateOfReport())
              .isEqualTo(CreateNewCaseSteps.caze.getDateOfReport());
          Truth.assertThat(aCase.getExternalId())
              .isEqualTo(CreateNewCaseSteps.caze.getExternalId());
          Truth.assertThat(aCase.getDisease()).isEqualTo(CreateNewCaseSteps.caze.getDisease());
          Truth.assertThat(aCase.getResponsibleRegion())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleRegion());
          Truth.assertThat(aCase.getResponsibleDistrict())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleDistrict());
          Truth.assertThat(aCase.getResponsibleCommunity())
              .isEqualTo(CreateNewCaseSteps.caze.getResponsibleCommunity());
          Truth.assertThat(aCase.getPlaceOfStay())
              .isEqualTo(CreateNewCaseSteps.caze.getPlaceOfStay());
          Truth.assertThat(aCase.getPlaceDescription())
              .isEqualTo(CreateNewCaseSteps.caze.getPlaceDescription());
          Truth.assertThat(aCase.getFirstName()).isEqualTo(CreateNewCaseSteps.caze.getFirstName());
          Truth.assertThat(
                  aCase.getLastName().equalsIgnoreCase(CreateNewCaseSteps.caze.getLastName()))
              .isTrue();
          Truth.assertThat(aCase.getDateOfBirth())
              .isEqualTo(CreateNewCaseSteps.caze.getDateOfBirth());
        });
  }

  public Case collectCasePersonData() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    LocalDate localDate = LocalDate.parse(dateOfReport, formatter);
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(localDate)
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(webDriverHelpers.getValueFromWebElement(PLACE_DESCRIPTION_INPUT))
        .build();
  }

  public Case getUserInformation() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), formatter);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }
}
