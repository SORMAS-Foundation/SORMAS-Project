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

package org.sormas.e2etests.steps.web.application.vaccinations;

import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.SAVE_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_DATE;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.cases.SymptomsTabSteps;

public class CreateNewVaccinationSteps implements En {
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewVaccinationSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on save case button in Create Vaccination page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(SAVE_VACCINATION_BUTTON);
        });
    When(
        "I set Vaccination date the same date as the symptom onset date",
        () -> {
          System.out.println(SymptomsTabSteps.symptomOnsetDate.toString());
          webDriverHelpers.clearAndFillInWebElement(
              VACCINATION_DATE, SymptomsTabSteps.symptomOnsetDate.toString());
        });
  }
}
