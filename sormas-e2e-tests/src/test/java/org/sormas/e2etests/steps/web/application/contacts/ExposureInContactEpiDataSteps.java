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
          Thread.sleep(5000);
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
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(EXPOSURE_EDIT);
          webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_EDIT);
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
          softly
              .assertThat(exposureDetailsOutput.getTypeOfActivity())
              .isEqualToIgnoringCase(exposureDetailsInput.getTypeOfActivity());
          softly
              .assertThat(exposureDetailsOutput.getExposureDetailsRole())
              .isEqualToIgnoringCase(exposureDetailsInput.getExposureDetailsRole());
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
        EXPOSURE_DETAILS_KNOWN, exposureInvestigationInput.getExposureDetailsKnown().toString());
    webDriverHelpers.clickWebElementByText(
        HIGH_TRANSMISSION_RISK_AREA,
        exposureInvestigationInput.getHighTransmissionRiskArea().toString());
    webDriverHelpers.clickWebElementByText(
        LARGE_OUTBREAKS_AREA, exposureInvestigationInput.getLargeOutbreaksArea());
    if (exposureInvestigationInput.getExposureNewEntry()) {
      webDriverHelpers.clickOnWebElementBySelector(EXPOSURE_DETAILS_NEW_ENTRY);
    }
  }

  public void addNewExposureEntry(ExposureDetails exposureDetailsInput)
      throws InterruptedException {
    webDriverHelpers.fillInWebElement(
        START_OF_EXPOSURE, exposureDetailsInput.getStartOfExposure().toString());
    webDriverHelpers.fillInWebElement(
        END_OF_EXPOSURE, exposureDetailsInput.getEndOfExposure().toString());
    webDriverHelpers.fillInWebElement(
        EXPOSURE_DESCRIPTION, exposureDetailsInput.getExposureDescription().toString());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_ACTIVITY, exposureDetailsInput.getTypeOfActivity().toString());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_DETAILS_ROLE, exposureDetailsInput.getExposureDetailsRole().toString());
    webDriverHelpers.clickWebElementByText(
        RISK_AREA, exposureDetailsInput.getRiskArea().toString());
    webDriverHelpers.clickWebElementByText(INDOORS, exposureDetailsInput.getIndoors().toString());
    webDriverHelpers.clickWebElementByText(OUTDOORS, exposureDetailsInput.getOutdoors().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_MASK, exposureDetailsInput.getWearingMask().toString());
    webDriverHelpers.clickWebElementByText(
        WEARING_PPE, exposureDetailsInput.getWearingPpe().toString());
    webDriverHelpers.clickWebElementByText(
        OTHER_PROTECTIVE_MEASURES, exposureDetailsInput.getOtherProtectiveMeasures().toString());
    webDriverHelpers.clickWebElementByText(
        SHORT_DISTANCE, exposureDetailsInput.getShortDistance().toString());
    webDriverHelpers.clickWebElementByText(
        LONG_FACE_TO_FACE_CONTACT, exposureDetailsInput.getLongFaceToFaceContact().toString());
    webDriverHelpers.clickWebElementByText(
        ANIMAL_MARKET, exposureDetailsInput.getAnimalMarket().toString());
    webDriverHelpers.clickWebElementByText(
        PERCUTANEOUS, exposureDetailsInput.getPercutaneous().toString());
    webDriverHelpers.clickWebElementByText(
        CONTACT_TO_BODY_FLUIDS, exposureDetailsInput.getContactToBodyFluids().toString());
    webDriverHelpers.clickWebElementByText(
        HANDLING_SAMPLES, exposureDetailsInput.getHandlingSamples().toString());
    webDriverHelpers.selectFromCombobox(
        TYPE_OF_PLACE, exposureDetailsInput.getTypeOfPlace().toString());
    webDriverHelpers.selectFromCombobox(CONTINENT, exposureDetailsInput.getContinent().toString());
    webDriverHelpers.selectFromCombobox(
        SUBCONTINENT, exposureDetailsInput.getSubcontinent().toString());
    webDriverHelpers.selectFromCombobox(COUNTRY, exposureDetailsInput.getCountry().toString());
    webDriverHelpers.selectFromCombobox(
        EXPOSURE_REGION, exposureDetailsInput.getExposureRegion().toString());
    webDriverHelpers.selectFromCombobox(DISTRICT, exposureDetailsInput.getDistrict().toString());
    webDriverHelpers.selectFromCombobox(COMMUNITY, exposureDetailsInput.getCommunity().toString());
    webDriverHelpers.fillInWebElement(STREET, exposureDetailsInput.getStreet().toString());
    webDriverHelpers.fillInWebElement(
        HOUSE_NUMBER, exposureDetailsInput.getHouseNumber().toString());
    webDriverHelpers.fillInWebElement(
        ADDITIONAL_INFORMATION, exposureDetailsInput.getAdditionalInformation().toString());
    webDriverHelpers.fillInWebElement(POSTAL_CODE, exposureDetailsInput.getPostalCode().toString());
    webDriverHelpers.fillInWebElement(CITY, exposureDetailsInput.getCity().toString());
    webDriverHelpers.selectFromCombobox(AREA_TYPE, exposureDetailsInput.getAreaType().toString());
    webDriverHelpers.fillInWebElement(
        COMMUNITY_CONTACT_PERSON, exposureDetailsInput.getCommunityContactPerson().toString());
    webDriverHelpers.fillInWebElement(
        GPS_LATITUDE, exposureDetailsInput.getGpsLatitude().toString());
      webDriverHelpers.fillInWebElement(
              GPS_LATITUDE, exposureDetailsInput.getGpsLatitude().toString());
    webDriverHelpers.fillInWebElement(
        GPS_LONGITUDE, exposureDetailsInput.getGpsLongitude().toString());
    webDriverHelpers.fillInWebElement(
        GPS_ACCURACY, exposureDetailsInput.getGpsAccuracy().toString());
  }

  public ExposureDetails getExposureDetailsOutput() {
    return ExposureDetails.builder()
        .startOfExposure(webDriverHelpers.getValueFromWebElement(START_OF_EXPOSURE))
        .endOfExposure(webDriverHelpers.getValueFromWebElement(END_OF_EXPOSURE))
        .exposureDescription(webDriverHelpers.getValueFromWebElement(EXPOSURE_DESCRIPTION))
        .typeOfActivity(webDriverHelpers.getValueFromWebElement(TYPE_OF_ACTIVITY))
        .exposureDetailsRole(webDriverHelpers.getValueFromWebElement(EXPOSURE_DETAILS_ROLE))
        .riskArea(webDriverHelpers.getValueFromWebElement(RISK_AREA))
        .indoors(webDriverHelpers.getValueFromWebElement(INDOORS))
        .outdoors(webDriverHelpers.getValueFromWebElement(OUTDOORS))
        .wearingMask(webDriverHelpers.getValueFromWebElement(WEARING_MASK))
        .wearingPpe(webDriverHelpers.getValueFromWebElement(WEARING_PPE))
        .otherProtectiveMeasures(webDriverHelpers.getValueFromWebElement(OTHER_PROTECTIVE_MEASURES))
        .shortDistance(webDriverHelpers.getValueFromWebElement(SHORT_DISTANCE))
        .longFaceToFaceContact(webDriverHelpers.getValueFromWebElement(LONG_FACE_TO_FACE_CONTACT))
        .animalMarket(webDriverHelpers.getValueFromWebElement(ANIMAL_MARKET))
        .percutaneous(webDriverHelpers.getValueFromWebElement(PERCUTANEOUS))
        .contactToBodyFluids(webDriverHelpers.getValueFromWebElement(CONTACT_TO_BODY_FLUIDS))
        .handlingSamples(webDriverHelpers.getValueFromWebElement(HANDLING_SAMPLES))
        .typeOfPlace(webDriverHelpers.getValueFromWebElement(TYPE_OF_PLACE))
        .continent(webDriverHelpers.getValueFromWebElement(CONTINENT))
        .subcontinent(webDriverHelpers.getValueFromWebElement(SUBCONTINENT))
        .country(webDriverHelpers.getValueFromWebElement(COUNTRY))
        .exposureRegion(webDriverHelpers.getValueFromWebElement(EXPOSURE_REGION))
        .district(webDriverHelpers.getValueFromWebElement(DISTRICT))
        .community(webDriverHelpers.getValueFromWebElement(COMMUNITY))
        .street(webDriverHelpers.getValueFromWebElement(STREET))
        .houseNumber(webDriverHelpers.getValueFromWebElement(HOUSE_NUMBER))
        .additionalInformation(webDriverHelpers.getValueFromWebElement(ADDITIONAL_INFORMATION))
        .postalCode(webDriverHelpers.getValueFromWebElement(POSTAL_CODE))
        .city(webDriverHelpers.getValueFromWebElement(CITY))
        .areaType(webDriverHelpers.getValueFromWebElement(AREA_TYPE))
        .communityContactPerson(webDriverHelpers.getValueFromWebElement(COMMUNITY_CONTACT_PERSON))
        .gpsLatitude(webDriverHelpers.getValueFromWebElement(GPS_LATITUDE))
        .gpsLongitude(webDriverHelpers.getValueFromWebElement(GPS_LONGITUDE))
        .gpsAccuracy(webDriverHelpers.getValueFromWebElement(GPS_ACCURACY))
        .build();
  }
}
