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
  public static final By TYPE_OF_ACTIVITY = By.cssSelector(".v-window #exposureType div");
  public static final By EXPOSURE_DETAILS_ROLE = By.cssSelector(".v-window #exposureRole div");
  public static final By RISK_AREA = By.cssSelector(".v-window #riskArea span");
  public static final By INDOORS = By.cssSelector(".v-window #indoors .v-select-option");
  public static final By OUTDOORS = By.cssSelector(".v-window #outdoors .v-select-option");
  public static final By WEARING_MASK =
      By.cssSelector(".v-window #wearingMask span.v-select-option");
  public static final By WEARING_PPE = By.cssSelector(".v-window #wearingPpe span.v-select-option");
  public static final By OTHER_PROTECTIVE_MEASURES =
      By.cssSelector(".v-window #otherProtectiveMeasures .v-select-option");
  public static final By SHORT_DISTANCE =
      By.cssSelector(".v-window #shortDistance .v-select-option");
  public static final By LONG_FACE_TO_FACE_CONTACT =
      By.cssSelector(".v-window #longFaceToFaceContact span.v-checkbox");
  public static final By ANIMAL_MARKET = By.cssSelector(".v-window #animalMarket span.v-checkbox");
  public static final By PERCUTANEOUS = By.cssSelector(".v-window #percutaneous .v-select-option");
  public static final By CONTACT_TO_BODY_FLUIDS =
      By.cssSelector(".v-window #contactToBodyFluids .v-select-option");
  public static final By HANDLING_SAMPLES =
      By.cssSelector(".v-window #handlingSamples .v-select-option");
  public static final By CONTACT_TO_SOURCE_CASE = By.cssSelector(".v-window #contactToCase");
  public static final By TYPE_OF_PLACE =
      By.cssSelector(".v-window #typeOfPlace div.v-filterselect-button");
  public static final By CONTINENT =
      By.cssSelector(".v-window #continent div.v-filterselect-button");
  public static final By SUBCONTINENT =
      By.cssSelector(".v-window #subcontinent div.v-filterselect-button");
  public static final By COUNTRY = By.cssSelector(".v-window #country div.v-filterselect-button");
  public static final By EXPOSURE_REGION =
      By.cssSelector(".v-window #region div.v-filterselect-button");
  public static final By DISTRICT = By.cssSelector(".v-window #district div.v-filterselect-button");
  public static final By COMMUNITY =
      By.cssSelector(".v-window #community div.v-filterselect-button");
  public static final By STREET = By.cssSelector(".v-window #street");
  public static final By HOUSE_NUMBER = By.cssSelector(".v-window #houseNumber");
  public static final By ADDITIONAL_INFORMATION =
      By.cssSelector(".v-window #additionalInformation");
  public static final By POSTAL_CODE = By.cssSelector(".v-window #postalCode");
  public static final By CITY = By.cssSelector(".v-window #city");
  public static final By AREA_TYPE =
      By.cssSelector(".v-window #areaType div.v-filterselect-button");
  public static final By COMMUNITY_CONTACT_PERSON = By.cssSelector(".v-window #details");
  public static final By GPS_LATITUDE = By.cssSelector(".v-window #latitude");
  public static final By GPS_LONGITUDE = By.cssSelector(".v-window #longitude");
  public static final By GPS_ACCURACY = By.cssSelector(".v-window #latLonAccuracy");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By DISCARD_BUTTON = By.cssSelector(".v-window #discard");
}
