package org.sormas.e2etests.steps.nonBDDactions;

import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.enums.EnvLangsTranslations;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.strings.LanguageDetectorHelper;
import org.sormas.e2etests.pages.application.NavBarPage;

@Slf4j
public class BackupSteps implements En {

  static WebDriverHelpers webDriverHelpers;

  @Inject
  public BackupSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;
  }

  public static void setAppLanguageToDefault(String envDefaultLanguage) {
    By referenceElement = By.xpath("//div[@id='dashboard']/span/span[2]");
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(referenceElement);
    String collectedText = webDriverHelpers.getTextFromWebElement(referenceElement);
    String langCode = LanguageDetectorHelper.scanLanguage(collectedText);
    StringBuilder languageToSelect = new StringBuilder();
    if (envDefaultLanguage.equalsIgnoreCase("main")) {
      languageToSelect.append(EnvLangsTranslations.getValueFor(langCode).getEnglishLang());
    } else if (envDefaultLanguage.equalsIgnoreCase("de")) {
      languageToSelect.append(EnvLangsTranslations.getValueFor(langCode).getGermanLang());
    }
    webDriverHelpers.clickOnWebElementBySelector(NavBarPage.USER_SETTINGS_BUTTON);
    webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, languageToSelect.toString());
    webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
    webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
  }
}
