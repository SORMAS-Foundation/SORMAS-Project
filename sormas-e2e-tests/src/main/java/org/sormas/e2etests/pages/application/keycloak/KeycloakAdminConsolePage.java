package org.sormas.e2etests.pages.application.keycloak;

import org.openqa.selenium.By;

public class KeycloakAdminConsolePage {
  public static final By VIEW_ALL_USERS_BUTTON = By.id("viewAllUsers");
  public static final By USER_ID = By.cssSelector("td.clip a");
  public static final By NEXT_PAGE_BUTTON = By.cssSelector("button.next.ng-binding");
}
