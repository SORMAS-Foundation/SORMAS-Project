package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class EditEpidemiologicalDataContactPage {
  public static final By EXPOSURE_DETAILS_KNOWN =
      By.cssSelector("div#exposureDetailsKnown span:nth-child(1) > label");
  public static final By HIGH_TRANSMISSION_RISK_AREA =
      By.cssSelector("div#highTransmissionRiskArea span:nth-child(1) > label");
  public static final By LARGE_OUTBREAKS_AREA =
      By.cssSelector("div#largeOutbreaksArea > span:nth-child(1)");
  public static final By EXPOSURE_DETAILS_NEW_ENTRY = By.cssSelector("div#actionNewEntry");
  public static final By SAVE_BUTTON = By.id("div#commit");
  public static final By EXPOSURE_EDIT =
      By.cssSelector("div#exposures td.v-table-cell-content span.v-button-wrap");
}
