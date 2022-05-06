package org.sormas.e2etests.pages.application;

import org.openqa.selenium.By;

public class AboutPage {
  public static final By DATA_DICTIONARY_BUTTON = By.id("aboutDataDictionary");
  public static final By SORMAS_VERSION_HYPERLINK =
      By.xpath("//div[@class='v-link v-widget vspace-3 v-link-vspace-3']//span");
}
