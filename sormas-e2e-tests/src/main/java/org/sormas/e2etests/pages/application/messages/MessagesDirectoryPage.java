package org.sormas.e2etests.pages.application.messages;

import org.openqa.selenium.By;

public class MessagesDirectoryPage {
  public static final By FETCH_MESSAGES_BUTTON = By.cssSelector("[id='externalMessageFetch']");
  public static final By MESSAGES_TABLE_DATA = By.tagName("td");
  public static final By MESSAGES_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By MESSAGES_DETAILED_TABLE_ROWS =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static By getProcessMessageButtonByIndex(int index) {
    return By.xpath(String.format("//table/tbody[1]/tr[%s]/td[14]", index));
  }
}
