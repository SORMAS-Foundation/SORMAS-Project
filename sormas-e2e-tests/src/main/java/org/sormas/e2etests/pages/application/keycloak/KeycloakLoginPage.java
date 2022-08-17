package org.sormas.e2etests.pages.application.keycloak;

import org.openqa.selenium.By;

public class KeycloakLoginPage {
  public static final By USERNAME_INPUT = By.cssSelector("input#username");
  public static final By PASSWORD_INPUT = By.cssSelector("input#password");
  public static final By LOGIN_KEYCLOAK_BUTTON = By.id("kc-login");
  public static final By USERNAME_TEXT = By.cssSelector("a.dropdown-toggle.ng-binding");
  public static final By LOGOUT_BUTTON = By.cssSelector("a.ng-binding");
}
