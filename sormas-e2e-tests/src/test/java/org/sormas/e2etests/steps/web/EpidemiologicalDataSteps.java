package org.sormas.e2etests.steps.web;

import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.enums.DiseasesValues;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ActivityAsCaseType;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.EpidemiologicalData;
import org.sormas.e2etests.pojo.web.epidemiologicalData.Activity;
import org.sormas.e2etests.pojo.web.epidemiologicalData.Exposure;
import org.sormas.e2etests.services.EpidemiologicalDataService;
import org.sormas.e2etests.state.ApiState;

public class EpidemiologicalDataSteps implements En {

  final WebDriverHelpers webDriverHelpers;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
  private static EpidemiologicalData epidemiologicalData;

  @Inject
  public EpidemiologicalDataSteps(
      WebDriverHelpers webDriverHelpers,
      ApiState apiState,
      EpidemiologicalDataService epidemiologicalDataService,
      @Named("ENVIRONMENT_URL") String environmentUrl)
      throws InterruptedException {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I am accessing via URL the Epidemiological data tab of the created case",
        () -> {
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + "/sormas-ui/#!cases/epidata/" + uuid);
        });

    Then(
        "I create a new Exposure fro Epidemiological data tab and fill all the data",
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
          Truth.assertThat(generatedExposureData).isEqualTo(actualExposureData);
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
          Truth.assertThat(generatedActivityData).isEqualTo(actualActivityData);
          webDriverHelpers.clickOnWebElementBySelector(ACTIVITY_DISCARD_BUTTON);
        });
  }

  public void fillExposure(Exposure exposureData) {
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
        TYPE_OF_PLACE_COMBOBOX, exposureData.getTypeOfPlace().getPlace());
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureData.getCountry());
    webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
  }

  public void fillActivity(Activity activityData) {
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

  public Exposure collectExposureData() {
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

  public Activity collectActivityData() {
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
}
