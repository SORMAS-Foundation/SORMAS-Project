package org.sormas.e2etests.pages.application.messages;

import org.openqa.selenium.By;

public class MessagesDirectoryPage {
  public static final By FETCH_MESSAGES_BUTTON = By.cssSelector("[id='externalMessageFetch']");
  public static final By MESSAGES_TABLE_DATA = By.tagName("td");
  public static final By MESSAGES_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By MESSAGES_DETAILED_TABLE_ROWS =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By MESSAGE_EYE_ICON =
      By.xpath("(//div//span[@class='v-icon v-icon-eye'])[1]");
  public static final By MESSAGE_UUID_TEXT = By.id("uuid");
  public static final By MESSAGE_POPUP_HEADER =
      By.xpath("//div[@class='popupContent']//div[@class='v-window-header' and text()='Meldung']");
  public static final By SEARCH_MESSAGE_INPUT = By.id("searchFieldLike");
  public static final By VERARBEITEN_BUTTON = By.id("externalMessageProcess");

  public static final By RELATED_FORWARDED_MESSAGE_HEADER =
      By.xpath("//*[contains(text(),'Zugeh\u00F6rige weitergeleitete Meldung(en) gefunden')]");
  public static final By POPUP_CONFIRM_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static final By CHOOSE_OR_CREATE_ENTRY_HEADER =
      By.xpath("//*[contains(text(),'Eintrag ausw\u00E4hlen oder erstellen')]");
  public static final By UPDATE_THE_DISEASE_VARIANT_HEADER =
      By.xpath("//*[contains(text(),'Krankheitsvariante des Falls aktualisieren')]");
  public static final By SAVE_POPUP_CONTENT_FIRST_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[1]");
  public static final By SAVE_POPUP_CONTENT_SECOND_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[2]");
  public static final By NO_NEW_REPORTS_POPUP =
      By.xpath("//*[contains(text(),'Keine neuen Meldungen verf\u00FCgbar')]");
  public static final By CLOSE_POPUP = By.xpath("//div[@class='v-window-closebox']");

  public static final By GET_NEW_MESSAGES_POPUP =
      By.xpath("//*[contains(text(),'Neue Meldungen abrufen')]");

  public static By checkMappedValueSelector(String value) {
    return By.xpath(
        String.format(
            "//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase v-label-undef-w' and contains(text(), '%s')]",
            value));
  }

  public static By getProcessMessageButtonByIndex(int index) {
    return By.xpath(String.format("//table/tbody[1]/tr[%s]/td[14]", index));
  }

  public static By getProcessStatusByIndex(int index) {
    return By.xpath(String.format("//table/tbody[1]/tr[%s]/td[12]", index));
  }

  public static final By CREATE_NEW_CASE_POPUP_WINDOW_DE =
      By.xpath("//div[@class= 'v-window-header'][text()='Neuen Fall erstellen']");
  public static final By CREATE_NEW_SAMPLE_POPUP_WINDOW_DE =
      By.xpath("//div[@class= 'v-window-header'][text()='Neue Probe erstellen']");
  public static final By UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By POPUP_WINDOW_CANCEL_BUTTON = By.xpath("(//*[@id='discard'])[1]");
  public static final By POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON =
      By.cssSelector("#saveAndOpenEntryButton");
  public static final By POPUP_WINDOW_DISCARD_BUTTON = By.xpath("(//*[@id='discard'])[2]");
  public static final By POPUP_WINDOW_SAVE_BUTTON = By.cssSelector("#commit");
  public static final By MESSAGE_DIRECTORY_HEADER_DE =
      By.xpath("//div[@class='v-slot v-slot-view-header']//div[text()='Meldungsverzeichnis']");
  public static final By PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE =
      By.xpath(
          "//div[@location='externalMessageDetails']//h1[text()='Erregernachweismeldevorgang']");
  public static final By ONE_TEST_IN_SAMPLES_DE = By.xpath("//div[@location='samples']//div[text()='Anzahl der Tests: 1']");
}
