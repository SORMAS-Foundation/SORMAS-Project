package org.sormas.e2etests.steps.web.application.immunizations;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SAVE_EDIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.*;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ACTION_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.ARCHIVE_DEARCHIVE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.BUTTONS_IN_VACCINATIONS_LOCATION;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.COMMIT_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DELETE_VACCINATION_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_CATEGORY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_COMBOBOX_IMMUNIZATION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_NAME_DESCRIPTION_VALUE;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.FACILITY_TYPE_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_MANAGEMENT_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_PERSON_TAB;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.IMMUNIZATION_STATUS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.MEANS_OF_IMMUNIZATIONS_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.NUMBER_OF_DOSES;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.RESPONSIBLE_REGION_INPUT;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.UUID;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DATE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_DOSE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_ID_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_MANUFACTURER_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_NAME_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.VACCINATION_TYPE_HEADER;
import static org.sormas.e2etests.pages.application.immunizations.EditImmunizationPage.getVaccinationByIndex;
import static org.sormas.e2etests.pages.application.persons.EditPersonPage.UUID_INPUT;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.DELETE_SAMPLE_REASON_POPUP;
import static org.sormas.e2etests.pages.application.samples.EditSamplePage.SAMPLE_DELETION_POPUP_YES_BUTTON;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Immunization;
import org.sormas.e2etests.entities.services.ImmunizationService;
import org.sormas.e2etests.helpers.AssertHelpers;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class EditImmunizationSteps implements En {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
  public static Immunization collectedImmunization;
  private static Immunization createdImmunization;
  private final WebDriverHelpers webDriverHelpers;
  private static String currentUrl;

  @SneakyThrows
  @Inject
  public EditImmunizationSteps(
      WebDriverHelpers webDriverHelpers,
      ImmunizationService immunizationService,
      AssertHelpers assertHelpers,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I check the created data is correctly displayed on Edit immunization page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID);
          collectedImmunization = collectImmunizationData();
          createdImmunization = CreateNewImmunizationSteps.immunization;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedImmunization,
              createdImmunization,
              List.of(
                  "disease",
                  "dateOfReport",
                  "responsibleRegion",
                  "responsibleDistrict",
                  "responsibleCommunity",
                  "facilityCategory",
                  "facilityType",
                  "facility",
                  "facilityDescription"));
        });
    When(
        "I click on New Entry button in Vaccination tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_ENTRY_BUTTON));
    When(
        "I set Number of doses to {int} on Edit Immunization Page",
        (Integer number) ->
            webDriverHelpers.fillInWebElement(NUMBER_OF_DOSES, String.valueOf(number)));
    When(
        "^I click SAVE button on Edit Immunization Page$",
        () -> {
          webDriverHelpers.scrollToElement(SAVE_EDIT_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_EDIT_BUTTON);
          TimeUnit.SECONDS.sleep(3);
        });
    When(
        "^I check if exceeded number of doses error popup message appeared$",
        () -> {
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getTextFromWebElement(VACCINE_DOSE_POPUP),
                      "Vaccine dose should be a number between 1 and 10",
                      "Exceeded number of doses message is incorrect"));
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });
    When(
        "I check if Immunization management status is set to {string}",
        (String expected) -> {
          if (webDriverHelpers.isElementVisibleWithTimeout(CASE_SAVED_POPUP, 5))
            webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
          assertHelpers.assertWithPoll20Second(
              () ->
                  Assert.assertEquals(
                      webDriverHelpers.getValueFromWebElement(IMMUNIZATION_MANAGEMENT_STATUS_INPUT),
                      expected,
                      "Immunization Management status is different than expected"));
        });
    When(
        "I check if Immunization status is set to {string}",
        (String expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getValueFromWebElement(IMMUNIZATION_STATUS_INPUT),
                        expected,
                        "Immunization status is different than expected")));
    When(
        "I check that number of added Vaccinations is {int}",
        (Integer expected) ->
            assertHelpers.assertWithPoll20Second(
                () ->
                    Assert.assertEquals(
                        webDriverHelpers.getNumberOfElements(BUTTONS_IN_VACCINATIONS_LOCATION) - 1,
                        (int) expected,
                        "Number of vaccinations is different than expected")));
    When(
        "I click to edit {int} vaccination on Edit Immunization page",
        (Integer index) -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              getVaccinationByIndex(String.valueOf(index + 1)));
          webDriverHelpers.clickOnWebElementBySelector(
              getVaccinationByIndex(String.valueOf(index + 1)));
        });
    When(
        "I click Delete button in Vaccination form",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DELETE_VACCINATION_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_VACCINATION_BUTTON);
        });
    When(
        "I choose the reason of deletion in popup for Vaccination",
        () -> {
          webDriverHelpers.selectFromCombobox(
              DELETE_SAMPLE_REASON_POPUP, "Entity created without legal reason");
          webDriverHelpers.clickOnWebElementBySelector(SAMPLE_DELETION_POPUP_YES_BUTTON);
        });
    When(
        "I click the header of column {int} of Vaccination table",
        (Integer col) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//tr//td[" + col.toString() + "]"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(15);
        });
    When(
        "I check that ([^\"]*) is visible in Vaccinations tab on Edit Immunization Page",
        (String option) -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(10);
          By selector = null;
          boolean elementVisible = true;
          switch (option) {
            case "Vaccination ID":
              selector = VACCINATION_ID_HEADER;
              break;
            case "Vaccination date":
              selector = VACCINATION_DATE_HEADER;
              break;
            case "Vaccine name":
              selector = VACCINATION_NAME_HEADER;
              break;
            case "Vaccine manufacturer":
              selector = VACCINATION_MANUFACTURER_HEADER;
              break;
            case "Vaccine type":
              selector = VACCINATION_TYPE_HEADER;
              break;
            case "Vaccine dose":
              selector = VACCINATION_DOSE_HEADER;
              break;
          }
          try {
            webDriverHelpers.scrollToElementUntilIsVisible(selector);
          } catch (Throwable ignored) {
            elementVisible = false;
          }
          Assert.assertTrue(elementVisible, option + " is not visible!");
        });

    When(
        "^I click on archive button from immunization tab$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.scrollToElement(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_DEARCHIVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_BUTTON);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "^I check if editable fields are read only for an archived immunization$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          TimeUnit.SECONDS.sleep(15);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMMUNIZATION_PERSON_TAB);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISEASE_INPUT),
              true,
              "Disease input is not is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(MEANS_OF_IMMUNIZATIONS_INPUT),
              true,
              "Means of immunization input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_REGION_INPUT),
              true,
              "Responsible region input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_DISTRICT_INPUT),
              true,
              "Responsible district input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_COMMUNITY_INPUT),
              true,
              "Responsible community input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DATE_OF_REPORT_INPUT),
              true,
              "Date of report input is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISCARD_BUTTON),
              true,
              "Discard button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(COMMIT_BUTTON),
              true,
              "Commit button is not editable state but it should be since archived entities default value is true!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DELETE_BUTTON),
              true,
              "Delete button is not editable state but it should be since archived entities default value is true!");
          softly.assertAll();
        });

    When(
        "I copy url of current immunization case", () -> currentUrl = webDriverHelpers.returnURL());

    When(
        "I click on Delete button from immunization case",
        () -> {
          webDriverHelpers.scrollToElement(DELETE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(DELETE_BUTTON);
        });

    When(
        "I check if reason for deletion as {string} is available",
        (String reason) -> {
          softly.assertTrue(
              webDriverHelpers.checkIfElementExistsInCombobox(DELETION_REASON_COMBOBOX, reason),
              "Deletion option does not exists: " + reason);
          softly.assertAll();
        });

    When(
        "I click on No option in Confirm deletion popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTION_CANCEL_BUTTON));

    When(
        "I click on Yes option in Confirm deletion popup",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(ACTION_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(ACTION_CONFIRM_BUTTON);
          TimeUnit.SECONDS.sleep(1); // wait for page loaded
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(100);
        });

    When(
        "I check if exclamation mark with message {string} appears next to Reason for deletion",
        (String message) -> {
          String hoverMessage;
          webDriverHelpers.hoverToElement(REASON_FOR_DELETION_EXCLAMATION_MARK);
          hoverMessage = webDriverHelpers.getTextFromWebElement(REASON_FOR_DELETION_MESSAGE);
          softly.assertEquals(message, hoverMessage, "Messages are not equal");
          softly.assertAll();
        });

    When(
        "I set Reason for deletion as {string}",
        (String reason) ->
            webDriverHelpers.selectFromComboboxEqual(DELETION_REASON_COMBOBOX, reason));

    When(
        "I check if {string} field is available in Confirm deletion popup in Immunization",
        (String label) ->
            webDriverHelpers.isElementVisibleWithTimeout(
                getReasonForDeletionDetailsFieldLabel(label), 1));

    When(
        "I back to deleted immunization case by url",
        () -> webDriverHelpers.accessWebSite(currentUrl));

    When(
        "I check if reason of deletion is set to {string}",
        (String reason) -> {
          String collectedReason;
          collectedReason =
              webDriverHelpers.getValueFromWebElement(REASON_FOR_DELETION_DISABLED_REASON_INPUT);
          softly.assertEquals(reason, collectedReason, "Reasons of deletion are not equal");
          softly.assertAll();
        });

    When(
        "I check if External ID input on immunization edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(EXTERNAL_ID_INPUT), "External ID input is enabled");
          softly.assertAll();
        });

    When(
        "I check if Additional details text area on immunization edit page is disabled",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(ADDITIONAL_DETAILS),
              "Additional details text area is enabled");
          softly.assertAll();
        });

    When(
        "I check the specific created data is correctly displayed on Edit immunization page",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(UUID);
          collectedImmunization = collectSpecificImmunizationData();
          createdImmunization = CreateNewImmunizationSteps.immunization;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedImmunization,
              createdImmunization,
              List.of("dateOfReport", "responsibleRegion", "responsibleDistrict"));
        });

    Then(
        "^I check that Immunization data is displayed as read-only on Edit immunization page$",
        () -> {
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(30);
          TimeUnit.SECONDS.sleep(5);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMMUNIZATION_PERSON_TAB);
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(DISEASE_INPUT),
              false,
              "Disease input shouldn't be editable, but it is!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(MEANS_OF_IMMUNIZATIONS_INPUT),
              false,
              "Means of immunization input shouldn't be editable, but it is!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_REGION_INPUT),
              false,
              "Responsible region input shouldn't be editable, but it is!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_DISTRICT_INPUT),
              false,
              "Responsible district input shouldn't be editable, but it is!");
          softly.assertEquals(
              webDriverHelpers.isElementEnabled(RESPONSIBLE_COMMUNITY_INPUT),
              false,
              "Responsible community input shouldn't be editable, but it is!");
          TimeUnit.SECONDS.sleep(2);
          softly.assertAll();
        });

    Then(
        "^I check the specific created data with immunization period is correctly displayed on Edit immunization page$",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(IMMUNIZATION_PERSON_TAB);
          collectedImmunization = collectImmunizationDataWithImmunizationPeriod();
          createdImmunization = CreateNewImmunizationSteps.immunization;
          ComparisonHelper.compareEqualFieldsOfEntities(
              collectedImmunization,
              createdImmunization,
              List.of("meansOfImmunization", "startDate", "endDate"));
        });
  }

  private Immunization collectImmunizationDataWithImmunizationPeriod() {
    return Immunization.builder()
        .dateOfReport(getDateOfReport())
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .meansOfImmunization(webDriverHelpers.getValueFromWebElement(MEANS_OF_IMMUNIZATIONS_INPUT))
        .managementStatus(
            webDriverHelpers.getValueFromWebElement(IMMUNIZATION_MANAGEMENT_STATUS_INPUT))
        .immunizationStatus(webDriverHelpers.getValueFromWebElement(IMMUNIZATION_STATUS_INPUT))
        .startDate(getStartDate())
        .endDate(getEndDate())
        .build();
  }

  private Immunization collectImmunizationData() {
    return Immunization.builder()
        .dateOfReport(getDateOfReport())
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .meansOfImmunization(webDriverHelpers.getValueFromWebElement(MEANS_OF_IMMUNIZATIONS_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_COMMUNITY_INPUT))
        .facilityDescription(
            webDriverHelpers.getValueFromWebElement(FACILITY_NAME_DESCRIPTION_VALUE))
        .facilityCategory(webDriverHelpers.getValueFromWebElement(FACILITY_CATEGORY_INPUT))
        .facilityType(webDriverHelpers.getValueFromWebElement(FACILITY_TYPE_INPUT))
        .facility(webDriverHelpers.getValueFromWebElement(FACILITY_COMBOBOX_IMMUNIZATION_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .managementStatus(
            webDriverHelpers.getValueFromWebElement(IMMUNIZATION_MANAGEMENT_STATUS_INPUT))
        .immunizationStatus(webDriverHelpers.getValueFromWebElement(IMMUNIZATION_STATUS_INPUT))
        .build();
  }

  private Immunization collectSpecificImmunizationData() {
    return Immunization.builder()
        .dateOfReport(getDateOfReport())
        .meansOfImmunization(webDriverHelpers.getValueFromWebElement(MEANS_OF_IMMUNIZATIONS_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(RESPONSIBLE_DISTRICT_INPUT))
        .uuid((webDriverHelpers.getValueFromWebElement(UUID_INPUT)))
        .build();
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(DATE_OF_REPORT_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private LocalDate getStartDate() {
    String startDate = webDriverHelpers.getValueFromWebElement(START_DATE_INPUT);
    return LocalDate.parse(startDate, DATE_FORMATTER);
  }

  private LocalDate getEndDate() {
    String endDate = webDriverHelpers.getValueFromWebElement(END_DATE_INPUT);
    return LocalDate.parse(endDate, DATE_FORMATTER);
  }
}
