package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class ExposureNewEntryPage {
  public static final By START_OF_EXPOSURE_INPUT = By.cssSelector(".v-window #startDate input");
  public static final By END_OF_EXPOSURE_INPUT = By.cssSelector(".v-window #endDate input");
  public static final By EXPOSURE_DESCRIPTION_INPUT = By.cssSelector(".v-window #description");
  public static final By TYPE_OF_ACTIVITY_COMBOBOX =
      By.cssSelector(".v-window [location='exposureType'] [role='combobox'] div");
  public static final By EXPOSURE_DETAILS_ROLE_COMBOBOX =
      By.cssSelector(".v-window [location='exposureRole'] [role='combobox'] div");
  public static final By RISK_AREA_CHECKBOX = By.cssSelector(".v-window #riskArea label");
  public static final By INDOORS_CHECKBOX = By.cssSelector(".v-window #indoors label");
  public static final By OUTDOORS_CHECKBOX = By.cssSelector(".v-window #outdoors label");
  public static final By WEARING_MASK_CHECKBOX = By.cssSelector(".v-window #wearingMask label");
  public static final By WEARING_PPE_CHECKBOX = By.cssSelector(".v-window #wearingPpe label");
  public static final By OTHER_PROTECTIVE_MEASURES_CHECKBOX =
      By.cssSelector(".v-window #otherProtectiveMeasures label");
  public static final By SHORT_DISTANCE_CHECKBOX = By.cssSelector(".v-window #shortDistance label");
  public static final By LONG_FACE_TO_FACE_CONTACT_CHECKBOX =
      By.cssSelector(".v-window #longFaceToFaceContact label");
  public static final By ANIMAL_MARKET_CHECKBOX = By.cssSelector(".v-window #animalMarket label");
  public static final By PERCUTANEOUS_CHECKBOX = By.cssSelector(".v-window #percutaneous label");
  public static final By CONTACT_TO_BODY_FLUIDS_CHECKBOX =
      By.cssSelector(".v-window #contactToBodyFluids label");
  public static final By HANDLING_SAMPLES_CHECKBOX =
      By.cssSelector(".v-window #handlingSamples label");
  public static final By TYPE_OF_PLACE_COMBOBOX =
      By.cssSelector(".v-window [location='typeOfPlace'] [role='combobox'] div");
  public static final By CONTINENT_COMBOBOX =
      By.cssSelector(".v-window [location='continent'] [role='combobox'] div");
  public static final By SUBCONTINENT_COMBOBOX =
      By.cssSelector(".v-window [location='subcontinent'] [role='combobox'] div");
  public static final By COUNTRY_COMBOBOX =
      By.cssSelector(".v-window [location='country'] [role='combobox'] div");
  public static final By EXPOSURE_REGION_COMBOBOX =
      By.cssSelector(".v-window [location='region'] [role='combobox'] div");
  public static final By DISTRICT_COMBOBOX =
      By.cssSelector(".v-window [location='district'] [role='combobox'] div");
  public static final By COMMUNITY_COMBOBOX =
      By.cssSelector(".v-window [location='community'] [role='combobox'] div");
  public static final By STREET_INPUT = By.cssSelector(".v-window #street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector(".v-window #houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT =
      By.cssSelector(".v-window #additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector(".v-window #postalCode");
  public static final By CITY_INPUT = By.cssSelector(".v-window #city");
  public static final By AREA_TYPE_COMBOBOX =
      By.cssSelector(".v-window [location='areaType'] [role='combobox'] div");
  public static final By COMMUNITY_CONTACT_PERSON_INPUT = By.cssSelector(".v-window #details");
  public static final By GPS_LATITUDE_INPUT = By.cssSelector(".v-window #latitude");
  public static final By GPS_LONGITUDE_INPUT = By.cssSelector(".v-window #longitude");
  public static final By GPS_ACCURACY_INPUT = By.cssSelector(".v-window #latLonAccuracy");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By TYPE_OF_ACTIVITY_DETAILS = By.id("exposureTypeDetails");
  public static final By TYPE_OF_GATHERING_COMBOBOX =
      By.cssSelector(".v-window #gatheringType div");
  public static final By TYPE_OF_GATHERING_DETAILS = By.id("gatheringDetails");
  public static final By TYPE_OF_PLACE_DETAILS = By.id("typeOfPlaceDetails");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector(".v-window #typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector(".v-window #facilityType div");
  public static final By FACILITY_DETAILS_COMBOBOX = By.cssSelector(".v-window #facility div");
  public static final By FACILITY_NAME_AND_DESCRIPTION =
      By.cssSelector(".v-window input#facilityDetails");
  public static final By CONTACT_PERSON_FIRST_NAME =
      By.cssSelector(".v-window input#contactPersonFirstName");
  public static final By CONTACT_PERSON_LAST_NAME =
      By.cssSelector(".v-window input#contactPersonLastName");
  public static final By CONTACT_PERSON_PHONE_NUMBER =
      By.cssSelector(".v-window input#contactPersonPhone");
  public static final By CONTACT_PERSON_EMAIL_ADRESS =
      By.cssSelector(".v-window input#contactPersonEmail");
}
