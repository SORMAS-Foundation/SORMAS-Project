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
  public static final By IMMUNIZATION_MANAGEMENT_STATUS_INPUT =
      By.cssSelector("#immunizationManagementStatus input");
  public static final By IMMUNIZATION_STATUS_INPUT = By.cssSelector("#immunizationStatus input");
  public static final By NEW_ENTRY_BUTTON = By.id("actionNewEntry");
  public static final By NUMBER_OF_DOSES = By.id("numberOfDoses");
  public static final By POPUP_MESSAGE = By.xpath("//div[@class='popupContent']//p");
  public static final By BUTTONS_IN_VACCINATIONS_LOCATION =
      By.xpath("//div[contains(@location,\"vaccinations\")]//span[@class=\"v-button-wrap\"]");
  public static final By DELETE_VACCINATION_BUTTON = By.cssSelector(".popupContent #delete");
  public static final By VACCINATION_ID_HEADER = By.xpath("//div[text()=\"Vaccination ID\"]");
  public static final By VACCINATION_DATE_HEADER = By.xpath("//div[text()=\"Vaccination date\"]");
  public static final By VACCINATION_NAME_HEADER = By.xpath("//div[text()=\"Vaccine name\"]");
  public static final By VACCINATION_MANUFACTURER_HEADER =
      By.xpath("//div[text()=\"Vaccine manufacturer\"]");
  public static final By VACCINATION_TYPE_HEADER = By.xpath("//div[text()=\"Vaccine type\"]");
  public static final By VACCINATION_DOSE_HEADER = By.xpath("//div[text()=\"Vaccine dose\"]");

  public static By getVaccinationByIndex(String index) {
    return By.xpath(
        String.format(
            "(//div[contains(@location,\"vaccinations\")]//span[@class=\"v-button-wrap\"])[%s]/..",
            index));
  }

  public static final By ARCHIVE_DEARCHIVE_BUTTON = By.id("archiveDearchive");
  public static final By ACTION_CONFIRM_BUTTON = By.id("actionConfirm");
  public static final By DISCARD_BUTTON = By.id("discard");
  public static final By COMMIT_BUTTON = By.id("commit");
  public static final By DELETE_BUTTON = By.id("delete");
  public static final By DELETION_REASON_COMBOBOX =
      By.cssSelector(".popupContent div[role='combobox'] input+div");
  public static final By ACTION_CANCEL_BUTTON = By.cssSelector(".popupContent #actionCancel");
  public static final By REASON_FOR_DELETION_EXCLAMATION_MARK =
      By.cssSelector(".popupContent span[class='v-errorindicator v-errorindicator-error']");
  public static final By REASON_FOR_DELETION_MESSAGE =
      By.xpath("//div[@class='v-errormessage v-errormessage-error']");

  public static By getReasonForDeletionDetailsFieldLabel(String label) {
    return By.xpath(String.format("//div[@class='popupContent']//span[text()='[%s[']", label));
  }

  public static By REASON_FOR_DELETION_DISABLED_REASON_INPUT =
      By.cssSelector("#deletionReason input");
  public static By EXTERNAL_ID_INPUT = By.cssSelector("#externalId");
  public static By ADDITIONAL_DETAILS = By.cssSelector("#additionalDetails");
  public static final By START_DATE_INPUT = By.cssSelector("#startDate input");
  public static final By END_DATE_INPUT = By.cssSelector("#endDate input");
}
