package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.*;
import static org.sormas.e2etests.pages.application.cases.LineListingPopup.*;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

public class CaseLineListingSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected static Case caze;
  public static Case caseLineListing;

  @Inject
  public CaseLineListingSteps(
      WebDriverHelpers webDriverHelpers, CaseService caseService, final SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "^I create a new case in line listing feature popup for DE version$",
        () -> {
          caze = caseService.buildCaseForLineListingFeatureDE();
          selectRegion(caze.getRegion());
          selectDistrict(caze.getDistrict());
          selectFacilityCategory(caze.getFacilityCategory());
          selectFacilityType(caze.getFacilityType());
          fillDateOfReport(caze.getDateOfReport(), Locale.GERMAN);
          selectCommunity(caze.getCommunity());
          selectFacility("Andere Einrichtung");
          fillFacilityName(caze.getPlaceDescription());
          fillFirstName(caze.getFirstName());
          fillLastName(caze.getLastName());
          fillDateOfBirth(caze.getDateOfBirth(), Locale.GERMAN);
          selectSex(caze.getSex());
          fillDateOfSymptom(caze.getDateOfSymptomOnset(), Locale.GERMAN);
        });

    When(
        "^I create a new case in line listing feature popup$",
        () -> {
          caseLineListing = caseService.buildCaseForLineListingFeature();
          selectDisease(caseLineListing.getDisease());
          selectRegion(caseLineListing.getRegion());
          selectDistrict(caseLineListing.getDistrict());
          selectFacilityCategory(caseLineListing.getFacilityCategory());
          selectFacilityType(caseLineListing.getFacilityType());
          fillDateOfReport(caseLineListing.getDateOfReport(), Locale.ENGLISH);
          selectCommunity(caseLineListing.getCommunity());
          selectFacility("Other facility");
          fillFacilityName(caseLineListing.getPlaceDescription());
          fillFirstName(caseLineListing.getFirstName());
          fillLastName(caseLineListing.getLastName());
          fillDateOfBirth(caseLineListing.getDateOfBirth(), Locale.ENGLISH);
          selectSex(caseLineListing.getSex());
          fillDateOfSymptom(caseLineListing.getDateOfSymptomOnset(), Locale.ENGLISH);
        });

    When(
        "^From case line listing I click on add line button$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_ADD_LINE_BUTTON));

    When(
        "^I save the new line listing case$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_SAVE_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(25);
        });

    When(
        "I check that case created from Line Listing is saved and displayed in results grid",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT);
          String caseName = caseLineListing.getFirstName() + " " + caseLineListing.getLastName();
          webDriverHelpers.fillInWebElement(
              PERSON_ID_NAME_CONTACT_INFORMATION_LIKE_INPUT, caseName);
          webDriverHelpers.clickOnWebElementBySelector(CASE_APPLY_FILTERS_BUTTON);
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(FIRST_CASE_ID_BUTTON);

          softly.assertEquals(
              getCaseDiseaseFromGridResults(),
              caseLineListing.getDisease(),
              "Disease value doesn't match");
          softly.assertEquals(
              getCaseFirstNameFromGridResults(),
              caseLineListing.getFirstName(),
              "First name value doesn't match");
          softly.assertEquals(
              getCaseLastNameFromGridResults(),
              caseLineListing.getLastName(),
              "Last name value doesn't match");
          softly.assertEquals(
              getCaseDistrictFromGridResults(),
              caseLineListing.getDistrict(),
              "District value doesn't match");
          softly.assertEquals(
              getCaseHealthFacilityFromGridResults(),
              "Other facility - " + caseLineListing.getPlaceDescription(),
              "Health facility value doesn't match");
          softly.assertAll();
        });

    When(
        "I check that case created from Line Listing for DE version is saved and displayed in results grid",
        () -> {
          webDriverHelpers.waitForPageLoaded();

          softly.assertEquals(
              getCaseDiseaseFromGridResults(), caze.getDisease(), "Disease value doesn't match");
          softly.assertEquals(
              getCaseFirstNameFromGridResults(),
              caze.getFirstName(),
              "First name value doesn't match");
          softly.assertEquals(
              getCaseLastNameFromGridResults(),
              caze.getLastName(),
              "Last name value doesn't match");
          softly.assertEquals(
              getCaseDistrictFromGridResults(), caze.getDistrict(), "District value doesn't match");
          softly.assertEquals(
              getCaseHealthFacilityFromGridResults(),
              "Andere Einrichtung - " + caze.getPlaceDescription(),
              "Health facility value doesn't match");
          softly.assertAll();
        });
  }

  private void selectDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX, region);
  }

  private void selectDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, district);
  }

  private void selectFacilityCategory(String facilityCategory) {
    webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_COMBOBOX, facilityCategory);
  }

  private void selectFacilityType(String facilityType) {
    webDriverHelpers.selectFromCombobox(FACILITY_TYPE_COMBOBOX, facilityType);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN))
      formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(Locale.GERMANY);
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT, formatter.format(date));
  }

  private void selectCommunity(String community) {
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, community);
  }

  private void selectFacility(String facility) {
    webDriverHelpers.selectFromCombobox(FACILITY_COMBOBOX, facility);
  }

  private void fillFacilityName(String facilityName) {
    webDriverHelpers.fillInWebElement(FACILITY_NAME_INPUT, facilityName);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void fillDateOfBirth(LocalDate localDate, Locale locale) {
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_MONTH_COMBOBOX, localDate.getMonth().getDisplayName(TextStyle.FULL, locale));
    webDriverHelpers.selectFromCombobox(
        DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX, sex);
  }

  private void fillDateOfSymptom(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN))
      formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(Locale.GERMANY);
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_SYMPTOM_INPUT, formatter.format(date));
  }

  // TODO refactor last methods and generate a POJO based on collected fields
  private String getCaseDiseaseFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_DISEASE, 0);
  }

  private String getCaseFirstNameFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_FIRST_NAME, 0);
  }

  private String getCaseLastNameFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_LAST_NAME, 0);
  }

  private String getCaseDistrictFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_DISTRICT, 0);
  }

  private String getCaseHealthFacilityFromGridResults() {
    return webDriverHelpers.getTextFromListElement(GRID_RESULTS_HEALTH_FACILITY, 0);
  }
}
