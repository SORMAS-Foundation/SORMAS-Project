package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.START_DATA_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.CreateNewImmunizationPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class CreateNewImmunizationSteps implements En {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization immunization;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewImmunizationSteps(
      WebDriverHelpers webDriverHelpers,
      ImmunizationService immunizationService,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new immunization with specific data$",
        () -> {
          immunization = immunizationService.buildGeneratedImmunization();
          fillDateOfReport(immunization.getDateOfReport());
          fillExternalId(immunization.getExternalId());
          fillDisease(immunization.getDisease());
          fillMeansOfImmunization(immunization.getMeansOfImmunization());
          selectResponsibleRegion(immunization.getResponsibleRegion());
          selectResponsibleDistrict(immunization.getResponsibleDistrict());
          selectResponsibleCommunity(immunization.getResponsibleCommunity());
          selectFacilityCategory(immunization.getFacilityCategory());
          selectFacilityType(immunization.getFacilityType());
          selectFacilityName(immunization.getFacility());
          fillFacilityNameAndDescription(immunization.getFacilityDescription());
          fillFirstName(immunization.getFirstName());
          fillLastName(immunization.getLastName());
          fillDateOfBirth(immunization.getDateOfBirth());
          selectSex(immunization.getSex());
          selectPresentConditionOfPerson(immunization.getPresentConditionOfPerson());
          fillPrimaryPhoneNumber(immunization.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(immunization.getPrimaryEmailAddress());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });

    When(
        "I check Overwrite immunization management status option",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS_INPUT);
        });

    When(
        "I check if Overwrite immunization management status is unchecked by Management Status",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MANAGEMENT_STATUS_INPUT);
          webDriverHelpers.scrollToElement(MANAGEMENT_STATUS_INPUT);
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(MANAGEMENT_STATUS_INPUT),
              "Expected management status is not correct");
          softly.assertAll();
        });

    When(
        "I click on discard button from immunization tab",
        () -> {
          webDriverHelpers.scrollToElement(DISCARD_IMMUNIZATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DISCARD_IMMUNIZATION_BUTTON);
        });

    When(
        "I fill only mandatory fields in immunization popup with means of immunization as a {string}",
        (String status) -> {
          immunization =
              immunizationService.buildGeneratedImmunizationWithMeansOfImmunizationFromCase(status);
          fillDateOfReport(immunization.getDateOfReport());
          fillMeansOfImmunization(immunization.getMeansOfImmunization());
          selectResponsibleRegion(immunization.getResponsibleRegion());
          selectResponsibleDistrict(immunization.getResponsibleDistrict());
          webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_POPUP_SAVE_BUTTON);
        });

    When(
        "I fill a new immunization form with {string} as a responsible region and {string} as a responsible district",
        (String region, String district) -> {
          immunization =
              immunizationService.buildImmunizationWithSpecificResponsibleLocation(
                  region, district);
          fillDateOfReport(immunization.getDateOfReport());
          fillExternalId(immunization.getExternalId());
          fillMeansOfImmunization(immunization.getMeansOfImmunization());
          selectResponsibleRegion(immunization.getResponsibleRegion());
          selectResponsibleDistrict(immunization.getResponsibleDistrict());
        });

    When(
        "^I fill mandatory fields and immunization period in a new immunization popup$",
        () -> {
          immunization = immunizationService.buildGeneratedImmunization();
          fillDateOfReport(immunization.getDateOfReport());
          fillExternalId(immunization.getExternalId());
          fillMeansOfImmunization(immunization.getMeansOfImmunization());
          selectResponsibleRegion(immunization.getResponsibleRegion());
          selectResponsibleDistrict(immunization.getResponsibleDistrict());
          fillStartData(immunization.getStartDate());
          fillEndData(immunization.getEndDate());
          webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_POPUP_SAVE_BUTTON);
        });

    And(
        "^I check that required fields are marked as mandatory on Create new immunization form$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(CREATE_NEW_IMMUNIZATION_TITLE);
          webDriverHelpers.checkWebElementContainsText(RESPONSIBLE_REGION_TITLE, "*");
          webDriverHelpers.checkWebElementContainsText(RESPONSIBLE_DISTRICT_TITLE, "*");
          webDriverHelpers.checkWebElementContainsText(DATE_OF_REPORT_TITLE, "*");
          webDriverHelpers.checkWebElementContainsText(DISEASE_TITLE, "*");
        });

    And(
        "I check if {string} is available on Create new immunization form",
        (String element) -> {
          switch (element) {
            case "means of immunization combobox":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(MEANS_OF_IMMUNIZATIONS_COMBOBOX);
              break;
            case "MEANS OF IMMUNIZATION DETAILS field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  MEANS_OF_IMMUNIZATION_DETAILS_INPUT);
              break;
            case "OVERWRITE IMMUNIZATION MANAGEMENT STATUS checkbox":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(
                  OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS_INPUT);
              break;
            case "EXTERNAL ID field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(EXTERNAL_ID_INPUT);
              break;
            case "RESPONSIBLE REGION field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_REGION_COMBOBOX);
              break;
            case "RESPONSIBLE DISTRICT field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_DISTRICT_COMBOBOX);
              break;
            case "RESPONSIBLE COMMUNITY field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(RESPONSIBLE_COMMUNITY_COMBOBOX);
              break;
            case "FACILITY field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(FACILITY_COMBOBOX);
              break;
            case "START DATE field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_DATE_INPUT);
              break;
            case "END DATE field":
              webDriverHelpers.waitUntilIdentifiedElementIsPresent(END_DATA_INPUT);
              break;
          }
        });

    And(
        "^I select \"([^\"]*)\" means of immunization on Create new immunization form$",
        (String option) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              MEANS_OF_IMMUNIZATIONS_COMBOBOX);
          webDriverHelpers.selectFromCombobox(MEANS_OF_IMMUNIZATIONS_COMBOBOX, option);
        });

    And(
        "I check if Management status is set to {string} on Create new immunization form",
        (String expected) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(MANAGEMENT_STATUS_INPUT),
              expected,
              "Management status is different than expected");
          softly.assertAll();
        });

    And(
        "I check if Management status is {string} on Create new immunization form",
        (String option) -> {
          switch (option) {
            case "editable":
              softly.assertTrue(
                  webDriverHelpers.isElementEnabled(MANAGEMENT_STATUS_INPUT),
                  "Management status field is not editable!");
              softly.assertAll();
              break;
            case "read only":
              softly.assertFalse(
                  webDriverHelpers.isElementEnabled(MANAGEMENT_STATUS_INPUT),
                  "Management status field is not read only!");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I check if Immunization status is set to \"([^\"]*)\" on Create new immunization form$",
        (String expected) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(IMMUNIZATION_STATUS_INPUT),
              expected,
              "Immunization status is different than expected");
          softly.assertAll();
        });

    And(
        "^I select \"([^\"]*)\" management status on Create new immunization form$",
        (String option) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(MANAGEMENT_STATUS_COMBOBOX);
          webDriverHelpers.selectFromCombobox(MANAGEMENT_STATUS_COMBOBOX, option);
        });

    And(
        "^I click on SAVE new immunization button$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              IMMUNIZATION_POPUP_SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(IMMUNIZATION_POPUP_SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(50);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(UUID_INPUT);
        });

    And(
        "^I fill a new immunization form with specific data$",
        () -> {
          immunization = immunizationService.buildGeneratedImmunization();
          fillDateOfReport(immunization.getDateOfReport());
          fillExternalId(immunization.getExternalId());
          fillDisease(immunization.getDisease());
          fillMeansOfImmunization(immunization.getMeansOfImmunization());
          selectResponsibleRegion(immunization.getResponsibleRegion());
          selectResponsibleDistrict(immunization.getResponsibleDistrict());
          selectResponsibleCommunity(immunization.getResponsibleCommunity());
          selectFacilityCategory(immunization.getFacilityCategory());
          selectFacilityType(immunization.getFacilityType());
          selectFacilityName(immunization.getFacility());
          fillFacilityNameAndDescription(immunization.getFacilityDescription());
          fillFirstName(immunization.getFirstName());
          fillLastName(immunization.getLastName());
          fillDateOfBirth(immunization.getDateOfBirth());
          selectSex(immunization.getSex());
          selectPresentConditionOfPerson(immunization.getPresentConditionOfPerson());
          fillPrimaryPhoneNumber(immunization.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(immunization.getPrimaryEmailAddress());
        });
  }

  private void fillDateOfReport(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillStartData(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectManagementStatusOption(String managementStatusOption) {
    webDriverHelpers.clickWebElementByText(EVENT_MANAGEMENT_STATUS_OPTIONS, managementStatusOption);
  }

  private void selectImmunizationStatus(String immunizationStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_MANAGEMENT_STATUS_OPTIONS, immunizationStatus);
  }

  private void fillEndData(LocalDate date) {
    webDriverHelpers.fillInWebElement(END_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void selectFacilityName(String facilityName) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facilityName);
  }

  private void fillFacilityNameAndDescription(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_DESCRIPTION, facilityName);
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void fillMeansOfImmunization(String meansOfImmunization) {
    webDriverHelpers.selectFromCombobox(MEANS_OF_IMMUNIZATIONS_COMBOBOX, meansOfImmunization);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void selectPresentConditionOfPerson(String presentConditionOfPerson) {
    webDriverHelpers.selectFromCombobox(
        PRESENT_CONDITION_OF_PERSON_COMBOBOX, presentConditionOfPerson);
  }

  private void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  private void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }
}
