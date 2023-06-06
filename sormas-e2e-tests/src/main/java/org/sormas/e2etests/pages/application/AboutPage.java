package org.sormas.e2etests.pages.application;

import org.openqa.selenium.By;

public class AboutPage {

  public static final By DATA_DICTIONARY_BUTTON = By.id("aboutDataDictionary");
  public static final By DATA_PROTECTION_DICTIONARY_BUTTON = By.id("aboutDataProtectionDictionary");
  public static final By CASE_CLASSIFICATION_RULES_HYPERLINK =
      By.id("aboutCaseClassificationRules");
  public static final By SORMAS_VERSION_LINK =
      By.xpath(
          "(//*[@class='v-label v-widget vspace-3 v-label-vspace-3 v-label-undef-w' or starts-with(@href, 'https://github.com/sormas-foundation/SORMAS-Project/commits/')])[1]");
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
  public static final By SURVNET_CONVERTER_VERSION_LABEL =
      By.xpath("//div[contains(text(), 'IfSG Meldesystem')]");
}
