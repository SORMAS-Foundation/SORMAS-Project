package org.sormas.e2etests.pages.application.vaccinations;

import org.openqa.selenium.By;

public class CreateNewVaccinationPage {

  public static final By VACCINATION_DATE_INPUT =
      By.cssSelector(".v-window #vaccinationDate input");
  public static final By VACCINATION_NAME_COMBOBOX = By.cssSelector(".v-window #vaccineName div");
  public static final By VACCINATION_MANUFACTURER_COMBOBOX =
      By.cssSelector(".v-window #vaccineManufacturer div");
  public static final By VACCINATION_TYPE_INPUT = By.cssSelector(".v-window #vaccineType");
  public static final By VACCINATION_INFO_SOURCE_COMBOBOX =
      By.cssSelector(".v-window #vaccinationInfoSource div");
  public static final By VACCINE_DOSE_INPUT = By.cssSelector(".v-window #vaccineDose");
  public static final By INN_INPUT = By.cssSelector(".v-window #vaccineInn");
  public static final By UNII_CODE_INPUT = By.cssSelector(".v-window #vaccineUniiCode");
  public static final By BATCH_NUMBER_INPUT = By.cssSelector(".v-window #vaccineBatchNumber");
  public static final By ATC_CODE_INPUT = By.cssSelector(".v-window #vaccineAtcCode");
  public static final By SAVE_VACCINATION_FORM_BUTTON = By.cssSelector(".v-window #commit");
  public static final By NEW_VACCINATION_DE_BUTTON = By.id("Neue Impfung");
}
