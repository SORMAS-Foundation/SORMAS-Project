package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.START_DATA_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.CreateNewImmunizationPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.WebDriverHelpers;

public class CreateNewImmunizationSteps implements En {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization immunization;
  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public CreateNewImmunizationSteps(
      WebDriverHelpers webDriverHelpers, ImmunizationService immunizationService) {
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
