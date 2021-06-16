package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class ExposureNewEntryPage {
  public static final By EXPOSURE_DETAILS_KNOWN =
      By.cssSelector("#exposureDetailsKnown span:nth-child(1)");
  public static final By EXPOSURE_DETAILS_NEW_ENTRY = By.cssSelector("div#actionNewEntry");
  public static final By EXPOSURE_UUID = By.cssSelector("input #uuid");
  public static final By START_OF_EXPOSURE = By.cssSelector(".v-window #startDate input");
  public static final By END_OF_EXPOSURE = By.cssSelector(".v-window #endDate input");
  public static final By EXPOSURE_DESCRIPTION = By.cssSelector(".v-window #description");
  public static final By TYPE_OF_ACTIVITY_COMBOBOX =
      By.cssSelector(".v-window [location='exposureType'] [role='combobox'] div");
  public static final By EXPOSURE_DETAILS_ROLE_COMBOBOX =
      By.cssSelector(".v-window [location='exposureRole'] [role='combobox'] div");
  public static final By RISK_AREA = By.cssSelector(".v-window #riskArea label");
  public static final By INDOORS = By.cssSelector(".v-window #indoors label");
  public static final By OUTDOORS = By.cssSelector(".v-window #outdoors label");
  public static final By WEARING_MASK = By.cssSelector(".v-window #wearingMask label");
  public static final By WEARING_PPE = By.cssSelector(".v-window #wearingPpe label");
  public static final By OTHER_PROTECTIVE_MEASURES =
      By.cssSelector(".v-window #otherProtectiveMeasures label");
  public static final By SHORT_DISTANCE = By.cssSelector(".v-window #shortDistance label");
  public static final By LONG_FACE_TO_FACE_CONTACT =
      By.cssSelector(".v-window #longFaceToFaceContact label");
  public static final By ANIMAL_MARKET = By.cssSelector(".v-window #animalMarket label");
  public static final By PERCUTANEOUS = By.cssSelector(".v-window #percutaneous label");
  public static final By CONTACT_TO_BODY_FLUIDS =
      By.cssSelector(".v-window #contactToBodyFluids label");
  public static final By HANDLING_SAMPLES = By.cssSelector(".v-window #handlingSamples label");
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
  public static final By STREET = By.cssSelector(".v-window #street");
  public static final By HOUSE_NUMBER = By.cssSelector(".v-window #houseNumber");
  public static final By ADDITIONAL_INFORMATION =
      By.cssSelector(".v-window #additionalInformation");
  public static final By POSTAL_CODE = By.cssSelector(".v-window #postalCode");
  public static final By CITY = By.cssSelector(".v-window #city");
  public static final By AREA_TYPE_COMBOBOX =
      By.cssSelector(".v-window [location='areaType'] [role='combobox'] div");
  public static final By COMMUNITY_CONTACT_PERSON = By.cssSelector(".v-window #details");
  public static final By GPS_LATITUDE = By.cssSelector(".v-window #latitude");
  public static final By GPS_LONGITUDE = By.cssSelector(".v-window #longitude");
  public static final By GPS_ACCURACY = By.cssSelector(".v-window #latLonAccuracy");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By DISCARD_BUTTON = By.cssSelector(".v-window #discard");
}
