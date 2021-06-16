package org.sormas.e2etests.steps.web.application.contacts;

import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.*;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_KNOWN;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.EXPOSURE_DETAILS_NEW_ENTRY;
import static org.sormas.e2etests.pages.application.contacts.EditEpidemiologicalDataContactPage.LARGE_OUTBREAKS_AREA;
import static org.sormas.e2etests.pages.application.contacts.ExposureNewEntryPage.*;

import cucumber.api.java8.En;
import javax.inject.Inject;
import javax.inject.Named;
import org.assertj.core.api.SoftAssertions;
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
          webDriverHelpers.waitForPageLoaded();
          EPIDATA_FOR_LAST_CREATED_CONTACT_URL =
              environmentUrl
                  + "/sormas-ui/#!contacts/epidata/"
                  + apiState.getCreatedContact().getUuid();
          webDriverHelpers.accessWebSite(EPIDATA_FOR_LAST_CREATED_CONTACT_URL);
          Thread.sleep(5000);
        });

    When(
        "I check and fill all data",
        () -> {
          exposureInvestigationInput =
              exposureInvestigationService.buildInputExposureInvestigation();
          exposureIvestigationOnContact(exposureInvestigationInput);

          exposureDetailsInput = exposureDetailsService.buildInputExposureDetails();
          addNewExposureEntry(exposureDetailsInput);
          webDriverHelpers.clickOnWebElementBySelector(DONE_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          webDriverHelpers.clickOnWebElementBySelector(CONTACT_DATA_SAVED_POPUP);
        });

    Then(
        "I am checking all data is saved and displayed on edit Exposure page",
        () -> {
          ExposureDetails exposureDetailsOutput = getExposureDetailsOutput();
          SoftAssertions softly = new SoftAssertions();
          softly
              .assertThat(exposureDetailsOutput.getStartOfExposure())
              .isEqualTo(exposureDetailsInput.getStartOfExposure());
          softly
              .assertThat(exposureDetailsOutput.getEndOfExposure())
              .isEqualTo(exposureDetailsInput.getEndOfExposure());
          softly
              .assertThat(exposureDetailsOutput.getExposureDescription())
              .isEqualToIgnoringCase(exposureDetailsInput.getExposureDescription());
          //          softly
          //              .assertThat(exposureDetailsOutput.getTypeOfActivity())
          //              .isEqualToIgnoringCase(exposureDetailsInput.getTypeOfActivity());
          //          softly
          //              .assertThat(exposureDetailsOutput.getExposureDetailsRole())
          //              .isEqualToIgnoringCase(exposureDetailsInput.getExposureDetailsRole());
          softly
              .assertThat(exposureDetailsOutput.getRiskArea())
              .isEqualToIgnoringCase(exposureDetailsInput.getRiskArea());
          softly
              .assertThat(exposureDetailsOutput.getIndoors())
              .isEqualToIgnoringCase(exposureDetailsInput.getIndoors());
          softly
              .assertThat(exposureDetailsOutput.getOutdoors())
              .isEqualToIgnoringCase(exposureDetailsInput.getOutdoors());
          softly
              .assertThat(exposureDetailsOutput.getWearingMask())
              .isEqualToIgnoringCase(exposureDetailsInput.getWearingMask());
          softly
              .assertThat(exposureDetailsOutput.getWearingPpe())
              .isEqualToIgnoringCase(exposureDetailsInput.getWearingPpe());
          softly
              .assertThat(exposureDetailsOutput.getOtherProtectiveMeasures())
              .isEqualToIgnoringCase(exposureDetailsInput.getOtherProtectiveMeasures());
          softly
              .assertThat(exposureDetailsOutput.getShortDistance())
              .isEqualToIgnoringCase(exposureDetailsInput.getShortDistance());
          softly
              .assertThat(exposureDetailsOutput.getLongFaceToFaceContact())
              .isEqualToIgnoringCase(exposureDetailsInput.getLongFaceToFaceContact());
          softly
              .assertThat(exposureDetailsOutput.getAnimalMarket())
              .isEqualToIgnoringCase(exposureDetailsInput.getAnimalMarket());
          softly
              .assertThat(exposureDetailsOutput.getPercutaneous())
              .isEqualToIgnoringCase(exposureDetailsInput.getPercutaneous());
          softly
              .assertThat(exposureDetailsOutput.getContactToBodyFluids())
              .isEqualToIgnoringCase(exposureDetailsInput.getContactToBodyFluids());
          softly
              .assertThat(exposureDetailsOutput.getHandlingSamples())
              .isEqualToIgnoringCase(exposureDetailsInput.getHandlingSamples());
          softly
              .assertThat(exposureDetailsOutput.getTypeOfPlace())
              .isEqualToIgnoringCase(exposureDetailsInput.getTypeOfPlace());
          softly
              .assertThat(exposureDetailsOutput.getContinent())
              .isEqualToIgnoringCase(exposureDetailsInput.getContinent());
          softly
              .assertThat(exposureDetailsOutput.getSubcontinent())
              .isEqualToIgnoringCase(exposureDetailsInput.getSubcontinent());
          softly
              .assertThat(exposureDetailsOutput.getCountry())
              .isEqualToIgnoringCase(exposureDetailsInput.getCountry());
          softly
              .assertThat(exposureDetailsOutput.getExposureRegion())
              .isEqualToIgnoringCase(exposureDetailsInput.getExposureRegion());
          softly
              .assertThat(exposureDetailsOutput.getDistrict())
              .isEqualToIgnoringCase(exposureDetailsInput.getDistrict());
          softly
              .assertThat(exposureDetailsOutput.getCommunity())
              .isEqualToIgnoringCase(exposureDetailsInput.getCommunity());
          softly
              .assertThat(exposureDetailsOutput.getStreet())
              .isEqualToIgnoringCase(exposureDetailsInput.getStreet());
          softly
              .assertThat(exposureDetailsOutput.getHouseNumber())
              .isEqualToIgnoringCase(exposureDetailsInput.getHouseNumber());
          softly
              .assertThat(exposureDetailsOutput.getAdditionalInformation())
              .isEqualToIgnoringCase(exposureDetailsInput.getAdditionalInformation());
          softly
              .assertThat(exposureDetailsOutput.getPostalCode())
              .isEqualToIgnoringCase(exposureDetailsInput.getPostalCode());
          softly
              .assertThat(exposureDetailsOutput.getCity())
              .isEqualToIgnoringCase(exposureDetailsInput.getCity());
          softly
              .assertThat(exposureDetailsOutput.getAreaType())
              .isEqualToIgnoringCase(exposureDetailsInput.getAreaType());
          softly
              .assertThat(exposureDetailsOutput.getCommunityContactPerson())
              .isEqualToIgnoringCase(exposureDetailsInput.getCommunityContactPerson());
          softly
              .assertThat(exposureDetailsOutput.getGpsLatitude())
              .isEqualToIgnoringCase(exposureDetailsInput.getGpsLatitude());
          softly
              .assertThat(exposureDetailsOutput.getGpsLongitude())
              .isEqualToIgnoringCase(exposureDetailsInput.getGpsLongitude());
          softly
              .assertThat(exposureDetailsOutput.getGpsAccuracy())
              .isEqualToIgnoringCase(exposureDetailsInput.getGpsAccuracy());
        });
  }

  public void exposureIvestigationOnContact(ExposureInvestigation exposureInvestigationInput) {
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
        .startOfExposure(webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE))
        .endOfExposure(webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE))
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
