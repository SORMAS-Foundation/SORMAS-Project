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
}
