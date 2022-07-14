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

package org.sormas.e2etests.steps.web.application.vaccination;

import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.ATC_CODE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.BATCH_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.INN_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.NEW_VACCINATION_DE_BUTTON;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.NEXT_PAGE_VACCINATION_TAB;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.SAVE_VACCINATION_FORM_BUTTON;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.UNII_CODE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_DATE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_INFO_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_INFO_SOURCE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_MANUFACTURER_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_MANUFACTURER_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_NAME_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_NAME_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINE_DOSE_INPUT;
import static org.sormas.e2etests.steps.web.application.cases.EditCaseSteps.DATE_FORMATTER_DE;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Vaccination;
import org.sormas.e2etests.entities.services.VaccinationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class CreateNewVaccinationSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Vaccination vaccination;
  public static Vaccination duplicatedVacinationDe;
  public static Vaccination collectedVaccination;
  public static final DateTimeFormatter formatterDE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  @Inject
  public CreateNewVaccinationSteps(
      WebDriverHelpers webDriverHelpers, VaccinationService vaccinationService, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    duplicatedVacinationDe = vaccinationService.buildGeneratedVaccinationDE();
    When(
        "I fill new vaccination data in new Vaccination form for DE",
        () -> {
          vaccination = vaccinationService.buildGeneratedVaccinationDE();
          fillVaccinationDate(vaccination.getVaccinationDate(), Locale.GERMAN);
          selectVaccineName(vaccination.getVaccineName());
          selectVaccineManufacturer(vaccination.getVaccineManufacturer());
          fillVaccineType(vaccination.getVaccineType());
          selectVaccinationInfoSource(vaccination.getVaccinationInfoSource());
          fillVaccineDose(vaccination.getVaccineDose());
          fillInn(vaccination.getInn());
          fillUniiCode(vaccination.getUniiCode());
          fillBatchNumber(vaccination.getBatchNumber());
          fillAtcCode(vaccination.getAtcCode());
        });
    When(
        "I fill new duplicate vaccination data in new Vaccination form for DE without vaccination date and name",
        () -> {
          selectVaccineManufacturer(duplicatedVacinationDe.getVaccineManufacturer());
          fillVaccineType(duplicatedVacinationDe.getVaccineType());
          selectVaccinationInfoSource(duplicatedVacinationDe.getVaccinationInfoSource());
          fillVaccineDose(duplicatedVacinationDe.getVaccineDose());
          fillInn(duplicatedVacinationDe.getInn());
          fillUniiCode(duplicatedVacinationDe.getUniiCode());
          fillBatchNumber(duplicatedVacinationDe.getBatchNumber());
          fillAtcCode(duplicatedVacinationDe.getAtcCode());
        });
    When(
        "I fill new vaccination data in new Vaccination form",
        () -> {
          vaccination = vaccinationService.buildGeneratedVaccination();
          fillVaccinationDate(vaccination.getVaccinationDate(), Locale.ENGLISH);
          selectVaccineName(vaccination.getVaccineName());
          selectVaccineManufacturer(vaccination.getVaccineManufacturer());
          fillVaccineType(vaccination.getVaccineType());
          selectVaccinationInfoSource(vaccination.getVaccinationInfoSource());
          fillVaccineDose(vaccination.getVaccineDose());
          fillInn(vaccination.getInn());
          fillUniiCode(vaccination.getUniiCode());
          fillBatchNumber(vaccination.getBatchNumber());
          fillAtcCode(vaccination.getAtcCode());
        });

    When(
        "I fill new vaccination data for duplicates in new Vaccination form for DE",
        () -> {
          fillVaccinationDate(duplicatedVacinationDe.getVaccinationDate(), Locale.GERMAN);
          selectVaccineName(duplicatedVacinationDe.getVaccineName());
          selectVaccineManufacturer(duplicatedVacinationDe.getVaccineManufacturer());
          fillVaccineType(duplicatedVacinationDe.getVaccineType());
          selectVaccinationInfoSource(duplicatedVacinationDe.getVaccinationInfoSource());
          fillVaccineDose(duplicatedVacinationDe.getVaccineDose());
          fillInn(duplicatedVacinationDe.getInn());
          fillUniiCode(duplicatedVacinationDe.getUniiCode());
          fillBatchNumber(duplicatedVacinationDe.getBatchNumber());
          fillAtcCode(duplicatedVacinationDe.getAtcCode());
        });
    When(
        "I check that displayed data in form is equal to whole data from duplicated entry",
        () -> {
          collectedVaccination = collectVaccinationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              duplicatedVacinationDe,
              collectedVaccination,
              List.of(
                  "vaccinationDate",
                  "vaccineName",
                  "vaccineManufacturer",
                  "vaccineType",
                  "vaccinationInfoSource",
                  "vaccineDose",
                  "inn",
                  "uniiCode",
                  "batchNumber",
                  "atcCode"));
        });
    When(
        "I check that displayed vaccination date in form is equal to date from duplicated entry",
        () -> {
          collectedVaccination = collectVaccinationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              duplicatedVacinationDe, collectedVaccination, List.of("vaccinationDate"));
        });
    When(
        "I click to navigate to next page in Vaccinations tab",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEXT_PAGE_VACCINATION_TAB);
        });
    When(
        "I check that displayed vaccination date is equal to {string}",
        (String expectedDate) -> {
          LocalDate date = getVaccinationDate();
          softly.assertEquals(
              date.format(formatterDE),
              expectedDate,
              "Vaccination date is different than expected!");
          softly.assertAll();
        });
    When(
        "I check that displayed vaccination name is equal to {string}",
        (String expectedName) -> {
          String name = webDriverHelpers.getValueFromWebElement(VACCINATION_NAME_INPUT);
          softly.assertEquals(name, expectedName, "Vaccination name is different than expected!");
          softly.assertAll();
        });
    When(
        "I check that displayed vaccination date in form is equal to name from duplicated entry",
        () -> {
          collectedVaccination = collectVaccinationData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              duplicatedVacinationDe, collectedVaccination, List.of("vaccinationName"));
        });
    When(
        "I check that displayed vaccination form has empty vaccination date and name",
        () -> {
          collectedVaccination = collectVaccinationData();
          softly.assertEquals(collectedVaccination.getVaccinationDate(), null);
          softly.assertEquals(collectedVaccination.getVaccineName(), "");
          softly.assertAll();
        });
    When(
        "I set new vaccination name the same as duplicate for DE",
        () -> {
          selectVaccineName(duplicatedVacinationDe.getVaccineName());
        });
    When(
        "I set new vaccination date the same as duplicate for DE",
        () -> {
          fillVaccinationDate(duplicatedVacinationDe.getVaccinationDate(), Locale.GERMAN);
        });
    When(
        "I click SAVE button in new Vaccination form",
        () -> webDriverHelpers.clickOnWebElementBySelector(SAVE_VACCINATION_FORM_BUTTON));

    When(
        "I click NEW VACCINATION button for DE",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_VACCINATION_DE_BUTTON));
  }

  private void fillVaccinationDate(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(VACCINATION_DATE_INPUT, formatter.format(date));
  }

  private void selectVaccineName(String vaccineName) {
    webDriverHelpers.selectFromCombobox(VACCINATION_NAME_COMBOBOX, vaccineName);
  }

  private void selectVaccineManufacturer(String vaccineManufacturer) {
    webDriverHelpers.selectFromCombobox(VACCINATION_MANUFACTURER_COMBOBOX, vaccineManufacturer);
  }

  private void fillVaccineType(String vaccineType) {
    webDriverHelpers.fillInWebElement(VACCINATION_TYPE_INPUT, vaccineType);
  }

  private void selectVaccinationInfoSource(String vaccinationInfoSource) {
    webDriverHelpers.selectFromCombobox(VACCINATION_INFO_SOURCE_COMBOBOX, vaccinationInfoSource);
  }

  private void fillVaccineDose(String vaccineDose) {
    webDriverHelpers.fillInWebElement(VACCINE_DOSE_INPUT, vaccineDose);
  }

  private void fillInn(String inn) {
    webDriverHelpers.fillInWebElement(INN_INPUT, inn);
  }

  private void fillUniiCode(String uniiCode) {
    webDriverHelpers.fillInWebElement(UNII_CODE_INPUT, uniiCode);
  }

  private void fillBatchNumber(String batchNumber) {
    webDriverHelpers.fillInWebElement(BATCH_NUMBER_INPUT, batchNumber);
  }

  private void fillAtcCode(String atcCode) {
    webDriverHelpers.fillInWebElement(ATC_CODE_INPUT, atcCode);
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_OF_REPORT_INPUT);
    if (dateOfReport != "") {
      return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
    }
    return null;
  }

  private LocalDate getVaccinationDate() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(VACCINATION_DATE_INPUT);
    if (!dateOfReport.isEmpty()) {
      return LocalDate.parse(dateOfReport, DATE_FORMATTER_DE);
    }
    return null;
  }

  private Vaccination collectVaccinationData() {
    return Vaccination.builder()
        .reportDate(getDateOfReport())
        .vaccinationDate(getVaccinationDate())
        .vaccineName(webDriverHelpers.getValueFromWebElement(VACCINATION_NAME_INPUT))
        .vaccineManufacturer(
            webDriverHelpers.getValueFromWebElement(VACCINATION_MANUFACTURER_INPUT))
        .vaccineType(webDriverHelpers.getValueFromWebElement(VACCINATION_TYPE_INPUT))
        .vaccinationInfoSource(
            webDriverHelpers.getValueFromWebElement(VACCINATION_INFO_SOURCE_INPUT))
        .vaccineDose(webDriverHelpers.getValueFromWebElement(VACCINE_DOSE_INPUT))
        .inn(webDriverHelpers.getValueFromWebElement(INN_INPUT))
        .uniiCode(webDriverHelpers.getValueFromWebElement(UNII_CODE_INPUT))
        .batchNumber(webDriverHelpers.getValueFromWebElement(BATCH_NUMBER_INPUT))
        .atcCode(webDriverHelpers.getValueFromWebElement(ATC_CODE_INPUT))
        .build();
  }
}
