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

package org.sormas.e2etests.steps.web.application.samples;

import static org.sormas.e2etests.pages.application.samples.EditSamplePage.*;
import static org.sormas.e2etests.pages.application.samples.SamplesDirectoryPage.SAMPLE_SEARCH_INPUT;

import cucumber.api.java8.En;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class EditSampleSteps implements En {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public EditSampleSteps(
      WebDriverHelpers webDriverHelpers,
      @Named("ENVIRONMENT_URL") String environmentUrl,
      ApiState apiState) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I open the last created sample via API",
        () -> {
          String LAST_CREATED_SAMPLE_URL =
              environmentUrl + "/sormas-ui/#!samples/data/" + apiState.getCreatedSample().getUuid();
          webDriverHelpers.accessWebSite(LAST_CREATED_SAMPLE_URL);
        });

    When(
        "I delete the sample",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_SAMPLE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAMPLE_DELETION_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SAMPLE_SEARCH_INPUT);
        });

    When(
        "I click on the new pathogen test from the Edit Sample page",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.scrollToElement(PATHOGEN_NEW_TEST_RESULT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(PATHOGEN_NEW_TEST_RESULT_BUTTON);
        });
  }
}
