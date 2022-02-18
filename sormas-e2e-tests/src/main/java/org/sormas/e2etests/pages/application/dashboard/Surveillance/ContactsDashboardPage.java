package org.sormas.e2etests.pages.application.dashboard.Surveillance;

import org.openqa.selenium.By;

public class ContactsDashboardPage {

  public static final By CONTACTS_DASHBOARD_NAME =
      By.xpath("//div[contains(text(),'Contacts Dashboard')]");
  public static final By CONTACTS_COVID19_COUNTER =
      By.xpath("//div[contains(text(),'COVID-19')]/parent::div/following-sibling::div[4]/div");
}
