package org.sormas.e2etests.pages.application.immunizations;

import org.openqa.selenium.By;

public class EditImmunizationPage {
  public static final By UUID = By.id("uuid");
  public static final By DATE_OF_REPORT_INPUT = By.cssSelector("#reportDate input");
  public static final By FACILITY_COMBOBOX_IMMUNIZATION_INPUT =
      By.cssSelector("#healthFacility input");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By MEANS_OF_IMMUNIZATIONS_INPUT =
      By.cssSelector("#meansOfImmunization input");
  public static final By RESPONSIBLE_REGION_INPUT = By.cssSelector("#responsibleRegion input");
  public static final By RESPONSIBLE_DISTRICT_INPUT = By.cssSelector("#responsibleDistrict input");
  public static final By RESPONSIBLE_COMMUNITY_INPUT =
      By.cssSelector("#responsibleCommunity input");
  public static final By FACILITY_NAME_DESCRIPTION_VALUE = By.id("healthFacilityDetails");
  public static final By FACILITY_CATEGORY_INPUT = By.cssSelector("#typeGroup input");
  public static final By FACILITY_TYPE_INPUT = By.cssSelector("#facilityType input");
  public static final By IMMUNIZATION_PERSON_TAB = By.cssSelector("div#tab-immunizations-person");
}
