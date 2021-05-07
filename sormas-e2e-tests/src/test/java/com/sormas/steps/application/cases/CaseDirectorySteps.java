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

package com.sormas.steps.application.cases;

import static com.sormas.pages.application.cases.CaseDirectoryPage.NEW_CASE_BUTTON;
import static com.sormas.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;

import com.sormas.helpers.WebDriverHelpers;
import cucumber.api.java8.En;
import javax.inject.Inject;

public class CaseDirectorySteps implements En {

  @Inject
  public CaseDirectorySteps(WebDriverHelpers webDriverHelpers) {

    When(
        "^I click on the NEW CASE button$",
        () ->
            webDriverHelpers.clickWhileOtherButtonIsDisplayed(
                NEW_CASE_BUTTON, DATE_OF_REPORT_INPUT));
  }
}
