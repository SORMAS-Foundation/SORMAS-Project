package org.sormas.e2etests.steps.web;

import com.google.common.truth.Truth;
import cucumber.api.java.en.When;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.enums.YesNoUnknownOptions;
import org.sormas.e2etests.enums.cases.epidemiologicalData.ExposureDetailsRole;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityActivity;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfActivityExposure;
import org.sormas.e2etests.enums.cases.epidemiologicalData.TypeOfPlace;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.pages.application.cases.EpidemiologicalDataCasePage;
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
        "I am accessing the Epidemiological data tab of the created case",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              NavBarPage.SAMPLE_BUTTON);
          String uuid = apiState.getCreatedCase().getUuid();
          webDriverHelpers.accessWebSite(environmentUrl + "/sormas-ui/#!cases/epidata/" + uuid);
        });

    Then(
        "I create a new Exposure and fill all the data",
        () -> {
          epidemiologicalData = epidemiologicalDataService.buildGeneratedEpidemiologicalData();
          webDriverHelpers.waitForPageLoaded();
          webDriverHelpers.clickWebElementByText(
              EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS,
              epidemiologicalData.getExposureDetailsKnown().toString());
          Thread.sleep(2000);
          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
          Thread.sleep(2000);
          Exposure exposureData = epidemiologicalDataService.buildGeneratedExposureData();
          fillExposure(exposureData);
        });

    Then(
        "I create a new Activity and fill all the data",
        () -> {
          EpidemiologicalData epidemiologicalData;
          epidemiologicalData = epidemiologicalDataService.buildGeneratedEpidemiologicalData();

          Activity activityData = epidemiologicalDataService.buildGeneratedActivityData();
          webDriverHelpers.clickWebElementByText(
              EpidemiologicalDataCasePage.ACTIVITY_DETAILS_KNOWN,
              epidemiologicalData.getActivityDetailsKnown().toString());
          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.ACTIVITY_DETAILS_NEW_ENTRY);
          fillActivity(activityData);
        });

    And(
        "I click on save",
        () -> {
          webDriverHelpers.scrollToElementUntilIsVisible(
              EpidemiologicalDataCasePage.SAVE_BUTTON_EPIDEMIOLOGICAL_DATA);

          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.SAVE_BUTTON_EPIDEMIOLOGICAL_DATA);
        });

    When(
        "I am checking all Exposure data is saved and displayed",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.OPEN_SAVED_EXPOSURE_BUTTON);
          Exposure generatedExposureData =
              epidemiologicalData.getExposures().stream()
                  .findFirst()
                  .orElse(Exposure.builder().build());
          Exposure actualExposureData = collectExposureData();
          Truth.assertThat(generatedExposureData).isEqualTo(actualExposureData);
          webDriverHelpers.clickOnWebElementBySelector(EpidemiologicalDataCasePage.DISCARD_BUTTON);
        });

    When(
        "I am checking all Activity data is saved and displayed",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.OPEN_SAVED_ACTIVITY_BUTTON);
          Activity generatedActivityData =
              epidemiologicalData.getActivities().stream()
                  .findFirst()
                  .orElse(Activity.builder().build());
          Activity actualActivityData = collectActivityData();
          Truth.assertThat(generatedActivityData).isEqualTo(actualActivityData);
          webDriverHelpers.clickOnWebElementBySelector(
              EpidemiologicalDataCasePage.ACTIVITY_DISCARD_BUTTON);
        });
  }

  public void fillExposure(Exposure exposureData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT,
        formatter.format(exposureData.getStartOfExposure()));
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT,
        formatter.format(exposureData.getEndOfExposure()));
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT,
        exposureData.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX,
        exposureData.getTypeOfActivity().getActivity());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX,
        exposureData.getExposureDetailsRole().getRole());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.RISK_AREA_OPTIONS, exposureData.getRiskArea().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.INDOORS_OPTIONS, exposureData.getIndoors().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.OUTDOORS_OPTIONS, exposureData.getOutdoors().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS, exposureData.getWearingMask().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS, exposureData.getWearingPpe().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS,
        exposureData.getOtherProtectiveMeasures().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS,
        exposureData.getShortDistance().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS,
        exposureData.getLongFaceToFaceContact().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.ANIMAL_MARKET_OPTIONS,
        exposureData.getAnimalMarket().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS,
        exposureData.getPercutaneous().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS,
        exposureData.getContactToBodyFluids().toString());
    webDriverHelpers.clickWebElementByText(
        EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS,
        exposureData.getHandlingSamples().toString());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX,
        exposureData.getTypeOfPlace().getPlace());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.CONTINENT_COMBOBOX, exposureData.getContinent());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX, exposureData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.COUNTRY_COMBOBOX, exposureData.getCountry());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.STREET_INPUT, exposureData.getStreet());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.HOUSE_NUMBER_INPUT, exposureData.getHouseNumber());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.POSTAL_CODE_INPUT, exposureData.getPostalCode());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.CITY_INPUT, exposureData.getCity());
    webDriverHelpers.clickOnWebElementBySelector(EpidemiologicalDataCasePage.DONE_BUTTON);
  }

  public void fillActivity(Activity activityData) {
    webDriverHelpers.waitForPageLoaded();
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.ACTIVITY_START_OF_ACTIVITY,
        formatter.format(activityData.getStartOfActivity()));
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.ACTIVITY_END_OF_ACTIVITY,
        formatter.format(activityData.getEndOfActivity()));
    webDriverHelpers.fillInWebElement(
        EpidemiologicalDataCasePage.ACTIVITY_DESCRIPTION, activityData.getDescription());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX,
        activityData.getTypeOfActivity().getActivityCase());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.ACTIVITY_FACILITY_COMBOBOX,
    //        activityData.getTypeOfPlace().getPlace());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.ACTIVITY_CONTINENT_COMBOBOX, activityData.getContinent());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.ACTIVITY_SUBCONTINENT_COMBOBOX, activityData.getSubcontinent());
    webDriverHelpers.selectFromCombobox(
        EpidemiologicalDataCasePage.ACTIVITY_COUNTRY_COMBOBOX, activityData.getCountry());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.ACTIVITY_STREET_INPUT, activityData.getStreet());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.ACTIVITY_HOUSE_NUMBER_INPUT,
    // activityData.getHouseNumber());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.AcC_POSTAL_CODE, activityData.getPostalCode());
    //    webDriverHelpers.selectFromCombobox(
    //        EpidemiologicalDataCasePage.ACTIVITY_CITY_INPUT, activityData.getCity());
    webDriverHelpers.clickOnWebElementBySelector(EpidemiologicalDataCasePage.ACTIVITY_DONE_BUTTON);
  }

  public EpidemiologicalData collectEpidemiologicalData() {
    List<Exposure> exposures = new ArrayList<Exposure>();
    exposures.add(collectExposureData());
    List<Activity> activities = new ArrayList<Activity>();
    activities.add(collectActivityData());

    return EpidemiologicalData.builder()
        .exposures(exposures)
        .activities(activities)
        .exposureDetailsKnown(getExposureDetailsKnown())
        .activityDetailsKnown(getActivityDetailsKnown())
        .residingAreaWithRisk(getResidingAreaWithRisk())
        .largeOutbreaksArea(getLargeOutbreaksArea())
        .contactsWithSourceCaseKnown(getContactsWithSource())
        .build();
  }

  public YesNoUnknownOptions getActivityDetailsKnown() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getExposureDetailsKnown() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.EXPOSURE_DETAILS_KNOWN_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getResidingAreaWithRisk() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.RESIDING_AREA_WITH_RISK);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getLargeOutbreaksArea() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.LARGE_OUTBREAKS_AREA);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getContactsWithSource() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.CONTACTS_WITH_SOURCE_CASE_KNOWN);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public Exposure collectExposureData() {
    return Exposure.builder()
        .startOfExposure(getStartOfExposure())
        .endOfExposure(getEndOfExposure())
        .exposureDescription(getExposureDescription())
        .typeOfActivity(getExposureTypeOfActivity())
        .exposureDetailsRole(getExposureRole())
        .riskArea(getRiskArea())
        .indoors(getIndoors())
        .outdoors(getOutdoors())
        .wearingMask(getWearingMask())
        .wearingPpe(getWearingPPE())
        .otherProtectiveMeasures(getOtherProtectiveMeasuresc())
        .shortDistance(getShortDistance())
        .longFaceToFaceContact(getFaceToFaceContact())
        .animalMarket(getAnimalMarket())
        .percutaneous(getPercutaneous())
        .contactToBodyFluids(getContactToBlood())
        .handlingSamples(getHandlingSamples())
        .typeOfPlace(getTypeOfPlace())
        .continent(getContinent())
        .subcontinent(getSubContinent())
        .country(getCountry())
        .build();
  }

  public Activity collectActivityData() {
    return Activity.builder()
        .startOfActivity(getStartOfActivity())
        .endOfActivity(getEndOfActivity())
        .description(getActivityDescription())
        .typeOfActivity(getActivityTypeOfActivity())
        .continent(getContinentActivity())
        .subcontinent(getSubContinentActivity())
        .country(getCountryActivity())
        .build();
  }

  public String getContinentActivity() {
    String Text =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.ACTIVITY_CONTINENT_COMBOBOX);
    return Text;
  }

  public String getSubContinentActivity() {
    String Text =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.ACTIVITY_SUBCONTINENT_COMBOBOX);
    return Text;
  }

  public String getCountryActivity() {
    String Text =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.ACTIVITY_COUNTRY_COMBOBOX);
    return Text;
  }

  public TypeOfActivityActivity getActivityTypeOfActivity() {

    String A =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX);
    return TypeOfActivityActivity.fromString(A);
  }

  public String getActivityDescription() {
    return webDriverHelpers.getValueFromWebElement(
        EpidemiologicalDataCasePage.ACTIVITY_DESCRIPTION);
  }

  public LocalDate getEndOfActivity() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(
            EpidemiologicalDataCasePage.ACTIVITY_END_OF_ACTIVITY),
        formatter);
  }

  public LocalDate getStartOfActivity() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(
            EpidemiologicalDataCasePage.ACTIVITY_START_OF_ACTIVITY),
        formatter);
  }

  public LocalDate getStartOfExposure() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(
            EpidemiologicalDataCasePage.START_OF_EXPOSURE_INPUT),
        formatter);
  }

  public LocalDate getEndOfExposure() {
    return LocalDate.parse(
        webDriverHelpers.getValueFromWebElement(EpidemiologicalDataCasePage.END_OF_EXPOSURE_INPUT),
        formatter);
  }

  public String getExposureDescription() {
    return webDriverHelpers.getValueFromWebElement(
        EpidemiologicalDataCasePage.EXPOSURE_DESCRIPTION_INPUT);
  }

  public TypeOfActivityExposure getExposureTypeOfActivity() {
    String A =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.TYPE_OF_ACTIVITY_COMBOBOX);
    return TypeOfActivityExposure.fromString(A);
  }

  public ExposureDetailsRole getExposureRole() {
    String A =
        webDriverHelpers.getValueFromCombobox(
            EpidemiologicalDataCasePage.EXPOSURE_DETAILS_ROLE_COMBOBOX);
    return ExposureDetailsRole.fromString(A);
  }

  public YesNoUnknownOptions getRiskArea() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.RISK_AREA_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getIndoors() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.INDOORS_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getOutdoors() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.OUTDOORS_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getWearingMask() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.WEARING_MASK_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getWearingPPE() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.WEARING_PPE_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getOtherProtectiveMeasuresc() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.OTHER_PROTECTIVE_MEASURES_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getShortDistance() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.SHORT_DISTANCE_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getFaceToFaceContact() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.LONG_FACE_TO_FACE_CONTACT_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getAnimalMarket() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.ANIMAL_MARKET_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getPercutaneous() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.PERCUTANEOUS_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getContactToBlood() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.CONTACT_TO_BODY_FLUIDS_OPTONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public YesNoUnknownOptions getHandlingSamples() {

    String Text =
        webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
            EpidemiologicalDataCasePage.HANDLING_SAMPLES_OPTIONS);
    return YesNoUnknownOptions.valueOf(Text);
  }

  public TypeOfPlace getTypeOfPlace() {
    String Text =
        webDriverHelpers.getValueFromCombobox(EpidemiologicalDataCasePage.TYPE_OF_PLACE_COMBOBOX);
    return TypeOfPlace.fromString(Text);
  }

  public String getContinent() {
    String Text =
        webDriverHelpers.getValueFromCombobox(EpidemiologicalDataCasePage.CONTINENT_COMBOBOX);
    return Text;
  }

  public String getSubContinent() {
    String Text =
        webDriverHelpers.getValueFromCombobox(EpidemiologicalDataCasePage.SUBCONTINENT_COMBOBOX);
    return Text;
  }

  public String getCountry() {
    String Text =
        webDriverHelpers.getValueFromCombobox(EpidemiologicalDataCasePage.COUNTRY_COMBOBOX);
    return Text;
  }
}
