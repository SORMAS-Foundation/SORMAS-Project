package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class LineListingPopup {

  public static final By DISEASE_COMBOBOX = By.cssSelector("#lineListingDisease div");
  public static final By REGION_COMBOBOX = By.cssSelector("#lineListingRegion div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector("#lineListingDistrict div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#type div");
  public static final By DATE_OF_REPORT = By.cssSelector("#lineListingDateOfReport_0 input");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector("#lineListingCommunity_0 div");
  public static final By FACILITY_COMBOBOX = By.cssSelector("#lineListingFacility_0 div");
  public static final By FACILITY_NAME_INPUT = By.cssSelector("#lineListingFacilityDetails_0");
  public static final By FIRST_NAME_INPUT = By.cssSelector("#firstName");
  public static final By LAST_NAME_INPUT = By.cssSelector("#lastName");
  public static final By DATE_OF_BIRTH_YEAR_COMBOBOX =
      By.cssSelector(".popupContent #dateOfBirthYear input+div");
  public static final By SECOND_BIRTHDATE_YEAR_COMBOBOX =
      By.cssSelector(
          "[class='v-scrollable']>div>div:nth-child(2)>div>div:last-child [id='dateOfBirthYear'] div");
  public static final By DATE_OF_BIRTH_MONTH_COMBOBOX =
      By.cssSelector(".popupContent #dateOfBirthMonth input+div");
  public static final By SECOND_BIRTHDATE_MONTH_COMBOBOX =
      By.cssSelector(
          "[class='v-scrollable']>div>div:nth-child(2)>div>div:last-child [id='dateOfBirthMonth'] div");
  public static final By DATE_OF_BIRTH_DAY_COMBOBOX =
      By.cssSelector(".popupContent #dateOfBirthDay input+div");
  public static final By SECOND_BIRTHDATE_DAY_COMBOBOX =
      By.cssSelector(
          "[class='v-scrollable']>div>div:nth-child(2)>div>div:last-child [id='dateOfBirthDay'] div");
  public static final By SEX = By.cssSelector("#sex div");
  public static final By SECOND_SEX =
      By.cssSelector(
          "[class='v-scrollable']>div>div:nth-child(2)>div>div:last-child [id='sex'] div");
  public static final By DATE_OF_SYMPTOM_INPUT =
      By.cssSelector(".v-window #lineListingDateOfOnSet_0 input");
  public static final By SECOND_DATE_OF_SYMPTOM_INPUT =
      By.cssSelector(".v-window #lineListingDateOfOnSet_1 input");
  public static final By LINE_LISTING_ADD_LINE_BUTTON = By.id("lineListingAddLine");
  public static final By LINE_LISTING_SAVE_BUTTON = By.id("actionSave");
}
