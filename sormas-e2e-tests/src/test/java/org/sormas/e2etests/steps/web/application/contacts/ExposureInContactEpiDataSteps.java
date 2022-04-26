package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_KNOWN_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.LARGE_OUTBREAKS_AREA_CHECKBOX;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.*;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.ExposureDetails;
import org.sormas.e2etests.entities.pojo.web.ExposureInvestigation;
import org.sormas.e2etests.entities.services.ExposureDetailsService;
import org.sormas.e2etests.entities.services.ExposureInvestigationService;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.state.ApiState;

public class ExposureInContactEpiDataSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static ExposureInvestigation exposureInvestigationInput;
  private static ExposureDetails exposureDetailsInput;
  private String EPIDATA_FOR_LAST_CREATED_CONTACT_URL;
  private ExposureDetails exposureDetailsOutput;

  @Inject
  public ExposureInContactEpiDataSteps(
      WebDriverHelpers webDriverHelpers,
      ExposureInvestigationService exposureInvestigationService,
      ExposureDetailsService exposureDetailsService,
      ApiState apiState,
      EnvironmentManager environmentManager) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I am accessing the Epidemiological tab using of created contact via api",
        () -> {
          EPIDATA_FOR_LAST_CREATED_CONTACT_URL =
              environmentManager.getEnvironmentUrlForMarket(locale)
                  + "/sormas-webdriver/#!contacts/epidata/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(EPIDATA_FOR_LAST_CREATED_CONTACT_URL);
          webDriverHelpers.waitForPageLoaded();
        });

    When(
        "I check and fill all data for a new EpiData Exposure",
        () -> {
          exposureInvestigationInput =
              exposureInvestigationService.buildInputExposureInvestigation();
          createExposureInvestigationOnContact(exposureInvestigationInput);

          exposureDetailsInput = exposureDetailsService.buildInputExposureDetails();
          addNewExposureEntry(exposureDetailsInput);
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DATA_SAVED_POPUP);
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_EDIT_BUTTON);
        });

    Then(
        "I am checking all data is saved and displayed on edit Exposure page",
        () -> {
          exposureDetailsOutput = getExposureDetailsOutput();
          ComparisonHelper.compareEqualEntities(exposureDetailsOutput, exposureDetailsInput);
        });
  }

  private void createExposureInvestigationOnContact(
      ExposureInvestigation exposureInvestigationInput) {
    webDriverHelpers.clickWebElementByText(
        EXPOSURE_DETAILS_KNOWN_CHECKBOX, exposureInvestigationInput.getExposureDetailsKnown());
    webDriverHelpers.clickWebElementByText(
        HIGH_TRANSMISSION_RISK_AREA_CHECKBOX,
        exposureInvestigationInput.getHighTransmissionRiskArea());
    webDriverHelpers.clickWebElementByText(
        LARGE_OUTBREAKS_AREA_CHECKBOX, exposureInvestigationInput.getLargeOutbreaksArea());
    if (exposureInvestigationInput.getExposureNewEntry()) {
      webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY_BUTTON);
    }
  }

  private void addNewExposureEntry(ExposureDetails exposureDetailsInput) {
    webDriverHelpers.fillInWebElement(
        START_OF_EXPOSURE_INPUT, exposureDetailsInput.getStartOfExposure());
    webDriverHelpers.fillInWebElement(
        END_OF_EXPOSURE_INPUT, exposureDetailsInput.getEndOfExposure());
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION_INPUT, exposureDetailsInput.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY_COMBOBOX, exposureDetailsInput.getTypeOfActivity());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE_COMBOBOX, exposureDetailsInput.getExposureDetailsRole());
    webDriverHelpers.clickWebElementByText(RISK_AREA_CHECKBOX, exposureDetailsInput.getRiskArea());
    webDriverHelpers.clickWebElementByText(INDOORS_CHECKBOX, exposureDetailsInput.getIndoors());
    webDriverHelpers.clickWebElementByText(OUTDOORS_CHECKBOX, exposureDetailsInput.getOutdoors());
    webDriverHelpers.clickWebElementByText(
        WEARING_MASK_CHECKBOX, exposureDetailsInput.getWearingMask());
    webDriverHelpers.clickWebElementByText(
        WEARING_PPE_CHECKBOX, exposureDetailsInput.getWearingPpe());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES_CHECKBOX, exposureDetailsInput.getOtherProtectiveMeasures());
    webDriverHelpers.clickWebElementByText(
        SHORT_DISTANCE_CHECKBOX, exposureDetailsInput.getShortDistance());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT_CHECKBOX, exposureDetailsInput.getLongFaceToFaceContact());
    webDriverHelpers.clickWebElementByText(
        ANIMAL_MARKET_CHECKBOX, exposureDetailsInput.getAnimalMarket());
    webDriverHelpers.clickWebElementByText(
        PERCUTANEOUS_CHECKBOX, exposureDetailsInput.getPercutaneous());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS_CHECKBOX, exposureDetailsInput.getContactToBodyFluids());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES_CHECKBOX, exposureDetailsInput.getHandlingSamples());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_PLACE_COMBOBOX, exposureDetailsInput.getTypeOfPlace());
    webDriverHelpers.selectFromCombobox(CONTINENT_COMBOBOX, exposureDetailsInput.getContinent());
    webDriverHelpers.selectFromCombobox(
        SUBCONTINENT_COMBOBOX, exposureDetailsInput.getSubcontinent());
    webDriverHelpers.selectFromCombobox(COUNTRY_COMBOBOX, exposureDetailsInput.getCountry());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_REGION_COMBOBOX, exposureDetailsInput.getExposureRegion());
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX, exposureDetailsInput.getDistrict());
    webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, exposureDetailsInput.getCommunity());
    webDriverHelpers.fillInWebElement(STREET_INPUT, exposureDetailsInput.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER_INPUT, exposureDetailsInput.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION_INPUT, exposureDetailsInput.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE_INPUT, exposureDetailsInput.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY_INPUT, exposureDetailsInput.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureDetailsInput.getAreaType());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureDetailsInput.getGpsLatitude());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE_INPUT, exposureDetailsInput.getGpsLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE_INPUT, exposureDetailsInput.getGpsLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY_INPUT, exposureDetailsInput.getGpsAccuracy());
  }

  private ExposureDetails getExposureDetailsOutput() {
    return ExposureDetails.builder()
        .startOfExposure(
            webDriverHelpers
                .getValueFromWebElement(START_OF_EXPOSURE_INPUT)
                .replaceFirst("^0+(?!$)", ""))
        .endOfExposure(
            webDriverHelpers
                .getValueFromWebElement(END_OF_EXPOSURE_INPUT)
                .replaceFirst("^0+(?!$)", ""))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION_INPUT))
        .typeOfActivity(webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX))
        .exposureDetailsRole(webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX))
        .riskArea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA_CHECKBOX))
        .indoors(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS_CHECKBOX))
        .outdoors(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS_CHECKBOX))
        .wearingMask(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK_CHECKBOX))
        .wearingPpe(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE_CHECKBOX))
        .otherProtectiveMeasures(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                OTHER_PROTECTIVE_MEASURES_CHECKBOX))
        .shortDistance(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE_CHECKBOX))
        .longFaceToFaceContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                LONG_FACE_TO_FACE_CONTACT_CHECKBOX))
        .animalMarket(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ANIMAL_MARKET_CHECKBOX))
        .percutaneous(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS_CHECKBOX))
        .contactToBodyFluids(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(
                CONTACT_TO_BODY_FLUIDS_CHECKBOX))
        .handlingSamples(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HANDLING_SAMPLES_CHECKBOX))
        .typeOfPlace(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .exposureRegion(webDriverHelpers.getValueFromCombobox(EXPOSURE_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .street(webDriverHelpers.getValueFromWebElement(STREET_INPUT))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER_INPUT))
        .additionalInformation(
            webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION_INPUT))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE_INPUT))
        .city(webDriverHelpers.getValueFromWebElement(CITY_INPUT))
        .areaType(webDriverHelpers.getValueFromCombobox(AREA_TYPE_COMBOBOX))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(GPS_LATITUDE_INPUT))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(GPS_LONGITUDE_INPUT))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(GPS_ACCURACY_INPUT))
        .build();
  }
}
