package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class EditEpidemiologicalDataContactPage {
  public static final By EXPOSURE_DETAILS_KNOWN =
      By.cssSelector("div#exposureDetailsKnown span.v-checkbox");
  public static final By HIGH_TRANSMISSION_RISK_AREA =
      By.cssSelector("div#highTransmissionRiskArea span.v-checkbox");
  public static final By LARGE_OUTBREAKS_AREA =
      By.cssSelector("div#largeOutbreaksArea span.v-checkbox");
  public static final By EXPOSURE_DETAILS_NEW_ENTRY = By.cssSelector("div#actionNewEntry");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By EXPOSURE_EDIT =
      By.cssSelector("[id*='de.symeda.sormas.api.exposure.ExposureDto']");
  public static final By CONTACT_DATA_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
}
