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

package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.FollowUpVisitsTabPage.*;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.state.ApiState;

public class FollowUpVisitsTabSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public FollowUpVisitsTabSteps(
      WebDriverHelpers webDriverHelpers, ApiState apiState, RunningConfiguration runningConfiguration) {

    When(
        "^I am accessing the Follow-up visits tab using of created contact via api$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.CONTACTS_BUTTON);
          String visitLinkPath = "/sormas-webdriver/#!contacts/visits/";
          String uuid = apiState.getCreatedContact().getUuid();
          String URL = runningConfiguration.getEnvironmentUrlForMarket(locale) + visitLinkPath + uuid;
          webDriverHelpers.accessWebSite(URL);
        });

    Then(
        "I click on New visit button from Follow-up visits tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_VISIT_BUTTON));

    And(
        "^I open the first displayed follow up$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(FIRST_VISIT_BUTTON);
        });
  }
}
