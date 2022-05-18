package org.sormas.e2etests.pages.application;

import org.openqa.selenium.By;

public class AboutPage {
  public static final By DATA_DICTIONARY_BUTTON = By.id("aboutDataDictionary");
  public static final By SORMAS_VERSION_HYPERLINK =
      By.xpath("//div[@class='v-link v-widget vspace-3 v-link-vspace-3']//span");
  public static final By CASE_CLASSIFICATION_RULES_HYPERLINK =
      By.id("aboutCaseClassificationRules");
  public static final By SORMAS_VERSION_LINK =
      By.cssSelector("[class='v-link v-widget vspace-3 v-link-vspace-3'] span");
  public static final By SORMAS_VERSION_HYPERLINK_TARGET =
      By.cssSelector("[class='v-link v-widget vspace-3 v-link-vspace-3'] a");
  public static final By WHATS_NEW_HYPERLINK =
      By.cssSelector(".v-vertical .v-slot:nth-of-type(4) span");
  public static final By OFFICIAL_SORMAS_WEBSITE_HYPERLINK =
      By.cssSelector(".v-vertical .v-slot:nth-of-type(5) span");
  public static final By SORMAS_GITHUB_HYPERLINK =
      By.cssSelector(".v-vertical .v-slot:nth-of-type(6) span");
  public static final By FULL_CHANGELOG_HYPERLINK =
      By.cssSelector(".v-vertical .v-slot:nth-of-type(7) span");
}
