package org.sormas.e2etests.pages.application.immunizations;

import org.openqa.selenium.By;

public class EditImmunizationPage {
  public static final By FACILITY_COMBOBOX_IMMUNIZATION_INPUT =
      By.cssSelector("#healthFacility input");
  public static final By DISEASE_COMBOBOX = By.cssSelector(".v-window #disease div");
  public static final By FACILITY_NAME_DESCRIPTION_VALUE = By.id("healthFacilityDetails");
  public static final By IMMUNIZATION_PERSON_TAB = By.cssSelector("div#tab-immunizations-person");
}
