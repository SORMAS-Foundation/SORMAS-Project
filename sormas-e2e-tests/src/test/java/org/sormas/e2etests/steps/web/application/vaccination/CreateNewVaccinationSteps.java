package org.sormas.e2etests.steps.web.application.vaccination;

import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.ATC_CODE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.BATCH_NUMBER_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.INN_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.NEW_VACCINATION_DE_BUTTON;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.SAVE_VACCINATION_FORM_BUTTON;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.UNII_CODE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_DATE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_INFO_SOURCE_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_MANUFACTURER_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_NAME_COMBOBOX;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINATION_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.vaccinations.CreateNewVaccinationPage.VACCINE_DOSE_INPUT;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Vaccination;
import org.sormas.e2etests.entities.services.VaccinationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class CreateNewVaccinationSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static Vaccination vaccination;

  @Inject
  public CreateNewVaccinationSteps(
      WebDriverHelpers webDriverHelpers, VaccinationService vaccinationService) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I fill new vaccination data in new Vaccination form",
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
}
