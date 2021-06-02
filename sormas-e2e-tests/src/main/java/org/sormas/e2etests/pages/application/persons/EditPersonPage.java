package org.sormas.e2etests.pages.application.persons;

import org.openqa.selenium.By;

public class EditPersonPage {
  public static final By EDIT_PERSON_POPUP_PERSON_ID = By.cssSelector("#uuid");
  public static final By EDIT_PERSON_POPUP_SAVE = By.cssSelector("#commit");
  public static final By EDIT_PERSON_POPUP_RESPONSIBLE_REGION_COMBOBOX =
      By.cssSelector("#region div");
  public static final By EDIT_PERSON_POPUP_RESPONSIBLE_DISTRICT_COMBOBOX =
      By.cssSelector("#district div");
  public static final By PERSON_DATA_SAVED = By.xpath("//*[contains(text(),'Person data saved')]");
  public static final By PERSON_DATA_ADDED_AS_A_PARTICIPANT_MESSAGE =
      By.xpath(
          "//*[contains(text(),'The case person was added as an event participant to the selected event.')]");
  public static final By EDIT_PERSON_SEE_EVENTS_FOR_PERSON =
      By.cssSelector("div#See\\ events\\ for\\ this\\ person");

  public static By getByPersonUuid(String personUuid) {
    return By.cssSelector("a[title='" + personUuid + "']");
  }
}
