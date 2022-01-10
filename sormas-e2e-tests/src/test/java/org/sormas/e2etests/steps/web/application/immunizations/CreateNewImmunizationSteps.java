package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.events.CreateNewEventPage.START_DATA_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.CreateNewImmunizationPage.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.Immunization;
import org.sormas.e2etests.services.ImmunizationService;

public class CreateNewImmunizationSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
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
          fillNationalHealthId(immunization.getNationalHealthId());
          fillPassportNumber(immunization.getPassportNumber());
          selectPresentConditionOfPerson(immunization.getPresentConditionOfPerson());
          fillPrimaryPhoneNumber(immunization.getPrimaryPhoneNumber());
          fillPrimaryEmailAddress(immunization.getPrimaryEmailAddress());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
        });
  }

  public void fillDateOfReport(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  public void fillStartData(LocalDate date) {
    webDriverHelpers.fillInWebElement(START_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  public void selectManagementStatusOption(String managementStatusOption) {
    webDriverHelpers.clickWebElementByText(EVENT_MANAGEMENT_STATUS_OPTIONS, managementStatusOption);
  }

  public void selectImmunizationStatus(String immunizationStatus) {
    webDriverHelpers.clickWebElementByText(EVENT_MANAGEMENT_STATUS_OPTIONS, immunizationStatus);
  }

  public void fillEndData(LocalDate date) {
    webDriverHelpers.fillInWebElement(END_DATA_INPUT, DATE_FORMATTER.format(date));
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  public void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  public void selectFacilityName(String facilityName) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facilityName);
  }

  public void fillFacilityNameAndDescription(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_DESCRIPTION, facilityName);
  }

  public void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }

  public void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  public void fillMeansOfImmunization(String meansOfImmunization) {
    webDriverHelpers.selectFromCombobox(MEANS_OF_IMMUNIZATIONS_COMBOBOX, meansOfImmunization);
  }

  public void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  public void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  public void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  public void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  public void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  public void fillDateOfBirth(LocalDate localDate) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  public void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  public void fillNationalHealthId(String nationalHealthId) {
    webDriverHelpers.fillInWebElement(NATIONAL_HEALTH_ID_INPUT, nationalHealthId);
  }

  public void fillPassportNumber(String passportNumber) {
    webDriverHelpers.fillInWebElement(PASSPORT_NUMBER_INPUT, passportNumber);
  }

  public void selectPresentConditionOfPerson(String presentConditionOfPerson) {
    webDriverHelpers.selectFromCombobox(
        PRESENT_CONDITION_OF_PERSON_COMBOBOX, presentConditionOfPerson);
  }

  public void fillPrimaryPhoneNumber(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_PHONE_NUMBER_INPUT, primaryPhoneNumber);
  }

  public void fillPrimaryEmailAddress(String primaryPhoneNumber) {
    webDriverHelpers.fillInWebElement(PRIMARY_EMAIL_ADDRESS_INPUT, primaryPhoneNumber);
  }
}

