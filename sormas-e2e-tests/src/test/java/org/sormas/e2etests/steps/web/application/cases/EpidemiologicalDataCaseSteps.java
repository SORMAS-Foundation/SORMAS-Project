package org.sormas.e2etests.steps.web.application.cases;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DESCRIPTION;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DETAILS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_END_OF_ACTIVITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_START_OF_ACTIVITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.BLUE_ERROR_EXCLAMATION_MARK_EXPOSURE_POPUP;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.BLUE_ERROR_EXCLAMATION_MARK_EXPOSURE_POPUP_TEXT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACTS_WITH_SOURCE_CASE_BOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTACT_WITH_SOURCE_CASE_KNOWN;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.CONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.COUNTRY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.DISCARD_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.DONE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_ACTION_CANCEL;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_ACTION_CONFIRM;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_CHOOSE_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.EXPOSURE_PROBABLE_INFECTION_ENVIRONMENT_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.FACILITY_CATEGORY_POPUP_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.FACILITY_TYPE_POPUP_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.INDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.NEW_CONTACT_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_ACTIVITY_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.OUTDOORS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.RISK_AREA_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SAVE_BUTTON_EPIDEMIOLOGICAL_DATA;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_EXPOSURES;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.getExposureTableData;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SOURCE_CASE_WINDOW_CONTACT;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_ACTIVITY_DETAILS;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.TYPE_OF_GATHERING_DETAILS;
import static org.sormas.e2etests.steps.BaseSteps.locale;
import static org.sormas.e2etests.steps.web.application.cases.FollowUpStep.faker;
import static org.sormas.e2etests.steps.web.application.contacts.ContactsLineListingSteps.DATE_FORMATTER_DE;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Activity;
import org.sormas.e2etests.entities.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.entities.services.EpidemiologicalDataService;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ActivityAsCaseType;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfGathering;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class EpidemiologicalDataCaseSteps implements En {

  final WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private static EpidemiologicalData epidemiologicalData;
  private static EpidemiologicalData specificCaseData;
  private static EpidemiologicalData epidemiologialDataSavedFromFields;

  @Inject
  public EpidemiologicalDataCaseSteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      EpidemiologicalDataService epidemiologicalDataService,
      RunningConfiguration runningConfiguration,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I set Start and End of activity by current date in Activity as Case form",
        () -> {
          webDriverHelpers.fillInWebElement(
              START_OF_EXPOSURE_INPUT, formatter.format(LocalDate.now()));
          webDriverHelpers.fillInWebElement(
              END_OF_EXPOSURE_INPUT, formatter.format(LocalDate.now()));
        });

    When(
        "I check that edit Activity as Case vision button is visible and clickable",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_ACTIVITY_BUTTON);
        });

    When(
        "I set Start and End of activity by current date in Activity as Case form for DE version",
        () -> {
          webDriverHelpers.fillInWebElement(
              START_OF_EXPOSURE_INPUT, DATE_FORMATTER_DE.format(LocalDate.now()));
          webDriverHelpers.fillInWebElement(
              END_OF_EXPOSURE_INPUT, DATE_FORMATTER_DE.format(LocalDate.now()));
        });
    When(
        "I fill Description field in Activity as Case form",
        () -> {
          webDriverHelpers.fillInWebElement(ACTIVITY_DESCRIPTION, faker.book().title());
        });

    When(
        "I check if created Activity as Case appears in a grid for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_ACTIVITY_BUTTON);
        });

    When(
        "I tick a Probable infection environmental box in Exposure for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              EXPOSURE_PROBABLE_INFECTION_ENVIRONMENT_CHECKBOX);
        });

    When(
        "I click on edit Activity as Case vision button",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_ACTIVITY_BUTTON);
        });

    When(
        "I am accessing via URL the Epidemiological data tab of the created case",
        () -> {
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(
              runningConfiguration.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!cases/epidata/"
                  + uuid);
        });

    When(
        "I click on Exposure details known with ([^\"]*) option",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(EXPOSURE_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I click on Contacts with source case known with ([^\"]*) option for DE",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(CONTACT_WITH_SOURCE_CASE_KNOWN, option);
        });

    When(
        "I click on Activity details known with ([^\"]*) option",
        (String option) ->
            webDriverHelpers.clickWebElementByText(ACTIVITY_DETAILS_KNOWN_OPTIONS, option));

    When(
        "I click on Residing or working in an area with high risk of transmission of the disease with ([^\"]*) option",
        (String option) -> {
          epidemiologialDataSavedFromFields =
              epidemiologialDataSavedFromFields.toBuilder()
                  .residingAreaWithRisk(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I click on Residing or travelling to countries, territories, areas experiencing larger outbreaks of local transmission with ([^\"]*) option",
        (String option) -> {
          epidemiologialDataSavedFromFields =
              epidemiologialDataSavedFromFields.toBuilder()
                  .largeOutbreaksArea(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(
              RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS, option);
        });

    When(
        "I click on Contacts with source case known with ([^\"]*) option",
        (String option) -> {
          epidemiologialDataSavedFromFields =
              epidemiologialDataSavedFromFields.toBuilder()
                  .contactsWithSourceCaseKnown(YesNoUnknownOptions.valueOf(option))
                  .build();
          webDriverHelpers.clickWebElementByText(CONTACT_WITH_SOURCE_CASE_KNOWN, option);
        });

    When(
        "I click on the NEW CONTACT button on Epidemiological Data Tab of Edit Case Page",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NEW_CONTACT_BUTTON);
        });

    When(
        "I select ([^\"]*) from Contacts With Source Case Known",
        (String option) -> {
          webDriverHelpers.clickWebElementByText(CONTACT_WITH_SOURCE_CASE_KNOWN, option);
        });

    When(
        "I check if Contacts of Source filed is available",
        () -> webDriverHelpers.waitUntilElementIsVisibleAndClickable(NEW_CONTACT_BUTTON));

    When(
        "I check that Contacts of Source filed is not available",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(NEW_CONTACT_BUTTON, 2),
              "Contacts With Source Case box is visible!");
          softly.assertAll();
        });

    When(
        "I check that Selected case is listed as Source Case in the CONTACTS WITH SOURCE CASE Box",
        () -> {
          String boxContents =
              webDriverHelpers.getTextFromWebElement(CONTACTS_WITH_SOURCE_CASE_BOX);
          String expectedCase =
              "Source case:\n"
                  + apiState.getCreatedCase().getPerson().getFirstName()
                  + " "
                  + apiState.getCreatedCase().getPerson().getLastName().toUpperCase()
                  + " ("
                  + apiState.getCreatedCase().getUuid().substring(0, 6).toUpperCase();
          softly.assertTrue(
              boxContents.contains(expectedCase), "The case is not correctly listed!");
          softly.assertAll();
        });

    When(
        "I click on the NEW CONTACT button in in Exposure for Epidemiological data tab in Cases",
        () -> webDriverHelpers.clickOnWebElementBySelector(NEW_CONTACT_BUTTON));

    When(
        "I click on the CHOOSE CASE button in Create new contact form in Exposure for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_CHOOSE_CASE_BUTTON);
          TimeUnit.SECONDS.sleep(5);
        });
    When(
        "I click on SAVE button in create contact form",
        () -> webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_DONE_BUTTON));

    When(
        "I search and chose the last case uuid created via API in the CHOOSE CASE Contact window",
        () -> {
          webDriverHelpers.fillInWebElement(
              SOURCE_CASE_WINDOW_CONTACT, apiState.getCreatedCase().getUuid());
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_WINDOW_SEARCH_CASE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              SOURCE_CASE_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.clickOnWebElementBySelector(
              SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitForRowToBeSelected(SOURCE_CASE_CONTACT_WINDOW_FIRST_RESULT_OPTION);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SOURCE_CASE_CONTACT_WINDOW_CONFIRM_BUTTON);
        });

    When(
        "I am checking if options in checkbox are displayed correctly",
        () -> {
          specificCaseData = collectSpecificData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              specificCaseData,
              epidemiologialDataSavedFromFields,
              List.of(
                  "exposureDetailsKnown",
                  "activityDetailsKnown",
                  "residingAreaWithRisk",
                  "largeOutbreaksArea",
                  "contactsWithSourceCaseKnown"));
        });

    When(
        "I click on New Entry in Exposure Details Known in Cases directory",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
        });
    When(
        "I select from Combobox all options in Type of activity field in Exposure for Epidemiological data tab for Cases",
        () -> {
          String[] ListOfTypeOfActivityExposure =
              TypeOfActivityExposure.ListOfTypeOfActivityExposure;
          for (String value : ListOfTypeOfActivityExposure) {
            webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, value);
          }
        });

    When(
        "I select from Combobox all options in Type of activity field in Exposure for Epidemiological data tab for Cases for DE version",
        () -> {
          String[] ListOfTypeOfActivityExposure =
              TypeOfActivityExposure.ListOfTypeOfActivityExposureDE;
          for (String value : ListOfTypeOfActivityExposure) {
            webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, value);
          }
        });
    When(
        "I select from Combobox all Type of gathering in Exposure for Epidemiological data tab in Cases",
        () -> {
          for (TypeOfGathering value : TypeOfGathering.values()) {
            if (value != TypeOfGathering.valueOf("OTHER")) {
              webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, value.toString());
            }
          }
        });

    When(
        "I select from Combobox all Type of gathering in Exposure for Epidemiological data tab in Cases for DE version",
        () -> {
          for (TypeOfGathering value : TypeOfGathering.values()) {
            if (value != TypeOfGathering.valueOf("OTHER")) {
              webDriverHelpers.selectFromCombobox(
                  TYPE_OF_GATHERING_COMBOBOX, TypeOfGathering.getNameForDE(value.toString()));
            }
          }
        });

    When(
        "I select from Combobox all options in Type of activity field in Activity as Case for Epidemiological data tab for Cases",
        () -> {
          for (ActivityAsCaseType value : ActivityAsCaseType.values()) {
            webDriverHelpers.selectFromCombobox(
                ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX,
                ActivityAsCaseType.getForName(value.getActivityCase()));
          }
        });

    When(
        "I select a type of gathering ([^\"]*) option from Combobox in Exposure for Epidemiological data tab in Cases",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_GATHERING_COMBOBOX, option);
        });
    When(
        "I fill a type of gathering details in Exposure for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_GATHERING_DETAILS, faker.chuckNorris().fact());
        });
    When(
        "I click on save button in Exposure for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });

    When(
        "I click on save button in Activity as Case data tab in Cases",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
        });

    When(
        "I click on {string} option to close Exposure as the probable infection environment case Popup",
        (String option) -> {
          switch (option) {
            case "NO":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPOSURE_ACTION_CANCEL);
              webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_ACTION_CANCEL);
              break;
            case "YES":
              webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPOSURE_ACTION_CONFIRM);
              webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_ACTION_CONFIRM);
              break;
          }
        });
    When(
        "I select a Type of activity ([^\"]*) option in Exposure for Epidemiological data tab in Cases",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX, option);
        });
    When(
        "I fill a Type of activity details field in Exposure for Epidemiological data tab in Cases",
        () -> {
          webDriverHelpers.fillInWebElement(TYPE_OF_ACTIVITY_DETAILS, faker.book().title());
        });

    Then(
        "I create a new Exposure for Epidemiological data tab and fill all the data",
        () -> {
          epidemiologialDataSavedFromFields =
              EpidemiologicalData.builder()
                  .exposureDetailsKnown(YesNoUnknownOptions.valueOf("YES"))
                  .build();
          epidemiologicalData =
              epidemiologicalDataService.buildGeneratedEpidemiologicalData(
                  apiState
                      .getCreatedCase()
                      .getDisease()
                      .equalsIgnoreCase(DiseasesValues.CORONAVIRUS.getDiseaseName()));
          webDriverHelpers.clickWebElementByText(
              EXPOSURE_DETAILS_KNOWN_OPTIONS,
              epidemiologicalData.getExposureDetailsKnown().toString());
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          Exposure exposureData =
              epidemiologicalDataService.buildGeneratedExposureData(
                  apiState.getCreatedCase().getDisease().contains("CORONA"));
          fillExposure(exposureData);
        });

    Then(
        "I create a new Activity from Epidemiological data tab and fill all the data",
        () -> {
          epidemiologialDataSavedFromFields =
              epidemiologialDataSavedFromFields.toBuilder()
                  .activityDetailsKnown(YesNoUnknownOptions.valueOf("YES"))
                  .build();
          EpidemiologicalData epidemiologicalData =
              epidemiologicalDataService.buildGeneratedEpidemiologicalData(
                  apiState.getCreatedCase().getDisease().contains("CORONA"));

          Activity activityData = epidemiologicalDataService.buildGeneratedActivityData();
          webDriverHelpers.clickWebElementByText(
              ACTIVITY_DETAILS_KNOWN_OPTIONS,
              epidemiologicalData.getActivityDetailsKnown().toString());
          webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_DETAILS_NEW_ENTRY_BUTTON);
          fillActivity(activityData);
        });

    And(
        "I click on save button from Epidemiological Data",
        () -> {
          webDriverHelpers.scrollToElementUntilIsVisible(SAVE_BUTTON_EPIDEMIOLOGICAL_DATA);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON_EPIDEMIOLOGICAL_DATA);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I am checking all Exposure data is saved and displayed",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_EXPOSURE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(START_OF_EXPOSURE_INPUT);
          Exposure generatedExposureData =
              epidemiologicalData.getExposures().stream()
                  .findFirst()
                  .orElse(Exposure.builder().build());
          Exposure actualExposureData = collectExposureData();
          ComparisonHelper.compareEqualEntities(generatedExposureData, actualExposureData);
        });

    Then(
        "I click on discard button from Epidemiological Data Exposure popup",
        () -> webDriverHelpers.clickOnWebElementBySelector(DISCARD_BUTTON));

    Then(
        "I open saved activity from Epidemiological Data",
        () -> webDriverHelpers.clickOnWebElementBySelector(OPEN_SAVED_ACTIVITY_BUTTON));

    When(
        "I am checking all Activity data is saved and displayed",
        () -> {
          Activity generatedActivityData =
              epidemiologicalData.getActivities().stream()
                  .findFirst()
                  .orElse(Activity.builder().build());
          Activity actualActivityData = collectActivityData();
          ComparisonHelper.compareEqualEntities(generatedActivityData, actualActivityData);
          webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_DISCARD_BUTTON);
        });

    Then(
        "I create a new Exposure for Epidemiological data",
        () -> {
          epidemiologicalData =
              epidemiologicalDataService.buildGeneratedEpidemiologicalData(
                  apiState
                      .getCreatedCase()
                      .getDisease()
                      .equalsIgnoreCase(DiseasesValues.CORONAVIRUS.getDiseaseName()));
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickWebElementByText(
              EXPOSURE_DETAILS_KNOWN_OPTIONS,
              epidemiologicalData.getExposureDetailsKnown().toString());
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
        });

    Then(
        "I set Type of place as a ([^\"]*) in a new Exposure for Epidemiological data",
        (String facility) -> {
          webDriverHelpers.scrollToElement(TYPE_OF_PLACE_COMBOBOX);
          webDriverHelpers.selectFromCombobox(TYPE_OF_PLACE_COMBOBOX, facility);
        });

    Then(
        "I set Facility Category as a ([^\"]*) in a new Exposure for Epidemiological data",
        (String facilityCategory) -> {
          webDriverHelpers.scrollToElement(FACILITY_CATEGORY_POPUP_COMBOBOX);
          webDriverHelpers.selectFromCombobox(FACILITY_CATEGORY_POPUP_COMBOBOX, facilityCategory);
        });

    Then(
        "I set Facility Type as a ([^\"]*) in a new Exposure for Epidemiological data",
        (String facilityType) -> {
          webDriverHelpers.scrollToElement(FACILITY_TYPE_POPUP_COMBOBOX);
          webDriverHelpers.selectFromCombobox(FACILITY_TYPE_POPUP_COMBOBOX, facilityType);
        });

    When(
        "I check if Facility field has blue exclamation mark and displays correct message",
        () -> {
          webDriverHelpers.hoverToElement(BLUE_ERROR_EXCLAMATION_MARK_EXPOSURE_POPUP);
          String displayedText =
              webDriverHelpers.getTextFromWebElement(
                  BLUE_ERROR_EXCLAMATION_MARK_EXPOSURE_POPUP_TEXT);
          softly.assertEquals(
              "Please define a district in order to select a facility.",
              displayedText,
              "Message is incorrect");
          softly.assertAll();
        });

    When(
        "I check if data is correctly displayed in Exposures table in Epidemiological data tab",
        () -> {
          List<String> values = new ArrayList<>();
          values.add(webDriverHelpers.getTextFromPresentWebElement(TYPE_OF_ACTIVITY_EXPOSURES));
          for (int i = 3; i <= 8; i++) {
            values.add(webDriverHelpers.getTextFromPresentWebElement(getExposureTableData(i)));
          }
          Exposure generatedExposureData =
              epidemiologicalData.getExposures().stream()
                  .findFirst()
                  .orElse(Exposure.builder().build());
          String date[] = values.get(3).split("\\s+");
          softly.assertEquals(
              generatedExposureData.getTypeOfActivity().toString(),
              values.get(0).toUpperCase(),
              "Activities are not equal");
          softly.assertEquals(
              generatedExposureData.getExposureDetailsRole().toString().replace("_", " "),
              values.get(1).toUpperCase(),
              "Exposure descriptions are not equal");
          softly.assertEquals(
              generatedExposureData.getTypeOfPlace().toString(),
              values.get(2).toUpperCase(),
              "Type of places are not equal");
          softly.assertEquals(
              formatter.format(generatedExposureData.getStartOfExposure()),
              date[0],
              "Start of exposure dates are not equal");
          softly.assertEquals(
              formatter.format(generatedExposureData.getEndOfExposure()),
              date[4],
              "End of exposure dates are not equal");
          softly.assertEquals(
              generatedExposureData.getExposureDescription(),
              values.get(5),
              "Exposure descriptions are not equal");
          softly.assertAll();
        });
  }

  private void fillExposure(Exposure exposureData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.fillInWebElement(
        START_OF_EXPOSURE_INPUT, formatter.format(exposureData.getStartOfExposure()));
    webDriverHelpers.fillInWebElement(
        END_OF_EXPOSURE_INPUT, formatter.format(exposureData.getEndOfExposure()));
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION_INPUT, exposureData.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY_COMBOBOX, exposureData.getTypeOfActivity().getActivity());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE_COMBOBOX, exposureData.getExposureDetailsRole().getRole());
    webDriverHelpers.clickWebElementByText(
        RISK_AREA_OPTIONS, exposureData.getRiskArea().toString());
    webDriverHelpers.clickWebElementByText(INDOORS_OPTIONS, exposureData.getIndoors().toString());
    webDriverHelpers.clickWebElementByText(OUTDOORS_OPTIONS, exposureData.getOutdoors().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_MASK_OPTIONS, exposureData.getWearingMask().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_PPE_OPTIONS, exposureData.getWearingPpe().toString());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES_OPTIONS, exposureData.getOtherProtectiveMeasures().toString());
    webDriverHelpers.clickWebElementByText(
        SHORT_DISTANCE_OPTIONS, exposureData.getShortDistance().toString());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT_OPTIONS, exposureData.getLongFaceToFaceContact().toString());
    webDriverHelpers.clickWebElementByText(
        PERCUTANEOUS_OPTIONS, exposureData.getPercutaneous().toString());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS_OPTONS, exposureData.getContactToBodyFluids().toString());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES_OPTIONS, exposureData.getHandlingSamples().toString());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_PLACE_COMBOBOX, exposureData.getTypeOfPlace().getUiValue());
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
  }

  private void fillActivity(Activity activityData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.fillInWebElement(
        ACTIVITY_START_OF_ACTIVITY_INPUT, formatter.format(activityData.getStartOfActivity()));
    webDriverHelpers.fillInWebElement(
        ACTIVITY_END_OF_ACTIVITY_INPUT, formatter.format(activityData.getEndOfActivity()));
    webDriverHelpers.fillInWebElement(ACTIVITY_DESCRIPTION, activityData.getDescription());
    webDriverHelpers.selectFromCombobox(
        ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX, activityData.getTypeOfActivity().getActivityCase());
    webDriverHelpers.selectFromCombobox(ACTIVITY_CONTINENT_COMBOBOX, activityData.getContinent());
    webDriverHelpers.selectFromCombobox(
        ACTIVITY_SUBCONTINENT_COMBOBOX, activityData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(ACTIVITY_COUNTRY_COMBOBOX, activityData.getCountry());

    webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_DONE_BUTTON);
  }

  private Exposure collectExposureData() {
    return Exposure.builder()
        .startOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE_INPUT), formatter))
        .endOfExposure(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE_INPUT), formatter))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION_INPUT))
        .typeOfActivity(
            TypeOfActivityExposure.fromString(
                webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX)))
        .exposureDetailsRole(
            ExposureDetailsRole.fromString(
                webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX)))
        .riskArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA_OPTIONS)))
        .indoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS_OPTIONS)))
        .outdoors(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS_OPTIONS)))
        .wearingMask(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK_OPTIONS)))
        .wearingPpe(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE_OPTIONS)))
        .otherProtectiveMeasures(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    OTHER_PROTECTIVE_MEASURES_OPTIONS)))
        .shortDistance(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE_OPTIONS)))
        .longFaceToFaceContact(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    LONG_FACE_TO_FACE_CONTACT_OPTIONS)))
        .percutaneous(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS_OPTIONS)))
        .contactToBodyFluids(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    CONTACT_TO_BODY_FLUIDS_OPTONS)))
        .handlingSamples(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    HANDLING_SAMPLES_OPTIONS)))
        .typeOfPlace(
            TypeOfPlace.fromString(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX)))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .build();
  }

  private Activity collectActivityData() {
    return Activity.builder()
        .startOfActivity(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(ACTIVITY_START_OF_ACTIVITY_INPUT),
                formatter))
        .endOfActivity(
            LocalDate.parse(
                webDriverHelpers.getValueFromWebElement(ACTIVITY_END_OF_ACTIVITY_INPUT), formatter))
        .description(webDriverHelpers.getValueFromWebElement(ACTIVITY_DESCRIPTION))
        .typeOfActivity(
            ActivityAsCaseType.fromString(
                webDriverHelpers.getValueFromCombobox(ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX)))
        .continent(webDriverHelpers.getValueFromCombobox(ACTIVITY_CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(ACTIVITY_SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(ACTIVITY_COUNTRY_COMBOBOX))
        .build();
  }

  private EpidemiologicalData collectSpecificData() {
    return EpidemiologicalData.builder()
        .activityDetailsKnown(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    ACTIVITY_DETAILS_KNOWN_OPTIONS)))
        .exposureDetailsKnown(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    EXPOSURE_DETAILS_KNOWN_OPTIONS)))
        .residingAreaWithRisk(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS)))
        .largeOutbreaksArea(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS)))
        .contactsWithSourceCaseKnown(
            YesNoUnknownOptions.valueOf(
                webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                    CONTACT_WITH_SOURCE_CASE_KNOWN)))
        .build();
  }
}
