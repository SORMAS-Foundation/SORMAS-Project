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
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_DATE_REPORT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINE_INFO_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINE_NAME_COMBOBOX;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.web.application.cases.SymptomsTabSteps;

public class CreateNewVaccinationSteps implements En {
  private final WebDriverHelpers webDriverHelpers;
  public static final DateTimeFormatter DATE_FORMATTER_DE =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
          LocalDate vaccintateDate = SymptomsTabSteps.symptomOnsetDate;
          webDriverHelpers.clearAndFillInWebElement(
              VACCINATION_DATE, DATE_FORMATTER_DE.format(vaccintateDate));
        });

    When(
        "I set Report Date to the some like data raport from Edit Case field as the symptom onset date",
        () -> {
          LocalDate reportDate = LocalDate.now().minusDays(1);
          webDriverHelpers.clearAndFillInWebElement(
              VACCINATION_DATE_REPORT, DATE_FORMATTER_DE.format(reportDate));
        });

    When(
        "I set Vaccine Name to {string} on Vaccination page",
        (String vaccineName) -> {
          webDriverHelpers.selectFromCombobox(VACCINE_NAME_COMBOBOX, vaccineName);
        });

    When(
        "I set Vaccine Info Source to {string} on Vaccination page",
        (String vaccineInfoSource) -> {
          webDriverHelpers.selectFromCombobox(VACCINE_INFO_SOURCE_COMBOBOX, vaccineInfoSource);
        });
  }
}
