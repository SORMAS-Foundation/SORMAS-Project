package org.sormas.e2etests.steps.nonBDDactions;

import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.LANGUAGE_COMBOBOX;
import static org.sormas.e2etests.pages.application.users.CreateNewUserPage.SAVE_BUTTON;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.NavBarPage;

@Slf4j
public class BackupSteps implements En {

  static WebDriverHelpers webDriverHelpers;

  @Inject
  public BackupSteps(WebDriverHelpers webDriverHelpers) {
    this.webDriverHelpers = webDriverHelpers;
  }

  public static void setAppLanguageToDefault(String language) {
    webDriverHelpers.clickOnWebElementBySelector(NavBarPage.USER_SETTINGS_BUTTON);
    webDriverHelpers.selectFromCombobox(LANGUAGE_COMBOBOX, language);
    webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
    webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
  }
}
