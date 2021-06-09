package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class EditEpidemiologicalDataContactPage {
    public static final By EPIDEMIOLOGICAL_DATA_TAB  = By.id("tab-contacts-epidata");
    public static final By EXPOSURE_DETAILS_KNOWN = By.cssSelector("#exposureDetailsKnown");
    public static final By HIGH_TRANSMISSION_RISK_AREA = By.cssSelector("#highTransmissionRiskArea");
    public static final By LARGE_OUTBREAKS_AREA = By.cssSelector("#largeOutbreaksArea label");
    public static final By SAVE_BUTTON = By.id("commit");
}
