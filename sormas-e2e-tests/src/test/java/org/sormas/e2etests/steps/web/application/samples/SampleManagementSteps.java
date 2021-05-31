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

import static org.sormas.e2etests.pages.application.samples.SampleManagementPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;

public class SampleManagementSteps implements En {

  @Inject
  public SampleManagementSteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I search last created Sample by Case ID$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(SAMPLE_SEARCH_INPUT);
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, EditCaseSteps.aCase.getUuid());
          webDriverHelpers.waitUntilNumberOfElementsIsReduceToGiven(SEARCH_RESULT_SAMPLE, 2);
        });

    When(
        "^I open created Sample$",
        () -> webDriverHelpers.clickOnWebElementBySelector(SEARCH_RESULT_SAMPLE));

    When(
        "^I search for Sample using Sample UUID from the created Sample",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SAMPLE_SEARCH_INPUT, CreateNewSampleSteps.sampleId);
          webDriverHelpers.waitUntilWebElementHasAttributeWithValue(
              SEARCH_RESULT_SAMPLE, "title", CreateNewSampleSteps.sampleId);
        });
  }
}
