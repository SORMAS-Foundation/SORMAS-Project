package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_KNOWN;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_NEW_ENTRY;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.LARGE_OUTBREAKS_AREA;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.*;

import com.google.common.truth.Truth;
import cucumber.api.java8.En;
import javax.inject.Inject;
import javax.inject.Named;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pojo.web.ExposureDetails;
import org.sormas.e2etests.pojo.web.ExposureInvestigation;
import org.sormas.e2etests.services.ExposureDetailsService;
import org.sormas.e2etests.services.ExposureInvestigationService;
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
      @Named("ENVIRONMENT_URL") String environmentUrl) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I am accessing the Epidemiological tab using of created contact via api",
        () -> {
          EPIDATA_FOR_LAST_CREATED_CONTACT_URL =
              environmentUrl
                  + "/sormas-ui/#!contacts/epidata/"
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

          Truth.assertThat(exposureDetailsOutput).isEqualTo(exposureDetailsInput);
        });
  }

  public void createExposureInvestigationOnContact(
      ExposureInvestigation exposureInvestigationInput) {
    webDriverHelpers.clickWebElementByText(
        EXPOSURE_DETAILS_KNOWN, exposureInvestigationInput.getExposureDetailsKnown());
    webDriverHelpers.clickWebElementByText(
        HIGH_TRANSMISSION_RISK_AREA, exposureInvestigationInput.getHighTransmissionRiskArea());
    webDriverHelpers.clickWebElementByText(
        LARGE_OUTBREAKS_AREA, exposureInvestigationInput.getLargeOutbreaksArea());
    if (exposureInvestigationInput.getExposureNewEntry()) {
      webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY);
    }
  }

  public void addNewExposureEntry(ExposureDetails exposureDetailsInput) {
    webDriverHelpers.fillInWebElement(START_OF_EXPOSURE, exposureDetailsInput.getStartOfExposure());
    webDriverHelpers.fillInWebElement(END_OF_EXPOSURE, exposureDetailsInput.getEndOfExposure());
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION, exposureDetailsInput.getExposureDescription());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY_COMBOBOX, exposureDetailsInput.getTypeOfActivity());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE_COMBOBOX, exposureDetailsInput.getExposureDetailsRole());
    webDriverHelpers.clickWebElementByText(RISK_AREA, exposureDetailsInput.getRiskArea());
    webDriverHelpers.clickWebElementByText(INDOORS, exposureDetailsInput.getIndoors());
    webDriverHelpers.clickWebElementByText(OUTDOORS, exposureDetailsInput.getOutdoors());
    webDriverHelpers.clickWebElementByText(WEARING_MASK, exposureDetailsInput.getWearingMask());
    webDriverHelpers.clickWebElementByText(WEARING_PPE, exposureDetailsInput.getWearingPpe());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES, exposureDetailsInput.getOtherProtectiveMeasures());
    webDriverHelpers.clickWebElementByText(SHORT_DISTANCE, exposureDetailsInput.getShortDistance());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT, exposureDetailsInput.getLongFaceToFaceContact());
    webDriverHelpers.clickWebElementByText(ANIMAL_MARKET, exposureDetailsInput.getAnimalMarket());
    webDriverHelpers.clickWebElementByText(PERCUTANEOUS, exposureDetailsInput.getPercutaneous());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS, exposureDetailsInput.getContactToBodyFluids());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES, exposureDetailsInput.getHandlingSamples());
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
    webDriverHelpers.fillInWebElement(STREET, exposureDetailsInput.getStreet());
    webDriverHelpers.fillInWebElement(HOUSE_NUMBER, exposureDetailsInput.getHouseNumber());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION, exposureDetailsInput.getAdditionalInformation());
    webDriverHelpers.fillInWebElement(POSTAL_CODE, exposureDetailsInput.getPostalCode());
    webDriverHelpers.fillInWebElement(CITY, exposureDetailsInput.getCity());
    webDriverHelpers.selectFromCombobox(AREA_TYPE_COMBOBOX, exposureDetailsInput.getAreaType());
    webDriverHelpers.fillInWebElement(
        COMMUNITY_CONTACT_PERSON, exposureDetailsInput.getCommunityContactPerson());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE, exposureDetailsInput.getGpsLatitude());
    webDriverHelpers.fillInWebElement(GPS_LATITUDE, exposureDetailsInput.getGpsLatitude());
    webDriverHelpers.fillInWebElement(GPS_LONGITUDE, exposureDetailsInput.getGpsLongitude());
    webDriverHelpers.fillInWebElement(GPS_ACCURACY, exposureDetailsInput.getGpsAccuracy());
  }

  public ExposureDetails getExposureDetailsOutput() {
    return ExposureDetails.builder()
        // new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date).replaceAll("0$", "");
        .startOfExposure(
            webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE).replaceFirst("^0+(?!$)", ""))
        .endOfExposure(
            webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE).replaceFirst("^0+(?!$)", ""))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION))
        .typeOfActivity(webDriverHelpers.getValueFromCombobox(TYPE_OF_ACTIVITY_COMBOBOX))
        .exposureDetailsRole(webDriverHelpers.getValueFromCombobox(EXPOSURE_DETAILS_ROLE_COMBOBOX))
        .riskArea(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(RISK_AREA))
        .indoors(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(INDOORS))
        .outdoors(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OUTDOORS))
        .wearingMask(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_MASK))
        .wearingPpe(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(WEARING_PPE))
        .otherProtectiveMeasures(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(OTHER_PROTECTIVE_MEASURES))
        .shortDistance(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(SHORT_DISTANCE))
        .longFaceToFaceContact(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(LONG_FACE_TO_FACE_CONTACT))
        .animalMarket(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(ANIMAL_MARKET))
        .percutaneous(webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(PERCUTANEOUS))
        .contactToBodyFluids(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(CONTACT_TO_BODY_FLUIDS))
        .handlingSamples(
            webDriverHelpers.getCheckedOptionFromHorizontalOptionGroup(HANDLING_SAMPLES))
        .typeOfPlace(webDriverHelpers.getValueFromCombobox(TYPE_OF_PLACE_COMBOBOX))
        .continent(webDriverHelpers.getValueFromCombobox(CONTINENT_COMBOBOX))
        .subcontinent(webDriverHelpers.getValueFromCombobox(SUBCONTINENT_COMBOBOX))
        .country(webDriverHelpers.getValueFromCombobox(COUNTRY_COMBOBOX))
        .exposureRegion(webDriverHelpers.getValueFromCombobox(EXPOSURE_REGION_COMBOBOX))
        .district(webDriverHelpers.getValueFromCombobox(DISTRICT_COMBOBOX))
        .community(webDriverHelpers.getValueFromCombobox(COMMUNITY_COMBOBOX))
        .street(webDriverHelpers.getValueFromWebElement(STREET))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER))
        .additionalInformation(webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE))
        .city(webDriverHelpers.getValueFromWebElement(CITY))
        .areaType(webDriverHelpers.getValueFromCombobox(AREA_TYPE_COMBOBOX))
        .communityContactPerson(webDriverHelpers.getValueFromWebElement(COMMUNITY_CONTACT_PERSON))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(GPS_LATITUDE))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(GPS_LONGITUDE))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(GPS_ACCURACY))
        .build();
  }
}
